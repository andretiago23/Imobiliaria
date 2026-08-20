package model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import dao.AnuncioDAO;
import dao.AvaliacaoDAO;
import dao.BuscaSalvaDAO;
import dao.ConfirmacaoStatusDAO;
import dao.ContatoInteresseDAO;
import dao.DAOException;
import dao.DisponibilidadeVisitaDAO;
import dao.FavoritoDAO;
import dao.FotoImovelDAO;
import dao.ImovelDAO;
import dao.UsuarioDAO;
import dao.VisitaAgendadaDAO;
import util.EmailService;

/**
 * Regras de negócio dos anúncios de imóveis.
 *
 * Além das validações de formulário, é aqui que fica a checagem de posse: um
 * usuário só pode alterar ou excluir os próprios anúncios.
 */
public class ImovelServico {

	private static final System.Logger LOG = System.getLogger(ImovelServico.class.getName());

	private static final int LIMITE_PADRAO_FEED = 20;
	private static final int TAMANHO_SIGLA_ESTADO = 2;

	private final ImovelDAO imovelDAO = new ImovelDAO();
	private final FotoImovelDAO fotoImovelDAO = new FotoImovelDAO();
	private final FavoritoDAO favoritoDAO = new FavoritoDAO();
	private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
	private final ContatoInteresseDAO contatoInteresseDAO = new ContatoInteresseDAO();
	private final BuscaSalvaDAO buscaSalvaDAO = new BuscaSalvaDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final EmailService emailService = new EmailService();
	private final AnuncioDAO anuncioDAO = new AnuncioDAO();
	private final DisponibilidadeVisitaDAO disponibilidadeVisitaDAO = new DisponibilidadeVisitaDAO();
	private final VisitaAgendadaDAO visitaAgendadaDAO = new VisitaAgendadaDAO();
	private final ConfirmacaoStatusDAO confirmacaoStatusDAO = new ConfirmacaoStatusDAO();

	/**
	 * Publica um novo anúncio.
	 *
	 * Qualquer usuário autenticado pode anunciar (ver Usuario.podeAnunciar()):
	 * o autor vira automaticamente o proprietário do imóvel, seja ele um
	 * cliente comum ou uma conta do tipo "vendedor"/imobiliária.
	 *
	 * @param imovel        dados preenchidos no formulário
	 * @param autor         usuário autenticado que está publicando
	 * @param linkBaseImovel URL completa até "...&#47;imovel?id=", sem o id —
	 *                       o id só existe depois do INSERT, então o link final
	 *                       de cada alerta é montado aqui dentro
	 */
	public void publicar(Imovel imovel, Usuario autor, String linkBaseImovel) throws RegraNegocioException, DAOException {
		validarDados(imovel);

		imovel.setIdUsuario(autor.getId());
		imovel.setStatus(StatusImovel.ATIVO);
		imovelDAO.inserir(imovel);

		dispararAlertasDeBuscaSalva(imovel, linkBaseImovel + imovel.getId());
	}

	/**
	 * Grava o imóvel como PENDENTE_PAGAMENTO, ao final da etapa 4 do
	 * assistente de anúncio — usado junto com a contratação de um plano
	 * (model.Anuncio). Não aparece no catálogo nem dispara alerta de busca
	 * salva; isso só acontece em ativarAposPagamento, quando o pagamento é
	 * confirmado.
	 */
	public void publicarComoPendente(Imovel imovel, Usuario autor) throws RegraNegocioException, DAOException {
		validarDados(imovel);

		imovel.setIdUsuario(autor.getId());
		imovel.setStatus(StatusImovel.PENDENTE_PAGAMENTO);
		imovelDAO.inserir(imovel);
	}

	/**
	 * Ativa um imóvel criado como PENDENTE_PAGAMENTO assim que o pagamento do
	 * anúncio é confirmado (ver controller.PagamentoServlet) — só a partir
	 * daqui ele passa a aparecer no catálogo e dispara alertas de busca salva.
	 */
	public void ativarAposPagamento(int idImovel, String linkImovel) throws DAOException {
		imovelDAO.atualizarStatus(idImovel, StatusImovel.ATIVO);
		imovelDAO.buscarPorId(idImovel).ifPresent(imovel -> dispararAlertasDeBuscaSalva(imovel, linkImovel));
	}

	/**
	 * Verifica quais buscas salvas com alerta ativo combinam com o imóvel
	 * recém-publicado e envia um e-mail para cada uma, evitando duplicidade
	 * via busca_salva_notificacao (BuscaSalvaDAO.jaNotificado).
	 *
	 * O imóvel já foi gravado com sucesso quando este método roda: uma falha
	 * aqui (banco ou SMTP) fica só registrada em log e nunca propaga para
	 * quebrar a resposta de "anúncio publicado" para quem cadastrou.
	 */
	private void dispararAlertasDeBuscaSalva(Imovel imovel, String linkImovel) {
		try {
			for (BuscaSalva busca : buscaSalvaDAO.listarComAlertaAtivo()) {
				if (!busca.combinaCom(imovel) || buscaSalvaDAO.jaNotificado(busca.getId(), imovel.getId())) {
					continue;
				}
				Optional<Usuario> cliente = usuarioDAO.buscarPorId(busca.getIdUsuario());
				if (cliente.isEmpty()) {
					continue;
				}
				emailService.notificarAlertaBuscaSalva(cliente.get(), busca, imovel, linkImovel);
				buscaSalvaDAO.registrarNotificacao(busca.getId(), imovel.getId());
			}
		} catch (DAOException e) {
			LOG.log(System.Logger.Level.WARNING,
					"Falha ao verificar/enviar alertas de busca salva para o imóvel " + imovel.getId() + ".", e);
		}
	}

	/**
	 * Atualiza um anúncio existente, desde que pertença a quem está editando.
	 *
	 * @param idUsuarioLogado usuário autenticado na sessão
	 */
	public void atualizar(Imovel imovel, int idUsuarioLogado) throws RegraNegocioException, DAOException {
		Imovel existente = buscarObrigatorio(imovel.getId());
		garantirPosse(existente, idUsuarioLogado);
		validarDados(imovel);

		imovel.setIdUsuario(existente.getIdUsuario());
		imovelDAO.atualizar(imovel);
	}

	/**
	 * Muda a situação do anúncio, por exemplo ao marcar como vendido.
	 */
	public void alterarStatus(int idImovel, StatusImovel status, int idUsuarioLogado)
			throws RegraNegocioException, DAOException {

		Imovel imovel = buscarObrigatorio(idImovel);
		garantirPosse(imovel, idUsuarioLogado);
		imovelDAO.atualizarStatus(idImovel, status);
	}

	/**
	 * Busca um anúncio para a página de detalhe, já com as fotos carregadas.
	 */
	public Optional<Imovel> buscarParaExibicao(int idImovel) throws DAOException {
		Optional<Imovel> encontrado = imovelDAO.buscarPorId(idImovel);
		if (encontrado.isPresent()) {
			fotoImovelDAO.carregarFotos(encontrado.get());
		}
		return encontrado;
	}

	/**
	 * Anúncios ativos mais recentes, com as fotos, para a página inicial.
	 */
	public List<Imovel> listarFeed() throws DAOException {
		return comFotos(imovelDAO.listarAtivos(LIMITE_PADRAO_FEED));
	}

	/**
	 * Resultado da busca com filtros, com as fotos carregadas.
	 */
	public List<Imovel> buscar(FiltroImovel filtro) throws DAOException {
		return comFotos(imovelDAO.buscarComFiltros(filtro));
	}

	/**
	 * Anúncios de um usuário, incluindo inativos e já negociados.
	 */
	public List<Imovel> listarDoUsuario(int idUsuario) throws DAOException {
		return comFotos(imovelDAO.listarPorUsuario(idUsuario));
	}

	/**
	 * Acrescenta uma foto ao final do carrossel do anúncio.
	 */
	public void adicionarFoto(int idImovel, String urlFoto, int idUsuarioLogado)
			throws RegraNegocioException, DAOException {

		if (urlFoto == null || urlFoto.isBlank()) {
			throw new RegraNegocioException("Informe o endereço da imagem.");
		}

		Imovel imovel = buscarObrigatorio(idImovel);
		garantirPosse(imovel, idUsuarioLogado);

		FotoImovel foto = new FotoImovel(idImovel, urlFoto.trim(), fotoImovelDAO.proximaOrdem(idImovel));
		fotoImovelDAO.inserir(foto);
	}

	public void removerFoto(int idFoto, int idImovel, int idUsuarioLogado)
			throws RegraNegocioException, DAOException {

		Imovel imovel = buscarObrigatorio(idImovel);
		garantirPosse(imovel, idUsuarioLogado);
		fotoImovelDAO.remover(idFoto);
	}

	/**
	 * Exclui definitivamente o anúncio.
	 *
	 * Como o banco não usa ON DELETE CASCADE, os registros que apontam para o
	 * imóvel precisam sair primeiro, nesta ordem, senão a chave estrangeira
	 * bloqueia a operação.
	 *
	 * Na maioria dos casos prefira desativar o anúncio com alterarStatus, que
	 * preserva o histórico da negociação.
	 */
	public void excluir(int idImovel, int idUsuarioLogado) throws RegraNegocioException, DAOException {
		Imovel imovel = buscarObrigatorio(idImovel);
		garantirPosse(imovel, idUsuarioLogado);

		contatoInteresseDAO.removerPorImovel(idImovel);
		avaliacaoDAO.removerPorImovel(idImovel);
		favoritoDAO.removerPorImovel(idImovel);
		fotoImovelDAO.removerPorImovel(idImovel);
		buscaSalvaDAO.removerPorImovel(idImovel);
		confirmacaoStatusDAO.removerPorImovel(idImovel);
		visitaAgendadaDAO.removerPorImovel(idImovel);
		disponibilidadeVisitaDAO.removerPorImovel(idImovel);
		anuncioDAO.removerPorImovel(idImovel);
		imovelDAO.remover(idImovel);
	}

	private List<Imovel> comFotos(List<Imovel> imoveis) throws DAOException {
		fotoImovelDAO.carregarFotos(imoveis);
		return imoveis;
	}

	private Imovel buscarObrigatorio(int idImovel) throws RegraNegocioException, DAOException {
		return imovelDAO.buscarPorId(idImovel)
				.orElseThrow(() -> new RegraNegocioException("Imóvel não encontrado."));
	}

	/**
	 * Impede que um usuário altere anúncio de outra pessoa.
	 */
	private void garantirPosse(Imovel imovel, int idUsuarioLogado) throws RegraNegocioException {
		if (imovel.getIdUsuario() != idUsuarioLogado) {
			throw new RegraNegocioException("Você não tem permissão para alterar este anúncio.");
		}
	}

	private void validarDados(Imovel imovel) throws RegraNegocioException {
		if (imovel.getTitulo() == null || imovel.getTitulo().isBlank()) {
			throw new RegraNegocioException("Informe o título do anúncio.");
		}
		if (imovel.getTipo() == null) {
			throw new RegraNegocioException("Selecione o tipo do imóvel.");
		}
		if (imovel.getFinalidade() == null) {
			throw new RegraNegocioException("Selecione se o imóvel é para venda ou aluguel.");
		}
		if (imovel.getPreco() == null || imovel.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
			throw new RegraNegocioException("Informe um preço maior que zero.");
		}
		if (imovel.getEstado() != null && !imovel.getEstado().isBlank()
				&& imovel.getEstado().trim().length() != TAMANHO_SIGLA_ESTADO) {
			throw new RegraNegocioException("O estado deve ser informado pela sigla, com 2 letras.");
		}
	}
}
