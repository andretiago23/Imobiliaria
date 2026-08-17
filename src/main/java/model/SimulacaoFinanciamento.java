package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro da tabela SIMULACAO_FINANCIAMENTO.
 *
 * Pode existir solta (o cliente só experimentou o simulador) ou vinculada a
 * um lead, quando o cliente escolhe anexá-la ao demonstrar interesse — por
 * isso idContato é opcional. Os valores calculados são ilustrativos: não há
 * integração real com nenhuma instituição financeira.
 */
public class SimulacaoFinanciamento {

	private int id;
	private Integer idContato;
	private BigDecimal valorImovel;
	private BigDecimal valorEntrada;
	private int prazoAnos;
	private SistemaAmortizacao sistemaAmortizacao;
	private String instituicaoReferencia;
	private BigDecimal valorFinanciado;
	private BigDecimal parcelaInicial;
	private BigDecimal totalJuros;
	private LocalDateTime dataSimulacao;

	public SimulacaoFinanciamento() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getIdContato() {
		return idContato;
	}

	public void setIdContato(Integer idContato) {
		this.idContato = idContato;
	}

	public BigDecimal getValorImovel() {
		return valorImovel;
	}

	public void setValorImovel(BigDecimal valorImovel) {
		this.valorImovel = valorImovel;
	}

	public BigDecimal getValorEntrada() {
		return valorEntrada;
	}

	public void setValorEntrada(BigDecimal valorEntrada) {
		this.valorEntrada = valorEntrada;
	}

	public int getPrazoAnos() {
		return prazoAnos;
	}

	public void setPrazoAnos(int prazoAnos) {
		this.prazoAnos = prazoAnos;
	}

	public SistemaAmortizacao getSistemaAmortizacao() {
		return sistemaAmortizacao;
	}

	public void setSistemaAmortizacao(SistemaAmortizacao sistemaAmortizacao) {
		this.sistemaAmortizacao = sistemaAmortizacao;
	}

	public String getInstituicaoReferencia() {
		return instituicaoReferencia;
	}

	public void setInstituicaoReferencia(String instituicaoReferencia) {
		this.instituicaoReferencia = instituicaoReferencia;
	}

	public BigDecimal getValorFinanciado() {
		return valorFinanciado;
	}

	public void setValorFinanciado(BigDecimal valorFinanciado) {
		this.valorFinanciado = valorFinanciado;
	}

	public BigDecimal getParcelaInicial() {
		return parcelaInicial;
	}

	public void setParcelaInicial(BigDecimal parcelaInicial) {
		this.parcelaInicial = parcelaInicial;
	}

	public BigDecimal getTotalJuros() {
		return totalJuros;
	}

	public void setTotalJuros(BigDecimal totalJuros) {
		this.totalJuros = totalJuros;
	}

	public LocalDateTime getDataSimulacao() {
		return dataSimulacao;
	}

	public void setDataSimulacao(LocalDateTime dataSimulacao) {
		this.dataSimulacao = dataSimulacao;
	}

	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof SimulacaoFinanciamento outra)) {
			return false;
		}
		return id != 0 && id == outra.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "SimulacaoFinanciamento [id=" + id + ", valorImovel=" + valorImovel + ", prazoAnos=" + prazoAnos + "]";
	}
}
