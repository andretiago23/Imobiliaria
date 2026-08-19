package dao;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Responsável por abrir conexões JDBC com o banco MySQL.
 *
 * As credenciais são lidas do arquivo db.properties, que fica no classpath
 * da aplicação (WEB-INF/classes/db.properties depois do deploy no Tomcat).
 *
 * O banco fica num servidor remoto (não localhost), e abrir uma conexão
 * nova custa cerca de 1,5s (round-trip de rede + handshake TLS/autenticação
 * do MySQL) — bem mais caro que qualquer consulta em si. Por isso as
 * conexões são reaproveitadas num pool simples: obterConexao() empresta uma
 * conexão já aberta em vez de abrir uma nova a cada chamada.
 *
 * Quem chama continua responsável por fechar a conexão, preferencialmente
 * com try-with-resources — só que "fechar" aqui devolve a conexão ao pool
 * em vez de encerrá-la de verdade:
 *
 * <pre>
 * try (Connection conexao = ConnectionFactory.obterConexao()) {
 *     // ...
 * }
 * </pre>
 */
public final class ConnectionFactory {

	private static final String ARQUIVO_PROPRIEDADES = "/db.properties";
	private static final String DRIVER_PADRAO = "com.mysql.cj.jdbc.Driver";

	/** Quantidade de conexões mantidas abertas no pool. */
	private static final int TAMANHO_POOL = 10;

	/** Tempo máximo de espera por uma conexão livre antes de desistir. */
	private static final long TEMPO_ESPERA_SEGUNDOS = 15;

	private static final String URL;
	private static final String USUARIO;
	private static final String SENHA;

	private static final BlockingQueue<Connection> POOL = new ArrayBlockingQueue<>(TAMANHO_POOL);

	static {
		Properties propriedades = carregarPropriedades();
		URL = propriedadeObrigatoria(propriedades, "db.url");
		USUARIO = propriedadeObrigatoria(propriedades, "db.usuario");
		SENHA = propriedadeObrigatoria(propriedades, "db.senha");
		carregarDriver(propriedades.getProperty("db.driver", DRIVER_PADRAO));
	}

	private ConnectionFactory() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * Empresta uma conexão do pool (abrindo uma nova na hora se o pool ainda
	 * não estiver cheio), ou abre uma avulsa se o pool estiver
	 * temporariamente esgotado. O close() da conexão devolvida não fecha a
	 * conexão de verdade — devolve ela ao pool para o próximo chamador.
	 *
	 * @return conexão pronta para uso, que deve ser "fechada" pelo chamador
	 * @throws DAOException se não for possível conectar
	 */
	public static Connection obterConexao() throws DAOException {
		Connection bruta = POOL.poll();

		if (bruta == null) {
			synchronized (ConnectionFactory.class) {
				if (contadorAbertas < TAMANHO_POOL) {
					bruta = abrirConexaoReal();
					contadorAbertas++;
				}
			}
		}

		if (bruta == null) {
			// Pool cheio e todas emprestadas no momento: espera uma sobrar em
			// vez de abrir mais uma (evitaria o benefício do pool sob carga).
			try {
				bruta = POOL.poll(TEMPO_ESPERA_SEGUNDOS, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new DAOException("Interrompido esperando uma conexão livre com o banco.", e);
			}
			if (bruta == null) {
				throw new DAOException("Nenhuma conexão com o banco ficou livre a tempo. Tente novamente.");
			}
		}

		if (!conexaoValida(bruta)) {
			fecharSilenciosamente(bruta);
			bruta = abrirConexaoReal();
		}

		return envolverComoPooled(bruta);
	}

	private static int contadorAbertas;

	private static boolean conexaoValida(Connection conexao) {
		try {
			return conexao.isValid(2);
		} catch (SQLException e) {
			return false;
		}
	}

	private static void fecharSilenciosamente(Connection conexao) {
		try {
			conexao.close();
		} catch (SQLException e) {
			// Conexão já quebrada mesmo — nada a fazer além de descartá-la.
		}
	}

	private static Connection abrirConexaoReal() throws DAOException {
		try {
			return DriverManager.getConnection(URL, USUARIO, SENHA);
		} catch (SQLException e) {
			throw new DAOException("Falha ao conectar no banco de dados.", e);
		}
	}

	/**
	 * Envolve a conexão real num proxy dinâmico que intercepta close(): em
	 * vez de fechar de verdade, devolve a conexão ao pool. Todo o resto do
	 * comportamento (consultas, transações, etc.) segue delegado direto para
	 * a conexão real — quem chama não percebe diferença nenhuma.
	 */
	private static Connection envolverComoPooled(Connection real) {
		InvocationHandler manipulador = (proxy, metodo, argumentos) -> {
			if ("close".equals(metodo.getName())) {
				POOL.offer(real);
				return null;
			}
			try {
				return metodo.invoke(real, argumentos);
			} catch (java.lang.reflect.InvocationTargetException e) {
				throw e.getCause();
			}
		};

		return (Connection) Proxy.newProxyInstance(
				ConnectionFactory.class.getClassLoader(),
				new Class<?>[] { Connection.class },
				manipulador);
	}

	private static Properties carregarPropriedades() {
		try (InputStream entrada = ConnectionFactory.class.getResourceAsStream(ARQUIVO_PROPRIEDADES)) {
			if (entrada == null) {
				throw new IllegalStateException(
						"Arquivo " + ARQUIVO_PROPRIEDADES + " não encontrado no classpath.");
			}
			Properties propriedades = new Properties();
			propriedades.load(entrada);
			return propriedades;
		} catch (IOException e) {
			throw new IllegalStateException("Não foi possível ler o arquivo " + ARQUIVO_PROPRIEDADES + ".", e);
		}
	}

	private static String propriedadeObrigatoria(Properties propriedades, String chave) {
		String valor = propriedades.getProperty(chave);
		if (valor == null || valor.isBlank()) {
			throw new IllegalStateException(
					"Propriedade obrigatória ausente em " + ARQUIVO_PROPRIEDADES + ": " + chave);
		}
		return valor;
	}

	private static void carregarDriver(String driver) {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Driver JDBC não encontrado: " + driver
					+ ". Verifique se o mysql-connector-j.jar está em WEB-INF/lib.", e);
		}
	}
}
