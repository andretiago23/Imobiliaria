package controller;

import java.io.IOException;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Usuario;
import model.UsuarioServico;
import util.SessaoUsuario;

/**
 * Configurações do perfil. Por enquanto cobre só a revogação da autorização
 * de consulta de crédito (PROJECT_SPEC seção 29); os demais dados de perfil
 * já existiam antes e continuam só em exibição aqui.
 */
@WebServlet("/perfil")
public class PerfilServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/perfil.jsp";

	private final UsuarioServico usuarioServico = new UsuarioServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);

		try {
			usuarioServico.buscarComImobiliaria(usuarioLogado.getId())
					.ifPresent(usuario -> requisicao.setAttribute("usuario", usuario));
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar o perfil.", e);
			requisicao.setAttribute("erro", "Não foi possível carregar seu perfil agora.");
		}

		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta) throws IOException {
		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);
		boolean autorizar = "on".equals(requisicao.getParameter("consentimentoCredito"));

		try {
			usuarioServico.alterarConsentimentoCredito(usuarioLogado.getId(), autorizar);
		} catch (DAOException e) {
			getServletContext().log("Falha ao atualizar o consentimento de crédito.", e);
		}

		resposta.sendRedirect(requisicao.getContextPath() + "/perfil");
	}
}
