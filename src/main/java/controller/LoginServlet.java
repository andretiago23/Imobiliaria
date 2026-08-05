package controller;

import java.io.IOException;
import java.util.Optional;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Usuario;
import model.UsuarioServico;
import util.Html;
import util.SessaoUsuario;

/**
 * Autenticação do usuário.
 *
 * GET exibe o formulário, POST confere as credenciais e cria a sessão.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_LOGIN = "/WEB-INF/jsp/login.jsp";

	private final UsuarioServico usuarioServico = new UsuarioServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		if (SessaoUsuario.estaAutenticado(requisicao)) {
			resposta.sendRedirect(requisicao.getContextPath() + "/inicio");
			return;
		}
		requisicao.getRequestDispatcher(PAGINA_LOGIN).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		String email = requisicao.getParameter("email");
		String senha = requisicao.getParameter("senha");

		try {
			Optional<Usuario> autenticado = usuarioServico.autenticar(email, senha);

			if (autenticado.isEmpty()) {
				// A mensagem é a mesma para e-mail inexistente e senha errada,
				// para não revelar quais e-mails estão cadastrados.
				reexibirFormulario(requisicao, resposta, email, "E-mail ou senha incorretos.");
				return;
			}

			SessaoUsuario.registrar(requisicao, autenticado.get());
			resposta.sendRedirect(requisicao.getContextPath() + "/inicio");

		} catch (DAOException e) {
			getServletContext().log("Falha ao autenticar o usuário.", e);
			reexibirFormulario(requisicao, resposta, email,
					"Não foi possível acessar o sistema agora. Tente novamente em instantes.");
		}
	}

	private void reexibirFormulario(HttpServletRequest requisicao, HttpServletResponse resposta,
			String email, String mensagemErro) throws ServletException, IOException {

		requisicao.setAttribute("erro", mensagemErro);
		requisicao.setAttribute("email", Html.escapar(email));
		requisicao.getRequestDispatcher(PAGINA_LOGIN).forward(requisicao, resposta);
	}
}
