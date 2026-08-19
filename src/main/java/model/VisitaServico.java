package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import dao.DAOException;
import dao.DisponibilidadeVisitaDAO;
import dao.ImovelDAO;
import dao.UsuarioDAO;
import dao.VisitaAgendadaDAO;
import util.EmailService;

/**
 * Regras de negócio do agendamento de visitas: monta os horários livres a
 * partir da disponibilidade configurada pelo anunciante e grava o
 * agendamento só se o horário escolhido realmente existir e ainda estiver
 * livre — nunca confia apenas no que veio do formulário.
 */
public class VisitaServico {

	/** Duração fixa de cada horário oferecido, dentro da janela configurada. */
	private static final int DURACAO_VISITA_MINUTOS = 60;

	/** Até quantos dias à frente o calendário de opções mostra. */
	private static final int DIAS_A_FRENTE = 21;

	private final DisponibilidadeVisitaDAO disponibilidadeDAO = new DisponibilidadeVisitaDAO();
	private final VisitaAgendadaDAO visitaAgendadaDAO = new VisitaAgendadaDAO();
	private final ImovelDAO imovelDAO = new ImovelDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final EmailService emailService = new EmailService();

	/**
	 * @return os horários livres nos próximos {@value #DIAS_A_FRENTE} dias,
	 *         batendo a disponibilidade configurada contra o que já foi
	 *         reservado
	 */
	public List<VisitaSlot> gerarSlotsDisponiveis(int idImovel) throws DAOException {
		List<DisponibilidadeVisita> janelas = disponibilidadeDAO.listarPorImovel(idImovel);
		List<VisitaSlot> slots = new ArrayList<>();

		LocalDate hoje = LocalDate.now();
		for (int deslocamento = 0; deslocamento <= DIAS_A_FRENTE; deslocamento++) {
			LocalDate data = hoje.plusDays(deslocamento);
			DiaSemana diaSemana = DiaSemana.deDayOfWeek(data.getDayOfWeek());

			for (DisponibilidadeVisita janela : janelas) {
				if (janela.getDiaSemana() != diaSemana) {
					continue;
				}
				LocalTime inicio = janela.getHoraInicio();
				while (inicio.plusMinutes(DURACAO_VISITA_MINUTOS).compareTo(janela.getHoraFim()) <= 0) {
					LocalTime fim = inicio.plusMinutes(DURACAO_VISITA_MINUTOS);
					if (!visitaAgendadaDAO.horarioOcupado(idImovel, data, inicio)) {
						slots.add(new VisitaSlot(data, inicio, fim));
					}
					inicio = fim;
				}
			}
		}
		return slots;
	}

	/**
	 * Agenda a visita, conferindo de novo — no servidor — que o horário
	 * pedido está mesmo dentro da disponibilidade configurada, para não
	 * confiar apenas na lista de opções que o formulário mandou de volta.
	 *
	 * @param linkPainel URL completa da página do imóvel, para o e-mail
	 *                   avisando o anunciante
	 */
	public VisitaAgendada agendar(int idImovel, int idCliente, LocalDate data, LocalTime horaInicio, String linkPainel)
			throws RegraNegocioException, DAOException {

		boolean horarioValido = gerarSlotsDisponiveis(idImovel).stream()
				.anyMatch(slot -> slot.data().equals(data) && slot.horaInicio().equals(horaInicio));
		if (!horarioValido) {
			throw new RegraNegocioException("Esse horário não está mais disponível. Escolha outro.");
		}

		Imovel imovel = imovelDAO.buscarPorId(idImovel)
				.orElseThrow(() -> new RegraNegocioException("Imóvel não encontrado."));

		VisitaAgendada visita = new VisitaAgendada(idImovel, idCliente, data, horaInicio,
				horaInicio.plusMinutes(DURACAO_VISITA_MINUTOS));
		visitaAgendadaDAO.inserir(visita);

		Usuario cliente = usuarioDAO.buscarPorId(idCliente).orElse(null);
		Usuario proprietario = usuarioDAO.buscarPorId(imovel.getIdUsuario()).orElse(null);
		if (cliente != null && proprietario != null) {
			visita.setCliente(cliente);
			emailService.notificarVisitaAgendada(visita, imovel, proprietario, cliente, linkPainel);
		}

		return visita;
	}
}
