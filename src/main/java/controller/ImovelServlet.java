package controller;

import java.io.IOException;
import java.util.Optional;

import dao.DAOException;
import dao.DisponibilidadeVisitaDAO;
import dao.FavoritoDAO;
import dao.ImovelDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Imovel;
import model.Usuario;
import util.SessaoUsuario;
import util.TokenCsrf;

/**
 * Detalhamento completo de um imóvel: endereço exato, descrição e ficha
 * técnica. Rota protegida pelo FiltroAutenticacao — quem não está logado é
 * mandado para /login e volta para cá assim que autenticar.
 */
@WebServlet("/imovel")
public class ImovelServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_IMOVEL = "/WEB-INF/jsp/imovel.jsp";

	private final ImovelDAO imovelDAO = new ImovelDAO();
	private final FavoritoDAO favoritoDAO = new FavoritoDAO();
	private final DisponibilidadeVisitaDAO disponibilidadeVisitaDAO = new DisponibilidadeVisitaDAO();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Integer id = idValido(requisicao.getParameter("id"));
		if (id == null) {
			resposta.sendRedirect(requisicao.getContextPath() + "/inicio");
			return;
		}

		try {
			Optional<Imovel> imovel = imovelDAO.buscarPorId(id);
			if (imovel.isEmpty()) {
				resposta.sendRedirect(requisicao.getContextPath() + "/inicio");
				return;
			}
			requisicao.setAttribute("imovel", imovel.get());
			requisicao.setAttribute("similares", imovelDAO.listarSimilares(imovel.get().getCidade(), id, 3));
			requisicao.setAttribute("disponibilidade", disponibilidadeVisitaDAO.listarPorImovel(id));

			Usuario usuarioLogado = SessaoUsuario.obter(requisicao);
			boolean donoDoAnuncio = usuarioLogado != null && usuarioLogado.getId() == imovel.get().getIdUsuario();
			if (usuarioLogado != null && !donoDoAnuncio) {
				requisicao.setAttribute("salvo", favoritoDAO.existe(usuarioLogado.getId(), id));
			}

			// Uma contagem simples de views, sem deduplicar por sessão/IP —
			// o suficiente para o painel "Imóveis anunciados" ter um número
			// de referência, não uma métrica de analytics de verdade. Não
			// conta a visualização do próprio dono, pra não inflar o número
			// toda vez que ele confere o próprio anúncio.
			if (!donoDoAnuncio) {
				imovelDAO.incrementarVisualizacao(id);
			}
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar o imóvel de id " + id + ".", e);
			requisicao.setAttribute("erro", "Não foi possível carregar este imóvel agora.");
		}

		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA_IMOVEL).forward(requisicao, resposta);
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
