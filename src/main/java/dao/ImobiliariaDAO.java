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
import util.LeitorResultSet;

/**
 * Acesso à tabela imobiliaria.
 *
 * A coluna codigo é UNIQUE: é o "convite" que o vendedor digita no cadastro
 * para provar o vínculo com a imobiliária, sem nenhum processo externo.
 */
public class ImobiliariaDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO imobiliaria (nome, codigo, cnpj, telefone, email, cidade, estado, ativa)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?)
			""";

	static final String COLUNAS = """
			im.id, im.nome, im.codigo, im.cnpj, im.telefone, im.email, im.cidade, im.estado,
			im.ativa, im.data_cadastro
			""";

	private static final String SQL_SELECT_BASE = "SELECT " + COLUNAS + " FROM imobiliaria im";

	private static final String SQL_BUSCAR_POR_ID = SQL_SELECT_BASE + " WHERE im.id = ?";

	private static final String SQL_BUSCAR_POR_CODIGO = SQL_SELECT_BASE + " WHERE im.codigo = ?";

	private static final String SQL_LISTAR_ATIVAS = SQL_SELECT_BASE + " WHERE im.ativa = TRUE ORDER BY im.nome";

	private static final String SQL_EXISTE_CODIGO = "SELECT COUNT(*) FROM imobiliaria WHERE codigo = ?";

	/**
	 * Cadastra uma nova imobiliária e preenche o id gerado no próprio objeto.
	 *
	 * O código já deve ter sido gerado e conferido como único pelo chamador
	 * (ver ImobiliariaServico), este método só grava.
	 */
	public void inserir(Imobiliaria imobiliaria) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

			comando.setString(1, imobiliaria.getNome());
			comando.setString(2, imobiliaria.getCodigo());
			comando.setString(3, imobiliaria.getCnpj());
			comando.setString(4, imobiliaria.getTelefone());
			comando.setString(5, imobiliaria.getEmail());
			comando.setString(6, imobiliaria.getCidade());
			comando.setString(7, imobiliaria.getEstado());
			comando.setBoolean(8, imobiliaria.isAtiva());
			comando.executeUpdate();

			try (ResultSet chaves = comando.getGeneratedKeys()) {
				if (chaves.next()) {
					imobiliaria.setId(chaves.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao cadastrar a imobiliária " + imobiliaria.getNome() + ".", e);
		}
	}

	public Optional<Imobiliaria> buscarPorId(int id) throws DAOException {
		return buscarPorParametro(SQL_BUSCAR_POR_ID, id);
	}

	/**
	 * Usado no cadastro do vendedor, para conferir o código digitado.
	 */
	public Optional<Imobiliaria> buscarPorCodigo(String codigo) throws DAOException {
		return buscarPorParametro(SQL_BUSCAR_POR_CODIGO, codigo);
	}

	public List<Imobiliaria> listarAtivas() throws DAOException {
		List<Imobiliaria> imobiliarias = new ArrayList<>();
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_ATIVAS);
				ResultSet resultado = comando.executeQuery()) {

			while (resultado.next()) {
				imobiliarias.add(montarImobiliaria(resultado));
			}
			return imobiliarias;
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar as imobiliárias.", e);
		}
	}

	/**
	 * Usado ao gerar um novo código aleatório, para garantir que ele ainda não existe.
	 */
	public boolean existeCodigo(String codigo) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_EXISTE_CODIGO)) {

			comando.setString(1, codigo);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() && resultado.getInt(1) > 0;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao verificar o código da imobiliária.", e);
		}
	}

	private Optional<Imobiliaria> buscarPorParametro(String sql, Object parametro) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql)) {

			comando.setObject(1, parametro);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? Optional.of(montarImobiliaria(resultado)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar a imobiliária.", e);
		}
	}

	/**
	 * Visível no pacote para que UsuarioDAO e ImovelDAO possam reaproveitar a
	 * montagem em consultas com JOIN, desde que usem as colunas de COLUNAS.
	 */
	static Imobiliaria montarImobiliaria(ResultSet resultado) throws SQLException {
		Imobiliaria imobiliaria = new Imobiliaria();
		imobiliaria.setId(resultado.getInt("id"));
		imobiliaria.setNome(resultado.getString("nome"));
		imobiliaria.setCodigo(resultado.getString("codigo"));
		imobiliaria.setCnpj(resultado.getString("cnpj"));
		imobiliaria.setTelefone(resultado.getString("telefone"));
		imobiliaria.setEmail(resultado.getString("email"));
		imobiliaria.setCidade(resultado.getString("cidade"));
		imobiliaria.setEstado(resultado.getString("estado"));
		imobiliaria.setAtiva(resultado.getBoolean("ativa"));
		imobiliaria.setDataCadastro(LeitorResultSet.lerDataHora(resultado, "data_cadastro"));
		return imobiliaria;
	}
}
