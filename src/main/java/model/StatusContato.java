package model;

/**
 * Corresponde à coluna ENUM status da tabela CONTATO_INTERESSE.
 */
public enum StatusContato implements ValorBanco {

	PENDENTE("pendente", "Pendente"),
	RESPONDIDO("respondido", "Respondido"),
	ENCERRADO("encerrado", "Encerrado");

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
