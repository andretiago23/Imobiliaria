package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.BuscaSalva;
import model.Finalidade;
import model.TipoImovel;
import util.ConversorEnum;
import util.LeitorResultSet;

/**
 * Acesso à tabela busca_salva.
 *
 * O disparo do alerta por e-mail em si não faz parte deste protótipo (ver
 * comentário em model.BuscaSalva); este DAO só guarda os critérios e o
 * opt-in do cliente.
 */
public class BuscaSalvaDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO busca_salva (id_usuario, nome, tipo, finalidade, cidade, quartos_minimo,
			                        preco_maximo, alerta_ativo)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?)
			""";

	private static final String SQL_SELECT_BASE = """
			SELECT id, id_usuario, nome, tipo, finalidade, cidade, quartos_minimo, preco_maximo,
			       alerta_ativo, data_criacao
			FROM busca_salva
			""";

	private static final String SQL_LISTAR_POR_USUARIO = SQL_SELECT_BASE
			+ " WHERE id_usuario = ? ORDER BY data_criacao DESC";

	private static final String SQL_BUSCAR_POR_ID = SQL_SELECT_BASE + " WHERE id = ?";

	private static final String SQL_ATUALIZAR_ALERTA = "UPDATE busca_salva SET alerta_ativo = ? WHERE id = ?";

	private static final String SQL_REMOVER = "DELETE FROM busca_salva WHERE id = ?";

	public void inserir(BuscaSalva busca) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

			comando.setInt(1, busca.getIdUsuario());
			comando.setString(2, busca.getNome());
			comando.setString(3, ConversorEnum.paraBanco(busca.getTipo()));
			comando.setString(4, ConversorEnum.paraBanco(busca.getFinalidade()));
			comando.setString(5, busca.getCidade());
			comando.setObject(6, busca.getQuartosMinimo());
			comando.setBigDecimal(7, busca.getPrecoMaximo());
			comando.setBoolean(8, busca.isAlertaAtivo());
			comando.executeUpdate();

			try (ResultSet chaves = comando.getGeneratedKeys()) {
				if (chaves.next()) {
					busca.setId(chaves.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao salvar a busca do usuário de id " + busca.getIdUsuario() + ".", e);
		}
	}

	public List<BuscaSalva> listarPorUsuario(int idUsuario) throws DAOException {
		List<BuscaSalva> buscas = new ArrayList<>();
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_POR_USUARIO)) {

			comando.setInt(1, idUsuario);
			try (ResultSet resultado = comando.executeQuery()) {
				while (resultado.next()) {
					buscas.add(montarBusca(resultado));
				}
			}
			return buscas;
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar as buscas salvas do usuário de id " + idUsuario + ".", e);
		}
	}

	public java.util.Optional<BuscaSalva> buscarPorId(int id) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_BUSCAR_POR_ID)) {

			comando.setInt(1, id);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? java.util.Optional.of(montarBusca(resultado)) : java.util.Optional.empty();
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar a busca salva de id " + id + ".", e);
		}
	}

	/**
	 * Usado nos botões "pausar alerta" e "reativar alerta" da tela de minhas buscas.
	 */
	public void atualizarAlerta(int id, boolean alertaAtivo) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_ATUALIZAR_ALERTA)) {

			comando.setBoolean(1, alertaAtivo);
			comando.setInt(2, id);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao atualizar o alerta da busca de id " + id + ".", e);
		}
	}

	public void remover(int id) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_REMOVER)) {

			comando.setInt(1, id);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao excluir a busca salva de id " + id + ".", e);
		}
	}

	private BuscaSalva montarBusca(ResultSet resultado) throws SQLException {
		BuscaSalva busca = new BuscaSalva();
		busca.setId(resultado.getInt("id"));
		busca.setIdUsuario(resultado.getInt("id_usuario"));
		busca.setNome(resultado.getString("nome"));
		busca.setTipo(ConversorEnum.paraEnum(TipoImovel.class, resultado.getString("tipo")));
		busca.setFinalidade(ConversorEnum.paraEnum(Finalidade.class, resultado.getString("finalidade")));
		busca.setCidade(resultado.getString("cidade"));
		busca.setQuartosMinimo(LeitorResultSet.lerInteiro(resultado, "quartos_minimo"));
		busca.setPrecoMaximo(resultado.getBigDecimal("preco_maximo"));
		busca.setAlertaAtivo(resultado.getBoolean("alerta_ativo"));
		busca.setDataCriacao(LeitorResultSet.lerDataHora(resultado, "data_criacao"));
		return busca;
	}
}
