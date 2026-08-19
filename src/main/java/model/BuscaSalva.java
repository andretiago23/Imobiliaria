package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa um registro da tabela BUSCA_SALVA: um filtro de catálogo que o
 * cliente guardou para não digitar de novo, com um alerta por e-mail opcional
 * ("me avise quando aparecer um imóvel assim").
 *
 * Todo campo de filtro é opcional — nulo significa "não filtra por isso",
 * igual ao FiltroImovel usado na busca do catálogo.
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

	/**
	 * @return true se o imóvel publicado combina com todos os critérios
	 *         preenchidos desta busca — usado para decidir se um alerta deve
	 *         ser disparado quando um novo anúncio entra no catálogo
	 */
	public boolean combinaCom(Imovel imovel) {
		if (tipo != null && tipo != imovel.getTipo()) {
			return false;
		}
		if (finalidade != null && finalidade != imovel.getFinalidade()) {
			return false;
		}
		if (cidade != null && !cidade.isBlank()
				&& (imovel.getCidade() == null || !imovel.getCidade().toLowerCase().contains(cidade.toLowerCase()))) {
			return false;
		}
		if (quartosMinimo != null && imovel.getQuartos() < quartosMinimo) {
			return false;
		}
		if (precoMaximo != null && imovel.getPreco() != null && imovel.getPreco().compareTo(precoMaximo) > 0) {
			return false;
		}
		return true;
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
}
