package controller;

import java.io.IOException;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.InteracaoServico;
import model.RegraNegocioException;
import model.Usuario;
import util.SessaoUsuario;
import util.TokenCsrf;

/**
 * Envio do "Tenho interesse" na página de detalhe do imóvel.
 *
 * Gera um lead para o anunciante — ação explícita do comprador, nunca
 * disparada automaticamente por login ou visualização passiva.
 */
@WebServlet("/interesse")
public class InteresseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final InteracaoServico interacaoServico = new InteracaoServico();

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		String idImovelTexto = requisicao.getParameter("idImovel");
		int idImovel = idValidoOuZero(idImovelTexto);
		String destino = requisicao.getContextPath() + "/imovel?id=" + idImovelTexto;

		if (idImovel == 0 || !TokenCsrf.valido(requisicao)) {
			resposta.sendRedirect(destino + "&erroInteresse=" + codificar("Não foi possível enviar seu interesse. Tente novamente."));
			return;
		}

		Usuario usuario = SessaoUsuario.obter(requisicao);
		try {
			String linkPainel = linkAbsoluto(requisicao, "/imovel?id=" + idImovel);
			interacaoServico.registrarInteresse(idImovel, usuario, requisicao.getParameter("mensagem"), linkPainel);
			resposta.sendRedirect(destino + "&interesseEnviado=1");
		} catch (RegraNegocioException e) {
			resposta.sendRedirect(destino + "&erroInteresse=" + codificar(e.getMessage()));
		} catch (DAOException e) {
			getServletContext().log("Falha ao registrar interesse no imóvel " + idImovel + ".", e);
			resposta.sendRedirect(destino + "&erroInteresse="
					+ codificar("Não foi possível enviar seu interesse agora. Tente novamente."));
		}
	}

	private int idValidoOuZero(String valor) {
		if (valor == null || valor.isBlank()) {
			return 0;
		}
		try {
			return Integer.parseInt(valor);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private String codificar(String valor) {
		return java.net.URLEncoder.encode(valor, java.nio.charset.StandardCharsets.UTF_8);
	}

	/**
	 * Monta a URL completa (com esquema e host) do link enviado por e-mail —
	 * um caminho relativo não abriria corretamente fora do navegador.
	 */
	private String linkAbsoluto(HttpServletRequest requisicao, String caminho) {
		return requisicao.getRequestURL().toString().replace(requisicao.getRequestURI(), "")
				+ requisicao.getContextPath() + caminho;
	}
}
