package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Acesso à tabela confirmacao_status: o token de uso único enviado no
 * e-mail "ainda está disponível?" disparado pelo job agendado
 * (util.AgendadorStatusImovel) a cada imóvel sem atualização há 15 dias.
 */
public class ConfirmacaoStatusDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO confirmacao_status (id_imovel, token) VALUES (?, ?)
			""";

	private static final String SQL_BUSCAR_POR_TOKEN = """
			SELECT id, id_imovel, token, data_envio, respondido FROM confirmacao_status WHERE token = ?
			""";

	private static final String SQL_MARCAR_RESPONDIDO = "UPDATE confirmacao_status SET respondido = 1 WHERE id = ?";

	/**
	 * Já existe uma confirmação pendente (ainda sem resposta) para este
	 * imóvel — evita mandar um segundo e-mail de confirmação por dia
	 * enquanto o primeiro ainda não expirou.
	 */
	private static final String SQL_EXISTE_PENDENTE = """
			SELECT COUNT(*) FROM confirmacao_status WHERE id_imovel = ? AND respondido = 0
			""";

	/**
	 * Confirmações enviadas há mais de :dias e ainda sem resposta — usadas
	 * pelo job para decidir quais imóveis viram PENDENTE_CONFIRMACAO.
	 */
	private static final String SQL_LISTAR_EXPIRADAS = """
			SELECT id, id_imovel, token, data_envio, respondido FROM confirmacao_status
			WHERE respondido = 0 AND data_envio <= (NOW() - INTERVAL ? DAY)
			""";

	public String criar(int idImovel, String token) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR)) {

			comando.setInt(1, idImovel);
			comando.setString(2, token);
			comando.executeUpdate();
			return token;
		} catch (SQLException e) {
			throw new DAOException("Erro ao criar a confirmação de status do imóvel " + idImovel + ".", e);
		}
	}

	public boolean existePendente(int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_EXISTE_PENDENTE)) {

			comando.setInt(1, idImovel);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() && resultado.getInt(1) > 0;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao verificar confirmações pendentes do imóvel " + idImovel + ".", e);
		}
	}

	public Optional<int[]> buscarIdImovelPorToken(String token) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_BUSCAR_POR_TOKEN)) {

			comando.setString(1, token);
			try (ResultSet resultado = comando.executeQuery()) {
				if (!resultado.next()) {
					return Optional.empty();
				}
				return Optional.of(new int[] { resultado.getInt("id"), resultado.getInt("id_imovel") });
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar a confirmação de status pelo token.", e);
		}
	}

	public void marcarRespondido(int idConfirmacao) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_MARCAR_RESPONDIDO)) {

			comando.setInt(1, idConfirmacao);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao marcar a confirmação " + idConfirmacao + " como respondida.", e);
		}
	}

	/**
	 * @return o id_imovel de cada confirmação enviada há mais de "dias" e
	 *         ainda sem resposta
	 */
	public java.util.List<Integer> listarImoveisComConfirmacaoExpirada(int dias) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_EXPIRADAS)) {

			comando.setInt(1, dias);
			try (ResultSet resultado = comando.executeQuery()) {
				java.util.List<Integer> ids = new java.util.ArrayList<>();
				while (resultado.next()) {
					ids.add(resultado.getInt("id_imovel"));
				}
				return ids;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar confirmações expiradas.", e);
		}
	}
}
