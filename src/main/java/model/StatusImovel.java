package model;

/**
 * Corresponde à coluna ENUM status da tabela IMOVEL.
 */
public enum StatusImovel implements ValorBanco {

	/**
	 * Rascunho criado pelo assistente de anúncio (4 etapas) ao final da
	 * etapa 4, antes de ir para o pagamento. Nunca aparece no catálogo nem
	 * no painel "Meus imóveis" — só vira ATIVO quando o pagamento é
	 * confirmado (ver controller.PagamentoServlet).
	 */
	PENDENTE_PAGAMENTO("pendente_pagamento", "Aguardando pagamento"),
	ATIVO("ativo", "Ativo"),
	RESERVADO("reservado", "Reservado"),
	VENDIDO("vendido", "Vendido"),
	ALUGADO("alugado", "Alugado"),
	INATIVO("inativo", "Inativo");

	private final String valorBanco;
	private final String rotulo;

	StatusImovel(String valorBanco, String rotulo) {
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

	/**
	 * @return true se o imóvel ainda deve aparecer nas buscas do feed
	 */
	public boolean estaDisponivel() {
		return this == ATIVO || this == RESERVADO;
	}
}
