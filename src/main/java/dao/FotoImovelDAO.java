package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.FotoImovel;
import model.Imovel;

/**
 * Acesso à tabela foto_imovel.
 *
 * A coluna ordem define a sequência das imagens no carrossel do anúncio.
 */
public class FotoImovelDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO foto_imovel (id_imovel, url_foto, ordem)
			VALUES (?, ?, ?)
			""";

	private static final String SQL_LISTAR_POR_IMOVEL = """
			SELECT id, id_imovel, url_foto, ordem
			FROM foto_imovel
			WHERE id_imovel = ?
			ORDER BY ordem
			""";

	private static final String SQL_PROXIMA_ORDEM = """
			SELECT COALESCE(MAX(ordem), -1) + 1
			FROM foto_imovel
			WHERE id_imovel = ?
			""";

	private static final String SQL_ATUALIZAR_ORDEM = "UPDATE foto_imovel SET ordem = ? WHERE id = ?";

	private static final String SQL_REMOVER = "DELETE FROM foto_imovel WHERE id = ?";

	private static final String SQL_REMOVER_POR_IMOVEL = "DELETE FROM foto_imovel WHERE id_imovel = ?";

	/**
	 * Grava uma nova foto e preenche o id gerado no próprio objeto.
	 */
	public void inserir(FotoImovel foto) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

			comando.setInt(1, foto.getIdImovel());
			comando.setString(2, foto.getUrlFoto());
			comando.setInt(3, foto.getOrdem());
			comando.executeUpdate();

			try (ResultSet chaves = comando.getGeneratedKeys()) {
				if (chaves.next()) {
					foto.setId(chaves.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao inserir a foto do imóvel de id " + foto.getIdImovel() + ".", e);
		}
	}

	public List<FotoImovel> listarPorImovel(int idImovel) throws DAOException {
		List<FotoImovel> fotos = new ArrayList<>();
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_POR_IMOVEL)) {

			comando.setInt(1, idImovel);
			try (ResultSet resultado = comando.executeQuery()) {
				while (resultado.next()) {
					fotos.add(montarFoto(resultado));
				}
			}
			return fotos;
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar as fotos do imóvel de id " + idImovel + ".", e);
		}
	}

	/**
	 * Carrega as fotos de um único imóvel, usado na página de detalhe.
	 */
	public void carregarFotos(Imovel imovel) throws DAOException {
		imovel.setFotos(listarPorImovel(imovel.getId()));
	}

	/**
	 * Carrega as fotos de vários imóveis em uma única consulta.
	 *
	 * Usado na montagem do feed: sem isso seria necessária uma consulta por
	 * imóvel exibido na listagem.
	 */
	public void carregarFotos(List<Imovel> imoveis) throws DAOException {
		if (imoveis == null || imoveis.isEmpty()) {
			return;
		}

		Map<Integer, Imovel> imoveisPorId = new HashMap<>();
		for (Imovel imovel : imoveis) {
			imovel.setFotos(new ArrayList<>());
			imoveisPorId.put(imovel.getId(), imovel);
		}

		String sql = """
				SELECT id, id_imovel, url_foto, ordem
				FROM foto_imovel
				WHERE id_imovel IN (%s)
				ORDER BY id_imovel, ordem
				""".formatted(montarPlaceholders(imoveisPorId.size()));

		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql)) {

			int posicao = 1;
			for (Integer idImovel : imoveisPorId.keySet()) {
				comando.setInt(posicao++, idImovel);
			}

			try (ResultSet resultado = comando.executeQuery()) {
				while (resultado.next()) {
					FotoImovel foto = montarFoto(resultado);
					Imovel imovel = imoveisPorId.get(foto.getIdImovel());
					if (imovel != null) {
						imovel.getFotos().add(foto);
					}
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao carregar as fotos dos imóveis.", e);
		}
	}

	/**
	 * @return a próxima posição livre no carrossel do imóvel
	 */
	public int proximaOrdem(int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_PROXIMA_ORDEM)) {

			comando.setInt(1, idImovel);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? resultado.getInt(1) : 0;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao calcular a ordem da foto do imóvel de id " + idImovel + ".", e);
		}
	}

	public void atualizarOrdem(int idFoto, int ordem) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_ATUALIZAR_ORDEM)) {

			comando.setInt(1, ordem);
			comando.setInt(2, idFoto);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao atualizar a ordem da foto de id " + idFoto + ".", e);
		}
	}

	public void remover(int id) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_REMOVER)) {

			comando.setInt(1, id);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao remover a foto de id " + id + ".", e);
		}
	}

	/**
	 * Remove todas as fotos de um imóvel. Precisa ser chamado antes de excluir
	 * o imóvel, por causa da chave estrangeira.
	 */
	public void removerPorImovel(int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_REMOVER_POR_IMOVEL)) {

			comando.setInt(1, idImovel);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao remover as fotos do imóvel de id " + idImovel + ".", e);
		}
	}

	/**
	 * Monta a sequência "?, ?, ?" usada na cláusula IN.
	 */
	private String montarPlaceholders(int quantidade) {
		return String.join(", ", Collections.nCopies(quantidade, "?"));
	}

	private FotoImovel montarFoto(ResultSet resultado) throws SQLException {
		FotoImovel foto = new FotoImovel();
		foto.setId(resultado.getInt("id"));
		foto.setIdImovel(resultado.getInt("id_imovel"));
		foto.setUrlFoto(resultado.getString("url_foto"));
		foto.setOrdem(resultado.getInt("ordem"));
		return foto;
	}
}
