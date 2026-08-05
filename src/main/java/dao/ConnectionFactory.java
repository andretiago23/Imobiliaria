package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Responsável por abrir conexões JDBC com o banco MySQL.
 *
 * As credenciais são lidas do arquivo db.properties, que fica no classpath
 * da aplicação (WEB-INF/classes/db.properties depois do deploy no Tomcat).
 *
 * Cada chamada a obterConexao() devolve uma nova conexão. Quem chama é
 * responsável por fechá-la, preferencialmente com try-with-resources:
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

	private static final String URL;
	private static final String USUARIO;
	private static final String SENHA;

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
	 * Abre uma nova conexão com o banco de dados.
	 *
	 * @return conexão pronta para uso, que deve ser fechada pelo chamador
	 * @throws DAOException se não for possível conectar
	 */
	public static Connection obterConexao() throws DAOException {
		try {
			return DriverManager.getConnection(URL, USUARIO, SENHA);
		} catch (SQLException e) {
			throw new DAOException("Falha ao conectar no banco de dados.", e);
		}
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
