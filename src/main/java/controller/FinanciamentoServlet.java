package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Página de financiamento: calculadora de poder de compra (renda + entrada
 * disponível → valor de imóvel estimado). Toda a conta é feita no cliente
 * (ver js/financiamento.js) — esta rota só entrega a página, sem estado
 * nem persistência nenhuma.
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
