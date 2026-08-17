package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.Imobiliaria;
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
			INSERT INTO usuario (nome, email, senha, cpf, cpf_valido, telefone, foto_perfil, creci,
			                    tipo_usuario, id_imobiliaria)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			""";

	/**
	 * Lista de colunas reaproveitada por outros DAOs que fazem JOIN com usuario.
	 * O apelido u precisa ser mantido nessas consultas.
	 */
	static final String COLUNAS = """
			u.id, u.nome, u.email, u.email_confirmado, u.senha, u.cpf, u.cpf_valido, u.telefone,
			u.telefone_confirmado, u.foto_perfil, u.creci, u.tipo_usuario, u.id_imobiliaria,
			u.consentimento_credito, u.data_cadastro
			""";

	private static final String SQL_SELECT_BASE = "SELECT " + COLUNAS + " FROM usuario u";

	private static final String SQL_BUSCAR_POR_ID = SQL_SELECT_BASE + " WHERE u.id = ?";

	private static final String SQL_BUSCAR_POR_EMAIL = SQL_SELECT_BASE + " WHERE u.email = ?";

	/**
	 * As colunas de imobiliaria recebem apelido próprio porque a consulta traz
	 * as duas tabelas juntas: sem isso, id/nome/email/telefone/data_cadastro
	 * colidiriam com as colunas de mesmo nome em usuario.
	 */
	private static final String SQL_BUSCAR_POR_ID_COM_IMOBILIARIA = "SELECT " + COLUNAS + """
			, im.id AS imob_id, im.nome AS imob_nome, im.codigo AS imob_codigo, im.cnpj AS imob_cnpj,
			im.telefone AS imob_telefone, im.email AS imob_email, im.cidade AS imob_cidade,
			im.estado AS imob_estado, im.ativa AS imob_ativa, im.data_cadastro AS imob_data_cadastro
			FROM usuario u
			LEFT JOIN imobiliaria im ON im.id = u.id_imobiliaria
			WHERE u.id = ?
			""";

	private static final String SQL_LISTAR = SQL_SELECT_BASE + " ORDER BY u.nome";

	private static final String SQL_ATUALIZAR = """
			UPDATE usuario
			SET nome = ?, email = ?, telefone = ?, foto_perfil = ?, tipo_usuario = ?
			WHERE id = ?
			""";

	private static final String SQL_ATUALIZAR_SENHA = "UPDATE usuario SET senha = ? WHERE id = ?";

	private static final String SQL_ATUALIZAR_CONSENTIMENTO_CREDITO = """
			UPDATE usuario SET consentimento_credito = ? WHERE id = ?
			""";

	private static final String SQL_CONFIRMAR_EMAIL = "UPDATE usuario SET email_confirmado = TRUE WHERE id = ?";

	private static final String SQL_CONFIRMAR_TELEFONE = "UPDATE usuario SET telefone_confirmado = TRUE WHERE id = ?";

	private static final String SQL_REMOVER = "DELETE FROM usuario WHERE id = ?";

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
			comando.setString(8, usuario.getCreci());
			comando.setString(9, ConversorEnum.paraBanco(usuario.getTipoUsuario()));
			comando.setObject(10, usuario.getIdImobiliaria());
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

	/**
	 * Usado na tela de perfil, para exibir o nome da imobiliária do vendedor.
	 */
	public Optional<Usuario> buscarPorIdComImobiliaria(int id) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_BUSCAR_POR_ID_COM_IMOBILIARIA)) {

			comando.setInt(1, id);
			try (ResultSet resultado = comando.executeQuery()) {
				if (!resultado.next()) {
					return Optional.empty();
				}
				Usuario usuario = montarUsuario(resultado);
				if (resultado.getObject("imob_id") != null) {
					Imobiliaria imobiliaria = new Imobiliaria();
					imobiliaria.setId(resultado.getInt("imob_id"));
					imobiliaria.setNome(resultado.getString("imob_nome"));
					imobiliaria.setCodigo(resultado.getString("imob_codigo"));
					imobiliaria.setCnpj(resultado.getString("imob_cnpj"));
					imobiliaria.setTelefone(resultado.getString("imob_telefone"));
					imobiliaria.setEmail(resultado.getString("imob_email"));
					imobiliaria.setCidade(resultado.getString("imob_cidade"));
					imobiliaria.setEstado(resultado.getString("imob_estado"));
					imobiliaria.setAtiva(resultado.getBoolean("imob_ativa"));
					imobiliaria.setDataCadastro(LeitorResultSet.lerDataHora(resultado, "imob_data_cadastro"));
					usuario.setImobiliaria(imobiliaria);
				}
				return Optional.of(usuario);
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar o usuário de id " + id + " com a imobiliária.", e);
		}
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
			comando.setString(2, usuario.getEmail());
			comando.setString(3, usuario.getTelefone());
			comando.setString(4, usuario.getFotoPerfil());
			comando.setString(5, ConversorEnum.paraBanco(usuario.getTipoUsuario()));
			comando.setInt(6, usuario.getId());
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao atualizar o usuário de id " + usuario.getId() + ".", e);
		}
	}

	/**
	 * @param senhaHash a senha já convertida em hash, nunca o texto puro
	 */
	public void atualizarSenha(int idUsuario, String senhaHash) throws DAOException {
		executarAtualizacao(SQL_ATUALIZAR_SENHA, "Erro ao atualizar a senha do usuário de id " + idUsuario + ".",
				senhaHash, idUsuario);
	}

	/**
	 * Grava a autorização (ou revogação) de consulta de crédito, ajustável nas
	 * configurações do perfil.
	 */
	public void atualizarConsentimentoCredito(int idUsuario, boolean autorizado) throws DAOException {
		executarAtualizacao(SQL_ATUALIZAR_CONSENTIMENTO_CREDITO,
				"Erro ao atualizar o consentimento de crédito do usuário de id " + idUsuario + ".",
				autorizado, idUsuario);
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
		usuario.setEmail(resultado.getString("email"));
		usuario.setEmailConfirmado(resultado.getBoolean("email_confirmado"));
		usuario.setSenha(resultado.getString("senha"));
		usuario.setCpf(resultado.getString("cpf"));
		usuario.setCpfValido(resultado.getBoolean("cpf_valido"));
		usuario.setTelefone(resultado.getString("telefone"));
		usuario.setTelefoneConfirmado(resultado.getBoolean("telefone_confirmado"));
		usuario.setFotoPerfil(resultado.getString("foto_perfil"));
		usuario.setCreci(resultado.getString("creci"));
		usuario.setTipoUsuario(ConversorEnum.paraEnum(TipoUsuario.class, resultado.getString("tipo_usuario")));
		usuario.setIdImobiliaria(LeitorResultSet.lerInteiro(resultado, "id_imobiliaria"));
		usuario.setConsentimentoCredito(resultado.getBoolean("consentimento_credito"));
		usuario.setDataCadastro(LeitorResultSet.lerDataHora(resultado, "data_cadastro"));
		return usuario;
	}
}
