package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

	private static final String SQL_INSERIR = "INSERT INTO imobiliaria (nome, codigo, ativa) VALUES (?, ?, ?)";

	private static final String SQL_SELECT_BASE =
			"SELECT id, nome, codigo, ativa, data_cadastro FROM imobiliaria";

	private static final String SQL_BUSCAR_POR_CODIGO = SQL_SELECT_BASE + " WHERE codigo = ?";

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
			comando.setBoolean(3, imobiliaria.isAtiva());
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

	/**
	 * Usado no cadastro do vendedor, para conferir o código informado.
	 */
	public Optional<Imobiliaria> buscarPorCodigo(String codigo) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_BUSCAR_POR_CODIGO)) {

			comando.setString(1, codigo);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? Optional.of(montarImobiliaria(resultado)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar a imobiliária.", e);
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

	private static Imobiliaria montarImobiliaria(ResultSet resultado) throws SQLException {
		Imobiliaria imobiliaria = new Imobiliaria();
		imobiliaria.setId(resultado.getInt("id"));
		imobiliaria.setNome(resultado.getString("nome"));
		imobiliaria.setCodigo(resultado.getString("codigo"));
		imobiliaria.setAtiva(resultado.getBoolean("ativa"));
		imobiliaria.setDataCadastro(LeitorResultSet.lerDataHora(resultado, "data_cadastro"));
		return imobiliaria;
	}
}
