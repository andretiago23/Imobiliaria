package model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro da tabela CONTATO_INTERESSE.
 *
 * Guarda a mensagem enviada por um comprador interessado em um imóvel.
 * Os campos imovel e comprador são preenchidos apenas quando o DAO fizer a
 * consulta com JOIN, para montar a caixa de mensagens do anunciante.
 */
public class ContatoInteresse {

	private int id;
	private int idImovel;
	private int idComprador;
	private String mensagem;
	private StatusContato status;
	private boolean consultaCreditoAutorizada;
	private ResultadoCredito resultadoCredito;
	private LocalDateTime dataContato;

	private Imovel imovel;
	private Usuario comprador;
	private SimulacaoFinanciamento simulacao;

	public ContatoInteresse() {
	}

	/**
	 * Construtor usado no envio de uma nova mensagem de interesse.
	 *
	 * O lead sempre nasce no início do funil, em NOVO, e sem verificação de
	 * crédito solicitada: essas duas coisas só mudam por ação explícita do
	 * cliente ou do vendedor.
	 */
	public ContatoInteresse(int idImovel, int idComprador, String mensagem) {
		this.idImovel = idImovel;
		this.idComprador = idComprador;
		this.mensagem = mensagem;
		this.status = StatusContato.NOVO;
		this.resultadoCredito = ResultadoCredito.NAO_SOLICITADO;
	}

	/**
	 * @return true se o vendedor ainda não deu nenhum andamento a este lead
	 */
	public boolean aguardaResposta() {
		return status == StatusContato.NOVO;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdImovel() {
		return idImovel;
	}

	public void setIdImovel(int idImovel) {
		this.idImovel = idImovel;
	}

	public int getIdComprador() {
		return idComprador;
	}

	public void setIdComprador(int idComprador) {
		this.idComprador = idComprador;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public StatusContato getStatus() {
		return status;
	}

	public void setStatus(StatusContato status) {
		this.status = status;
	}

	public boolean isConsultaCreditoAutorizada() {
		return consultaCreditoAutorizada;
	}

	public void setConsultaCreditoAutorizada(boolean consultaCreditoAutorizada) {
		this.consultaCreditoAutorizada = consultaCreditoAutorizada;
	}

	public ResultadoCredito getResultadoCredito() {
		return resultadoCredito;
	}

	public void setResultadoCredito(ResultadoCredito resultadoCredito) {
		this.resultadoCredito = resultadoCredito;
	}

	public SimulacaoFinanciamento getSimulacao() {
		return simulacao;
	}

	public void setSimulacao(SimulacaoFinanciamento simulacao) {
		this.simulacao = simulacao;
	}

	public LocalDateTime getDataContato() {
		return dataContato;
	}

	public void setDataContato(LocalDateTime dataContato) {
		this.dataContato = dataContato;
	}

	public Imovel getImovel() {
		return imovel;
	}

	public void setImovel(Imovel imovel) {
		this.imovel = imovel;
	}

	public Usuario getComprador() {
		return comprador;
	}

	public void setComprador(Usuario comprador) {
		this.comprador = comprador;
	}

	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof ContatoInteresse outro)) {
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
		return "ContatoInteresse [id=" + id + ", idImovel=" + idImovel + ", idComprador=" + idComprador
				+ ", status=" + status + "]";
	}
}
