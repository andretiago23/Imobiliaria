package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Um horário específico e livre para agendar visita — não é uma entidade de
 * banco, é o resultado de cruzar model.DisponibilidadeVisita (a configuração
 * geral do anunciante) com model.VisitaAgendada (o que já foi reservado),
 * calculado por model.VisitaServico ao montar o "calendário" de opções para
 * o comprador.
 */
public record VisitaSlot(LocalDate data, LocalTime horaInicio, LocalTime horaFim) {

	/**
	 * Valor único usado no <option> do formulário de agendamento, decodificado
	 * de volta em controller.VisitaServlet ao processar o POST.
	 */
	public String chave() {
		return data + "|" + horaInicio;
	}
}
