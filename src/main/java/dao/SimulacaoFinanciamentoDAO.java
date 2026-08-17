package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import model.SimulacaoFinanciamento;
import model.SistemaAmortizacao;
import util.ConversorEnum;
import util.LeitorResultSet;

/**
 * Acesso à tabela simulacao_financiamento.
 *
 * Um registro pode existir sem lead vinculado (o cliente só experimentou o
 * simulador) ou ser anexado a um contato_interesse quando o cliente escolhe
 * enviar a simulação junto do interesse.
 */
public class SimulacaoFinanciamentoDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO simulacao_financiamento
			    (id_contato, valor_imovel, valor_entrada, prazo_anos, sistema_amortizacao,
			     instituicao_referencia, valor_financiado, parcela_inicial, total_juros)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
			""";

	private static final String SQL_SELECT_BASE = """
			SELECT id, id_contato, valor_imovel, valor_entrada, prazo_anos, sistema_amortizacao,
			       instituicao_referencia, valor_financiado, parcela_inicial, total_juros, data_simulacao
			FROM simulacao_financiamento
			""";

	private static final String SQL_BUSCAR_POR_CONTATO = SQL_SELECT_BASE + " WHERE id_contato = ?";

	private static final String SQL_VINCULAR_CONTATO = "UPDATE simulacao_financiamento SET id_contato = ? WHERE id = ?";

	/**
	 * Grava a simulação e preenche o id gerado no próprio objeto.
	 */
	public void inserir(SimulacaoFinanciamento simulacao) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

			comando.setObject(1, simulacao.getIdContato());
			comando.setBigDecimal(2, simulacao.getValorImovel());
			comando.setBigDecimal(3, simulacao.getValorEntrada());
			comando.setInt(4, simulacao.getPrazoAnos());
			comando.setString(5, ConversorEnum.paraBanco(simulacao.getSistemaAmortizacao()));
			comando.setString(6, simulacao.getInstituicaoReferencia());
			comando.setBigDecimal(7, simulacao.getValorFinanciado());
			comando.setBigDecimal(8, simulacao.getParcelaInicial());
			comando.setBigDecimal(9, simulacao.getTotalJuros());
			comando.executeUpdate();

			try (ResultSet chaves = comando.getGeneratedKeys()) {
				if (chaves.next()) {
					simulacao.setId(chaves.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao gravar a simulação de financiamento.", e);
		}
	}

	/**
	 * Liga uma simulação já calculada a um lead recém-criado, quando o cliente
	 * escolhe anexá-la ao demonstrar interesse.
	 */
	public void vincularContato(int idSimulacao, int idContato) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_VINCULAR_CONTATO)) {

			comando.setInt(1, idContato);
			comando.setInt(2, idSimulacao);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao anexar a simulação de id " + idSimulacao + " ao lead.", e);
		}
	}

	public Optional<SimulacaoFinanciamento> buscarPorContato(int idContato) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_BUSCAR_POR_CONTATO)) {

			comando.setInt(1, idContato);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? Optional.of(montarSimulacao(resultado)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar a simulação do contato de id " + idContato + ".", e);
		}
	}

	private SimulacaoFinanciamento montarSimulacao(ResultSet resultado) throws SQLException {
		SimulacaoFinanciamento simulacao = new SimulacaoFinanciamento();
		simulacao.setId(resultado.getInt("id"));
		simulacao.setIdContato(LeitorResultSet.lerInteiro(resultado, "id_contato"));
		simulacao.setValorImovel(resultado.getBigDecimal("valor_imovel"));
		simulacao.setValorEntrada(resultado.getBigDecimal("valor_entrada"));
		simulacao.setPrazoAnos(resultado.getInt("prazo_anos"));
		simulacao.setSistemaAmortizacao(
				ConversorEnum.paraEnum(SistemaAmortizacao.class, resultado.getString("sistema_amortizacao")));
		simulacao.setInstituicaoReferencia(resultado.getString("instituicao_referencia"));
		simulacao.setValorFinanciado(resultado.getBigDecimal("valor_financiado"));
		simulacao.setParcelaInicial(resultado.getBigDecimal("parcela_inicial"));
		simulacao.setTotalJuros(resultado.getBigDecimal("total_juros"));
		simulacao.setDataSimulacao(LeitorResultSet.lerDataHora(resultado, "data_simulacao"));
		return simulacao;
	}
}
