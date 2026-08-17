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
import util.TokenCsrf;

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

		String redirecionar = destinoPosLogin(requisicao);

		if (SessaoUsuario.estaAutenticado(requisicao)) {
			resposta.sendRedirect(requisicao.getContextPath() + redirecionar);
			return;
		}
		requisicao.setAttribute("redirecionar", Html.escapar(redirecionar));
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA_LOGIN).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		String email = requisicao.getParameter("email");
		String senha = requisicao.getParameter("senha");
		String redirecionar = destinoPosLogin(requisicao);

		if (!TokenCsrf.valido(requisicao)) {
			reexibirFormulario(requisicao, resposta, email, redirecionar,
					"Sua sessão expirou. Tente entrar novamente.");
			return;
		}

		try {
			Optional<Usuario> autenticado = usuarioServico.autenticar(email, senha);

			if (autenticado.isEmpty()) {
				// A mensagem é a mesma para e-mail inexistente e senha errada,
				// para não revelar quais e-mails estão cadastrados.
				reexibirFormulario(requisicao, resposta, email, redirecionar, "E-mail ou senha incorretos.");
				return;
			}

			SessaoUsuario.registrar(requisicao, autenticado.get());
			resposta.sendRedirect(requisicao.getContextPath() + redirecionar);

		} catch (DAOException e) {
			getServletContext().log("Falha ao autenticar o usuário.", e);
			reexibirFormulario(requisicao, resposta, email, redirecionar,
					"Não foi possível acessar o sistema agora. Tente novamente em instantes.");
		}
	}

	private void reexibirFormulario(HttpServletRequest requisicao, HttpServletResponse resposta,
			String email, String redirecionar, String mensagemErro) throws ServletException, IOException {

		requisicao.setAttribute("erro", mensagemErro);
		requisicao.setAttribute("email", Html.escapar(email));
		requisicao.setAttribute("redirecionar", Html.escapar(redirecionar));
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA_LOGIN).forward(requisicao, resposta);
	}

	/**
	 * Resolve para onde mandar o usuário depois de autenticar: o destino
	 * pedido em "redirecionar" (ex.: o imóvel que tentou abrir sem login),
	 * ou "/inicio" quando não há um valor confiável.
	 *
	 * Só aceita caminhos internos começando com "/" — nunca uma URL completa
	 * nem um caminho iniciado por "//", que o navegador trataria como
	 * endereço externo (open redirect).
	 */
	private String destinoPosLogin(HttpServletRequest requisicao) {
		String valor = requisicao.getParameter("redirecionar");
		if (valor == null || valor.isBlank()) {
			return "/inicio";
		}
		String destino = java.net.URLDecoder.decode(valor, java.nio.charset.StandardCharsets.UTF_8);
		boolean caminhoInterno = destino.startsWith("/") && !destino.startsWith("//");
		return caminhoInterno ? destino : "/inicio";
	}
}
