package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Credenciais e parâmetros do servidor SMTP, lidos de mail.properties no
 * classpath (mesmo padrão do google.properties usado por ConfiguracaoGoogle).
 *
 * O envio de e-mail é um recurso auxiliar: se o arquivo não existir ou algum
 * disparo falhar, a aplicação continua funcionando normalmente — o lead ou o
 * anúncio já foram gravados no banco antes do e-mail ser tentado, então uma
 * falha de SMTP nunca deve impedir a ação principal do usuário.
 */
public final class ConfiguracaoEmail {

	private static final String ARQUIVO_PROPRIEDADES = "/mail.properties";

	private static volatile Properties propriedadesCarregadas;
	private static volatile boolean tentouCarregar;

	private ConfiguracaoEmail() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * @return true se host, usuário e senha SMTP estiverem configurados
	 */
	public static boolean configurado() {
		Properties propriedades = obterPropriedades();
		return propriedades != null
				&& textoPreenchido(propriedades.getProperty("mail.smtp.host"))
				&& textoPreenchido(propriedades.getProperty("mail.smtp.usuario"))
				&& textoPreenchido(propriedades.getProperty("mail.smtp.senha"));
	}

	public static String host() {
		return valor("mail.smtp.host", "");
	}

	public static int porta() {
		return Integer.parseInt(valor("mail.smtp.porta", "587"));
	}

	public static boolean autenticar() {
		return Boolean.parseBoolean(valor("mail.smtp.auth", "true"));
	}

	public static boolean starttls() {
		return Boolean.parseBoolean(valor("mail.smtp.starttls", "true"));
	}

	public static String usuario() {
		return valor("mail.smtp.usuario", "");
	}

	public static String senha() {
		return valor("mail.smtp.senha", "");
	}

	/**
	 * Endereço que aparece como remetente. Cai para o usuário SMTP se não for
	 * informado separadamente (comum quando o provedor exige que os dois
	 * sejam o mesmo endereço).
	 */
	public static String remetente() {
		String configurado = valor("mail.remetente", "");
		return textoPreenchido(configurado) ? configurado : usuario();
	}

	public static String nomeRemetente() {
		return valor("mail.remetente.nome", "Habittar");
	}

	/**
	 * URL base da aplicação (sem barra no final), usada para montar links em
	 * e-mails disparados fora do contexto de uma requisição HTTP — como o
	 * job agendado de confirmação de status (util.AgendadorStatusImovel), que
	 * não tem um HttpServletRequest de onde deduzir host/porta.
	 */
	public static String urlBase() {
		return valor("app.urlBase", "http://localhost:8080/imobiliaria");
	}

	private static String valor(String chave, String padrao) {
		Properties propriedades = obterPropriedades();
		String valor = propriedades == null ? null : propriedades.getProperty(chave);
		return textoPreenchido(valor) ? valor.trim() : padrao;
	}

	/**
	 * Carrega o arquivo uma única vez. Se não existir, guarda esse resultado
	 * (sem lançar exceção) para não tentar reabrir o arquivo a cada envio.
	 */
	private static Properties obterPropriedades() {
		if (tentouCarregar) {
			return propriedadesCarregadas;
		}
		synchronized (ConfiguracaoEmail.class) {
			if (!tentouCarregar) {
				propriedadesCarregadas = carregarSeExistir();
				tentouCarregar = true;
			}
		}
		return propriedadesCarregadas;
	}

	private static Properties carregarSeExistir() {
		try (InputStream entrada = ConfiguracaoEmail.class.getResourceAsStream(ARQUIVO_PROPRIEDADES)) {
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
