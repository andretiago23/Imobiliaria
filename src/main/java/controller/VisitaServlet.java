package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import dao.DAOException;
import dao.FotoImovelDAO;
import dao.ImovelDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Imovel;
import model.RegraNegocioException;
import model.Usuario;
import model.VisitaServico;
import model.VisitaSlot;
import util.SessaoUsuario;
import util.TokenCsrf;

/**
 * Agendamento de visita: GET mostra os horários livres (calculados a partir
 * da disponibilidade configurada pelo anunciante, menos o que já foi
 * reservado), POST confirma a escolha.
 */
@WebServlet("/imovel/visita")
public class VisitaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_VISITA = "/WEB-INF/jsp/agendar-visita.jsp";

	private final ImovelDAO imovelDAO = new ImovelDAO();
	private final FotoImovelDAO fotoImovelDAO = new FotoImovelDAO();
	private final VisitaServico visitaServico = new VisitaServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Integer idImovel = idValido(requisicao.getParameter("idImovel"));
		if (idImovel == null) {
			resposta.sendRedirect(requisicao.getContextPath() + "/inicio");
			return;
		}

		try {
			Optional<Imovel> imovel = imovelDAO.buscarPorId(idImovel);
			if (imovel.isEmpty()) {
				resposta.sendRedirect(requisicao.getContextPath() + "/inicio");
				return;
			}
			fotoImovelDAO.carregarFotos(imovel.get());
			requisicao.setAttribute("imovel", imovel.get());
			requisicao.setAttribute("slots", visitaServico.gerarSlotsDisponiveis(idImovel));
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar horários de visita do imóvel " + idImovel + ".", e);
			requisicao.setAttribute("erro", "Não foi possível carregar os horários agora.");
		}

		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA_VISITA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Integer idImovel = idValido(requisicao.getParameter("idImovel"));
		String destino = requisicao.getContextPath() + "/imovel/visita?idImovel=" + idImovel;

		if (idImovel == null || !TokenCsrf.valido(requisicao)) {
			resposta.sendRedirect(destino + "&erro=" + codificar("Não foi possível agendar. Tente novamente."));
			return;
		}

		List<String> partesSlot = List.of();
		String slotEscolhido = requisicao.getParameter("slot");
		if (slotEscolhido != null && slotEscolhido.contains("|")) {
			partesSlot = List.of(slotEscolhido.split("\\|", 2));
		}
		if (partesSlot.size() != 2) {
			resposta.sendRedirect(destino + "&erro=" + codificar("Escolha um horário disponível."));
			return;
		}

		Usuario usuario = SessaoUsuario.obter(requisicao);
		try {
			LocalDate data = LocalDate.parse(partesSlot.get(0));
			LocalTime horaInicio = LocalTime.parse(partesSlot.get(1));
			String linkPainel = linkAbsoluto(requisicao, "/imovel?id=" + idImovel);

			visitaServico.agendar(idImovel, usuario.getId(), data, horaInicio, linkPainel);
			resposta.sendRedirect(requisicao.getContextPath() + "/imovel?id=" + idImovel + "&visitaAgendada=1");

		} catch (java.time.format.DateTimeParseException e) {
			resposta.sendRedirect(destino + "&erro=" + codificar("Escolha um horário disponível."));
		} catch (RegraNegocioException e) {
			resposta.sendRedirect(destino + "&erro=" + codificar(e.getMessage()));
		} catch (DAOException e) {
			getServletContext().log("Falha ao agendar visita ao imóvel " + idImovel + ".", e);
			resposta.sendRedirect(destino + "&erro=" + codificar("Não foi possível agendar agora. Tente novamente."));
		}
	}

	private Integer idValido(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		try {
			return Integer.valueOf(valor);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String codificar(String valor) {
		return java.net.URLEncoder.encode(valor, java.nio.charset.StandardCharsets.UTF_8);
	}

	private String linkAbsoluto(HttpServletRequest requisicao, String caminho) {
		return requisicao.getRequestURL().toString().replace(requisicao.getRequestURI(), "")
				+ requisicao.getContextPath() + caminho;
	}
}
