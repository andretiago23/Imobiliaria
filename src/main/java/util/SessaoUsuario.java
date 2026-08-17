package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.Usuario;

/**
 * Centraliza o acesso ao usuário autenticado guardado na sessão.
 *
 * Evita que o nome do atributo fique repetido como texto solto em cada
 * Servlet e em cada JSP.
 */
public final class SessaoUsuario {

	/** Nome do atributo de sessão, também usado nas páginas JSP. */
	public static final String ATRIBUTO_USUARIO = "usuarioLogado";

	/**
	 * Guarda a URL que o visitante tentou acessar antes de ser mandado para o
	 * login, para o LoginServlet devolvê-lo exatamente aonde ele queria ir.
	 */
	public static final String ATRIBUTO_DESTINO = "destinoAposLogin";

	private SessaoUsuario() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * Guarda a URL de destino antes de redirecionar para o login.
	 *
	 * Guardada sem o contexto da aplicação, para o chamador poder devolvê-la
	 * concatenando contextPath + destino, do mesmo jeito que os outros redirects.
	 */
	public static void guardarDestino(HttpServletRequest requisicao) {
		String destino = requisicao.getRequestURI().substring(requisicao.getContextPath().length());
		String consulta = requisicao.getQueryString();
		if (consulta != null) {
			destino += "?" + consulta;
		}
		requisicao.getSession().setAttribute(ATRIBUTO_DESTINO, destino);
	}

	/**
	 * Recupera e remove a URL de destino guardada, ou null se não houver nenhuma.
	 */
	public static String retirarDestino(HttpServletRequest requisicao) {
		HttpSession sessao = requisicao.getSession(false);
		if (sessao == null) {
			return null;
		}
		String destino = (String) sessao.getAttribute(ATRIBUTO_DESTINO);
		sessao.removeAttribute(ATRIBUTO_DESTINO);
		return destino;
	}

	/**
	 * @return o usuário autenticado, ou null se não houver sessão ativa
	 */
	public static Usuario obter(HttpServletRequest requisicao) {
		HttpSession sessao = requisicao.getSession(false);
		return sessao == null ? null : (Usuario) sessao.getAttribute(ATRIBUTO_USUARIO);
	}

	public static boolean estaAutenticado(HttpServletRequest requisicao) {
		return obter(requisicao) != null;
	}

	/**
	 * Registra o usuário na sessão após o login.
	 *
	 * O identificador da sessão é trocado de propósito: impede o ataque de
	 * fixação de sessão, em que o invasor força a vítima a usar um id que ele
	 * já conhece.
	 */
	public static void registrar(HttpServletRequest requisicao, Usuario usuario) {
		requisicao.changeSessionId();
		requisicao.getSession().setAttribute(ATRIBUTO_USUARIO, usuario);
	}

	public static void encerrar(HttpServletRequest requisicao) {
		HttpSession sessao = requisicao.getSession(false);
		if (sessao != null) {
			sessao.invalidate();
		}
	}
}
