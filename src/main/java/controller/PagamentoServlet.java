package controller;

import java.io.IOException;
import java.util.Optional;

import dao.AnuncioDAO;
import dao.DAOException;
import dao.ImovelDAO;
import dao.PlanoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Anuncio;
import model.Imovel;
import model.ImovelServico;
import model.StatusPagamento;
import model.Usuario;
import util.SessaoUsuario;
import util.TokenCsrf;

/**
 * Tela fictícia de "confirmar pagamento" que fecha o assistente de anúncio.
 *
 * Não integra com nenhum gateway de verdade — o botão já marca
 * status_pagamento = pago diretamente. Uma integração real trocaria o POST
 * daqui por um redirecionamento ao gateway e a confirmação viria por um
 * webhook, mas isso está fora do escopo deste projeto.
 */
@WebServlet("/anunciar/pagamento")
public class PagamentoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_PAGAMENTO = "/WEB-INF/jsp/pagamento.jsp";

	private final AnuncioDAO anuncioDAO = new AnuncioDAO();
	private final ImovelDAO imovelDAO = new ImovelDAO();
	private final PlanoDAO planoDAO = new PlanoDAO();
	private final ImovelServico imovelServico = new ImovelServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);
		Integer idAnuncio = idValido(requisicao.getParameter("id"));

		try {
			Optional<Anuncio> anuncio = idAnuncio == null ? Optional.empty() : anuncioDAO.buscarPorId(idAnuncio);
			if (anuncio.isEmpty() || anuncio.get().getIdAnunciante() != usuario.getId()) {
				resposta.sendRedirect(requisicao.getContextPath() + "/anunciar");
				return;
			}
			if (anuncio.get().getStatusPagamento() == StatusPagamento.PAGO) {
				resposta.sendRedirect(requisicao.getContextPath() + "/imovel?id=" + anuncio.get().getIdImovel());
				return;
			}

			Optional<Imovel> imovel = imovelDAO.buscarPorId(anuncio.get().getIdImovel());
			requisicao.setAttribute("anuncio", anuncio.get());
			requisicao.setAttribute("imovel", imovel.orElse(null));
			requisicao.setAttribute("plano", planoDAO.buscarPorId(anuncio.get().getIdPlano()).orElse(null));
			requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
			requisicao.getRequestDispatcher(PAGINA_PAGAMENTO).forward(requisicao, resposta);

		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar a tela de pagamento do anúncio " + idAnuncio + ".", e);
			resposta.sendRedirect(requisicao.getContextPath() + "/anunciar");
		}
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);
		Integer idAnuncio = idValido(requisicao.getParameter("id"));

		if (idAnuncio == null || !TokenCsrf.valido(requisicao)) {
			resposta.sendRedirect(requisicao.getContextPath() + "/anunciar");
			return;
		}

		try {
			Optional<Anuncio> anuncio = anuncioDAO.buscarPorId(idAnuncio);
			if (anuncio.isEmpty() || anuncio.get().getIdAnunciante() != usuario.getId()) {
				resposta.sendRedirect(requisicao.getContextPath() + "/anunciar");
				return;
			}

			boolean atualizou = anuncioDAO.marcarComoPago(idAnuncio, usuario.getId());
			if (atualizou) {
				String linkImovel = linkAbsoluto(requisicao, "/imovel?id=" + anuncio.get().getIdImovel());
				imovelServico.ativarAposPagamento(anuncio.get().getIdImovel(), linkImovel);
			}

			resposta.sendRedirect(requisicao.getContextPath() + "/imovel?id=" + anuncio.get().getIdImovel() + "&publicado=1");

		} catch (DAOException e) {
			getServletContext().log("Falha ao confirmar o pagamento do anúncio " + idAnuncio + ".", e);
			resposta.sendRedirect(requisicao.getContextPath() + "/anunciar/pagamento?id=" + idAnuncio);
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

	private String linkAbsoluto(HttpServletRequest requisicao, String caminho) {
		return requisicao.getRequestURL().toString().replace(requisicao.getRequestURI(), "")
				+ requisicao.getContextPath() + caminho;
	}
}
