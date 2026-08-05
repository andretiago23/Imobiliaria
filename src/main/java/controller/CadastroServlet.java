package controller;

import java.io.IOException;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.RegraNegocioException;
import model.TipoUsuario;
import model.Usuario;
import model.UsuarioServico;
import util.ConversorEnum;
import util.Html;
import util.SessaoUsuario;

/**
 * Cadastro de novos usuários.
 *
 * GET exibe o formulário, POST cria a conta e já autentica o usuário.
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
		requisicao.getRequestDispatcher(PAGINA_CADASTRO).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		String senha = requisicao.getParameter("senha");
		String confirmacaoSenha = requisicao.getParameter("confirmacaoSenha");

		if (senha == null || !senha.equals(confirmacaoSenha)) {
			reexibirFormulario(requisicao, resposta, "As senhas informadas não coincidem.");
			return;
		}

		try {
			Usuario usuario = montarUsuario(requisicao);
			usuarioServico.cadastrar(usuario, senha);

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

		requisicao.setAttribute("erro", mensagemErro);
		requisicao.setAttribute("nome", Html.escapar(requisicao.getParameter("nome")));
		requisicao.setAttribute("email", Html.escapar(requisicao.getParameter("email")));
		requisicao.setAttribute("cpf", Html.escapar(requisicao.getParameter("cpf")));
		requisicao.setAttribute("telefone", Html.escapar(requisicao.getParameter("telefone")));
		requisicao.setAttribute("tipoUsuario", Html.escapar(requisicao.getParameter("tipoUsuario")));
		requisicao.getRequestDispatcher(PAGINA_CADASTRO).forward(requisicao, resposta);
	}
}
