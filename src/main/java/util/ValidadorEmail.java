package util;

import java.util.regex.Pattern;

/**
 * Validação do formato de endereços de e-mail.
 *
 * Confere apenas a estrutura do endereço. A existência real da caixa é
 * comprovada pela confirmação por e-mail, registrada na coluna
 * email_confirmado.
 */
public final class ValidadorEmail {

	private static final Pattern PADRAO_EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

	private static final int TAMANHO_MAXIMO = 150;

	private ValidadorEmail() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * @param email endereço informado pelo usuário
	 * @return true se o formato for válido e couber na coluna email
	 */
	public static boolean isValido(String email) {
		if (email == null || email.isBlank() || email.length() > TAMANHO_MAXIMO) {
			return false;
		}
		return PADRAO_EMAIL.matcher(email.trim()).matches();
	}

	/**
	 * Padroniza o endereço antes de gravar: sem espaços nas pontas e em
	 * minúsculas, para que a restrição UNIQUE funcione como esperado.
	 */
	public static String normalizar(String email) {
		return email == null ? null : email.trim().toLowerCase();
	}
}
