package controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.ContatoInteresse;
import model.Imovel;
import model.ImovelServico;
import model.InteracaoServico;
import model.RegraNegocioException;
import model.Usuario;
import model.UsuarioServico;
import util.SessaoUsuario;
import util.TokenCsrf;

/**
 * Página de perfil do usuário autenticado: foto, apelido, telefone, e a
 * seção que muda conforme o tipo de conta — painel de imóveis publicados
 * para quem é vendedor, ou "Meus interesses" para quem é comprador.
 */
@WebServlet("/perfil")
@MultipartConfig(maxFileSize = 3 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024 + 1024)
public class PerfilServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_PERFIL = "/WEB-INF/jsp/perfil.jsp";
	private static final String PASTA_FOTOS = "/imagens/perfil";

	private static final Map<String, String> EXTENSOES_ACEITAS = Map.of(
			"image/jpeg", ".jpg",
			"image/png", ".png",
			"image/webp", ".webp");

	private final UsuarioServico usuarioServico = new UsuarioServico();
	private final ImovelServico imovelServico = new ImovelServico();
	private final InteracaoServico interacaoServico = new InteracaoServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		carregarResumoConta(requisicao);
		carregarSecaoDoPerfil(requisicao);
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA_PERFIL).forward(requisicao, resposta);
	}

	/**
	 * A nota é formatada aqui para que o JSP cuide apenas da apresentação,
	 * sem precisar de biblioteca de tags para arredondar o número.
	 */
	private void carregarResumoConta(HttpServletRequest requisicao) {
		Usuario usuario = SessaoUsuario.obter(requisicao);
		try {
			double reputacao = interacaoServico.calcularReputacao(usuario.getId());
			requisicao.setAttribute("reputacao", String.format("%.1f", reputacao));
			requisicao.setAttribute("totalAvaliacoes", interacaoServico.contarAvaliacoes(usuario.getId()));
			requisicao.setAttribute("interessesPendentes", interacaoServico.contarInteressesPendentes(usuario.getId()));
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar o resumo da conta.", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		if (!TokenCsrf.valido(requisicao)) {
			requisicao.setAttribute("erro", "Sua sessão expirou. Tente novamente.");
			carregarSecaoDoPerfil(requisicao);
			requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
			requisicao.getRequestDispatcher(PAGINA_PERFIL).forward(requisicao, resposta);
			return;
		}

		String acao = requisicao.getParameter("acao");
		if ("foto".equals(acao)) {
			atualizarFoto(requisicao, resposta);
		} else {
			atualizarDadosBasicos(requisicao, resposta);
		}
	}

	private void atualizarDadosBasicos(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);
		try {
			usuarioServico.atualizarApelidoETelefone(usuario.getId(),
					requisicao.getParameter("apelido"), requisicao.getParameter("telefone"));

			// A sessão guarda uma cópia do usuário — sem recarregar, a página
			// continuaria mostrando o apelido antigo até um novo login.
			usuario.setApelido(requisicao.getParameter("apelido"));
			usuario.setTelefone(requisicao.getParameter("telefone"));

			resposta.sendRedirect(requisicao.getContextPath() + "/perfil");
		} catch (RegraNegocioException e) {
			requisicao.setAttribute("erro", e.getMessage());
			carregarSecaoDoPerfil(requisicao);
			requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
			requisicao.getRequestDispatcher(PAGINA_PERFIL).forward(requisicao, resposta);
		} catch (DAOException e) {
			getServletContext().log("Falha ao atualizar o perfil.", e);
			requisicao.setAttribute("erro", "Não foi possível salvar agora. Tente novamente em instantes.");
			carregarSecaoDoPerfil(requisicao);
			requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
			requisicao.getRequestDispatcher(PAGINA_PERFIL).forward(requisicao, resposta);
		}
	}

	private void atualizarFoto(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);
		String erro = null;

		try {
			Part parte = requisicao.getPart("foto");
			String extensao = parte == null ? null : EXTENSOES_ACEITAS.get(parte.getContentType());

			if (parte == null || parte.getSize() == 0) {
				erro = "Escolha uma imagem para enviar.";
			} else if (extensao == null) {
				erro = "Envie uma imagem JPG, PNG ou WEBP.";
			} else {
				String caminhoFoto = salvarArquivo(parte, extensao, usuario.getId());
				usuarioServico.atualizarFotoPerfil(usuario.getId(), caminhoFoto);
				usuario.setFotoPerfil(caminhoFoto);
			}
		} catch (IllegalStateException e) {
			// Lançado pelo container quando o arquivo excede maxFileSize.
			erro = "A imagem é muito grande. O limite é 3 MB.";
		} catch (DAOException e) {
			getServletContext().log("Falha ao salvar a foto de perfil.", e);
			erro = "Não foi possível salvar a foto agora. Tente novamente em instantes.";
		}

		if (erro != null) {
			requisicao.setAttribute("erro", erro);
			carregarSecaoDoPerfil(requisicao);
			requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
			requisicao.getRequestDispatcher(PAGINA_PERFIL).forward(requisicao, resposta);
			return;
		}

		resposta.sendRedirect(requisicao.getContextPath() + "/perfil");
	}

	/**
	 * Grava o arquivo enviado dentro de /imagens/perfil no próprio deploy.
	 * O nome é gerado no servidor (nunca o nome original do arquivo), o que
	 * evita path traversal e colisão entre usuários.
	 */
	private String salvarArquivo(Part parte, String extensao, int idUsuario) throws IOException {
		String pastaReal = getServletContext().getRealPath(PASTA_FOTOS);
		Path diretorio = Path.of(pastaReal);
		Files.createDirectories(diretorio);

		String nomeArquivo = "usuario-" + idUsuario + "-" + UUID.randomUUID() + extensao;
		Path destino = diretorio.resolve(nomeArquivo);

		try (InputStream entrada = parte.getInputStream()) {
			Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
		}

		return PASTA_FOTOS + "/" + nomeArquivo;
	}

	private void carregarSecaoDoPerfil(HttpServletRequest requisicao) {
		Usuario usuario = SessaoUsuario.obter(requisicao);
		try {
			if (usuario.podeAnunciar()) {
				List<Imovel> imoveis = imovelServico.listarDoUsuario(usuario.getId());
				requisicao.setAttribute("imoveis", imoveis);
			} else {
				List<ContatoInteresse> interesses = interacaoServico.listarInteressesEnviados(usuario.getId());
				requisicao.setAttribute("interesses", interesses);
			}
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar a seção do perfil.", e);
			requisicao.setAttribute("erroSecao", "Não foi possível carregar essa seção agora.");
		}
	}
}
