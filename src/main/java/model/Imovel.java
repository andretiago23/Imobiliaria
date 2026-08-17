package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Representa um registro da tabela IMOVEL.
 *
 * Os campos dono e fotos são associações opcionais: só ficam preenchidos
 * quando o DAO fizer a consulta com JOIN ou carregar as fotos explicitamente.
 */
public class Imovel {

	private int id;
	private int idUsuario;
	private String titulo;
	private String descricao;
	private TipoImovel tipo;
	private Finalidade finalidade;
	private BigDecimal preco;
	private double areaM2;
	private int quartos;
	private int banheiros;
	private int vagasGaragem;
	private Integer ano;
	private String endereco;
	private String cidade;
	private String estado;
	private String cep;
	private Double latitude;
	private Double longitude;
	private StatusImovel status;
	private LocalDateTime dataPublicacao;

	private Usuario dono;
	private List<FotoImovel> fotos = new ArrayList<>();

	public Imovel() {
	}

	/**
	 * Construtor usado na publicação de um novo anúncio.
	 */
	public Imovel(int idUsuario, String titulo, TipoImovel tipo, Finalidade finalidade, BigDecimal preco) {
		this.idUsuario = idUsuario;
		this.titulo = titulo;
		this.tipo = tipo;
		this.finalidade = finalidade;
		this.preco = preco;
		this.status = StatusImovel.ATIVO;
	}

	/**
	 * @return true se o imóvel ainda deve aparecer nas buscas
	 */
	public boolean estaDisponivel() {
		return status != null && status.estaDisponivel();
	}

	/**
	 * @return a foto de menor ordem, usada como capa no feed, ou null se não houver fotos
	 */
	public FotoImovel getFotoPrincipal() {
		return fotos.stream()
				.min(Comparator.comparingInt(FotoImovel::getOrdem))
				.orElse(null);
	}

	/**
	 * @return o endereço formatado para exibição nas páginas
	 */
	public String getEnderecoCompleto() {
		return endereco + ", " + cidade + " - " + estado;
	}

	/**
	 * @return o valor do imóvel dividido pela área, usado no comparador, ou
	 *         null se a área não estiver preenchida
	 */
	public BigDecimal getPrecoPorM2() {
		if (preco == null || areaM2 <= 0) {
			return null;
		}
		return preco.divide(BigDecimal.valueOf(areaM2), 2, java.math.RoundingMode.HALF_UP);
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

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	public double getAreaM2() {
		return areaM2;
	}

	public void setAreaM2(double areaM2) {
		this.areaM2 = areaM2;
	}

	public int getQuartos() {
		return quartos;
	}

	public void setQuartos(int quartos) {
		this.quartos = quartos;
	}

	public int getBanheiros() {
		return banheiros;
	}

	public void setBanheiros(int banheiros) {
		this.banheiros = banheiros;
	}

	public int getVagasGaragem() {
		return vagasGaragem;
	}

	public void setVagasGaragem(int vagasGaragem) {
		this.vagasGaragem = vagasGaragem;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

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

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public StatusImovel getStatus() {
		return status;
	}

	public void setStatus(StatusImovel status) {
		this.status = status;
	}

	public LocalDateTime getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(LocalDateTime dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public Usuario getDono() {
		return dono;
	}

	public void setDono(Usuario dono) {
		this.dono = dono;
	}

	public List<FotoImovel> getFotos() {
		return fotos;
	}

	public void setFotos(List<FotoImovel> fotos) {
		this.fotos = fotos == null ? new ArrayList<>() : fotos;
	}

	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof Imovel outro)) {
			return false;
		}
		return id != 0 && id == outro.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Imovel [id=" + id + ", titulo=" + titulo + ", tipo=" + tipo + ", finalidade=" + finalidade
				+ ", preco=" + preco + ", status=" + status + "]";
	}
}
