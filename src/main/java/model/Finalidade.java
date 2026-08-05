package model;

/**
 * Corresponde à coluna ENUM finalidade da tabela IMOVEL.
 */
public enum Finalidade implements ValorBanco {

	VENDA("venda", "Venda"),
	ALUGUEL("aluguel", "Aluguel");

	private final String valorBanco;
	private final String rotulo;

	Finalidade(String valorBanco, String rotulo) {
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
