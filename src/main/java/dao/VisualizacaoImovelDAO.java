package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Registra qual usuário já visualizou qual imóvel, pra que a contagem de
 * "Visualizações" (item 6.3 da revisão de UX) conte no máximo uma vez por
 * pessoa, não uma vez por acesso.
 *
 * A chave primária composta (id_usuario, id_imovel) faz o próprio banco
 * recusar duplicata — usamos INSERT IGNORE e olhamos a contagem de linhas
 * afetadas pra saber se foi a primeira vez (só nesse caso o chamador deve
 * incrementar o contador em imovel.visualizacoes).
 */
public class VisualizacaoImovelDAO {

	private static final String SQL_REGISTRAR = """
			INSERT IGNORE INTO visualizacao_imovel (id_usuario, id_imovel) VALUES (?, ?)
			""";

	/**
	 * @return true se essa é a primeira visualização desse usuário nesse
	 *         imóvel (linha nova gravada); false se ele já tinha visto antes
	 */
	public boolean registrarSePrimeiraVez(int idUsuario, int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_REGISTRAR)) {

			comando.setInt(1, idUsuario);
			comando.setInt(2, idImovel);
			int linhasAfetadas = comando.executeUpdate();
			return linhasAfetadas > 0;
		} catch (SQLException e) {
			throw new DAOException(
					"Erro ao registrar visualização do imóvel " + idImovel + " pelo usuário " + idUsuario + ".", e);
		}
	}
}
