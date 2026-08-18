package util;

import java.security.SecureRandom;

/**
 * Geração de códigos curtos e legíveis, usados como identificador público de
 * uma imobiliária (o "convite" que o vendedor digita no cadastro).
 *
 * Não usa I, O, 0, 1 no alfabeto para evitar confusão visual ao digitar.
 */
public final class GeradorCodigo {

	private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
	private static final int TAMANHO_PADRAO = 6;
	private static final String PREFIXO_IMOBILIARIA = "IMB-";

	private static final SecureRandom GERADOR_ALEATORIO = new SecureRandom();

	private GeradorCodigo() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * @return um código no formato "IMB-XXXXXX", pronto para ser conferido de
	 *         unicidade pelo DAO antes de ser gravado
	 */
	public static String gerarCodigoImobiliaria() {
		return PREFIXO_IMOBILIARIA + gerarTrecho(TAMANHO_PADRAO);
	}

	private static String gerarTrecho(int tamanho) {
		StringBuilder codigo = new StringBuilder(tamanho);
		for (int posicao = 0; posicao < tamanho; posicao++) {
			codigo.append(ALFABETO.charAt(GERADOR_ALEATORIO.nextInt(ALFABETO.length())));
		}
		return codigo.toString();
	}
}
