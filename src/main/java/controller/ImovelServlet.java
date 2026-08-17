package controller;

import java.io.IOException;
import java.util.Optional;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Imovel;
import model.ImovelServico;
import model.InteracaoServico;
import model.Usuario;
import util.SessaoUsuario;

/**
 * Detalhamento completo do imóvel.
 *
 * Fica de fora da lista de caminhos livres do FiltroAutenticacao de
 * propósito: só quem está autenticado chega aqui, e quem não estava é
 * devolvido para este mesmo imóvel depois do login, preservando a intenção
 * original (ver SessaoUsuario.guardarDestino/retirarDestino).
 */
@WebServlet("/imovel")
public class ImovelServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/imovel-detalhe.jsp";

	private final ImovelServico imovelServico = new ImovelServico();
	private final InteracaoServico interacaoServico = new InteracaoServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		int idImovel = idOuMenosUm(requisicao.getParameter("id"));
		if (idImovel <= 0) {
			resposta.sendRedirect(requisicao.getContextPath() + "/imoveis");
			return;
		}

		Object erroInteresse = requisicao.getSession().getAttribute("erroInteresse");
		if (erroInteresse != null) {
			requisicao.getSession().removeAttribute("erroInteresse");
			requisicao.setAttribute("erro", erroInteresse);
		}

		try {
			Optional<Imovel> imovel = imovelServico.buscarParaExibicao(idImovel);
			if (imovel.isEmpty()) {
				resposta.sendRedirect(requisicao.getContextPath() + "/imoveis");
				return;
			}

			requisicao.setAttribute("imovel", imovel.get());

			Usuario usuarioLogado = SessaoUsuario.obter(requisicao);
			requisicao.setAttribute("jaFavoritado", interacaoServico.listarFavoritos(usuarioLogado.getId())
					.stream().anyMatch(imovelFavoritado -> imovelFavoritado.getId() == idImovel));

		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar o imóvel de id " + idImovel + ".", e);
			requisicao.setAttribute("erro", "Não foi possível carregar este imóvel agora.");
		}

		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	private int idOuMenosUm(String valor) {
		try {
			return valor == null ? -1 : Integer.parseInt(valor.trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}
