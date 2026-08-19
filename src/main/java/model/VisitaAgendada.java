package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representa um registro da tabela VISITA_AGENDADA: um horário específico
 * (data + intervalo) que um cliente reservou dentro da disponibilidade que o
 * anunciante configurou para o imóvel.
 */
public class VisitaAgendada {

	private int id;
	private int idImovel;
	private int idCliente;
	private LocalDate dataVisita;
	private LocalTime horaInicio;
	private LocalTime horaFim;
	private StatusVisita status;
	private LocalDateTime dataCriacao;

	private Imovel imovel;
	private Usuario cliente;

	public VisitaAgendada() {
	}

	public VisitaAgendada(int idImovel, int idCliente, LocalDate dataVisita, LocalTime horaInicio, LocalTime horaFim) {
		this.idImovel = idImovel;
		this.idCliente = idCliente;
		this.dataVisita = dataVisita;
		this.horaInicio = horaInicio;
		this.horaFim = horaFim;
		this.status = StatusVisita.AGENDADA;
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

	public int getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}

	public LocalDate getDataVisita() {
		return dataVisita;
	}

	public void setDataVisita(LocalDate dataVisita) {
		this.dataVisita = dataVisita;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalTime getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(LocalTime horaFim) {
		this.horaFim = horaFim;
	}

	public StatusVisita getStatus() {
		return status;
	}

	public void setStatus(StatusVisita status) {
		this.status = status;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Imovel getImovel() {
		return imovel;
	}

	public void setImovel(Imovel imovel) {
		this.imovel = imovel;
	}

	public Usuario getCliente() {
		return cliente;
	}

	public void setCliente(Usuario cliente) {
		this.cliente = cliente;
	}

	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof VisitaAgendada outra)) {
			return false;
		}
		return id != 0 && id == outra.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
