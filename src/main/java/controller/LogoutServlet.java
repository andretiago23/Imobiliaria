package controller;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.SessaoUsuario;

/**
 * Encerra a sessão do usuário e devolve para a tela de login.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta) throws IOException {
		SessaoUsuario.encerrar(requisicao);
		resposta.sendRedirect(requisicao.getContextPath() + "/login");
	}
}
