package util;

/**
 * Validação matemática de CPF pelos dígitos verificadores.
 *
 * O resultado alimenta a coluna cpf_valido da tabela usuario.
 */
public final class ValidadorCPF {

	private static final int TAMANHO_CPF = 11;

	private ValidadorCPF() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * Confere os dois dígitos verificadores do CPF.
	 *
	 * Aceita o número com ou sem pontuação. Sequências de dígitos repetidos,
	 * como 111.111.111-11, são rejeitadas: passam no cálculo, mas não existem.
	 *
	 * @param cpf número informado pelo usuário
	 * @return true se o CPF for matematicamente válido
	 */
	public static boolean isValido(String cpf) {
		String digitos = apenasDigitos(cpf);

		if (digitos.length() != TAMANHO_CPF || todosDigitosIguais(digitos)) {
			return false;
		}

		int primeiroDigito = calcularDigitoVerificador(digitos, 9);
		int segundoDigito = calcularDigitoVerificador(digitos, 10);

		return primeiroDigito == valorNaPosicao(digitos, 9)
				&& segundoDigito == valorNaPosicao(digitos, 10);
	}

	/**
	 * Remove pontos, traços e espaços, deixando apenas os números.
	 *
	 * É este formato que deve ser gravado no banco, já que a coluna cpf é
	 * VARCHAR(11).
	 */
	public static String apenasDigitos(String cpf) {
		return cpf == null ? "" : cpf.replaceAll("\\D", "");
	}

	/**
	 * Formata o CPF como 000.000.000-00 para exibição nas páginas.
	 *
	 * @return o CPF formatado, ou o valor original se não tiver 11 dígitos
	 */
	public static String formatar(String cpf) {
		String digitos = apenasDigitos(cpf);
		if (digitos.length() != TAMANHO_CPF) {
			return cpf;
		}
		return digitos.substring(0, 3) + "." + digitos.substring(3, 6) + "."
				+ digitos.substring(6, 9) + "-" + digitos.substring(9);
	}

	/**
	 * Calcula um dígito verificador a partir dos primeiros dígitos do CPF.
	 *
	 * @param digitos    CPF contendo apenas números
	 * @param quantidade quantos dígitos entram no cálculo (9 para o primeiro
	 *                   verificador, 10 para o segundo)
	 */
	private static int calcularDigitoVerificador(String digitos, int quantidade) {
		int soma = 0;
		int peso = quantidade + 1;

		for (int posicao = 0; posicao < quantidade; posicao++) {
			soma += valorNaPosicao(digitos, posicao) * peso;
			peso--;
		}

		int resto = soma % TAMANHO_CPF;
		return resto < 2 ? 0 : TAMANHO_CPF - resto;
	}

	private static boolean todosDigitosIguais(String digitos) {
		return digitos.chars().distinct().count() == 1;
	}

	private static int valorNaPosicao(String digitos, int posicao) {
		return Character.getNumericValue(digitos.charAt(posicao));
	}
}
