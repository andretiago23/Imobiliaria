package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import model.Anuncio;
import model.StatusPagamento;
import model.TipoAnunciante;
import util.ConversorEnum;
import util.LeitorResultSet;

/**
 * Acesso à tabela anuncio: a contratação de um plano para um imóvel, criada
 * ao final do assistente de anúncio, antes do pagamento.
 */
public class AnuncioDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO anuncio (id_imovel, id_plano, id_anunciante, tipo_anunciante, status_pagamento)
			VALUES (?, ?, ?, ?, ?)
			""";

	private static final String SQL_SELECT_BASE = """
			SELECT id, id_imovel, id_plano, id_anunciante, tipo_anunciante, status_pagamento, data_contratacao, data_pagamento
			FROM anuncio
			""";

	private static final String SQL_BUSCAR_POR_ID = SQL_SELECT_BASE + " WHERE id = ?";

	private static final String SQL_BUSCAR_POR_IMOVEL = SQL_SELECT_BASE + " WHERE id_imovel = ? ORDER BY id DESC LIMIT 1";

	private static final String SQL_MARCAR_PAGO = """
			UPDATE anuncio SET status_pagamento = 'pago', data_pagamento = NOW() WHERE id = ? AND id_anunciante = ?
			""";

	public void inserir(Anuncio anuncio) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

			comando.setInt(1, anuncio.getIdImovel());
			comando.setInt(2, anuncio.getIdPlano());
			comando.setInt(3, anuncio.getIdAnunciante());
			comando.setString(4, ConversorEnum.paraBanco(anuncio.getTipoAnunciante()));
			comando.setString(5, ConversorEnum.paraBanco(anuncio.getStatusPagamento()));
			comando.executeUpdate();

			try (ResultSet chaves = comando.getGeneratedKeys()) {
				if (chaves.next()) {
					anuncio.setId(chaves.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao contratar o anúncio do imóvel " + anuncio.getIdImovel() + ".", e);
		}
	}

	public Optional<Anuncio> buscarPorId(int id) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_BUSCAR_POR_ID)) {

			comando.setInt(1, id);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? Optional.of(montarAnuncio(resultado)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar o anúncio de id " + id + ".", e);
		}
	}

	public Optional<Anuncio> buscarPorImovel(int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_BUSCAR_POR_IMOVEL)) {

			comando.setInt(1, idImovel);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? Optional.of(montarAnuncio(resultado)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar o anúncio do imóvel " + idImovel + ".", e);
		}
	}

	/**
	 * Confirma o pagamento. A cláusula id_anunciante impede que alguém marque
	 * como pago o anúncio de outra pessoa só adivinhando o id na URL.
	 *
	 * @return true se algum registro foi de fato atualizado
	 */
	public boolean marcarComoPago(int idAnuncio, int idAnunciante) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_MARCAR_PAGO)) {

			comando.setInt(1, idAnuncio);
			comando.setInt(2, idAnunciante);
			return comando.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new DAOException("Erro ao confirmar o pagamento do anúncio " + idAnuncio + ".", e);
		}
	}

	private Anuncio montarAnuncio(ResultSet resultado) throws SQLException {
		Anuncio anuncio = new Anuncio();
		anuncio.setId(resultado.getInt("id"));
		anuncio.setIdImovel(resultado.getInt("id_imovel"));
		anuncio.setIdPlano(resultado.getInt("id_plano"));
		anuncio.setIdAnunciante(resultado.getInt("id_anunciante"));
		anuncio.setTipoAnunciante(ConversorEnum.paraEnum(TipoAnunciante.class, resultado.getString("tipo_anunciante")));
		anuncio.setStatusPagamento(ConversorEnum.paraEnum(StatusPagamento.class, resultado.getString("status_pagamento")));
		anuncio.setDataContratacao(LeitorResultSet.lerDataHora(resultado, "data_contratacao"));
		anuncio.setDataPagamento(LeitorResultSet.lerDataHora(resultado, "data_pagamento"));
		return anuncio;
	}
}
