package model;

/**
 * Corresponde à coluna ENUM status da tabela CONTATO_INTERESSE.
 *
 * Representa o funil do lead gerado quando o cliente demonstra interesse em
 * um imóvel: começa em NOVO e é movido pelo vendedor conforme a negociação
 * avança.
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
