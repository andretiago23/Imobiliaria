package controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dao.AnuncioDAO;
import dao.ContatoInteresseDAO;
import dao.DAOException;
import dao.ImovelDAO;
import dao.VisitaAgendadaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Imovel;
import model.RegraNegocioException;
import model.StatusImovel;
import model.Usuario;
import util.ConversorEnum;
import util.SessaoUsuario;
import util.TokenCsrf;

/**
 * "Imóveis anunciados": todos os imóveis do usuário logado (comprador ou
 * vendedor — qualquer conta pode anunciar), com um resumo de métricas por
 * card e um atalho para trocar o status. O histórico continua visível
 * mesmo depois de vendido/alugado — a lista não filtra por status.
 */
@WebServlet("/imoveis-anunciados")
public class ImoveisAnunciadosServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/imoveis-anunciados.jsp";

	private final ImovelDAO imovelDAO = new ImovelDAO();
	private final AnuncioDAO anuncioDAO = new AnuncioDAO();
	private final ContatoInteresseDAO contatoInteresseDAO = new ContatoInteresseDAO();
	private final VisitaAgendadaDAO visitaAgendadaDAO = new VisitaAgendadaDAO();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);

		try {
			List<Imovel> imoveis = imovelDAO.listarPorUsuario(usuario.getId());
			requisicao.setAttribute("imoveis", imoveis);

			// Uma consulta por imóvel é aceitável aqui: a lista é do próprio
			// usuário, não tem paginação nem volume alto no escopo do projeto.
			Map<Integer, Integer> leadsPorImovel = new LinkedHashMap<>();
			Map<Integer, Integer> visitasPorImovel = new LinkedHashMap<>();
			for (Imovel imovel : imoveis) {
				leadsPorImovel.put(imovel.getId(), contatoInteresseDAO.listarPorImovel(imovel.getId()).size());
				visitasPorImovel.put(imovel.getId(), visitaAgendadaDAO.contarAgendadasFuturasPorImovel(imovel.getId()));
			}
			requisicao.setAttribute("leadsPorImovel", leadsPorImovel);
			requisicao.setAttribute("visitasPorImovel", visitasPorImovel);
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar os imóveis anunciados.", e);
			requisicao.setAttribute("erro", "Não foi possível carregar seus imóveis agora.");
		}

		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	/**
	 * Troca o status de um imóvel (disponível/reservado/vendido/alugado) a
	 * partir da própria lista, sem precisar abrir a edição completa.
	 */
	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);
		Integer idImovel = idValido(requisicao.getParameter("idImovel"));
		StatusImovel novoStatus = ConversorEnum.paraEnum(StatusImovel.class, requisicao.getParameter("status"));

		if (idImovel == null || novoStatus == null || !TokenCsrf.valido(requisicao)) {
			resposta.sendRedirect(requisicao.getContextPath() + "/imoveis-anunciados");
			return;
		}

		try {
			Imovel imovel = imovelDAO.buscarPorId(idImovel)
					.orElseThrow(() -> new RegraNegocioException("Imóvel não encontrado."));
			if (imovel.getIdUsuario() != usuario.getId()) {
				throw new RegraNegocioException("Você não tem permissão para alterar este anúncio.");
			}
			imovelDAO.atualizarStatus(idImovel, novoStatus);
		} catch (RegraNegocioException | DAOException e) {
			getServletContext().log("Falha ao alterar status do imóvel " + idImovel + ".", e);
		}

		resposta.sendRedirect(requisicao.getContextPath() + "/imoveis-anunciados");
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
}
