package controller;

import java.io.IOException;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ContatoInteresse;
import model.InteracaoServico;
import model.RegraNegocioException;
import model.SimulacaoFinanciamento;
import model.Usuario;
import util.SessaoUsuario;

/**
 * Ação explícita "Tenho interesse", que gera o lead.
 *
 * O simples acesso, login, pesquisa ou visualização do imóvel nunca cria um
 * lead sozinho — só o POST desta tela (Regra 7 do PROJECT_SPEC).
 */
@WebServlet("/interesse")
public class InteresseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_CONFIRMACAO = "/WEB-INF/jsp/interesse-confirmado.jsp";

	private final InteracaoServico interacaoServico = new InteracaoServico();

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);
		int idImovel = Integer.parseInt(requisicao.getParameter("idImovel"));
		String mensagem = requisicao.getParameter("mensagem");
		boolean autorizarCredito = "on".equals(requisicao.getParameter("autorizarCredito"));
		boolean anexarSimulacao = "on".equals(requisicao.getParameter("anexarSimulacao"));

		SimulacaoFinanciamento simulacaoPendente = anexarSimulacao
				? (SimulacaoFinanciamento) requisicao.getSession().getAttribute(SimuladorServlet.ATRIBUTO_SIMULACAO)
				: null;

		try {
			ContatoInteresse lead = interacaoServico.registrarInteresse(idImovel, usuarioLogado, mensagem,
					autorizarCredito, simulacaoPendente);

			if (simulacaoPendente != null) {
				requisicao.getSession().removeAttribute(SimuladorServlet.ATRIBUTO_SIMULACAO);
			}

			requisicao.setAttribute("lead", lead);
			requisicao.getRequestDispatcher(PAGINA_CONFIRMACAO).forward(requisicao, resposta);

		} catch (RegraNegocioException e) {
			requisicao.getSession().setAttribute("erroInteresse", e.getMessage());
			resposta.sendRedirect(requisicao.getContextPath() + "/imovel?id=" + idImovel);

		} catch (DAOException e) {
			getServletContext().log("Falha ao registrar interesse no imóvel de id " + idImovel + ".", e);
			requisicao.getSession().setAttribute("erroInteresse",
					"Não foi possível registrar seu interesse agora. Tente novamente.");
			resposta.sendRedirect(requisicao.getContextPath() + "/imovel?id=" + idImovel);
		}
	}
}
