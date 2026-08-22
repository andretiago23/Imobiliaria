package controller;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import dao.DAOException;
import dao.RedefinicaoSenhaDAO;
import dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Usuario;
import util.EmailService;
import util.TokenCsrf;

/**
 * "Esqueci minha senha": pede o e-mail e, se existir uma conta com ele,
 * manda um link de redefinição de uso único (ver RedefinirSenhaServlet).
 *
 * Sempre mostra a mesma mensagem de confirmação, exista ou não uma conta com
 * aquele e-mail — contar pra quem não tem conta que "esse e-mail não está
 * cadastrado" permitiria descobrir quais e-mails têm conta no site.
 */
@WebServlet("/esqueci-senha")
public class EsqueciSenhaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/esqueci-senha.jsp";

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final RedefinicaoSenhaDAO redefinicaoSenhaDAO = new RedefinicaoSenhaDAO();
	private final EmailService emailService = new EmailService();
	private final SecureRandom geradorAleatorio = new SecureRandom();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		String email = requisicao.getParameter("email");

		if (TokenCsrf.valido(requisicao) && email != null && !email.isBlank()) {
			try {
				Optional<Usuario> usuario = usuarioDAO.buscarPorEmail(email.trim());
				if (usuario.isPresent()) {
					enviarLinkDeRedefinicao(requisicao, usuario.get());
				}
			} catch (DAOException e) {
				getServletContext().log("Falha ao processar \"esqueci minha senha\" para " + email + ".", e);
				// Some mesmo assim pra tela de confirmação — não é seguro
				// (nem necessário) diferenciar esse erro de um e-mail que
				// simplesmente não existe.
			}
		}

		requisicao.setAttribute("enviado", true);
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	private void enviarLinkDeRedefinicao(HttpServletRequest requisicao, Usuario usuario) throws DAOException {
		byte[] bytes = new byte[32];
		geradorAleatorio.nextBytes(bytes);
		String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

		redefinicaoSenhaDAO.criar(usuario.getId(), token);

		String linkRedefinicao = linkAbsoluto(requisicao, "/redefinir-senha?token=" + token);
		emailService.notificarRedefinicaoSenha(usuario, linkRedefinicao);
	}

	private String linkAbsoluto(HttpServletRequest requisicao, String caminho) {
		return requisicao.getRequestURL().toString().replace(requisicao.getRequestURI(), "")
				+ requisicao.getContextPath() + caminho;
	}
}
