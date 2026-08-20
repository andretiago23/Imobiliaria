package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.VideoImovel;

/**
 * Acesso à tabela video_imovel — espelha FotoImovelDAO.
 */
public class VideoImovelDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO video_imovel (id_imovel, url_video, ordem)
			VALUES (?, ?, ?)
			""";

	private static final String SQL_LISTAR_POR_IMOVEL = """
			SELECT id, id_imovel, url_video, ordem
			FROM video_imovel
			WHERE id_imovel = ?
			ORDER BY ordem
			""";

	private static final String SQL_REMOVER_POR_IMOVEL = "DELETE FROM video_imovel WHERE id_imovel = ?";

	public void inserir(VideoImovel video) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

			comando.setInt(1, video.getIdImovel());
			comando.setString(2, video.getUrlVideo());
			comando.setInt(3, video.getOrdem());
			comando.executeUpdate();

			try (ResultSet chaves = comando.getGeneratedKeys()) {
				if (chaves.next()) {
					video.setId(chaves.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao inserir o vídeo do imóvel de id " + video.getIdImovel() + ".", e);
		}
	}

	public List<VideoImovel> listarPorImovel(int idImovel) throws DAOException {
		List<VideoImovel> videos = new ArrayList<>();
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_POR_IMOVEL)) {

			comando.setInt(1, idImovel);
			try (ResultSet resultado = comando.executeQuery()) {
				while (resultado.next()) {
					VideoImovel video = new VideoImovel();
					video.setId(resultado.getInt("id"));
					video.setIdImovel(resultado.getInt("id_imovel"));
					video.setUrlVideo(resultado.getString("url_video"));
					video.setOrdem(resultado.getInt("ordem"));
					videos.add(video);
				}
			}
			return videos;
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar os vídeos do imóvel de id " + idImovel + ".", e);
		}
	}

	/**
	 * Remove todos os vídeos de um imóvel. Precisa ser chamado antes de
	 * excluir o imóvel, por causa da chave estrangeira.
	 */
	public void removerPorImovel(int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_REMOVER_POR_IMOVEL)) {

			comando.setInt(1, idImovel);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao remover os vídeos do imóvel de id " + idImovel + ".", e);
		}
	}
}
