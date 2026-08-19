package model;

/**
 * Corresponde à coluna ENUM status_pagamento da tabela ANUNCIO.
 */
public enum StatusPagamento implements ValorBanco {

	PENDENTE("pendente", "Aguardando pagamento"),
	PAGO("pago", "Pago"),
	CANCELADO("cancelado", "Cancelado");

	private final String valorBanco;
	private final String rotulo;

	StatusPagamento(String valorBanco, String rotulo) {
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
