package controller;

import java.io.IOException;
import java.util.Optional;

import dao.DAOException;
import dao.ImovelDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Imovel;
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
