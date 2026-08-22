package controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import dao.AnuncioDAO;
import dao.DAOException;
import dao.DisponibilidadeVisitaDAO;
import dao.FotoImovelDAO;
import dao.PlanoDAO;
import dao.UsuarioDAO;
import dao.VideoImovelDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.Anuncio;
import model.CategoriaPlano;
import model.DiaSemana;
import model.DisponibilidadeVisita;
import model.Finalidade;
import model.FotoImovel;
import model.Imovel;
import model.ImovelServico;
import model.Plano;
import model.RascunhoAnuncio;
import model.RegraNegocioException;
import model.TipoAnunciante;
import model.TipoImovel;
import model.Usuario;
import model.VideoImovel;
import util.ConversorEnum;
import util.SessaoUsuario;
import util.TokenCsrf;
import util.ValidadorCNPJ;
import util.ValidadorCPF;

/**
 * Assistente de anúncio em 5 etapas (Tela 0 fica em AnuncioServlet):
 * 1. Negócio (alugar/vender) + endereço do imóvel
 * 2. Plano
 * 3. Dados do anunciante
 * 4. Disponibilidade para visitas
 * 5. Confirmação/resumo → pagamento
 *
 * Nada é gravado no banco até o fim da etapa 5: os dados ficam só na sessão
 * (model.RascunhoAnuncio), evitando imóveis "fantasmas" para quem desiste no
 * meio do caminho. Cada doPost valida a etapa, salva na sessão e redireciona
 * (nunca forward) para a próxima — assim um F5 não reenvia o formulário.
 *
 * GET de uma etapa cuja etapa anterior ainda não foi concluída redireciona
 * de volta para a primeira etapa pendente, em vez de mostrar um formulário
 * pela metade.
 */
@WebServlet(urlPatterns = { "/anunciar/etapa1", "/anunciar/etapa2", "/anunciar/etapa3", "/anunciar/etapa4",
		"/anunciar/etapa5" })
@MultipartConfig(
		maxFileSize = 1024L * 1024 * 100,        // 100MB por arquivo (o vídeo é o maior)
		maxRequestSize = 1024L * 1024 * 350,      // limite da etapa 1 inteira (fotos + vídeo)
		fileSizeThreshold = 1024 * 1024)          // acima de 1MB grava direto em disco, não em memória
public class AnuncioWizardServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PASTA_UPLOADS = "/imagens/uploads/anuncios";
	private static final Set<String> EXTENSOES_FOTO = Set.of("jpg", "jpeg", "png", "webp");
	private static final Set<String> EXTENSOES_VIDEO = Set.of("mp4", "webm", "mov");

	private final PlanoDAO planoDAO = new PlanoDAO();
	private final AnuncioDAO anuncioDAO = new AnuncioDAO();
	private final DisponibilidadeVisitaDAO disponibilidadeVisitaDAO = new DisponibilidadeVisitaDAO();
	private final ImovelServico imovelServico = new ImovelServico();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final FotoImovelDAO fotoImovelDAO = new FotoImovelDAO();
	private final VideoImovelDAO videoImovelDAO = new VideoImovelDAO();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		int etapa = etapaDaUrl(requisicao);
		RascunhoAnuncio rascunho = obterRascunho(requisicao);

		if (etapa == 1) {
			removerMidiaSeSolicitado(requisicao, rascunho);
		}

		if (etapa == 1 && rascunho.getTipoAnunciante() == null) {
			TipoAnunciante tipoDaLanding = ConversorEnum.paraEnum(TipoAnunciante.class, requisicao.getParameter("tipo"));
			rascunho.setTipoAnunciante(tipoDaLanding != null ? tipoDaLanding : TipoAnunciante.PROPRIETARIO);
		}

		Integer etapaPendente = primeiraEtapaPendente(rascunho, etapa);
		if (etapaPendente != null) {
			resposta.sendRedirect(requisicao.getContextPath() + "/anunciar/etapa" + etapaPendente);
			return;
		}

		if (etapa == 3 && (rascunho.getNomeAnunciante() == null || rascunho.getNomeAnunciante().isBlank())) {
			Usuario usuario = SessaoUsuario.obter(requisicao);
			rascunho.setNomeAnunciante(usuario.getNome());
			rascunho.setCelularAnunciante(usuario.getTelefone());
			rascunho.setCpfCnpjAnunciante(usuario.getCpf());
		}

		try {
			if (etapa == 2) {
				requisicao.setAttribute("planos", planosIndividuaisDoTipo(rascunho.getTipoAnunciante()));
			}
			if (etapa == 5) {
				requisicao.setAttribute("plano", planoDAO.buscarPorId(rascunho.getIdPlano()).orElse(null));
			}
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar dados da etapa " + etapa + " do assistente de anúncio.", e);
			requisicao.setAttribute("erro", "Não foi possível carregar esta etapa agora. Tente novamente.");
		}

		requisicao.setAttribute("rascunho", rascunho);
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher("/WEB-INF/jsp/anuncio-etapa" + etapa + ".jsp").forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		int etapa = etapaDaUrl(requisicao);
		RascunhoAnuncio rascunho = obterRascunho(requisicao);

		if (!TokenCsrf.valido(requisicao)) {
			requisicao.setAttribute("erro", "Sua sessão expirou. Preencha o formulário novamente.");
			reexibir(requisicao, resposta, etapa, rascunho);
			return;
		}

		try {
			switch (etapa) {
				case 1 -> {
					processarUploads(requisicao, rascunho);
					processarEtapa1(requisicao, rascunho);
				}
				case 2 -> processarEtapa2(requisicao, rascunho);
				case 3 -> processarEtapa3(requisicao, rascunho);
				case 4 -> processarEtapa4(requisicao, rascunho);
				case 5 -> {
					processarEtapa5(requisicao, resposta, rascunho);
					return;
				}
				default -> throw new IllegalStateException("Etapa inválida.");
			}
			resposta.sendRedirect(requisicao.getContextPath() + "/anunciar/etapa" + (etapa + 1));

		} catch (RegraNegocioException e) {
			requisicao.setAttribute("erro", e.getMessage());
			reexibir(requisicao, resposta, etapa, rascunho);
		} catch (NumberFormatException e) {
			requisicao.setAttribute("erro", "Confira os valores numéricos informados.");
			reexibir(requisicao, resposta, etapa, rascunho);
		} catch (DAOException e) {
			getServletContext().log("Falha ao processar a etapa " + etapa + " do assistente de anúncio.", e);
			requisicao.setAttribute("erro", "Não foi possível continuar agora. Tente novamente em instantes.");
			reexibir(requisicao, resposta, etapa, rascunho);
		}
	}

	// -------------------------------------------------------------------
	// Etapa 1 — o que anunciar + endereço do imóvel
	// -------------------------------------------------------------------
	private void processarEtapa1(HttpServletRequest requisicao, RascunhoAnuncio rascunho) throws RegraNegocioException {
		Finalidade finalidade = ConversorEnum.paraEnum(Finalidade.class, requisicao.getParameter("finalidade"));
		if (finalidade == null) {
			throw new RegraNegocioException("Escolha se você quer alugar ou vender.");
		}
		String titulo = textoObrigatorio(requisicao, "titulo", "Dê um título para o anúncio.");
		TipoImovel tipo = ConversorEnum.paraEnum(TipoImovel.class, requisicao.getParameter("tipo"));
		if (tipo == null) {
			throw new RegraNegocioException("Selecione o tipo do imóvel.");
		}
		BigDecimal preco = parseDecimal(requisicao.getParameter("preco"));
		if (preco == null || preco.signum() <= 0) {
			throw new RegraNegocioException("Informe um preço maior que zero.");
		}
		String cep = apenasDigitos(requisicao.getParameter("cep"));
		if (cep.length() != 8) {
			throw new RegraNegocioException("Informe um CEP válido, com 8 dígitos.");
		}
		String endereco = textoObrigatorio(requisicao, "endereco", "Informe a rua do imóvel.");
		String numero = textoObrigatorio(requisicao, "numero", "Informe o número do imóvel.");
		String bairro = textoObrigatorio(requisicao, "bairro", "Informe o bairro do imóvel.");

		rascunho.setFinalidade(finalidade);
		rascunho.setTitulo(titulo);
		rascunho.setTipo(tipo);
		rascunho.setPreco(preco);
		rascunho.setAreaM2(parseDouble(requisicao.getParameter("areaM2")));
		rascunho.setQuartos(parseInteiro(requisicao.getParameter("quartos")));
		rascunho.setBanheiros(parseInteiro(requisicao.getParameter("banheiros")));
		rascunho.setDescricao(requisicao.getParameter("descricao"));
		rascunho.setCep(cep);
		rascunho.setEndereco(endereco);
		rascunho.setNumero(numero);
		rascunho.setBairro(bairro);
		rascunho.setCidade(requisicao.getParameter("cidade"));
		rascunho.setEstado(requisicao.getParameter("estado"));

		// Item 1.1: bloqueia o avanço sem o mínimo de mídia anexada.
		if (rascunho.getFotos().size() < RascunhoAnuncio.MINIMO_FOTOS) {
			throw new RegraNegocioException(
					"Anexe pelo menos " + RascunhoAnuncio.MINIMO_FOTOS + " fotos do imóvel para continuar.");
		}
		if (rascunho.getVideo() == null || rascunho.getVideo().isBlank()) {
			throw new RegraNegocioException("Anexe pelo menos 1 vídeo do imóvel para continuar.");
		}
	}

	// -------------------------------------------------------------------
	// Item 1.1 — fotos e vídeo do imóvel (mínimo 3 fotos + 1 vídeo,
	// obrigatório para avançar da etapa 1). Os arquivos são gravados em
	// disco a cada POST desta etapa (mesmo que outros campos do formulário
	// ainda falhem na validação), e a URL relativa fica guardada no
	// rascunho até a etapa 5, quando os registros de FOTO_IMOVEL/
	// VIDEO_IMOVEL são criados de fato — assim reenviar o formulário (ex.:
	// corrigindo o título) não obriga a pessoa a escolher os arquivos de
	// novo.
	// -------------------------------------------------------------------
	private void processarUploads(HttpServletRequest requisicao, RascunhoAnuncio rascunho)
			throws RegraNegocioException, IOException, ServletException {

		for (Part parte : requisicao.getParts()) {
			String nomeArquivo = nomeArquivoOriginal(parte);
			if (nomeArquivo == null || nomeArquivo.isBlank()) {
				continue; // input vazio (usuário não trocou o arquivo neste envio)
			}

			if ("fotos".equals(parte.getName())) {
				String extensao = extensao(nomeArquivo);
				if (!EXTENSOES_FOTO.contains(extensao)) {
					throw new RegraNegocioException("Formato de foto não suportado. Envie JPG, PNG ou WEBP.");
				}
				rascunho.getFotos().add(salvarArquivo(requisicao, parte, extensao));
			} else if ("video".equals(parte.getName())) {
				String extensao = extensao(nomeArquivo);
				if (!EXTENSOES_VIDEO.contains(extensao)) {
					throw new RegraNegocioException("Formato de vídeo não suportado. Envie MP4, WEBM ou MOV.");
				}
				rascunho.setVideo(salvarArquivo(requisicao, parte, extensao));
			}
		}
	}

	/**
	 * O nome de arquivo vem embutido no cabeçalho Content-Disposition da
	 * parte; quando o input file foi deixado vazio nesse envio, o
	 * navegador ainda manda a parte, só que sem nome de arquivo.
	 */
	private String nomeArquivoOriginal(Part parte) {
		String cabecalho = parte.getHeader("content-disposition");
		if (cabecalho == null) {
			return null;
		}
		for (String trecho : cabecalho.split(";")) {
			trecho = trecho.trim();
			if (trecho.startsWith("filename")) {
				return trecho.substring(trecho.indexOf('=') + 1).replace("\"", "").trim();
			}
		}
		return null;
	}

	private String extensao(String nomeArquivo) {
		int ponto = nomeArquivo.lastIndexOf('.');
		return ponto < 0 ? "" : nomeArquivo.substring(ponto + 1).toLowerCase(Locale.ROOT);
	}

	/**
	 * Grava o arquivo enviado em imagens/uploads/anuncios (fora do
	 * versionamento — ver .gitignore) com um nome único, e devolve a URL
	 * relativa já pronta para servir como recurso estático.
	 */
	private String salvarArquivo(HttpServletRequest requisicao, Part parte, String extensao) throws IOException {
		String pastaReal = getServletContext().getRealPath(PASTA_UPLOADS);
		Path diretorio = Path.of(pastaReal);
		Files.createDirectories(diretorio);

		String nomeUnico = UUID.randomUUID() + "." + extensao;
		Path destino = diretorio.resolve(nomeUnico);
		try (InputStream entrada = parte.getInputStream()) {
			Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
		}

		return requisicao.getContextPath() + PASTA_UPLOADS + "/" + nomeUnico;
	}

	/**
	 * GET da etapa 1 com ?removerFoto=<url> ou ?removerVideo=1 apaga o
	 * arquivo do disco e tira a referência do rascunho — usado pelos
	 * botões "Remover" das miniaturas já enviadas.
	 */
	private void removerMidiaSeSolicitado(HttpServletRequest requisicao, RascunhoAnuncio rascunho) {
		String urlFotoRemover = requisicao.getParameter("removerFoto");
		if (urlFotoRemover != null && rascunho.getFotos().remove(urlFotoRemover)) {
			apagarArquivo(urlFotoRemover);
		}

		if ("1".equals(requisicao.getParameter("removerVideo")) && rascunho.getVideo() != null) {
			apagarArquivo(rascunho.getVideo());
			rascunho.setVideo(null);
		}
	}

	private void apagarArquivo(String urlRelativa) {
		String contextPath = getServletContext().getContextPath();
		String caminhoNaPasta = urlRelativa.startsWith(contextPath)
				? urlRelativa.substring(contextPath.length()) : urlRelativa;
		String pastaReal = getServletContext().getRealPath(caminhoNaPasta);
		if (pastaReal != null) {
			new File(pastaReal).delete();
		}
	}

	// -------------------------------------------------------------------
	// Etapa 2 — plano
	// -------------------------------------------------------------------
	private void processarEtapa2(HttpServletRequest requisicao, RascunhoAnuncio rascunho)
			throws RegraNegocioException, DAOException {

		Integer idPlano = parseInteiroOuNulo(requisicao.getParameter("idPlano"));
		if (idPlano == null || planoDAO.buscarPorId(idPlano).isEmpty()) {
			throw new RegraNegocioException("Escolha um plano para continuar.");
		}
		rascunho.setIdPlano(idPlano);
	}

	// -------------------------------------------------------------------
	// Etapa 3 — dados do anunciante
	// -------------------------------------------------------------------
	private void processarEtapa3(HttpServletRequest requisicao, RascunhoAnuncio rascunho) throws RegraNegocioException {
		String nome = textoObrigatorio(requisicao, "nomeAnunciante", "Informe seu nome completo.");
		String celular = apenasDigitos(requisicao.getParameter("celularAnunciante"));
		if (celular.length() < 10 || celular.length() > 11) {
			throw new RegraNegocioException("Informe um celular válido, com DDD.");
		}
		String cpfCnpj = apenasDigitos(requisicao.getParameter("cpfCnpjAnunciante"));
		boolean ehCnpj = cpfCnpj.length() == 14;
		if ((cpfCnpj.length() != 11 && cpfCnpj.length() != 14)
				|| (ehCnpj ? !ValidadorCNPJ.isValido(cpfCnpj) : !ValidadorCPF.isValido(cpfCnpj))) {
			throw new RegraNegocioException("Informe um CPF ou CNPJ válido.");
		}

		boolean enderecoIgual = "on".equals(requisicao.getParameter("enderecoIgualImovel"));
		rascunho.setEnderecoAnuncianteIgualImovel(enderecoIgual);
		if (!enderecoIgual) {
			rascunho.setCepAnunciante(apenasDigitos(requisicao.getParameter("cepAnunciante")));
			rascunho.setEnderecoAnunciante(requisicao.getParameter("enderecoAnunciante"));
			rascunho.setNumeroAnunciante(requisicao.getParameter("numeroAnunciante"));
			rascunho.setBairroAnunciante(requisicao.getParameter("bairroAnunciante"));
			rascunho.setCidadeAnunciante(requisicao.getParameter("cidadeAnunciante"));
			rascunho.setEstadoAnunciante(requisicao.getParameter("estadoAnunciante"));
		}

		rascunho.setNomeAnunciante(nome);
		rascunho.setCelularAnunciante(celular);
		rascunho.setCpfCnpjAnunciante(cpfCnpj);
	}

	// -------------------------------------------------------------------
	// Etapa 4 — disponibilidade para visitas: dias da semana marcados +
	// um intervalo de horário compartilhado por todos eles (ex.: "Seg, Qua
	// e Sex, das 9h às 18h"). Simples de preencher e cobre o caso comum;
	// horários diferentes por dia ficam para uma versão futura.
	// -------------------------------------------------------------------
	private void processarEtapa4(HttpServletRequest requisicao, RascunhoAnuncio rascunho) throws RegraNegocioException {
		String[] diasMarcados = requisicao.getParameterValues("diaSemana");
		if (diasMarcados == null || diasMarcados.length == 0) {
			throw new RegraNegocioException("Marque ao menos um dia em que aceita visitas.");
		}

		LocalTime horaInicio = parseHora(requisicao.getParameter("horaInicio"), "Informe o horário inicial.");
		LocalTime horaFim = parseHora(requisicao.getParameter("horaFim"), "Informe o horário final.");
		if (!horaFim.isAfter(horaInicio)) {
			throw new RegraNegocioException("O horário final precisa ser depois do inicial.");
		}

		List<DisponibilidadeVisita> janelas = new ArrayList<>();
		for (String diaTexto : diasMarcados) {
			DiaSemana dia = ConversorEnum.paraEnum(DiaSemana.class, diaTexto);
			if (dia != null) {
				janelas.add(new DisponibilidadeVisita(0, dia, horaInicio, horaFim));
			}
		}
		rascunho.setDisponibilidade(janelas);
	}

	private LocalTime parseHora(String valor, String mensagemErro) throws RegraNegocioException {
		if (valor == null || valor.isBlank()) {
			throw new RegraNegocioException(mensagemErro);
		}
		try {
			return LocalTime.parse(valor.trim());
		} catch (java.time.format.DateTimeParseException e) {
			throw new RegraNegocioException(mensagemErro);
		}
	}

	// -------------------------------------------------------------------
	// Etapa 5 — confirmação: grava o rascunho como imóvel pendente de
	// pagamento + a disponibilidade de visitas + a contratação do plano, e
	// manda para o pagamento.
	// -------------------------------------------------------------------
	private void processarEtapa5(HttpServletRequest requisicao, HttpServletResponse resposta, RascunhoAnuncio rascunho)
			throws DAOException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);

		// Confere se o usuário da sessão ainda existe no banco: uma sessão de
		// login sobrevive mais tempo que o objeto Usuario nela guardado, então
		// se a conta foi removida nesse meio-tempo (ex.: "Excluir minha
		// conta" em outra aba, ou uma limpeza manual de teste) o INSERT do
		// imóvel a seguir quebraria a chave estrangeira com uma mensagem
		// genérica. Detectando aqui, mandamos a pessoa logar de novo com uma
		// mensagem clara em vez disso.
		if (usuario == null || usuarioDAO.buscarPorId(usuario.getId()).isEmpty()) {
			SessaoUsuario.encerrar(requisicao);
			requisicao.getSession().setAttribute("erroLogin", "Sua sessão expirou. Entre novamente para continuar.");
			resposta.sendRedirect(requisicao.getContextPath() + "/login");
			return;
		}

		Imovel imovel = new Imovel();
		imovel.setTitulo(rascunho.getTitulo());
		imovel.setDescricao(rascunho.getDescricao());
		imovel.setTipo(rascunho.getTipo());
		imovel.setFinalidade(rascunho.getFinalidade());
		imovel.setPreco(rascunho.getPreco());
		imovel.setAreaM2(rascunho.getAreaM2());
		imovel.setQuartos(rascunho.getQuartos());
		imovel.setBanheiros(rascunho.getBanheiros());
		imovel.setVagasGaragem(rascunho.getVagasGaragem());
		imovel.setEndereco(rascunho.getEndereco() + ", " + rascunho.getNumero() + " — " + rascunho.getBairro());
		imovel.setCidade(rascunho.getCidade());
		imovel.setEstado(rascunho.getEstado());
		imovel.setCep(rascunho.getCep());

		try {
			imovelServico.publicarComoPendente(imovel, usuario);
		} catch (RegraNegocioException e) {
			// Os dados já passaram pela validação de cada etapa — só chega
			// aqui em caso de sessão adulterada; volta para o início.
			requisicao.getSession().removeAttribute(RascunhoAnuncio.SESSAO_CHAVE);
			resposta.sendRedirect(requisicao.getContextPath() + "/anunciar/etapa1");
			return;
		}

		for (DisponibilidadeVisita janela : rascunho.getDisponibilidade()) {
			janela.setIdImovel(imovel.getId());
		}
		disponibilidadeVisitaDAO.salvarTodas(imovel.getId(), rascunho.getDisponibilidade());

		// Item 1.1: só agora os arquivos já gravados em disco durante a
		// etapa 1 viram registros de FOTO_IMOVEL/VIDEO_IMOVEL, vinculados
		// ao id do imóvel que acabou de ser criado.
		int ordem = 0;
		for (String urlFoto : rascunho.getFotos()) {
			fotoImovelDAO.inserir(new FotoImovel(imovel.getId(), urlFoto, ordem++));
		}
		if (rascunho.getVideo() != null && !rascunho.getVideo().isBlank()) {
			videoImovelDAO.inserir(new VideoImovel(imovel.getId(), rascunho.getVideo(), 0));
		}

		TipoAnunciante tipoAnunciante = rascunho.getTipoAnunciante() != null
				? rascunho.getTipoAnunciante() : TipoAnunciante.PROPRIETARIO;
		Anuncio anuncio = new Anuncio(imovel.getId(), rascunho.getIdPlano(), usuario.getId(), tipoAnunciante);
		anuncioDAO.inserir(anuncio);

		requisicao.getSession().removeAttribute(RascunhoAnuncio.SESSAO_CHAVE);
		resposta.sendRedirect(requisicao.getContextPath() + "/anunciar/pagamento?id=" + anuncio.getId());
	}

	// -------------------------------------------------------------------
	// Auxiliares
	// -------------------------------------------------------------------

	private int etapaDaUrl(HttpServletRequest requisicao) {
		String caminho = requisicao.getServletPath();
		return Character.getNumericValue(caminho.charAt(caminho.length() - 1));
	}

	private RascunhoAnuncio obterRascunho(HttpServletRequest requisicao) {
		HttpSession sessao = requisicao.getSession();
		RascunhoAnuncio rascunho = (RascunhoAnuncio) sessao.getAttribute(RascunhoAnuncio.SESSAO_CHAVE);
		if (rascunho == null) {
			rascunho = new RascunhoAnuncio();
			sessao.setAttribute(RascunhoAnuncio.SESSAO_CHAVE, rascunho);
		}
		return rascunho;
	}

	/**
	 * @return a primeira etapa ainda incompleta que impede o acesso à etapa
	 *         pedida, ou null se pode seguir normalmente
	 */
	private Integer primeiraEtapaPendente(RascunhoAnuncio rascunho, int etapaPedida) {
		if (etapaPedida >= 2 && !rascunho.etapa1Completa()) {
			return 1;
		}
		if (etapaPedida >= 3 && !rascunho.etapa2Completa()) {
			return 2;
		}
		if (etapaPedida >= 4 && !rascunho.etapa3Completa()) {
			return 3;
		}
		if (etapaPedida >= 5 && !rascunho.etapa4Completa()) {
			return 4;
		}
		return null;
	}

	private void reexibir(HttpServletRequest requisicao, HttpServletResponse resposta, int etapa, RascunhoAnuncio rascunho)
			throws ServletException, IOException {

		try {
			if (etapa == 2) {
				requisicao.setAttribute("planos", planosIndividuaisDoTipo(rascunho.getTipoAnunciante()));
			}
		} catch (DAOException e) {
			getServletContext().log("Falha ao recarregar planos.", e);
		}
		requisicao.setAttribute("rascunho", rascunho);
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher("/WEB-INF/jsp/anuncio-etapa" + etapa + ".jsp").forward(requisicao, resposta);
	}

	/**
	 * Planos que podem ser escolhidos dentro do assistente: só os
	 * individuais (destaque de um único anúncio) do tipo de anunciante em
	 * questão. Packs (lotes de créditos para vários anúncios) só aparecem na
	 * tela de planos avulsa (ver controller.PlanosServlet), fora do
	 * assistente de anúncio único.
	 */
	private List<Plano> planosIndividuaisDoTipo(TipoAnunciante tipoAnunciante) throws DAOException {
		List<Plano> todos = planoDAO.listarPorTipo(tipoAnunciante);
		List<Plano> individuais = new ArrayList<>();
		for (Plano plano : todos) {
			if (plano.getCategoria() == CategoriaPlano.INDIVIDUAL) {
				individuais.add(plano);
			}
		}
		return individuais;
	}

	private String textoObrigatorio(HttpServletRequest requisicao, String nome, String mensagemErro)
			throws RegraNegocioException {
		String valor = requisicao.getParameter(nome);
		if (valor == null || valor.isBlank()) {
			throw new RegraNegocioException(mensagemErro);
		}
		return valor.trim();
	}

	private String apenasDigitos(String valor) {
		return valor == null ? "" : valor.replaceAll("\\D", "");
	}

	/**
	 * Interpreta um valor em reais digitado com a máscara de moeda
	 * ("R$ 450.000,00"): mantém só dígitos e vírgula (descarta "R$", espaços
	 * e os pontos de milhar) e trata a vírgula como separador decimal.
	 */
	private BigDecimal parseDecimal(String valor) {
		String limpo = valorMonetarioLimpo(valor);
		if (limpo == null) {
			return null;
		}
		return new BigDecimal(limpo);
	}

	/**
	 * Mesma lógica de parseDecimal, para o campo de área ("70,5 m²").
	 */
	private double parseDouble(String valor) {
		String limpo = valorMonetarioLimpo(valor);
		return limpo == null ? 0 : Double.parseDouble(limpo);
	}

	private String valorMonetarioLimpo(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		String semSeparadorDecimal = valor.replaceAll("[^0-9,]", "");
		if (semSeparadorDecimal.isBlank()) {
			return null;
		}
		return semSeparadorDecimal.replace(",", ".");
	}

	private int parseInteiro(String valor) {
		if (valor == null || valor.isBlank()) {
			return 0;
		}
		return Integer.parseInt(valor.trim());
	}

	private Integer parseInteiroOuNulo(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		try {
			return Integer.valueOf(valor.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
