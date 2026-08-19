package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Tela 0 do fluxo de anúncio: landing "Anuncie seu imóvel", com duas
 * chamadas — "Anunciar agora" (inicia o assistente de 4 etapas, em
 * /anunciar/etapa1) e "Veja nossos planos" (leva direto para /planos, só
 * para consulta, sem iniciar o fluxo de anúncio).
 *
 * Qualquer usuário autenticado pode anunciar — ver Usuario.podeAnunciar() e
 * o comentário em ImovelServico.publicar().
 */
@WebServlet("/anunciar")
public class AnuncioServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_LANDING = "/WEB-INF/jsp/anuncio-landing.jsp";

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		requisicao.getRequestDispatcher(PAGINA_LANDING).forward(requisicao, resposta);
	}
}
