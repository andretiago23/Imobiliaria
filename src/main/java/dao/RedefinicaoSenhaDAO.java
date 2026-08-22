package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Acesso à tabela redefinicao_senha: o token de uso único enviado no e-mail
 * de "esqueci minha senha" (ver controller.EsqueciSenhaServlet e
 * controller.RedefinirSenhaServlet). Mesmo padrão de dao.ConfirmacaoStatusDAO
 * (token de uso único ligado a uma data de envio, sem tabela de sessão à
 * parte).
 */
public class RedefinicaoSenhaDAO {

	/** Um link de redefinição vale por 2 horas a partir do envio. */
	private static final int HORAS_VALIDADE = 2;

	private static final String SQL_INSERIR = """
			INSERT INTO redefinicao_senha (id_usuario, token) VALUES (?, ?)
			""";

	private static final String SQL_BUSCAR_VALIDO_POR_TOKEN = """
			SELECT id, id_usuario FROM redefinicao_senha
			WHERE token = ? AND usado = 0 AND data_envio >= (NOW() - INTERVAL """ + HORAS_VALIDADE + """
			 HOUR)
			""";

	private static final String SQL_MARCAR_USADO = "UPDATE redefinicao_senha SET usado = 1 WHERE id = ?";

	private static final String SQL_INVALIDAR_PENDENTES_DO_USUARIO =
			"UPDATE redefinicao_senha SET usado = 1 WHERE id_usuario = ? AND usado = 0";

	/**
	 * Grava um novo token de redefinição, invalidando primeiro qualquer
	 * outro ainda não usado desse mesmo usuário — só o link mais recente do
	 * e-mail funciona.
	 */
	public void criar(int idUsuario, String token) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao()) {
			try (PreparedStatement invalidar = conexao.prepareStatement(SQL_INVALIDAR_PENDENTES_DO_USUARIO)) {
				invalidar.setInt(1, idUsuario);
				invalidar.executeUpdate();
			}
			try (PreparedStatement inserir = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {
				inserir.setInt(1, idUsuario);
				inserir.setString(2, token);
				inserir.executeUpdate();
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao criar o token de redefinição de senha do usuário " + idUsuario + ".", e);
		}
	}

	/**
	 * @return {id da redefinição, id do usuário}, só se o token existir,
	 *         ainda não tiver sido usado e ainda estiver dentro da validade
	 */
	public Optional<int[]> buscarValidoPorToken(String token) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_BUSCAR_VALIDO_POR_TOKEN)) {

			comando.setString(1, token);
			try (ResultSet resultado = comando.executeQuery()) {
				if (!resultado.next()) {
					return Optional.empty();
				}
				return Optional.of(new int[] { resultado.getInt("id"), resultado.getInt("id_usuario") });
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar o token de redefinição de senha.", e);
		}
	}

	public void marcarUsado(int idRedefinicao) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_MARCAR_USADO)) {

			comando.setInt(1, idRedefinicao);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao marcar a redefinição " + idRedefinicao + " como usada.", e);
		}
	}
}
