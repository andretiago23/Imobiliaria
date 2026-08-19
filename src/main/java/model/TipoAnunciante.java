package model;

/**
 * Corresponde à coluna ENUM tipo_anunciante da tabela ANUNCIO.
 *
 * É uma escolha por anúncio, não uma propriedade fixa da conta — a mesma
 * pessoa pode anunciar um imóvel próprio como proprietário e, num outro
 * anúncio, atuar como corretor. Não confundir com model.TipoUsuario
 * (comprador/vendedor), que é só o rótulo de cadastro da conta.
 */
public enum TipoAnunciante implements ValorBanco {

	PROPRIETARIO("proprietario", "Proprietário"),
	CORRETOR("corretor", "Corretor / imobiliária");

	private final String valorBanco;
	private final String rotulo;

	TipoAnunciante(String valorBanco, String rotulo) {
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
