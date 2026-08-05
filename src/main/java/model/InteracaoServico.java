package model;

import java.util.List;

import dao.AvaliacaoDAO;
import dao.ContatoInteresseDAO;
import dao.DAOException;
import dao.FavoritoDAO;
import dao.ImovelDAO;
import dao.SeguidorDAO;

/**
 * Regras de negócio das interações entre usuários: favoritos, seguidores,
 * avaliações e mensagens de interesse.
 *
 * As restrições UNIQUE do banco impedem duplicatas, mas as regras que o banco
 * não consegue expressar ficam aqui, como impedir que alguém avalie a si mesmo
 * ou demonstre interesse no próprio anúncio.
 */
public class InteracaoServico {

	private final FavoritoDAO favoritoDAO = new FavoritoDAO();
	private final SeguidorDAO seguidorDAO = new SeguidorDAO();
	private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
	private final ContatoInteresseDAO contatoInteresseDAO = new ContatoInteresseDAO();
	private final ImovelDAO imovelDAO = new ImovelDAO();

	/**
	 * Marca ou desmarca o imóvel como favorito.
	 *
	 * @return true se o imóvel ficou favoritado
	 */
	public boolean alternarFavorito(int idUsuario, int idImovel) throws DAOException {
		return favoritoDAO.alternar(idUsuario, idImovel);
	}

	public List<Imovel> listarFavoritos(int idUsuario) throws DAOException {
		return favoritoDAO.listarImoveisFavoritos(idUsuario);
	}

	/**
	 * Passa a seguir ou deixa de seguir um usuário.
	 *
	 * @return true se passou a seguir
	 */
	public boolean alternarSeguir(int idSeguidor, int idSeguido) throws RegraNegocioException, DAOException {
		if (idSeguidor == idSeguido) {
			throw new RegraNegocioException("Você não pode seguir a si mesmo.");
		}
		return seguidorDAO.alternar(idSeguidor, idSeguido);
	}

	/**
	 * Registra a avaliação de um usuário sobre outro.
	 *
	 * A nota já é validada no construtor de Avaliacao, entre 1 e 5.
	 */
	public void avaliar(Avaliacao avaliacao) throws RegraNegocioException, DAOException {
		if (avaliacao.getIdAvaliador() == avaliacao.getIdAvaliado()) {
			throw new RegraNegocioException("Você não pode avaliar a si mesmo.");
		}
		if (avaliacaoDAO.jaAvaliou(avaliacao.getIdAvaliador(), avaliacao.getIdAvaliado(), avaliacao.getIdImovel())) {
			throw new RegraNegocioException("Você já avaliou este usuário nesta negociação.");
		}
		avaliacaoDAO.inserir(avaliacao);
	}

	public List<Avaliacao> listarAvaliacoesRecebidas(int idUsuario) throws DAOException {
		return avaliacaoDAO.listarPorAvaliado(idUsuario);
	}

	public double calcularReputacao(int idUsuario) throws DAOException {
		return avaliacaoDAO.calcularMedia(idUsuario);
	}

	public int contarAvaliacoes(int idUsuario) throws DAOException {
		return avaliacaoDAO.contarPorAvaliado(idUsuario);
	}

	/**
	 * Envia uma mensagem de interesse ao anunciante.
	 */
	public void registrarInteresse(int idImovel, int idComprador, String mensagem)
			throws RegraNegocioException, DAOException {

		if (mensagem == null || mensagem.isBlank()) {
			throw new RegraNegocioException("Escreva uma mensagem para o anunciante.");
		}

		Imovel imovel = imovelDAO.buscarPorId(idImovel)
				.orElseThrow(() -> new RegraNegocioException("Imóvel não encontrado."));

		if (imovel.getIdUsuario() == idComprador) {
			throw new RegraNegocioException("Você não pode demonstrar interesse no próprio anúncio.");
		}
		if (!imovel.estaDisponivel()) {
			throw new RegraNegocioException("Este anúncio não está mais disponível.");
		}

		contatoInteresseDAO.inserir(new ContatoInteresse(idImovel, idComprador, mensagem.trim()));
	}

	public List<ContatoInteresse> listarInteressesRecebidos(int idAnunciante) throws DAOException {
		return contatoInteresseDAO.listarRecebidos(idAnunciante);
	}

	public List<ContatoInteresse> listarInteressesEnviados(int idComprador) throws DAOException {
		return contatoInteresseDAO.listarEnviados(idComprador);
	}

	public int contarInteressesPendentes(int idAnunciante) throws DAOException {
		return contatoInteresseDAO.contarPendentes(idAnunciante);
	}

	/**
	 * Atualiza a situação de uma mensagem recebida.
	 *
	 * Só o dono do imóvel pode fazer isso, por isso a comparação com o
	 * anunciante autenticado.
	 */
	public void atualizarSituacaoInteresse(int idContato, StatusContato status, int idAnunciante)
			throws RegraNegocioException, DAOException {

		ContatoInteresse contato = contatoInteresseDAO.buscarPorId(idContato)
				.orElseThrow(() -> new RegraNegocioException("Mensagem não encontrada."));

		if (contato.getImovel().getIdUsuario() != idAnunciante) {
			throw new RegraNegocioException("Você não tem permissão para responder esta mensagem.");
		}

		contatoInteresseDAO.atualizarStatus(idContato, status);
	}
}
