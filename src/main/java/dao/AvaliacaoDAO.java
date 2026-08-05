package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Avaliacao;
import model.Usuario;
import util.LeitorResultSet;

/**
 * Acesso à tabela avaliacao.
 *
 * Um usuário avalia outro com nota de 1 a 5, opcionalmente vinculando a
 * avaliação ao imóvel que originou a negociação.
 */
public class AvaliacaoDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO avaliacao (id_avaliador, id_avaliado, id_imovel, nota, comentario)
			VALUES (?, ?, ?, ?, ?)
			""";

	private static final String SQL_SELECT_BASE = """
			SELECT a.id AS avaliacao_id, a.id_avaliador, a.id_avaliado, a.id_imovel, a.nota,
			       a.comentario, a.data_avaliacao,
			       u.nome AS avaliador_nome, u.foto_perfil AS avaliador_foto
			FROM avaliacao a
			JOIN usuario u ON u.id = a.id_avaliador
			""";

	private static final String SQL_LISTAR_POR_AVALIADO = SQL_SELECT_BASE
			+ " WHERE a.id_avaliado = ? ORDER BY a.data_avaliacao DESC";

	private static final String SQL_LISTAR_POR_IMOVEL = SQL_SELECT_BASE
			+ " WHERE a.id_imovel = ? ORDER BY a.data_avaliacao DESC";

	private static final String SQL_CALCULAR_MEDIA = """
			SELECT COALESCE(AVG(nota), 0)
			FROM avaliacao
			WHERE id_avaliado = ?
			""";

	private static final String SQL_CONTAR_POR_AVALIADO = "SELECT COUNT(*) FROM avaliacao WHERE id_avaliado = ?";

	/**
	 * O operador <=> compara considerando NULL, necessário porque id_imovel é
	 * opcional. Com o operador = comum, a comparação com NULL nunca seria
	 * verdadeira.
	 */
	private static final String SQL_JA_AVALIOU = """
			SELECT COUNT(*)
			FROM avaliacao
			WHERE id_avaliador = ? AND id_avaliado = ? AND id_imovel <=> ?
			""";

	private static final String SQL_REMOVER = "DELETE FROM avaliacao WHERE id = ?";

	private static final String SQL_REMOVER_POR_IMOVEL = "DELETE FROM avaliacao WHERE id_imovel = ?";

	/**
	 * Grava uma nova avaliação e preenche o id gerado no próprio objeto.
	 */
	public void inserir(Avaliacao avaliacao) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

			comando.setInt(1, avaliacao.getIdAvaliador());
			comando.setInt(2, avaliacao.getIdAvaliado());
			comando.setObject(3, avaliacao.getIdImovel());
			comando.setInt(4, avaliacao.getNota());
			comando.setString(5, avaliacao.getComentario());
			comando.executeUpdate();

			try (ResultSet chaves = comando.getGeneratedKeys()) {
				if (chaves.next()) {
					avaliacao.setId(chaves.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao inserir a avaliação do usuário de id " + avaliacao.getIdAvaliado() + ".",
					e);
		}
	}

	/**
	 * Lista as avaliações recebidas por um usuário, já com os dados de quem avaliou.
	 */
	public List<Avaliacao> listarPorAvaliado(int idAvaliado) throws DAOException {
		return listar(SQL_LISTAR_POR_AVALIADO,
				"Erro ao listar as avaliações do usuário de id " + idAvaliado + ".", idAvaliado);
	}

	/**
	 * Lista as avaliações feitas no contexto de um imóvel específico.
	 */
	public List<Avaliacao> listarPorImovel(int idImovel) throws DAOException {
		return listar(SQL_LISTAR_POR_IMOVEL,
				"Erro ao listar as avaliações do imóvel de id " + idImovel + ".", idImovel);
	}

	/**
	 * @return a média das notas recebidas, ou zero se o usuário ainda não foi avaliado
	 */
	public double calcularMedia(int idAvaliado) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_CALCULAR_MEDIA)) {

			comando.setInt(1, idAvaliado);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? resultado.getDouble(1) : 0;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao calcular a média do usuário de id " + idAvaliado + ".", e);
		}
	}

	public int contarPorAvaliado(int idAvaliado) throws DAOException {
		return contar(SQL_CONTAR_POR_AVALIADO,
				"Erro ao contar as avaliações do usuário de id " + idAvaliado + ".", idAvaliado);
	}

	/**
	 * Impede que a mesma pessoa avalie outra duas vezes no mesmo contexto.
	 *
	 * @param idImovel pode ser nulo, para avaliações sem imóvel vinculado
	 */
	public boolean jaAvaliou(int idAvaliador, int idAvaliado, Integer idImovel) throws DAOException {
		return contar(SQL_JA_AVALIOU, "Erro ao verificar a avaliação.", idAvaliador, idAvaliado, idImovel) > 0;
	}

	public void remover(int id) throws DAOException {
		executar(SQL_REMOVER, "Erro ao remover a avaliação de id " + id + ".", id);
	}

	/**
	 * Remove as avaliações vinculadas a um imóvel. Precisa ser chamado antes de
	 * excluir o imóvel, por causa da chave estrangeira.
	 */
	public void removerPorImovel(int idImovel) throws DAOException {
		executar(SQL_REMOVER_POR_IMOVEL, "Erro ao remover as avaliações do imóvel de id " + idImovel + ".", idImovel);
	}

	private List<Avaliacao> listar(String sql, String mensagemErro, int parametro) throws DAOException {
		List<Avaliacao> avaliacoes = new ArrayList<>();
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql)) {

			comando.setInt(1, parametro);
			try (ResultSet resultado = comando.executeQuery()) {
				while (resultado.next()) {
					avaliacoes.add(montarAvaliacao(resultado));
				}
			}
			return avaliacoes;
		} catch (SQLException e) {
			throw new DAOException(mensagemErro, e);
		}
	}

	private void executar(String sql, String mensagemErro, Object... parametros) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql)) {

			preencherParametros(comando, parametros);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException(mensagemErro, e);
		}
	}

	private int contar(String sql, String mensagemErro, Object... parametros) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql)) {

			preencherParametros(comando, parametros);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? resultado.getInt(1) : 0;
			}
		} catch (SQLException e) {
			throw new DAOException(mensagemErro, e);
		}
	}

	private void preencherParametros(PreparedStatement comando, Object... parametros) throws SQLException {
		for (int posicao = 0; posicao < parametros.length; posicao++) {
			comando.setObject(posicao + 1, parametros[posicao]);
		}
	}

	/**
	 * Converte a linha atual do ResultSet em uma Avaliacao, já com um Usuario
	 * reduzido representando quem avaliou.
	 */
	private Avaliacao montarAvaliacao(ResultSet resultado) throws SQLException {
		Avaliacao avaliacao = new Avaliacao();
		avaliacao.setId(resultado.getInt("avaliacao_id"));
		avaliacao.setIdAvaliador(resultado.getInt("id_avaliador"));
		avaliacao.setIdAvaliado(resultado.getInt("id_avaliado"));
		avaliacao.setIdImovel(LeitorResultSet.lerInteiro(resultado, "id_imovel"));
		avaliacao.setNota(resultado.getInt("nota"));
		avaliacao.setComentario(resultado.getString("comentario"));
		avaliacao.setDataAvaliacao(LeitorResultSet.lerDataHora(resultado, "data_avaliacao"));

		Usuario avaliador = new Usuario();
		avaliador.setId(resultado.getInt("id_avaliador"));
		avaliador.setNome(resultado.getString("avaliador_nome"));
		avaliador.setFotoPerfil(resultado.getString("avaliador_foto"));
		avaliacao.setAvaliador(avaliador);

		return avaliacao;
	}
}
