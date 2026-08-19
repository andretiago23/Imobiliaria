package model;

/**
 * Corresponde à coluna ENUM tipo_usuario da tabela usuario.
 *
 * O banco define apenas 'comprador' e 'vendedor'. Esse campo é só o "perfil
 * principal" de cadastro (ex.: rótulo exibido, imobiliárias profissionais
 * marcadas como vendedor) — desde que qualquer conta pode anunciar imóveis
 * (ver Usuario.podeAnunciar()), ele não restringe mais quem tem permissão
 * de publicar um anúncio.
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
}
