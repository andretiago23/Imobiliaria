package controller;

import java.io.IOException;
import java.util.Optional;

import dao.DAOException;
import dao.DisponibilidadeVisitaDAO;
import dao.FavoritoDAO;
import dao.ImovelDAO;
import dao.VisualizacaoImovelDAO;
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
	private final VisualizacaoImovelDAO visualizacaoImovelDAO = new VisualizacaoImovelDAO();

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

			// Item 6.3: cada usuário conta no máximo 1 visualização por
			// imóvel, não uma por acesso — visualizacao_imovel (chave
			// composta id_usuario+id_imovel) impede duplicata, e só
			// incrementamos o contador quando é mesmo a primeira vez. A rota
			// exige login (FiltroAutenticacao), então sempre há um
			// usuarioLogado aqui. Não conta a visualização do próprio dono,
			// pra não inflar o número toda vez que ele confere o próprio
			// anúncio.
			if (usuarioLogado != null && !donoDoAnuncio
					&& visualizacaoImovelDAO.registrarSePrimeiraVez(usuarioLogado.getId(), id)) {
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
