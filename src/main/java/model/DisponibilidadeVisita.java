package model;

import java.time.LocalTime;

/**
 * Representa um registro da tabela DISPONIBILIDADE_VISITA: uma janela de
 * horário (ex.: Segunda, 09:00–12:00) em que o anunciante aceita visitas
 * àquele imóvel. Um imóvel tem uma linha por combinação de dia+intervalo —
 * "Seg-Sex das 9h às 18h" vira 5 linhas, uma por dia.
 */
public class DisponibilidadeVisita {

	private int id;
	private int idImovel;
	private DiaSemana diaSemana;
	private LocalTime horaInicio;
	private LocalTime horaFim;

	public DisponibilidadeVisita() {
	}

	public DisponibilidadeVisita(int idImovel, DiaSemana diaSemana, LocalTime horaInicio, LocalTime horaFim) {
		this.idImovel = idImovel;
		this.diaSemana = diaSemana;
		this.horaInicio = horaInicio;
		this.horaFim = horaFim;
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

	public DiaSemana getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(DiaSemana diaSemana) {
		this.diaSemana = diaSemana;
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
}
