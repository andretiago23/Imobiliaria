package controller;

import java.io.IOException;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.InteracaoServico;
import model.Usuario;
import util.SessaoUsuario;

/**
 * Alterna o favorito de um imóvel e lista os favoritos do usuário logado.
 */
@WebServlet({ "/favoritar", "/favoritos" })
public class FavoritoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/favoritos.jsp";

	private final InteracaoServico interacaoServico = new InteracaoServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);

		try {
			requisicao.setAttribute("imoveis", interacaoServico.listarFavoritos(usuarioLogado.getId()));
		} catch (DAOException e) {
			getServletContext().log("Falha ao listar favoritos.", e);
			requisicao.setAttribute("erro", "Não foi possível carregar seus favoritos agora.");
		}

		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta) throws IOException {
		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);

		try {
			int idImovel = Integer.parseInt(requisicao.getParameter("idImovel"));
			interacaoServico.alternarFavorito(usuarioLogado.getId(), idImovel);
		} catch (NumberFormatException | DAOException e) {
			getServletContext().log("Falha ao alternar favorito.", e);
		}

		String origem = requisicao.getParameter("origem");
		resposta.sendRedirect(requisicao.getContextPath() + (origem != null && !origem.isBlank() ? origem : "/favoritos"));
	}
}
