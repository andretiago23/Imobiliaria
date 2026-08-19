package model;

import java.math.BigDecimal;

/**
 * Guarda os dados do assistente de anúncio (4 etapas) na sessão HTTP entre
 * uma etapa e outra. Nada é gravado no banco até o fim da etapa 4 — assim um
 * usuário que desiste no meio do caminho não deixa um imóvel "fantasma" no
 * catálogo nem no painel de ninguém.
 *
 * Uma instância vive na sessão sob a chave SESSAO_CHAVE, criada na etapa 1 e
 * descartada assim que o anúncio é efetivamente contratado (fim da etapa 4).
 */
public class RascunhoAnuncio implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final String SESSAO_CHAVE = "rascunhoAnuncio";

	// Etapa 1 — o que anunciar + endereço do imóvel
	private Finalidade finalidade;
	private String titulo;
	private TipoImovel tipo;
	private BigDecimal preco;
	private double areaM2;
	private int quartos;
	private int banheiros;
	private int vagasGaragem;
	private String descricao;
	private String cep;
	private String endereco;
	private String numero;
	private String bairro;
	private String cidade;
	private String estado;

	// Etapa 2 — plano escolhido
	private Integer idPlano;

	// Etapa 3 — dados do anunciante
	private String nomeAnunciante;
	private String cpfCnpjAnunciante;
	private String celularAnunciante;
	private boolean enderecoAnuncianteIgualImovel;
	private String cepAnunciante;
	private String enderecoAnunciante;
	private String numeroAnunciante;
	private String bairroAnunciante;
	private String cidadeAnunciante;
	private String estadoAnunciante;

	public boolean etapa1Completa() {
		return finalidade != null && titulo != null && !titulo.isBlank() && tipo != null && preco != null
				&& cep != null && !cep.isBlank() && endereco != null && !endereco.isBlank()
				&& numero != null && !numero.isBlank() && bairro != null && !bairro.isBlank();
	}

	public boolean etapa2Completa() {
		return idPlano != null;
	}

	public boolean etapa3Completa() {
		return nomeAnunciante != null && !nomeAnunciante.isBlank()
				&& cpfCnpjAnunciante != null && !cpfCnpjAnunciante.isBlank()
				&& celularAnunciante != null && !celularAnunciante.isBlank();
	}

	public Finalidade getFinalidade() {
		return finalidade;
	}

	public void setFinalidade(Finalidade finalidade) {
		this.finalidade = finalidade;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public TipoImovel getTipo() {
		return tipo;
	}

	public void setTipo(TipoImovel tipo) {
		this.tipo = tipo;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
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

	public Integer getIdPlano() {
		return idPlano;
	}

	public void setIdPlano(Integer idPlano) {
		this.idPlano = idPlano;
	}

	public String getNomeAnunciante() {
		return nomeAnunciante;
	}

	public void setNomeAnunciante(String nomeAnunciante) {
		this.nomeAnunciante = nomeAnunciante;
	}

	public String getCpfCnpjAnunciante() {
		return cpfCnpjAnunciante;
	}

	public void setCpfCnpjAnunciante(String cpfCnpjAnunciante) {
		this.cpfCnpjAnunciante = cpfCnpjAnunciante;
	}

	public String getCelularAnunciante() {
		return celularAnunciante;
	}

	public void setCelularAnunciante(String celularAnunciante) {
		this.celularAnunciante = celularAnunciante;
	}

	public boolean isEnderecoAnuncianteIgualImovel() {
		return enderecoAnuncianteIgualImovel;
	}

	public void setEnderecoAnuncianteIgualImovel(boolean enderecoAnuncianteIgualImovel) {
		this.enderecoAnuncianteIgualImovel = enderecoAnuncianteIgualImovel;
	}

	public String getCepAnunciante() {
		return enderecoAnuncianteIgualImovel ? cep : cepAnunciante;
	}

	public void setCepAnunciante(String cepAnunciante) {
		this.cepAnunciante = cepAnunciante;
	}

	public String getEnderecoAnunciante() {
		return enderecoAnuncianteIgualImovel ? endereco : enderecoAnunciante;
	}

	public void setEnderecoAnunciante(String enderecoAnunciante) {
		this.enderecoAnunciante = enderecoAnunciante;
	}

	public String getNumeroAnunciante() {
		return enderecoAnuncianteIgualImovel ? numero : numeroAnunciante;
	}

	public void setNumeroAnunciante(String numeroAnunciante) {
		this.numeroAnunciante = numeroAnunciante;
	}

	public String getBairroAnunciante() {
		return enderecoAnuncianteIgualImovel ? bairro : bairroAnunciante;
	}

	public void setBairroAnunciante(String bairroAnunciante) {
		this.bairroAnunciante = bairroAnunciante;
	}

	public String getCidadeAnunciante() {
		return enderecoAnuncianteIgualImovel ? cidade : cidadeAnunciante;
	}

	public void setCidadeAnunciante(String cidadeAnunciante) {
		this.cidadeAnunciante = cidadeAnunciante;
	}

	public String getEstadoAnunciante() {
		return enderecoAnuncianteIgualImovel ? estado : estadoAnunciante;
	}

	public void setEstadoAnunciante(String estadoAnunciante) {
		this.estadoAnunciante = estadoAnunciante;
	}

	/**
	 * @return o endereço do imóvel em uma única linha, para o resumo da etapa 4
	 */
	public String enderecoImovelResumido() {
		StringBuilder partes = new StringBuilder();
		if (endereco != null && !endereco.isBlank()) {
			partes.append(endereco);
			if (numero != null && !numero.isBlank()) partes.append(", ").append(numero);
		}
		if (bairro != null && !bairro.isBlank()) {
			if (!partes.isEmpty()) partes.append(", ");
			partes.append(bairro);
		}
		if (cidade != null && !cidade.isBlank()) {
			if (!partes.isEmpty()) partes.append(" — ");
			partes.append(cidade);
			if (estado != null && !estado.isBlank()) partes.append("/").append(estado);
		}
		return partes.isEmpty() ? "Endereço não informado" : partes.toString();
	}
}
