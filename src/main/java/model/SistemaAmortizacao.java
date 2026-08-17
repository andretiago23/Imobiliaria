package model;

/**
 * Corresponde à coluna ENUM sistema_amortizacao da tabela
 * SIMULACAO_FINANCIAMENTO.
 *
 * O protótipo simplifica para SAC apenas, como o próprio escopo do projeto
 * permite. O enum já existe para não exigir migração de banco caso o Price
 * seja acrescentado depois.
 */
public enum SistemaAmortizacao implements ValorBanco {

	SAC("sac", "SAC (Amortização Constante)");

	private final String valorBanco;
	private final String rotulo;

	SistemaAmortizacao(String valorBanco, String rotulo) {
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
