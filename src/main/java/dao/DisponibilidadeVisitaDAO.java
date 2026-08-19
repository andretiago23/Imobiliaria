package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.DiaSemana;
import model.DisponibilidadeVisita;
import util.ConversorEnum;

/**
 * Acesso à tabela disponibilidade_visita: as janelas de horário em que o
 * anunciante aceita visitas a um imóvel, configuradas na etapa 4 do
 * assistente de anúncio (ou depois, em "Imóveis anunciados" → editar).
 */
public class DisponibilidadeVisitaDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO disponibilidade_visita (id_imovel, dia_semana, hora_inicio, hora_fim)
			VALUES (?, ?, ?, ?)
			""";

	private static final String SQL_LISTAR_POR_IMOVEL = """
			SELECT id, id_imovel, dia_semana, hora_inicio, hora_fim
			FROM disponibilidade_visita
			WHERE id_imovel = ?
			ORDER BY FIELD(dia_semana,'SEG','TER','QUA','QUI','SEX','SAB','DOM'), hora_inicio
			""";

	private static final String SQL_REMOVER_POR_IMOVEL = "DELETE FROM disponibilidade_visita WHERE id_imovel = ?";

	/**
	 * Grava a disponibilidade inteira de um imóvel de uma vez, substituindo
	 * o que existia — mais simples do que calcular um diff linha a linha
	 * para uma tabela pequena que só é editada como um todo pela tela.
	 */
	public void salvarTodas(int idImovel, List<DisponibilidadeVisita> janelas) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao()) {
			try (PreparedStatement remover = conexao.prepareStatement(SQL_REMOVER_POR_IMOVEL)) {
				remover.setInt(1, idImovel);
				remover.executeUpdate();
			}
			try (PreparedStatement inserir = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {
				for (DisponibilidadeVisita janela : janelas) {
					inserir.setInt(1, idImovel);
					inserir.setString(2, ConversorEnum.paraBanco(janela.getDiaSemana()));
					inserir.setObject(3, janela.getHoraInicio());
					inserir.setObject(4, janela.getHoraFim());
					inserir.addBatch();
				}
				if (!janelas.isEmpty()) {
					inserir.executeBatch();
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao salvar a disponibilidade de visitas do imóvel " + idImovel + ".", e);
		}
	}

	public List<DisponibilidadeVisita> listarPorImovel(int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_POR_IMOVEL)) {

			comando.setInt(1, idImovel);
			try (ResultSet resultado = comando.executeQuery()) {
				List<DisponibilidadeVisita> janelas = new ArrayList<>();
				while (resultado.next()) {
					DisponibilidadeVisita janela = new DisponibilidadeVisita();
					janela.setId(resultado.getInt("id"));
					janela.setIdImovel(resultado.getInt("id_imovel"));
					janela.setDiaSemana(ConversorEnum.paraEnum(DiaSemana.class, resultado.getString("dia_semana")));
					janela.setHoraInicio(resultado.getTime("hora_inicio").toLocalTime());
					janela.setHoraFim(resultado.getTime("hora_fim").toLocalTime());
					janelas.add(janela);
				}
				return janelas;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar a disponibilidade de visitas do imóvel " + idImovel + ".", e);
		}
	}
}
