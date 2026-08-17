package model;

import java.util.List;

import dao.BuscaSalvaDAO;
import dao.DAOException;

/**
 * Regras de negócio das buscas salvas.
 *
 * O disparo do alerta por e-mail em si fica para uma etapa futura do
 * projeto; esta classe cobre o cadastro, a consulta e o opt-in/opt-out do
 * alerta (Regras 9 e 22-24 do PROJECT_SPEC).
 */
public class BuscaSalvaServico {

	private final BuscaSalvaDAO buscaSalvaDAO = new BuscaSalvaDAO();

	public void salvar(BuscaSalva busca) throws RegraNegocioException, DAOException {
		boolean temAoMenosUmCriterio = busca.getTipo() != null || busca.getFinalidade() != null
				|| (busca.getCidade() != null && !busca.getCidade().isBlank())
				|| busca.getQuartosMinimo() != null || busca.getPrecoMaximo() != null;

		if (!temAoMenosUmCriterio) {
			throw new RegraNegocioException("Informe ao menos um critério para salvar a busca.");
		}

		buscaSalvaDAO.inserir(busca);
	}

	public List<BuscaSalva> listarDoUsuario(int idUsuario) throws DAOException {
		return buscaSalvaDAO.listarPorUsuario(idUsuario);
	}

	public void pausarAlerta(int idBusca, int idUsuarioLogado) throws RegraNegocioException, DAOException {
		alterarAlerta(idBusca, idUsuarioLogado, false);
	}

	public void reativarAlerta(int idBusca, int idUsuarioLogado) throws RegraNegocioException, DAOException {
		alterarAlerta(idBusca, idUsuarioLogado, true);
	}

	public void excluir(int idBusca, int idUsuarioLogado) throws RegraNegocioException, DAOException {
		BuscaSalva busca = buscarObrigatoria(idBusca);
		garantirPosse(busca, idUsuarioLogado);
		buscaSalvaDAO.remover(idBusca);
	}

	private void alterarAlerta(int idBusca, int idUsuarioLogado, boolean ativo)
			throws RegraNegocioException, DAOException {

		BuscaSalva busca = buscarObrigatoria(idBusca);
		garantirPosse(busca, idUsuarioLogado);
		buscaSalvaDAO.atualizarAlerta(idBusca, ativo);
	}

	private BuscaSalva buscarObrigatoria(int idBusca) throws RegraNegocioException, DAOException {
		return buscaSalvaDAO.buscarPorId(idBusca)
				.orElseThrow(() -> new RegraNegocioException("Busca salva não encontrada."));
	}

	private void garantirPosse(BuscaSalva busca, int idUsuarioLogado) throws RegraNegocioException {
		if (busca.getIdUsuario() != idUsuarioLogado) {
			throw new RegraNegocioException("Você não tem permissão para alterar esta busca.");
		}
	}
}
