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
 * Acesso à tabela busca_salva e ao seu par busca_salva_notificacao, que evita
 * mandar o mesmo alerta duas vezes para o mesmo cliente sobre o mesmo imóvel.
 */
public class BuscaSalvaDAO {

	private static final String COLUNAS = """
			id, id_usuario, nome, tipo, finalidade, cidade, quartos_minimo,
			preco_maximo, alerta_ativo, data_criacao
			""";

	private static final String SQL_INSERIR = """
			INSERT INTO busca_salva (id_usuario, nome, tipo, finalidade, cidade, quartos_minimo,
			                         preco_maximo, alerta_ativo)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?)
			""";

	private static final String SQL_LISTAR_POR_USUARIO =
			"SELECT " + COLUNAS + " FROM busca_salva WHERE id_usuario = ? ORDER BY data_criacao DESC";

	/**
	 * Só buscas com o alerta ligado entram na verificação disparada quando um
	 * imóvel novo é publicado — evita consultar (e filtrar em memória) buscas
	 * que a pessoa só guardou como atalho, sem pedir para ser avisada.
	 */
	private static final String SQL_LISTAR_COM_ALERTA_ATIVO =
			"SELECT " + COLUNAS + " FROM busca_salva WHERE alerta_ativo = TRUE";

	private static final String SQL_ATUALIZAR_ALERTA = "UPDATE busca_salva SET alerta_ativo = ? WHERE id = ? AND id_usuario = ?";

	private static final String SQL_REMOVER = "DELETE FROM busca_salva WHERE id = ? AND id_usuario = ?";

	private static final String SQL_JA_NOTIFICADO =
			"SELECT COUNT(*) FROM busca_salva_notificacao WHERE id_busca_salva = ? AND id_imovel = ?";

	private static final String SQL_REGISTRAR_NOTIFICACAO =
			"INSERT INTO busca_salva_notificacao (id_busca_salva, id_imovel) VALUES (?, ?)";

	private static final String SQL_REMOVER_NOTIFICACOES_POR_IMOVEL =
			"DELETE FROM busca_salva_notificacao WHERE id_imovel = ?";

	/** Usado ao excluir um imóvel por completo (ImovelServico.excluir). */
	public void removerPorImovel(int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_REMOVER_NOTIFICACOES_POR_IMOVEL)) {

			comando.setInt(1, idImovel);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao remover as notificações de busca salva do imóvel " + idImovel + ".", e);
		}
	}

	public void inserir(BuscaSalva busca) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

			comando.setInt(1, busca.getIdUsuario());
			comando.setString(2, busca.getNome());
			comando.setString(3, busca.getTipo() == null ? null : ConversorEnum.paraBanco(busca.getTipo()));
			comando.setString(4, busca.getFinalidade() == null ? null : ConversorEnum.paraBanco(busca.getFinalidade()));
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
			throw new DAOException("Erro ao salvar a busca do usuário " + busca.getIdUsuario() + ".", e);
		}
	}

	public List<BuscaSalva> listarPorUsuario(int idUsuario) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_POR_USUARIO)) {

			comando.setInt(1, idUsuario);
			return executarConsulta(comando);
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar as buscas salvas do usuário " + idUsuario + ".", e);
		}
	}

	/**
	 * Usado logo após um imóvel entrar no catálogo (status disponível), para
	 * descobrir quais buscas salvas devem receber um alerta por e-mail.
	 */
	public List<BuscaSalva> listarComAlertaAtivo() throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_COM_ALERTA_ATIVO);
				ResultSet resultado = comando.executeQuery()) {

			List<BuscaSalva> buscas = new ArrayList<>();
			while (resultado.next()) {
				buscas.add(montarBuscaSalva(resultado));
			}
			return buscas;
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar as buscas salvas com alerta ativo.", e);
		}
	}

	public void atualizarAlerta(int idBusca, boolean alertaAtivo, int idUsuario) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_ATUALIZAR_ALERTA)) {

			comando.setBoolean(1, alertaAtivo);
			comando.setInt(2, idBusca);
			comando.setInt(3, idUsuario);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao atualizar o alerta da busca " + idBusca + ".", e);
		}
	}

	public void remover(int idBusca, int idUsuario) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_REMOVER)) {

			comando.setInt(1, idBusca);
			comando.setInt(2, idUsuario);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao remover a busca " + idBusca + ".", e);
		}
	}

	/**
	 * @return true se esta busca já recebeu um alerta sobre este imóvel — a
	 *         UNIQUE KEY (id_busca_salva, id_imovel) da tabela garante a
	 *         mesma coisa no banco; esta checagem evita depender só da
	 *         exceção de chave duplicada para não reenviar o e-mail.
	 */
	public boolean jaNotificado(int idBusca, int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_JA_NOTIFICADO)) {

			comando.setInt(1, idBusca);
			comando.setInt(2, idImovel);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() && resultado.getInt(1) > 0;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao verificar notificações da busca " + idBusca + ".", e);
		}
	}

	public void registrarNotificacao(int idBusca, int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_REGISTRAR_NOTIFICACAO)) {

			comando.setInt(1, idBusca);
			comando.setInt(2, idImovel);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao registrar a notificação da busca " + idBusca + ".", e);
		}
	}

	private List<BuscaSalva> executarConsulta(PreparedStatement comando) throws SQLException {
		List<BuscaSalva> buscas = new ArrayList<>();
		try (ResultSet resultado = comando.executeQuery()) {
			while (resultado.next()) {
				buscas.add(montarBuscaSalva(resultado));
			}
		}
		return buscas;
	}

	private BuscaSalva montarBuscaSalva(ResultSet resultado) throws SQLException {
		BuscaSalva busca = new BuscaSalva();
		busca.setId(resultado.getInt("id"));
		busca.setIdUsuario(resultado.getInt("id_usuario"));
		busca.setNome(resultado.getString("nome"));
		String tipo = resultado.getString("tipo");
		busca.setTipo(tipo == null ? null : ConversorEnum.paraEnum(TipoImovel.class, tipo));
		String finalidade = resultado.getString("finalidade");
		busca.setFinalidade(finalidade == null ? null : ConversorEnum.paraEnum(Finalidade.class, finalidade));
		busca.setCidade(resultado.getString("cidade"));
		busca.setQuartosMinimo(LeitorResultSet.lerInteiro(resultado, "quartos_minimo"));
		busca.setPrecoMaximo(resultado.getBigDecimal("preco_maximo"));
		busca.setAlertaAtivo(resultado.getBoolean("alerta_ativo"));
		busca.setDataCriacao(LeitorResultSet.lerDataHora(resultado, "data_criacao"));
		return busca;
	}
}
