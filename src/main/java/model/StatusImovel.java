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
	ATIVO("ativo", "Disponível"),
	RESERVADO("reservado", "Reservado"),
	VENDIDO("vendido", "Vendido"),
	ALUGADO("alugado", "Alugado"),
	INATIVO("inativo", "Inativo"),
	/**
	 * O anunciante não confirmou "ainda está disponível?" dentro do prazo
	 * (15 dias sem atualização + 7 dias sem resposta ao e-mail de
	 * confirmação) — some do catálogo até ele confirmar. Ver
	 * controller.ConfirmacaoStatusServlet e util.AgendadorStatusImovel.
	 */
	PENDENTE_CONFIRMACAO("pendente_confirmacao", "Aguardando confirmação");

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
