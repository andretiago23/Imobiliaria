package model;

import java.math.BigDecimal;

/**
 * Agrupa os critérios opcionais de busca de imóveis.
 *
 * Todo campo nulo é ignorado na consulta, o que permite montar a pesquisa do
 * feed sem criar um método de DAO diferente para cada combinação de filtros.
 */
public class FiltroImovel {

	private String cidade;
	private String estado;
	private TipoImovel tipo;
	private Finalidade finalidade;
	private BigDecimal precoMinimo;
	private BigDecimal precoMaximo;
	private Integer quartosMinimo;

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public TipoImovel getTipo() {
		return tipo;
	}

	public void setTipo(TipoImovel tipo) {
		this.tipo = tipo;
	}

	public Finalidade getFinalidade() {
		return finalidade;
	}

	public void setFinalidade(Finalidade finalidade) {
		this.finalidade = finalidade;
	}

	public BigDecimal getPrecoMinimo() {
		return precoMinimo;
	}

	public void setPrecoMinimo(BigDecimal precoMinimo) {
		this.precoMinimo = precoMinimo;
	}

	public BigDecimal getPrecoMaximo() {
		return precoMaximo;
	}

	public void setPrecoMaximo(BigDecimal precoMaximo) {
		this.precoMaximo = precoMaximo;
	}

	public Integer getQuartosMinimo() {
		return quartosMinimo;
	}

	public void setQuartosMinimo(Integer quartosMinimo) {
		this.quartosMinimo = quartosMinimo;
	}
}
