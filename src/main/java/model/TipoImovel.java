package model;

/**
 * Corresponde à coluna ENUM tipo da tabela IMOVEL.
 *
 * IMPORTANTE: os valores de banco precisam ser idênticos aos declarados no
 * ENUM do MySQL. Ajustar conforme o script de criação das tabelas.
 */
public enum TipoImovel implements ValorBanco {

	CASA("casa", "Casa"),
	APARTAMENTO("apartamento", "Apartamento"),
	TERRENO("terreno", "Terreno"),
	COMERCIAL("comercial", "Imóvel comercial"),
	RURAL("rural", "Imóvel rural");

	private final String valorBanco;
	private final String rotulo;

	TipoImovel(String valorBanco, String rotulo) {
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
