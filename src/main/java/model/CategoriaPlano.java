package model;

/**
 * Corresponde à coluna ENUM categoria da tabela PLANO.
 *
 * INDIVIDUAL é a compra de destaque para um único anúncio (o fluxo padrão do
 * assistente de anúncio). PACK é um lote de créditos de anúncio vendido só
 * para corretor/imobiliária, exibido na tela de planos como uma aba à parte.
 */
public enum CategoriaPlano implements ValorBanco {

	INDIVIDUAL("individual", "Individual"),
	PACK("pack", "Pack");

	private final String valorBanco;
	private final String rotulo;

	CategoriaPlano(String valorBanco, String rotulo) {
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
