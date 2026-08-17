package model;

/**
 * Corresponde à coluna ENUM tipo_usuario da tabela usuario.
 *
 * O banco define apenas 'comprador' e 'vendedor' — quem também quer comprar
 * enquanto anuncia imóveis escolhe "vendedor" mesmo, já que essa conta já
 * enxerga o catálogo inteiro normalmente.
 */
public enum TipoUsuario implements ValorBanco {

	COMPRADOR("comprador", "Comprador"),
	VENDEDOR("vendedor", "Vendedor / imobiliária");

	private final String valorBanco;
	private final String rotulo;

	TipoUsuario(String valorBanco, String rotulo) {
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
	 * @return true se o usuário tem permissão para anunciar imóveis
	 */
	public boolean podeAnunciar() {
		return this == VENDEDOR;
	}
}
