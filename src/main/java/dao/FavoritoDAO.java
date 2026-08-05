package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Imovel;

/**
 * Acesso à tabela favorito.
 *
 * A tabela tem restrição UNIQUE (id_usuario, id_imovel), então o mesmo imóvel
 * não pode ser favoritado duas vezes pelo mesmo usuário.
 */
public class FavoritoDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO favorito (id_usuario, id_imovel)
			VALUES (?, ?)
			""";

	private static final String SQL_REMOVER = """
			DELETE FROM favorito
			WHERE id_usuario = ? AND id_imovel = ?
			""";

	private static final String SQL_EXISTE = """
			SELECT COUNT(*)
			FROM favorito
			WHERE id_usuario = ? AND id_imovel = ?
			""";

	private static final String SQL_LISTAR_IMOVEIS = "SELECT " + ImovelDAO.COLUNAS + """
			FROM favorito f
			JOIN imovel i ON i.id = f.id_imovel
			JOIN usuario u ON u.id = i.id_usuario
			WHERE f.id_usuario = ?
			ORDER BY f.data_adicao DESC
			""";

	private static final String SQL_CONTAR_POR_IMOVEL = "SELECT COUNT(*) FROM favorito WHERE id_imovel = ?";

	private static final String SQL_REMOVER_POR_IMOVEL = "DELETE FROM favorito WHERE id_imovel = ?";

	/**
	 * Favorita o imóvel. Não faz nada se o usuário já o tinha favoritado,
	 * evitando erro de chave duplicada.
	 */
	public void favoritar(int idUsuario, int idImovel) throws DAOException {
		if (existe(idUsuario, idImovel)) {
			return;
		}
		executar(SQL_INSERIR, "Erro ao favoritar o imóvel de id " + idImovel + ".", idUsuario, idImovel);
	}

	public void desfavoritar(int idUsuario, int idImovel) throws DAOException {
		executar(SQL_REMOVER, "Erro ao desfavoritar o imóvel de id " + idImovel + ".", idUsuario, idImovel);
	}

	/**
	 * Inverte o estado do favorito, comportamento esperado do botão de coração.
	 *
	 * @return true se o imóvel ficou favoritado, false se deixou de ser
	 */
	public boolean alternar(int idUsuario, int idImovel) throws DAOException {
		if (existe(idUsuario, idImovel)) {
			desfavoritar(idUsuario, idImovel);
			return false;
		}
		favoritar(idUsuario, idImovel);
		return true;
	}

	public boolean existe(int idUsuario, int idImovel) throws DAOException {
		return contar(SQL_EXISTE, "Erro ao verificar o favorito.", idUsuario, idImovel) > 0;
	}

	/**
	 * Lista os imóveis favoritados por um usuário, do mais recente para o mais
	 * antigo. As fotos não vêm carregadas: use FotoImovelDAO.carregarFotos.
	 */
	public List<Imovel> listarImoveisFavoritos(int idUsuario) throws DAOException {
		List<Imovel> imoveis = new ArrayList<>();
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_IMOVEIS)) {

			comando.setInt(1, idUsuario);
			try (ResultSet resultado = comando.executeQuery()) {
				while (resultado.next()) {
					imoveis.add(ImovelDAO.montarImovel(resultado));
				}
			}
			return imoveis;
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar os favoritos do usuário de id " + idUsuario + ".", e);
		}
	}

	/**
	 * @return quantas pessoas favoritaram o imóvel, usado como métrica no anúncio
	 */
	public int contarPorImovel(int idImovel) throws DAOException {
		return contar(SQL_CONTAR_POR_IMOVEL, "Erro ao contar os favoritos do imóvel de id " + idImovel + ".",
				idImovel);
	}

	/**
	 * Remove todos os favoritos de um imóvel. Precisa ser chamado antes de
	 * excluir o imóvel, por causa da chave estrangeira.
	 */
	public void removerPorImovel(int idImovel) throws DAOException {
		executar(SQL_REMOVER_POR_IMOVEL, "Erro ao remover os favoritos do imóvel de id " + idImovel + ".", idImovel);
	}

	private void executar(String sql, String mensagemErro, Object... parametros) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql)) {

			preencherParametros(comando, parametros);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException(mensagemErro, e);
		}
	}

	private int contar(String sql, String mensagemErro, Object... parametros) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql)) {

			preencherParametros(comando, parametros);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? resultado.getInt(1) : 0;
			}
		} catch (SQLException e) {
			throw new DAOException(mensagemErro, e);
		}
	}

	private void preencherParametros(PreparedStatement comando, Object... parametros) throws SQLException {
		for (int posicao = 0; posicao < parametros.length; posicao++) {
			comando.setObject(posicao + 1, parametros[posicao]);
		}
	}
}
