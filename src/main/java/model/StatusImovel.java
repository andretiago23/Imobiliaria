package model;

/**
 * Corresponde à coluna ENUM status da tabela IMOVEL.
 */
public enum StatusImovel implements ValorBanco {

	ATIVO("ativo", "Ativo"),
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
		return this == ATIVO;
	}
}
