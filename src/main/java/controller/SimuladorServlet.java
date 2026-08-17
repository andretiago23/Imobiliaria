package controller;

import java.io.IOException;
import java.math.BigDecimal;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.FinanciamentoServico;
import model.RegraNegocioException;
import model.SimulacaoFinanciamento;

/**
 * Simulador de financiamento (SAC), com aviso de que é apenas ilustrativo.
 *
 * O resultado calculado fica guardado na sessão, não no banco: só é gravado
 * de fato se o cliente escolher anexá-lo a um lead em InteresseServlet. Isso
 * evita registrar no banco toda simulação que o cliente só experimentou.
 */
@WebServlet("/simulador")
public class SimuladorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/simulador.jsp";

	/** Nome do atributo de sessão com a última simulação calculada, reaproveitado por InteresseServlet. */
	public static final String ATRIBUTO_SIMULACAO = "simulacaoPendente";

	private final FinanciamentoServico financiamentoServico = new FinanciamentoServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		requisicao.setAttribute("prazoMinimo", FinanciamentoServico.PRAZO_MINIMO_ANOS);
		requisicao.setAttribute("prazoMaximo", FinanciamentoServico.PRAZO_MAXIMO_ANOS);

		String idImovelOrigem = requisicao.getParameter("idImovelOrigem");
		String valorImovelOrigem = requisicao.getParameter("valorImovel");
		if (idImovelOrigem != null) {
			requisicao.setAttribute("idImovelOrigem", idImovelOrigem);
		}
		if (valorImovelOrigem != null) {
			requisicao.setAttribute("valorImovelSugerido", valorImovelOrigem);
		}

		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		requisicao.setAttribute("prazoMinimo", FinanciamentoServico.PRAZO_MINIMO_ANOS);
		requisicao.setAttribute("prazoMaximo", FinanciamentoServico.PRAZO_MAXIMO_ANOS);

		try {
			BigDecimal valorImovel = new BigDecimal(requisicao.getParameter("valorImovel").trim());
			BigDecimal valorEntrada = calcularEntrada(requisicao, valorImovel);
			int prazoAnos = Integer.parseInt(requisicao.getParameter("prazoAnos").trim());
			String instituicao = requisicao.getParameter("instituicaoReferencia");

			SimulacaoFinanciamento simulacao = financiamentoServico.simular(valorImovel, valorEntrada, prazoAnos,
					instituicao);

			requisicao.getSession().setAttribute(ATRIBUTO_SIMULACAO, simulacao);
			requisicao.setAttribute("resultado", simulacao);
			requisicao.setAttribute("idImovelOrigem", requisicao.getParameter("idImovelOrigem"));

		} catch (RegraNegocioException e) {
			requisicao.setAttribute("erro", e.getMessage());
		} catch (NumberFormatException | NullPointerException e) {
			requisicao.setAttribute("erro", "Preencha todos os campos com valores válidos.");
		}

		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	/**
	 * Aceita a entrada tanto em valor absoluto quanto em percentual do valor
	 * do imóvel, conforme o campo que o formulário enviar preenchido.
	 */
	private BigDecimal calcularEntrada(HttpServletRequest requisicao, BigDecimal valorImovel) {
		String percentualTexto = requisicao.getParameter("entradaPercentual");
		if (percentualTexto != null && !percentualTexto.isBlank()) {
			BigDecimal percentual = new BigDecimal(percentualTexto.trim());
			return valorImovel.multiply(percentual).divide(BigDecimal.valueOf(100));
		}
		return new BigDecimal(requisicao.getParameter("valorEntrada").trim());
	}
}
