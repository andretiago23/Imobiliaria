package controller;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	 * Endereços que podem ser acessados sem login: o catálogo público (cards,
	 * busca e filtros) e o cadastro de imobiliária de exemplo, que precisa
	 * estar disponível antes mesmo de o vendedor ter conta. O detalhamento
	 * completo do imóvel (/imovel) fica de fora de propósito — continua
	 * exigindo login, preservando a URL de destino.
	 */
	private static final Set<String> CAMINHOS_LIVRES = Set.of(
			"/", "/login", "/cadastro", "/logout", "/index.jsp", "/imoveis", "/buscar", "/imobiliarias/nova");

	/** Pastas de conteúdo estático, liberadas para que o visual carregue na tela de login. */
	private static final Set<String> PASTAS_LIVRES = Set.of("/css/", "/js/", "/imagens/");

	@Override
	public void doFilter(ServletRequest requisicao, ServletResponse resposta, FilterChain cadeia)
			throws IOException, ServletException {

		HttpServletRequest requisicaoHttp = (HttpServletRequest) requisicao;
		HttpServletResponse respostaHttp = (HttpServletResponse) resposta;

		if (SessaoUsuario.estaAutenticado(requisicaoHttp) || ehCaminhoLivre(requisicaoHttp)) {
			cadeia.doFilter(requisicao, resposta);
			return;
		}

		// Guarda para onde o visitante queria ir, para o LoginServlet devolvê-lo
		// exatamente ali depois de autenticado.
		SessaoUsuario.guardarDestino(requisicaoHttp);
		respostaHttp.sendRedirect(requisicaoHttp.getContextPath() + "/login");
	}

	private boolean ehCaminhoLivre(HttpServletRequest requisicao) {
		String caminho = requisicao.getRequestURI().substring(requisicao.getContextPath().length());

		if (CAMINHOS_LIVRES.contains(caminho)) {
			return true;
		}
		return PASTAS_LIVRES.stream().anyMatch(caminho::startsWith);
	}
}
