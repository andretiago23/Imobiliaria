package model;

/**
 * Corresponde à coluna ENUM status da tabela contato_interesse.
 */
public enum StatusContato implements ValorBanco {

	NOVO("novo", "Novo"),
	CONTATADO("contatado", "Contatado"),
	NEGOCIANDO("negociando", "Negociando"),
	CONVERTIDO("convertido", "Convertido"),
	PERDIDO("perdido", "Perdido");

	private final String valorBanco;
	private final String rotulo;

	StatusContato(String valorBanco, String rotulo) {
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
