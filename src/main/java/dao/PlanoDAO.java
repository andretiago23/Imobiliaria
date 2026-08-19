package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.Plano;

/**
 * Acesso à tabela plano. Os planos são cadastrados diretamente no banco (não
 * há tela de administração para isso no escopo deste projeto), então este
 * DAO só lê.
 */
public class PlanoDAO {

	private static final String COLUNAS =
			"id, nome, preco, duracao_dias, limite_fotos, descricao, destaque, ordem";

	private static final String SQL_LISTAR = "SELECT " + COLUNAS + " FROM plano ORDER BY ordem";

	private static final String SQL_BUSCAR_POR_ID = "SELECT " + COLUNAS + " FROM plano WHERE id = ?";

	public List<Plano> listar() throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR);
				ResultSet resultado = comando.executeQuery()) {

			List<Plano> planos = new ArrayList<>();
			while (resultado.next()) {
				planos.add(montarPlano(resultado));
			}
			return planos;
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar os planos.", e);
		}
	}

	public Optional<Plano> buscarPorId(int id) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_BUSCAR_POR_ID)) {

			comando.setInt(1, id);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? Optional.of(montarPlano(resultado)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar o plano de id " + id + ".", e);
		}
	}

	private Plano montarPlano(ResultSet resultado) throws SQLException {
		Plano plano = new Plano();
		plano.setId(resultado.getInt("id"));
		plano.setNome(resultado.getString("nome"));
		plano.setPreco(resultado.getBigDecimal("preco"));
		plano.setDuracaoDias(resultado.getInt("duracao_dias"));
		plano.setLimiteFotos(resultado.getInt("limite_fotos"));
		plano.setDescricao(resultado.getString("descricao"));
		plano.setDestaque(resultado.getBoolean("destaque"));
		plano.setOrdem(resultado.getInt("ordem"));
		return plano;
	}
}
