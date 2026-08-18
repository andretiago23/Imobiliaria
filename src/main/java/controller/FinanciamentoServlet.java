package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Página de financiamento. O simulador (valor financiado, parcela estimada
 * por SAC, aviso de simulação ilustrativa) ainda não foi construído — esta
 * rota existe para a página não quebrar com 404 enquanto isso, e deixa claro
 * ao visitante que o recurso está a caminho, em vez de simular algo falso.
 */
@WebServlet("/financiamento")
public class FinanciamentoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_FINANCIAMENTO = "/WEB-INF/jsp/financiamento.jsp";

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {
		requisicao.getRequestDispatcher(PAGINA_FINANCIAMENTO).forward(requisicao, resposta);
	}
}
