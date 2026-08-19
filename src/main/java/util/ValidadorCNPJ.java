package util;

/**
 * Validação matemática de CNPJ pelos dígitos verificadores — mesma lógica de
 * ValidadorCPF, para o campo "CPF ou CNPJ" usado tanto por pessoa física
 * quanto jurídica (ex.: uma imobiliária se cadastrando com CNPJ).
 */
public final class ValidadorCNPJ {

	private static final int TAMANHO_CNPJ = 14;
	private static final int[] PESOS_PRIMEIRO_DIGITO = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
	private static final int[] PESOS_SEGUNDO_DIGITO = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

	private ValidadorCNPJ() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * Confere os dois dígitos verificadores do CNPJ.
	 *
	 * @param cnpj número informado pelo usuário, com ou sem pontuação
	 * @return true se o CNPJ for matematicamente válido
	 */
	public static boolean isValido(String cnpj) {
		String digitos = apenasDigitos(cnpj);

		if (digitos.length() != TAMANHO_CNPJ || todosDigitosIguais(digitos)) {
			return false;
		}

		int primeiroDigito = calcularDigitoVerificador(digitos, PESOS_PRIMEIRO_DIGITO);
		int segundoDigito = calcularDigitoVerificador(digitos, PESOS_SEGUNDO_DIGITO);

		return primeiroDigito == valorNaPosicao(digitos, 12) && segundoDigito == valorNaPosicao(digitos, 13);
	}

	public static String apenasDigitos(String cnpj) {
		return cnpj == null ? "" : cnpj.replaceAll("\\D", "");
	}

	/**
	 * Formata o CNPJ como 00.000.000/0000-00 para exibição nas páginas.
	 *
	 * @return o CNPJ formatado, ou o valor original se não tiver 14 dígitos
	 */
	public static String formatar(String cnpj) {
		String digitos = apenasDigitos(cnpj);
		if (digitos.length() != TAMANHO_CNPJ) {
			return cnpj;
		}
		return digitos.substring(0, 2) + "." + digitos.substring(2, 5) + "." + digitos.substring(5, 8)
				+ "/" + digitos.substring(8, 12) + "-" + digitos.substring(12);
	}

	private static int calcularDigitoVerificador(String digitos, int[] pesos) {
		int soma = 0;
		for (int posicao = 0; posicao < pesos.length; posicao++) {
			soma += valorNaPosicao(digitos, posicao) * pesos[posicao];
		}
		int resto = soma % 11;
		return resto < 2 ? 0 : 11 - resto;
	}

	private static boolean todosDigitosIguais(String digitos) {
		return digitos.chars().distinct().count() == 1;
	}

	private static int valorNaPosicao(String digitos, int posicao) {
		return Character.getNumericValue(digitos.charAt(posicao));
	}
}
