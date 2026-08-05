package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Usuario;

/**
 * Acesso à tabela seguidor.
 *
 * A tabela tem restrição UNIQUE (id_seguidor, id_seguido), então não é
 * possível seguir a mesma pessoa duas vezes.
 */
public class SeguidorDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO seguidor (id_seguidor, id_seguido)
			VALUES (?, ?)
			""";

	private static final String SQL_REMOVER = """
			DELETE FROM seguidor
			WHERE id_seguidor = ? AND id_seguido = ?
			""";

	private static final String SQL_EXISTE = """
			SELECT COUNT(*)
			FROM seguidor
			WHERE id_seguidor = ? AND id_seguido = ?
			""";

	/** Quem segue o usuário informado. */
	private static final String SQL_LISTAR_SEGUIDORES = "SELECT " + UsuarioDAO.COLUNAS + """
			FROM seguidor s
			JOIN usuario u ON u.id = s.id_seguidor
			WHERE s.id_seguido = ?
			ORDER BY s.data_inicio DESC
			""";

	/** Quem o usuário informado segue. */
	private static final String SQL_LISTAR_SEGUINDO = "SELECT " + UsuarioDAO.COLUNAS + """
			FROM seguidor s
			JOIN usuario u ON u.id = s.id_seguido
			WHERE s.id_seguidor = ?
			ORDER BY s.data_inicio DESC
			""";

	private static final String SQL_CONTAR_SEGUIDORES = "SELECT COUNT(*) FROM seguidor WHERE id_seguido = ?";

	private static final String SQL_CONTAR_SEGUINDO = "SELECT COUNT(*) FROM seguidor WHERE id_seguidor = ?";

	/**
	 * Passa a seguir um usuário. Não faz nada se já o seguia.
	 *
	 * @throws IllegalArgumentException se o usuário tentar seguir a si mesmo
	 */
	public void seguir(int idSeguidor, int idSeguido) throws DAOException {
		if (idSeguidor == idSeguido) {
			throw new IllegalArgumentException("Um usuário não pode seguir a si mesmo.");
		}
		if (segue(idSeguidor, idSeguido)) {
			return;
		}
		executar(SQL_INSERIR, "Erro ao seguir o usuário de id " + idSeguido + ".", idSeguidor, idSeguido);
	}

	public void deixarDeSeguir(int idSeguidor, int idSeguido) throws DAOException {
		executar(SQL_REMOVER, "Erro ao deixar de seguir o usuário de id " + idSeguido + ".", idSeguidor, idSeguido);
	}

	/**
	 * Inverte o estado do relacionamento, comportamento do botão Seguir.
	 *
	 * @return true se passou a seguir, false se deixou de seguir
	 */
	public boolean alternar(int idSeguidor, int idSeguido) throws DAOException {
		if (segue(idSeguidor, idSeguido)) {
			deixarDeSeguir(idSeguidor, idSeguido);
			return false;
		}
		seguir(idSeguidor, idSeguido);
		return true;
	}

	public boolean segue(int idSeguidor, int idSeguido) throws DAOException {
		return contar(SQL_EXISTE, "Erro ao verificar o relacionamento.", idSeguidor, idSeguido) > 0;
	}

	public List<Usuario> listarSeguidores(int idUsuario) throws DAOException {
		return listarUsuarios(SQL_LISTAR_SEGUIDORES,
				"Erro ao listar os seguidores do usuário de id " + idUsuario + ".", idUsuario);
	}

	public List<Usuario> listarSeguindo(int idUsuario) throws DAOException {
		return listarUsuarios(SQL_LISTAR_SEGUINDO,
				"Erro ao listar quem o usuário de id " + idUsuario + " segue.", idUsuario);
	}

	public int contarSeguidores(int idUsuario) throws DAOException {
		return contar(SQL_CONTAR_SEGUIDORES, "Erro ao contar os seguidores do usuário de id " + idUsuario + ".",
				idUsuario);
	}

	public int contarSeguindo(int idUsuario) throws DAOException {
		return contar(SQL_CONTAR_SEGUINDO, "Erro ao contar quem o usuário de id " + idUsuario + " segue.", idUsuario);
	}

	private List<Usuario> listarUsuarios(String sql, String mensagemErro, int idUsuario) throws DAOException {
		List<Usuario> usuarios = new ArrayList<>();
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql)) {

			comando.setInt(1, idUsuario);
			try (ResultSet resultado = comando.executeQuery()) {
				while (resultado.next()) {
					usuarios.add(UsuarioDAO.montarUsuario(resultado));
				}
			}
			return usuarios;
		} catch (SQLException e) {
			throw new DAOException(mensagemErro, e);
		}
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
