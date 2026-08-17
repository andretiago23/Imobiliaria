package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Credenciais do login social com Google, lidas de google.properties no
 * classpath (mesmo padrão do db.properties usado pelo ConnectionFactory).
 *
 * Ao contrário do banco de dados, o login social é um recurso opcional: se o
 * arquivo não existir, a aplicação continua funcionando normalmente com login
 * por e-mail e senha — só o botão "Continuar com Google" fica indisponível.
 */
public final class ConfiguracaoGoogle {

	private static final String ARQUIVO_PROPRIEDADES = "/google.properties";

	private static volatile Properties propriedadesCarregadas;
	private static volatile boolean tentouCarregar;

	private ConfiguracaoGoogle() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * @return true se o client id e o client secret estiverem configurados
	 */
	public static boolean configurado() {
		Properties propriedades = obterPropriedades();
		return propriedades != null
				&& textoPreenchido(propriedades.getProperty("google.clientId"))
				&& textoPreenchido(propriedades.getProperty("google.clientSecret"));
	}

	public static String clientId() {
		return valorObrigatorio("google.clientId");
	}

	public static String clientSecret() {
		return valorObrigatorio("google.clientSecret");
	}

	/**
	 * URL completa de callback cadastrada no Google Cloud Console, ex.:
	 * http://localhost:8080/imobiliaria/auth/google/callback
	 */
	public static String redirectUri() {
		return valorObrigatorio("google.redirectUri");
	}

	private static String valorObrigatorio(String chave) {
		Properties propriedades = obterPropriedades();
		String valor = propriedades == null ? null : propriedades.getProperty(chave);
		if (!textoPreenchido(valor)) {
			throw new IllegalStateException(
					"Login com Google não está configurado (" + chave + " ausente em google.properties).");
		}
		return valor;
	}

	/**
	 * Carrega o arquivo uma única vez. Se não existir, guarda esse resultado
	 * (sem lançar exceção) para não tentar reabrir o arquivo a cada requisição.
	 */
	private static Properties obterPropriedades() {
		if (tentouCarregar) {
			return propriedadesCarregadas;
		}
		synchronized (ConfiguracaoGoogle.class) {
			if (!tentouCarregar) {
				propriedadesCarregadas = carregarSeExistir();
				tentouCarregar = true;
			}
		}
		return propriedadesCarregadas;
	}

	private static Properties carregarSeExistir() {
		try (InputStream entrada = ConfiguracaoGoogle.class.getResourceAsStream(ARQUIVO_PROPRIEDADES)) {
			if (entrada == null) {
				return null;
			}
			Properties propriedades = new Properties();
			propriedades.load(entrada);
			return propriedades;
		} catch (IOException e) {
			throw new IllegalStateException("Não foi possível ler o arquivo " + ARQUIVO_PROPRIEDADES + ".", e);
		}
	}

	private static boolean textoPreenchido(String texto) {
		return texto != null && !texto.isBlank();
	}
}
