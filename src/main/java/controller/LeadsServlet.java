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
import model.StatusContato;
import model.Usuario;
import util.ConversorEnum;
import util.SessaoUsuario;

/**
 * Mini-CRM do vendedor: leads recebidos (ContatoInteresse) e o andamento do
 * funil (Novo, Contatado, Negociando, Convertido, Perdido).
 */
@WebServlet("/meus-leads")
public class LeadsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/meus-leads.jsp";

	private final InteracaoServico interacaoServico = new InteracaoServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);

		Object erroPendente = requisicao.getSession().getAttribute("erroMeusLeads");
		if (erroPendente != null) {
			requisicao.getSession().removeAttribute("erroMeusLeads");
			requisicao.setAttribute("erro", erroPendente);
		}

		try {
			requisicao.setAttribute("leads", interacaoServico.listarInteressesRecebidos(usuarioLogado.getId()));
		} catch (DAOException e) {
			getServletContext().log("Falha ao listar leads recebidos.", e);
			requisicao.setAttribute("erro", "Não foi possível carregar seus leads agora.");
		}

		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta) throws IOException {
		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);

		try {
			int idContato = Integer.parseInt(requisicao.getParameter("idContato"));
			StatusContato novoStatus = ConversorEnum.paraEnum(StatusContato.class, requisicao.getParameter("status"));
			interacaoServico.atualizarSituacaoInteresse(idContato, novoStatus, usuarioLogado.getId());

		} catch (RegraNegocioException | IllegalArgumentException e) {
			requisicao.getSession().setAttribute("erroMeusLeads", e.getMessage());
		} catch (DAOException e) {
			getServletContext().log("Falha ao atualizar o status do lead.", e);
			requisicao.getSession().setAttribute("erroMeusLeads", "Não foi possível atualizar o lead agora.");
		}

		resposta.sendRedirect(requisicao.getContextPath() + "/meus-leads");
	}
}
