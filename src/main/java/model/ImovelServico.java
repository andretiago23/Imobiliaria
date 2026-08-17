package model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import dao.AvaliacaoDAO;
import dao.ContatoInteresseDAO;
import dao.DAOException;
import dao.FavoritoDAO;
import dao.FotoImovelDAO;
import dao.ImovelDAO;

/**
 * Regras de negócio dos anúncios de imóveis.
 *
 * Além das validações de formulário, é aqui que fica a checagem de posse: um
 * usuário só pode alterar ou excluir os próprios anúncios.
 */
public class ImovelServico {

	private static final int LIMITE_PADRAO_FEED = 20;
	private static final int TAMANHO_SIGLA_ESTADO = 2;
	private static final int ANO_MINIMO_CONSTRUCAO = 1900;

	private final ImovelDAO imovelDAO = new ImovelDAO();
	private final FotoImovelDAO fotoImovelDAO = new FotoImovelDAO();
	private final FavoritoDAO favoritoDAO = new FavoritoDAO();
	private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
	private final ContatoInteresseDAO contatoInteresseDAO = new ContatoInteresseDAO();

	/**
	 * Publica um novo anúncio.
	 *
	 * @param imovel dados preenchidos no formulário
	 * @param autor  usuário autenticado que está publicando
	 */
	public void publicar(Imovel imovel, Usuario autor) throws RegraNegocioException, DAOException {
		if (!autor.podeAnunciar()) {
			throw new RegraNegocioException(
					"Sua conta é do tipo comprador. Altere o tipo no perfil para publicar imóveis.");
		}

		validarDados(imovel);

		imovel.setIdUsuario(autor.getId());
		imovel.setStatus(StatusImovel.ATIVO);
		imovelDAO.inserir(imovel);
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
		int anoAtual = java.time.Year.now().getValue();
		if (imovel.getAno() != null && (imovel.getAno() < ANO_MINIMO_CONSTRUCAO || imovel.getAno() > anoAtual + 1)) {
			throw new RegraNegocioException(
					"Informe um ano de construção válido, entre " + ANO_MINIMO_CONSTRUCAO + " e " + (anoAtual + 1) + ".");
		}
	}
}
