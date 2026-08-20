package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.StatusVisita;
import model.Usuario;
import model.VisitaAgendada;
import util.ConversorEnum;
import util.LeitorResultSet;

/**
 * Acesso à tabela visita_agendada.
 */
public class VisitaAgendadaDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO visita_agendada (id_imovel, id_cliente, data_visita, hora_inicio, hora_fim, status)
			VALUES (?, ?, ?, ?, ?, ?)
			""";

	private static final String COLUNAS = """
			v.id, v.id_imovel, v.id_cliente, v.data_visita, v.hora_inicio, v.hora_fim, v.status, v.data_criacao,
			u.nome AS cliente_nome, u.email AS cliente_email, u.telefone AS cliente_telefone
			""";

	private static final String SQL_SELECT_BASE = "SELECT " + COLUNAS + """
			FROM visita_agendada v
			JOIN usuario u ON u.id = v.id_cliente
			""";

	private static final String SQL_LISTAR_POR_IMOVEL = SQL_SELECT_BASE
			+ " WHERE v.id_imovel = ? ORDER BY v.data_visita, v.hora_inicio";

	private static final String SQL_CONTAR_AGENDADAS_FUTURAS_POR_IMOVEL = """
			SELECT COUNT(*) FROM visita_agendada
			WHERE id_imovel = ? AND status = 'agendada' AND data_visita >= CURDATE()
			""";

	/**
	 * Confere se já existe uma visita agendada (não cancelada) para o mesmo
	 * imóvel, data e horário de início — impede duas pessoas marcando o
	 * mesmo horário.
	 */
	private static final String SQL_HORARIO_OCUPADO = """
			SELECT COUNT(*) FROM visita_agendada
			WHERE id_imovel = ? AND data_visita = ? AND hora_inicio = ? AND status = 'agendada'
			""";

	private static final String SQL_REMOVER_POR_IMOVEL = "DELETE FROM visita_agendada WHERE id_imovel = ?";

	/** Usado ao excluir um imóvel por completo (ImovelServico.excluir). */
	public void removerPorImovel(int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_REMOVER_POR_IMOVEL)) {

			comando.setInt(1, idImovel);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao remover as visitas agendadas do imóvel " + idImovel + ".", e);
		}
	}

	public void inserir(VisitaAgendada visita) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

			comando.setInt(1, visita.getIdImovel());
			comando.setInt(2, visita.getIdCliente());
			comando.setObject(3, visita.getDataVisita());
			comando.setObject(4, visita.getHoraInicio());
			comando.setObject(5, visita.getHoraFim());
			comando.setString(6, ConversorEnum.paraBanco(visita.getStatus()));
			comando.executeUpdate();

			try (ResultSet chaves = comando.getGeneratedKeys()) {
				if (chaves.next()) {
					visita.setId(chaves.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao agendar a visita ao imóvel " + visita.getIdImovel() + ".", e);
		}
	}

	public boolean horarioOcupado(int idImovel, java.time.LocalDate data, java.time.LocalTime horaInicio) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_HORARIO_OCUPADO)) {

			comando.setInt(1, idImovel);
			comando.setObject(2, data);
			comando.setObject(3, horaInicio);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() && resultado.getInt(1) > 0;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao verificar disponibilidade do horário.", e);
		}
	}

	public List<VisitaAgendada> listarPorImovel(int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_POR_IMOVEL)) {

			comando.setInt(1, idImovel);
			try (ResultSet resultado = comando.executeQuery()) {
				List<VisitaAgendada> visitas = new ArrayList<>();
				while (resultado.next()) {
					visitas.add(montarVisita(resultado));
				}
				return visitas;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar as visitas do imóvel " + idImovel + ".", e);
		}
	}

	public int contarAgendadasFuturasPorImovel(int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_CONTAR_AGENDADAS_FUTURAS_POR_IMOVEL)) {

			comando.setInt(1, idImovel);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? resultado.getInt(1) : 0;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao contar as visitas do imóvel " + idImovel + ".", e);
		}
	}

	private VisitaAgendada montarVisita(ResultSet resultado) throws SQLException {
		VisitaAgendada visita = new VisitaAgendada();
		visita.setId(resultado.getInt("id"));
		visita.setIdImovel(resultado.getInt("id_imovel"));
		visita.setIdCliente(resultado.getInt("id_cliente"));
		visita.setDataVisita(resultado.getDate("data_visita").toLocalDate());
		visita.setHoraInicio(resultado.getTime("hora_inicio").toLocalTime());
		visita.setHoraFim(resultado.getTime("hora_fim").toLocalTime());
		visita.setStatus(ConversorEnum.paraEnum(StatusVisita.class, resultado.getString("status")));
		visita.setDataCriacao(LeitorResultSet.lerDataHora(resultado, "data_criacao"));

		Usuario cliente = new Usuario();
		cliente.setId(resultado.getInt("id_cliente"));
		cliente.setNome(resultado.getString("cliente_nome"));
		cliente.setEmail(resultado.getString("cliente_email"));
		cliente.setTelefone(resultado.getString("cliente_telefone"));
		visita.setCliente(cliente);

		return visita;
	}
}
