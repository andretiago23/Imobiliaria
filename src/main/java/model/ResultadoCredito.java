package model;

/**
 * Corresponde à coluna ENUM resultado_credito da tabela CONTATO_INTERESSE.
 *
 * A consulta é inteiramente fictícia (ver CreditoServico): nunca há
 * integração real com birô de crédito, e o resultado nunca detalha valores
 * de dívida, credor ou qualquer dado financeiro.
 */
public enum ResultadoCredito implements ValorBanco {

	NAO_SOLICITADO("nao_solicitado", "Não solicitado"),
	NOME_REGULAR("nome_regular", "Nome regular"),
	RESTRICAO("restricao", "Restrição encontrada");

	private final String valorBanco;
	private final String rotulo;

	ResultadoCredito(String valorBanco, String rotulo) {
		this.valorBanco = valorBanco;
		this.rotulo = rotulo;
	}

	@Override
	public String getValorBanco() {
		return valorBanco;
	}

	@Override
	public String getRotulo() {
		return rotulo;
	}
}
