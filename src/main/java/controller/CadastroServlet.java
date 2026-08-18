package controller;

import java.io.IOException;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.RegraNegocioException;
import model.TipoUsuario;
import model.Usuario;
import model.UsuarioServico;
import util.ConversorEnum;
import util.Html;
import util.SessaoUsuario;
import util.TokenCsrf;

/**
 * Cadastro de novos usuários.
 *
 * GET exibe o formulário, POST cria a conta e já autentica o usuário.
 *
 * Também conclui o cadastro de quem chega pelo login com Google: nesse caso o
 * nome e o e-mail já vêm confirmados pelo Google (guardados na sessão pelo
 * GoogleOAuthServlet) e o formulário pede só o restante — CPF, telefone e
 * tipo de conta —, sem exigir senha.
 */
@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_CADASTRO = "/WEB-INF/jsp/cadastro.jsp";

	private final UsuarioServico usuarioServico = new UsuarioServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		if (SessaoUsuario.estaAutenticado(requisicao)) {
			resposta.sendRedirect(requisicao.getContextPath() + "/inicio");
			return;
		}

		String nomeGoogle = sessaoAtributo(requisicao, GoogleOAuthServlet.SESSAO_PENDENTE_NOME);
		String emailGoogle = sessaoAtributo(requisicao, GoogleOAuthServlet.SESSAO_PENDENTE_EMAIL);
		if (emailGoogle != null) {
			requisicao.setAttribute("modoGoogle", true);
			requisicao.setAttribute("nome", Html.escapar(nomeGoogle));
			requisicao.setAttribute("email", Html.escapar(emailGoogle));
		}

		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA_CADASTRO).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		if (!TokenCsrf.valido(requisicao)) {
			reexibirFormulario(requisicao, resposta, "Sua sessão expirou. Preencha o formulário novamente.");
			return;
		}

		String emailGoogle = sessaoAtributo(requisicao, GoogleOAuthServlet.SESSAO_PENDENTE_EMAIL);
		if (emailGoogle != null) {
			concluirCadastroGoogle(requisicao, resposta, emailGoogle);
			return;
		}

		String senha = requisicao.getParameter("senha");
		String confirmacaoSenha = requisicao.getParameter("confirmacaoSenha");

		if (senha == null || !senha.equals(confirmacaoSenha)) {
			reexibirFormulario(requisicao, resposta, "As senhas informadas não coincidem.");
			return;
		}

		try {
			Usuario usuario = montarUsuario(requisicao);
			boolean aceitouTermos = "on".equals(requisicao.getParameter("aceiteTermos"));
			String codigoImobiliaria = requisicao.getParameter("codigoImobiliaria");
			usuarioServico.cadastrar(usuario, senha, aceitouTermos, codigoImobiliaria);

			// Entra direto no sistema após o cadastro, evitando pedir os mesmos
			// dados duas vezes seguidas.
			SessaoUsuario.registrar(requisicao, usuario);
			resposta.sendRedirect(requisicao.getContextPath() + "/inicio");

		} catch (RegraNegocioException e) {
			reexibirFormulario(requisicao, resposta, e.getMessage());

		} catch (IllegalArgumentException e) {
			reexibirFormulario(requisicao, resposta, "Selecione um tipo de conta válido.");

		} catch (DAOException e) {
			getServletContext().log("Falha ao cadastrar o usuário.", e);
			reexibirFormulario(requisicao, resposta,
					"Não foi possível concluir o cadastro agora. Tente novamente em instantes.");
		}
	}

	private void concluirCadastroGoogle(HttpServletRequest requisicao, HttpServletResponse resposta,
			String emailGoogle) throws ServletException, IOException {

		// Nome e e-mail vêm da sessão (preenchidos pelo Google), nunca do
		// formulário: assim ninguém consegue criar a conta em nome de outro
		// e-mail só editando o campo escondido do formulário.
		String nomeGoogle = sessaoAtributo(requisicao, GoogleOAuthServlet.SESSAO_PENDENTE_NOME);
		String redirecionar = sessaoAtributo(requisicao, GoogleOAuthServlet.SESSAO_PENDENTE_REDIRECIONAR);

		try {
			Usuario usuario = new Usuario();
			usuario.setNome(nomeGoogle);
			usuario.setEmail(emailGoogle);
			usuario.setCpf(requisicao.getParameter("cpf"));
			usuario.setTelefone(requisicao.getParameter("telefone"));
			usuario.setTipoUsuario(ConversorEnum.paraEnum(TipoUsuario.class, requisicao.getParameter("tipoUsuario")));

			boolean aceitouTermos = "on".equals(requisicao.getParameter("aceiteTermos"));
			String codigoImobiliaria = requisicao.getParameter("codigoImobiliaria");
			usuarioServico.cadastrarComLoginSocial(usuario, aceitouTermos, codigoImobiliaria);

			HttpSession sessao = requisicao.getSession();
			sessao.removeAttribute(GoogleOAuthServlet.SESSAO_PENDENTE_NOME);
			sessao.removeAttribute(GoogleOAuthServlet.SESSAO_PENDENTE_EMAIL);
			sessao.removeAttribute(GoogleOAuthServlet.SESSAO_PENDENTE_REDIRECIONAR);

			SessaoUsuario.registrar(requisicao, usuario);
			resposta.sendRedirect(requisicao.getContextPath() + (redirecionar == null ? "/inicio" : redirecionar));

		} catch (RegraNegocioException e) {
			reexibirFormulario(requisicao, resposta, e.getMessage());
		} catch (IllegalArgumentException e) {
			reexibirFormulario(requisicao, resposta, "Selecione um tipo de conta válido.");
		} catch (DAOException e) {
			getServletContext().log("Falha ao concluir o cadastro via Google.", e);
			reexibirFormulario(requisicao, resposta,
					"Não foi possível concluir o cadastro agora. Tente novamente em instantes.");
		}
	}

	private Usuario montarUsuario(HttpServletRequest requisicao) {
		Usuario usuario = new Usuario();
		usuario.setNome(requisicao.getParameter("nome"));
		usuario.setEmail(requisicao.getParameter("email"));
		usuario.setCpf(requisicao.getParameter("cpf"));
		usuario.setTelefone(requisicao.getParameter("telefone"));
		usuario.setTipoUsuario(ConversorEnum.paraEnum(TipoUsuario.class, requisicao.getParameter("tipoUsuario")));
		return usuario;
	}

	/**
	 * Reexibe o formulário com a mensagem de erro, preservando o que já havia
	 * sido digitado. As senhas não voltam preenchidas, de propósito.
	 */
	private void reexibirFormulario(HttpServletRequest requisicao, HttpServletResponse resposta, String mensagemErro)
			throws ServletException, IOException {

		String emailGoogle = sessaoAtributo(requisicao, GoogleOAuthServlet.SESSAO_PENDENTE_EMAIL);
		if (emailGoogle != null) {
			requisicao.setAttribute("modoGoogle", true);
			requisicao.setAttribute("nome", Html.escapar(sessaoAtributo(requisicao, GoogleOAuthServlet.SESSAO_PENDENTE_NOME)));
			requisicao.setAttribute("email", Html.escapar(emailGoogle));
		} else {
			requisicao.setAttribute("nome", Html.escapar(requisicao.getParameter("nome")));
			requisicao.setAttribute("email", Html.escapar(requisicao.getParameter("email")));
		}

		requisicao.setAttribute("erro", mensagemErro);
		requisicao.setAttribute("cpf", Html.escapar(requisicao.getParameter("cpf")));
		requisicao.setAttribute("telefone", Html.escapar(requisicao.getParameter("telefone")));
		requisicao.setAttribute("tipoUsuario", Html.escapar(requisicao.getParameter("tipoUsuario")));
		requisicao.setAttribute("codigoImobiliaria", Html.escapar(requisicao.getParameter("codigoImobiliaria")));
		requisicao.setAttribute("aceiteTermos", "on".equals(requisicao.getParameter("aceiteTermos")));
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA_CADASTRO).forward(requisicao, resposta);
	}

	private String sessaoAtributo(HttpServletRequest requisicao, String nome) {
		HttpSession sessao = requisicao.getSession(false);
		return sessao == null ? null : (String) sessao.getAttribute(nome);
	}
}
