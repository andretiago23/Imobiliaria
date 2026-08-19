package model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro da tabela ANUNCIO: a contratação de um plano pago
 * para publicar um imóvel, criada ao final da etapa 4 do assistente, antes
 * do redirecionamento para o pagamento.
 *
 * O imóvel em si (model.Imovel) só entra no catálogo público quando
 * statusPagamento vira PAGO — até lá fica com StatusImovel.PENDENTE_PAGAMENTO.
 */
public class Anuncio {

	private int id;
	private int idImovel;
	private int idPlano;
	private int idAnunciante;
	private StatusPagamento statusPagamento;
	private LocalDateTime dataContratacao;
	private LocalDateTime dataPagamento;

	private Imovel imovel;
	private Plano plano;

	public Anuncio() {
	}

	public Anuncio(int idImovel, int idPlano, int idAnunciante) {
		this.idImovel = idImovel;
		this.idPlano = idPlano;
		this.idAnunciante = idAnunciante;
		this.statusPagamento = StatusPagamento.PENDENTE;
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

	public int getIdPlano() {
		return idPlano;
	}

	public void setIdPlano(int idPlano) {
		this.idPlano = idPlano;
	}

	public int getIdAnunciante() {
		return idAnunciante;
	}

	public void setIdAnunciante(int idAnunciante) {
		this.idAnunciante = idAnunciante;
	}

	public StatusPagamento getStatusPagamento() {
		return statusPagamento;
	}

	public void setStatusPagamento(StatusPagamento statusPagamento) {
		this.statusPagamento = statusPagamento;
	}

	public LocalDateTime getDataContratacao() {
		return dataContratacao;
	}

	public void setDataContratacao(LocalDateTime dataContratacao) {
		this.dataContratacao = dataContratacao;
	}

	public LocalDateTime getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(LocalDateTime dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public Imovel getImovel() {
		return imovel;
	}

	public void setImovel(Imovel imovel) {
		this.imovel = imovel;
	}

	public Plano getPlano() {
		return plano;
	}

	public void setPlano(Plano plano) {
		this.plano = plano;
	}

	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof Anuncio outro)) {
			return false;
		}
		return id != 0 && id == outro.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
