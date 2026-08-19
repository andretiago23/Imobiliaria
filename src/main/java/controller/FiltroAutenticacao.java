package controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.DAOException;
import dao.UsuarioDAO;
import model.Usuario;
import util.SessaoUsuario;

/**
 * Bloqueia o acesso às páginas restritas de quem não está autenticado.
 *
 * Concentrar essa verificação aqui evita repetir a checagem de sessão no
 * início de cada Servlet, e garante que nenhuma tela nova fique desprotegida
 * por esquecimento.
 *
 * O filtro atua apenas na requisição original, não nos encaminhamentos
 * internos, então as páginas dentro de WEB-INF continuam acessíveis via
 * forward feito pelos Servlets.
 */
@WebFilter("/*")
public class FiltroAutenticacao implements Filter {

	/**
	 * Endereços que podem ser acessados sem login. O catálogo (/inicio) é
	 * público: visitantes navegam e buscam livremente, e só esbarram no login
	 * ao tentar abrir o detalhamento completo de um imóvel (/imovel).
	 */
	private static final Set<String> CAMINHOS_LIVRES = Set.of("/", "/login", "/cadastro", "/logout", "/index.jsp",
			"/inicio", "/auth/google", "/auth/google/callback", "/financiamento", "/confirmar-status");

	/** Pastas de conteúdo estático, liberadas para que o visual carregue na tela de login. */
	private static final Set<String> PASTAS_LIVRES = Set.of("/css/", "/js/", "/imagens/", "/legal/");

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	@Override
	public void doFilter(ServletRequest requisicao, ServletResponse resposta, FilterChain cadeia)
			throws IOException, ServletException {

		HttpServletRequest requisicaoHttp = (HttpServletRequest) requisicao;
		HttpServletResponse respostaHttp = (HttpServletResponse) resposta;

		if (ehCaminhoLivre(requisicaoHttp)) {
			cadeia.doFilter(requisicao, resposta);
			return;
		}

		Usuario usuario = SessaoUsuario.obter(requisicaoHttp);
		if (usuario != null && contaAindaExiste(usuario)) {
			cadeia.doFilter(requisicao, resposta);
			return;
		}

		if (usuario != null) {
			// A sessão tinha um login válido, mas a conta não existe mais no
			// banco (excluída pelo próprio usuário em outra aba, ou removida
			// manualmente) — sem isso, cada Servlet protegido quebraria com
			// erro genérico na primeira consulta que dependesse do id.
			SessaoUsuario.encerrar(requisicaoHttp);
			requisicaoHttp.getSession().setAttribute("erroLogin", "Sua sessão expirou. Entre novamente para continuar.");
		}

		respostaHttp.sendRedirect(requisicaoHttp.getContextPath() + "/login?redirecionar=" + destinoOriginal(requisicaoHttp));
	}

	/**
	 * Reconfirma no banco que a conta da sessão ainda existe. Uma consulta a
	 * mais por requisição autenticada é aceitável para o volume desta
	 * aplicação, e evita todo um tipo de erro genérico (chave estrangeira,
	 * NullPointerException) espalhado pelos Servlets que assumem que o
	 * usuário da sessão é sempre válido.
	 */
	private boolean contaAindaExiste(Usuario usuario) {
		try {
			return usuarioDAO.buscarPorId(usuario.getId()).isPresent();
		} catch (DAOException e) {
			// Falha ao consultar o banco: não derruba a sessão por causa de
			// uma instabilidade passageira, deixa passar normalmente.
			return true;
		}
	}

	/**
	 * Codifica o caminho + query string que o visitante tentou acessar, para
	 * que o login o devolva exatamente ali depois de autenticar (ex.: o imóvel
	 * que ele tentou detalhar).
	 */
	private String destinoOriginal(HttpServletRequest requisicao) {
		String caminho = requisicao.getRequestURI().substring(requisicao.getContextPath().length());
		String query = requisicao.getQueryString();
		String destino = query == null ? caminho : caminho + "?" + query;
		return java.net.URLEncoder.encode(destino, StandardCharsets.UTF_8);
	}

	private boolean ehCaminhoLivre(HttpServletRequest requisicao) {
		String caminho = requisicao.getRequestURI().substring(requisicao.getContextPath().length());

		if (CAMINHOS_LIVRES.contains(caminho)) {
			return true;
		}
		return PASTAS_LIVRES.stream().anyMatch(caminho::startsWith);
	}
}
