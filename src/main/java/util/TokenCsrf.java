package util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Token contra CSRF para os formulários que alteram estado (login, cadastro,
 * publicação de imóvel).
 *
 * Um token é gerado por sessão e reaproveitado enquanto ela durar. O POST só
 * é aceito se o valor enviado no campo oculto "csrf" bater com o guardado na
 * sessão do próprio navegador — um site malicioso não tem como descobrir esse
 * valor para forjar a requisição.
 */
public final class TokenCsrf {

	private static final String ATRIBUTO_SESSAO = "tokenCsrf";
	private static final int TAMANHO_BYTES = 32;

	private static final SecureRandom GERADOR_ALEATORIO = new SecureRandom();

	private TokenCsrf() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * @return o token da sessão atual, gerando um novo se ainda não existir
	 */
	public static String obter(HttpServletRequest requisicao) {
		HttpSession sessao = requisicao.getSession();
		String token = (String) sessao.getAttribute(ATRIBUTO_SESSAO);
		if (token == null) {
			byte[] bytes = new byte[TAMANHO_BYTES];
			GERADOR_ALEATORIO.nextBytes(bytes);
			token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
			sessao.setAttribute(ATRIBUTO_SESSAO, token);
		}
		return token;
	}

	/**
	 * Confere o campo "csrf" enviado no POST contra o token da sessão.
	 *
	 * @return true se o formulário realmente veio de uma página deste site
	 */
	public static boolean valido(HttpServletRequest requisicao) {
		HttpSession sessao = requisicao.getSession(false);
		if (sessao == null) {
			return false;
		}
		String esperado = (String) sessao.getAttribute(ATRIBUTO_SESSAO);
		String recebido = requisicao.getParameter("csrf");
		if (esperado == null || recebido == null) {
			return false;
		}
		// Comparação em tempo constante, mesmo padrão usado na conferência de senha.
		return MessageDigest.isEqual(esperado.getBytes(), recebido.getBytes());
	}
}
