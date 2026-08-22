package controller;

import java.io.IOException;
import java.util.Optional;

import dao.DAOException;
import dao.RedefinicaoSenhaDAO;
import dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HashSenha;
import util.TokenCsrf;

/**
 * Segunda metade do fluxo de "esqueci minha senha" (ver
 * EsqueciSenhaServlet): confere o token de uso único do link do e-mail e,
 * sendo válido, deixa a pessoa escolher uma senha nova.
 *
 * Rota fora do FiltroAutenticacao (ver CAMINHOS_LIVRES) — precisa funcionar
 * a partir de um clique direto no e-mail, sem sessão logada.
 */
@WebServlet("/redefinir-senha")
public class RedefinirSenhaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/redefinir-senha.jsp";
	private static final int TAMANHO_MINIMO_SENHA = 8;

	private final RedefinicaoSenhaDAO redefinicaoSenhaDAO = new RedefinicaoSenhaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		String token = requisicao.getParameter("token");
		boolean tokenValido = conferirToken(token).isPresent();

		requisicao.setAttribute("token", token);
		requisicao.setAttribute("tokenValido", tokenValido);
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		String token = requisicao.getParameter("token");
		String senha = requisicao.getParameter("senha");
		String confirmarSenha = requisicao.getParameter("confirmarSenha");

		Optional<int[]> redefinicao = TokenCsrf.valido(requisicao) ? conferirToken(token) : Optional.empty();

		if (redefinicao.isEmpty()) {
			requisicao.setAttribute("token", token);
			requisicao.setAttribute("tokenValido", false);
			requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
			requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
			return;
		}

		String erro = validar(senha, confirmarSenha);
		if (erro != null) {
			requisicao.setAttribute("token", token);
			requisicao.setAttribute("tokenValido", true);
			requisicao.setAttribute("erro", erro);
			requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
			requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
			return;
		}

		try {
			int idRedefinicao = redefinicao.get()[0];
			int idUsuario = redefinicao.get()[1];
			usuarioDAO.atualizarSenha(idUsuario, HashSenha.gerar(senha));
			redefinicaoSenhaDAO.marcarUsado(idRedefinicao);
			resposta.sendRedirect(requisicao.getContextPath() + "/login?senhaRedefinida=1");
		} catch (DAOException e) {
			getServletContext().log("Falha ao redefinir a senha via token.", e);
			requisicao.setAttribute("token", token);
			requisicao.setAttribute("tokenValido", true);
			requisicao.setAttribute("erro", "Não foi possível salvar a nova senha agora. Tente novamente.");
			requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
			requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
		}
	}

	private Optional<int[]> conferirToken(String token) throws ServletException {
		if (token == null || token.isBlank()) {
			return Optional.empty();
		}
		try {
			return redefinicaoSenhaDAO.buscarValidoPorToken(token);
		} catch (DAOException e) {
			getServletContext().log("Falha ao conferir o token de redefinição de senha.", e);
			return Optional.empty();
		}
	}

	private String validar(String senha, String confirmarSenha) {
		if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
			return "A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.";
		}
		if (!senha.equals(confirmarSenha)) {
			return "As senhas não coincidem.";
		}
		return null;
	}
}
