package controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Usuario;
import model.UsuarioServico;
import util.ConfiguracaoGoogle;
import util.JsonPlano;
import util.SessaoUsuario;

/**
 * Login social com Google (OAuth 2.0, fluxo "Authorization Code").
 *
 * /auth/google          — inicia o fluxo, redireciona para a tela de consentimento do Google.
 * /auth/google/callback — recebe o código de volta, troca por um token e confere a identidade.
 *
 * Sem biblioteca de OAuth: as duas chamadas HTTP (troca do código e consulta
 * do perfil) usam o cliente HTTP do próprio JDK.
 */
@WebServlet({ "/auth/google", "/auth/google/callback" })
public class GoogleOAuthServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String URL_AUTORIZACAO = "https://accounts.google.com/o/oauth2/v2/auth";
	private static final String URL_TOKEN = "https://oauth2.googleapis.com/token";
	private static final String URL_USERINFO = "https://www.googleapis.com/oauth2/v3/userinfo";
	private static final String ESCOPO = "openid email profile";

	private static final String SESSAO_ESTADO = "google.oauth.state";
	private static final String SESSAO_REDIRECIONAR = "google.oauth.redirecionar";

	/** Atributos temporários com o perfil do Google, até o cadastro ser concluído com o CPF. */
	public static final String SESSAO_PENDENTE_NOME = "google.pendente.nome";
	public static final String SESSAO_PENDENTE_EMAIL = "google.pendente.email";
	public static final String SESSAO_PENDENTE_REDIRECIONAR = SESSAO_REDIRECIONAR;

	private static final SecureRandom GERADOR_ALEATORIO = new SecureRandom();

	private final UsuarioServico usuarioServico = new UsuarioServico();
	private final HttpClient clienteHttp = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		if (requisicao.getServletPath().endsWith("/callback")) {
			processarCallback(requisicao, resposta);
		} else {
			iniciarFluxo(requisicao, resposta);
		}
	}

	private void iniciarFluxo(HttpServletRequest requisicao, HttpServletResponse resposta) throws IOException {
		if (!ConfiguracaoGoogle.configurado()) {
			resposta.sendRedirect(requisicao.getContextPath()
					+ "/login?erroGoogle=" + java.net.URLEncoder.encode(
							"Login com Google ainda não está configurado neste ambiente.", StandardCharsets.UTF_8));
			return;
		}

		HttpSession sessao = requisicao.getSession();
		String estado = tokenAleatorio();
		sessao.setAttribute(SESSAO_ESTADO, estado);
		sessao.setAttribute(SESSAO_REDIRECIONAR, caminhoInternoOuPadrao(requisicao.getParameter("redirecionar")));

		String url = URL_AUTORIZACAO
				+ "?client_id=" + codificar(ConfiguracaoGoogle.clientId())
				+ "&redirect_uri=" + codificar(ConfiguracaoGoogle.redirectUri())
				+ "&response_type=code"
				+ "&scope=" + codificar(ESCOPO)
				+ "&state=" + codificar(estado)
				+ "&prompt=select_account";

		resposta.sendRedirect(url);
	}

	private void processarCallback(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws IOException, ServletException {

		HttpSession sessao = requisicao.getSession(false);
		String estadoEsperado = sessao == null ? null : (String) sessao.getAttribute(SESSAO_ESTADO);
		String redirecionar = sessao == null ? "/inicio" : (String) sessao.getAttribute(SESSAO_REDIRECIONAR);
		if (redirecionar == null) {
			redirecionar = "/inicio";
		}

		String erroGoogle = requisicao.getParameter("error");
		String estadoRecebido = requisicao.getParameter("state");
		String codigo = requisicao.getParameter("code");

		if (erroGoogle != null) {
			// A pessoa cancelou o consentimento — não é uma falha do sistema.
			redirecionarComErro(requisicao, resposta, "Login com Google cancelado.");
			return;
		}
		if (estadoEsperado == null || !estadoEsperado.equals(estadoRecebido) || codigo == null) {
			redirecionarComErro(requisicao, resposta, "Não foi possível confirmar a origem do login com Google.");
			return;
		}
		if (sessao != null) {
			sessao.removeAttribute(SESSAO_ESTADO);
		}

		try {
			Map<String, String> token = trocarCodigoPorToken(codigo);
			String acessoToken = token.get("access_token");
			if (acessoToken == null) {
				redirecionarComErro(requisicao, resposta, "O Google não retornou um token de acesso válido.");
				return;
			}

			Map<String, String> perfil = buscarPerfil(acessoToken);
			String email = perfil.get("email");
			boolean emailVerificado = "true".equalsIgnoreCase(perfil.get("email_verified"));
			String nome = perfil.getOrDefault("name", email);

			if (email == null || email.isBlank() || !emailVerificado) {
				redirecionarComErro(requisicao, resposta,
						"Sua conta Google precisa ter um e-mail verificado para entrar por aqui.");
				return;
			}

			var usuarioExistente = usuarioServico.buscarPorEmail(email);
			if (usuarioExistente.isPresent()) {
				SessaoUsuario.registrar(requisicao, usuarioExistente.get());
				resposta.sendRedirect(requisicao.getContextPath() + redirecionar);
				return;
			}

			// Primeiro acesso por login social: falta o CPF, que o Google não
			// fornece. Guarda o perfil na sessão e manda concluir no cadastro.
			HttpSession novaSessao = requisicao.getSession();
			novaSessao.setAttribute(SESSAO_PENDENTE_NOME, nome);
			novaSessao.setAttribute(SESSAO_PENDENTE_EMAIL, email);
			novaSessao.setAttribute(SESSAO_REDIRECIONAR, redirecionar);
			resposta.sendRedirect(requisicao.getContextPath() + "/cadastro?google=1");

		} catch (DAOException e) {
			getServletContext().log("Falha ao consultar o usuário durante o login com Google.", e);
			redirecionarComErro(requisicao, resposta, "Não foi possível completar o login agora. Tente novamente.");
		} catch (IOException | InterruptedException e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			getServletContext().log("Falha ao comunicar com o Google durante o login.", e);
			redirecionarComErro(requisicao, resposta, "Não foi possível falar com o Google agora. Tente novamente.");
		} catch (IllegalStateException e) {
			getServletContext().log("Login com Google chamado sem configuração.", e);
			redirecionarComErro(requisicao, resposta, "Login com Google ainda não está configurado neste ambiente.");
		}
	}

	private Map<String, String> trocarCodigoPorToken(String codigo) throws IOException, InterruptedException {
		String corpo = "code=" + codificar(codigo)
				+ "&client_id=" + codificar(ConfiguracaoGoogle.clientId())
				+ "&client_secret=" + codificar(ConfiguracaoGoogle.clientSecret())
				+ "&redirect_uri=" + codificar(ConfiguracaoGoogle.redirectUri())
				+ "&grant_type=authorization_code";

		HttpRequest requisicaoHttp = HttpRequest.newBuilder(URI.create(URL_TOKEN))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.timeout(Duration.ofSeconds(10))
				.POST(BodyPublishers.ofString(corpo))
				.build();

		HttpResponse<String> resposta = clienteHttp.send(requisicaoHttp, BodyHandlers.ofString());
		return JsonPlano.lerObjeto(resposta.body());
	}

	private Map<String, String> buscarPerfil(String acessoToken) throws IOException, InterruptedException {
		HttpRequest requisicaoHttp = HttpRequest.newBuilder(URI.create(URL_USERINFO))
				.header("Authorization", "Bearer " + acessoToken)
				.timeout(Duration.ofSeconds(10))
				.GET()
				.build();

		HttpResponse<String> resposta = clienteHttp.send(requisicaoHttp, BodyHandlers.ofString());
		return JsonPlano.lerObjeto(resposta.body());
	}

	private void redirecionarComErro(HttpServletRequest requisicao, HttpServletResponse resposta, String mensagem)
			throws IOException {
		resposta.sendRedirect(requisicao.getContextPath() + "/login?erroGoogle=" + codificar(mensagem));
	}

	/**
	 * Mesma regra do LoginServlet: só aceita caminho interno, nunca uma URL
	 * completa (evita open redirect através do parâmetro "redirecionar").
	 */
	private String caminhoInternoOuPadrao(String valor) {
		if (valor == null || valor.isBlank()) {
			return "/inicio";
		}
		String destino = java.net.URLDecoder.decode(valor, StandardCharsets.UTF_8);
		boolean caminhoInterno = destino.startsWith("/") && !destino.startsWith("//");
		return caminhoInterno ? destino : "/inicio";
	}

	private String tokenAleatorio() {
		byte[] bytes = new byte[24];
		GERADOR_ALEATORIO.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private String codificar(String valor) {
		return java.net.URLEncoder.encode(valor, StandardCharsets.UTF_8);
	}
}
