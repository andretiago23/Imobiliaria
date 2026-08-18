package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.TipoUsuario;
import model.Usuario;
import util.ConversorEnum;
import util.LeitorResultSet;

/**
 * Acesso à tabela usuario.
 *
 * Todas as consultas usam PreparedStatement e todas as conexões são fechadas
 * automaticamente pelo try-with-resources.
 */
public class UsuarioDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO usuario
			    (nome, email, senha, cpf, cpf_valido, telefone, foto_perfil, tipo_usuario, id_imobiliaria, termos_aceitos_em)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			""";

	/**
	 * Lista de colunas reaproveitada por outros DAOs que fazem JOIN com usuario.
	 * O alias u precisa ser mantido nessas consultas.
	 */
	static final String COLUNAS = """
			u.id, u.nome, u.apelido, u.email, u.email_confirmado, u.senha, u.cpf, u.cpf_valido, u.telefone,
			u.telefone_confirmado, u.foto_perfil, u.tipo_usuario, u.id_imobiliaria, u.data_cadastro, u.termos_aceitos_em
			""";

	private static final String SQL_SELECT_BASE = "SELECT " + COLUNAS + " FROM usuario u";

	private static final String SQL_BUSCAR_POR_ID = SQL_SELECT_BASE + " WHERE u.id = ?";

	private static final String SQL_BUSCAR_POR_EMAIL = SQL_SELECT_BASE + " WHERE u.email = ?";

	private static final String SQL_LISTAR = SQL_SELECT_BASE + " ORDER BY u.nome";

	private static final String SQL_ATUALIZAR = """
			UPDATE usuario
			SET nome = ?, apelido = ?, email = ?, telefone = ?, foto_perfil = ?, tipo_usuario = ?
			WHERE id = ?
			""";

	private static final String SQL_ATUALIZAR_SENHA = "UPDATE usuario SET senha = ? WHERE id = ?";

	private static final String SQL_ATUALIZAR_FOTO_PERFIL = "UPDATE usuario SET foto_perfil = ? WHERE id = ?";

	private static final String SQL_CONFIRMAR_EMAIL = "UPDATE usuario SET email_confirmado = TRUE WHERE id = ?";

	private static final String SQL_CONFIRMAR_TELEFONE = "UPDATE usuario SET telefone_confirmado = TRUE WHERE id = ?";

	private static final String SQL_REMOVER = "DELETE FROM usuario WHERE id = ?";

	/**
	 * Anonimiza os dados de identificação diretos, preservando a linha (e as
	 * chaves estrangeiras que outras tabelas mantêm com ela — imóveis,
	 * interesses, avaliações) como histórico. É assim que o "excluir minha
	 * conta" da LGPD é atendido sem quebrar integridade referencial nem apagar
	 * o histórico de negociações de terceiros.
	 */
	private static final String SQL_ANONIMIZAR = """
			UPDATE usuario
			SET nome = 'Usuário removido', apelido = NULL, email = ?, senha = ?,
			    telefone = NULL, foto_perfil = NULL
			WHERE id = ?
			""";

	private static final String SQL_REGISTRAR_ACEITE_TERMOS =
			"UPDATE usuario SET termos_aceitos_em = ? WHERE id = ?";

	private static final String SQL_CONTAR_POR_EMAIL = "SELECT COUNT(*) FROM usuario WHERE email = ?";

	private static final String SQL_CONTAR_POR_CPF = "SELECT COUNT(*) FROM usuario WHERE cpf = ?";

	/**
	 * Grava um novo usuário e preenche o id gerado pelo banco no próprio objeto.
	 */
	public void inserir(Usuario usuario) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

			comando.setString(1, usuario.getNome());
			comando.setString(2, usuario.getEmail());
			comando.setString(3, usuario.getSenha());
			comando.setString(4, usuario.getCpf());
			comando.setBoolean(5, usuario.isCpfValido());
			comando.setString(6, usuario.getTelefone());
			comando.setString(7, usuario.getFotoPerfil());
			comando.setString(8, ConversorEnum.paraBanco(usuario.getTipoUsuario()));
			comando.setObject(9, usuario.getIdImobiliaria());
			comando.setObject(10, usuario.getTermosAceitosEm());
			comando.executeUpdate();

			try (ResultSet chaves = comando.getGeneratedKeys()) {
				if (chaves.next()) {
					usuario.setId(chaves.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao inserir o usuário de e-mail " + usuario.getEmail() + ".", e);
		}
	}

	public Optional<Usuario> buscarPorId(int id) throws DAOException {
		return buscarPorParametro(SQL_BUSCAR_POR_ID, id);
	}

	/**
	 * Usado na autenticação: recupera o usuário para comparação do hash da senha.
	 */
	public Optional<Usuario> buscarPorEmail(String email) throws DAOException {
		return buscarPorParametro(SQL_BUSCAR_POR_EMAIL, email);
	}

	public List<Usuario> listar() throws DAOException {
		List<Usuario> usuarios = new ArrayList<>();
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR);
				ResultSet resultado = comando.executeQuery()) {

			while (resultado.next()) {
				usuarios.add(montarUsuario(resultado));
			}
			return usuarios;
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar os usuários.", e);
		}
	}

	/**
	 * Atualiza apenas os dados de perfil. A senha tem método próprio.
	 */
	public void atualizar(Usuario usuario) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_ATUALIZAR)) {

			comando.setString(1, usuario.getNome());
			comando.setString(2, usuario.getApelido());
			comando.setString(3, usuario.getEmail());
			comando.setString(4, usuario.getTelefone());
			comando.setString(5, usuario.getFotoPerfil());
			comando.setString(6, ConversorEnum.paraBanco(usuario.getTipoUsuario()));
			comando.setInt(7, usuario.getId());
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao atualizar o usuário de id " + usuario.getId() + ".", e);
		}
	}

	/**
	 * Atualiza só a foto de perfil, sem precisar recarregar e regravar o
	 * restante dos dados do usuário.
	 */
	public void atualizarFotoPerfil(int idUsuario, String caminhoFoto) throws DAOException {
		executarAtualizacao(SQL_ATUALIZAR_FOTO_PERFIL,
				"Erro ao atualizar a foto de perfil do usuário de id " + idUsuario + ".", caminhoFoto, idUsuario);
	}

	/**
	 * @param senhaHash a senha já convertida em hash, nunca o texto puro
	 */
	public void atualizarSenha(int idUsuario, String senhaHash) throws DAOException {
		executarAtualizacao(SQL_ATUALIZAR_SENHA, "Erro ao atualizar a senha do usuário de id " + idUsuario + ".",
				senhaHash, idUsuario);
	}

	public void confirmarEmail(int idUsuario) throws DAOException {
		executarAtualizacao(SQL_CONFIRMAR_EMAIL, "Erro ao confirmar o e-mail do usuário de id " + idUsuario + ".",
				idUsuario);
	}

	public void confirmarTelefone(int idUsuario) throws DAOException {
		executarAtualizacao(SQL_CONFIRMAR_TELEFONE, "Erro ao confirmar o telefone do usuário de id " + idUsuario + ".",
				idUsuario);
	}

	public void remover(int id) throws DAOException {
		executarAtualizacao(SQL_REMOVER, "Erro ao remover o usuário de id " + id + ".", id);
	}

	/**
	 * @param emailPlaceholder e-mail único e não reaproveitável para liberar o
	 *                         e-mail original para um futuro cadastro
	 * @param senhaHash        hash de uma senha aleatória: a conta some do login
	 */
	public void anonimizar(int idUsuario, String emailPlaceholder, String senhaHash) throws DAOException {
		executarAtualizacao(SQL_ANONIMIZAR, "Erro ao anonimizar o usuário de id " + idUsuario + ".",
				emailPlaceholder, senhaHash, idUsuario);
	}

	public void registrarAceiteTermos(int idUsuario, java.time.LocalDateTime momento) throws DAOException {
		executarAtualizacao(SQL_REGISTRAR_ACEITE_TERMOS,
				"Erro ao registrar o aceite dos termos do usuário de id " + idUsuario + ".", momento, idUsuario);
	}

	/**
	 * Verificação usada no cadastro, já que a coluna email é UNIQUE.
	 */
	public boolean emailJaCadastrado(String email) throws DAOException {
		return contar(SQL_CONTAR_POR_EMAIL, email) > 0;
	}

	/**
	 * Verificação usada no cadastro, já que a coluna cpf é UNIQUE.
	 */
	public boolean cpfJaCadastrado(String cpf) throws DAOException {
		return contar(SQL_CONTAR_POR_CPF, cpf) > 0;
	}

	private Optional<Usuario> buscarPorParametro(String sql, Object parametro) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql)) {

			comando.setObject(1, parametro);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? Optional.of(montarUsuario(resultado)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar o usuário.", e);
		}
	}

	private int contar(String sql, Object parametro) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql)) {

			comando.setObject(1, parametro);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? resultado.getInt(1) : 0;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao consultar a tabela usuario.", e);
		}
	}

	private void executarAtualizacao(String sql, String mensagemErro, Object... parametros) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql)) {

			for (int posicao = 0; posicao < parametros.length; posicao++) {
				comando.setObject(posicao + 1, parametros[posicao]);
			}
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException(mensagemErro, e);
		}
	}

	/**
	 * Converte a linha atual do ResultSet em um objeto Usuario.
	 *
	 * Visível no pacote para que os demais DAOs possam reaproveitar a montagem
	 * em consultas com JOIN, desde que usem as colunas da constante COLUNAS.
	 */
	static Usuario montarUsuario(ResultSet resultado) throws SQLException {
		Usuario usuario = new Usuario();
		usuario.setId(resultado.getInt("id"));
		usuario.setNome(resultado.getString("nome"));
		usuario.setApelido(resultado.getString("apelido"));
		usuario.setEmail(resultado.getString("email"));
		usuario.setEmailConfirmado(resultado.getBoolean("email_confirmado"));
		usuario.setSenha(resultado.getString("senha"));
		usuario.setCpf(resultado.getString("cpf"));
		usuario.setCpfValido(resultado.getBoolean("cpf_valido"));
		usuario.setTelefone(resultado.getString("telefone"));
		usuario.setTelefoneConfirmado(resultado.getBoolean("telefone_confirmado"));
		usuario.setFotoPerfil(resultado.getString("foto_perfil"));
		usuario.setTipoUsuario(ConversorEnum.paraEnum(TipoUsuario.class, resultado.getString("tipo_usuario")));
		usuario.setIdImobiliaria(LeitorResultSet.lerInteiro(resultado, "id_imobiliaria"));
		usuario.setDataCadastro(LeitorResultSet.lerDataHora(resultado, "data_cadastro"));
		usuario.setTermosAceitosEm(LeitorResultSet.lerDataHora(resultado, "termos_aceitos_em"));
		return usuario;
	}
}
