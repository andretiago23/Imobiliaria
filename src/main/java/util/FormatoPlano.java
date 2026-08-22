package util;

/**
 * Formata a duração de um model.Plano em texto curto para os cards de preço
 * (ex.: "1 ano", "6 meses", "mês"), do jeito que aparece nas telas de planos
 * e na etapa 2 do assistente de anúncio.
 */
public final class FormatoPlano {

	private FormatoPlano() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * @param duracaoDias duração do plano, em dias
	 * @return "1 ano" para 365 dias, "N meses" para múltiplos de ~30 dias
	 *         maiores que 30, "mês" para 30 dias (cobrança mensal recorrente)
	 *         e "N dias" como último recurso
	 */
	public static String periodo(int duracaoDias) {
		if (duracaoDias == 365) {
			return "1 ano";
		}
		if (duracaoDias == 30) {
			return "mês";
		}
		if (duracaoDias % 30 == 0) {
			return (duracaoDias / 30) + " meses";
		}
		return duracaoDias + " dias";
	}
}
