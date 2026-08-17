package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro da tabela BUSCA_SALVA.
 *
 * Guarda os critérios de uma pesquisa para o cliente reaproveitar depois e,
 * opcionalmente, ser avisado quando um imóvel novo bater com o perfil salvo.
 * O envio do e-mail em si (o job que varre imóveis novos e dispara o alerta)
 * fica fora deste protótipo; aqui só existe o cadastro da busca e a flag de
 * opt-in, conforme as Regras 9 e 26 do PROJECT_SPEC.
 */
public class BuscaSalva {

	private int id;
	private int idUsuario;
	private String nome;
	private TipoImovel tipo;
	private Finalidade finalidade;
	private String cidade;
	private Integer quartosMinimo;
	private BigDecimal precoMaximo;
	private boolean alertaAtivo;
	private LocalDateTime dataCriacao;

	public BuscaSalva() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public Integer getQuartosMinimo() {
		return quartosMinimo;
	}

	public void setQuartosMinimo(Integer quartosMinimo) {
		this.quartosMinimo = quartosMinimo;
	}

	public BigDecimal getPrecoMaximo() {
		return precoMaximo;
	}

	public void setPrecoMaximo(BigDecimal precoMaximo) {
		this.precoMaximo = precoMaximo;
	}

	public boolean isAlertaAtivo() {
		return alertaAtivo;
	}

	public void setAlertaAtivo(boolean alertaAtivo) {
		this.alertaAtivo = alertaAtivo;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof BuscaSalva outra)) {
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
		return "BuscaSalva [id=" + id + ", idUsuario=" + idUsuario + ", nome=" + nome + "]";
	}
}
