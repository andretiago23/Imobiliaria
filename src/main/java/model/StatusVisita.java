package model;

/**
 * Corresponde à coluna ENUM status da tabela VISITA_AGENDADA.
 */
public enum StatusVisita implements ValorBanco {

	AGENDADA("agendada", "Agendada"),
	CANCELADA("cancelada", "Cancelada"),
	REALIZADA("realizada", "Realizada");

	private final String valorBanco;
	private final String rotulo;

	StatusVisita(String valorBanco, String rotulo) {
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
