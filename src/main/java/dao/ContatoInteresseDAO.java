package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.ContatoInteresse;
import model.Imovel;
import model.ResultadoCredito;
import model.StatusContato;
import model.StatusImovel;
import model.Usuario;
import util.ConversorEnum;
import util.LeitorResultSet;

/**
 * Acesso à tabela contato_interesse.
 *
 * Guarda as mensagens enviadas por compradores interessados em um imóvel e o
 * andamento de cada negociação.
 */
public class ContatoInteresseDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO contato_interesse
			    (id_imovel, id_comprador, mensagem, status, consulta_credito_autorizada, resultado_credito)
			VALUES (?, ?, ?, ?, ?, ?)
			""";

	private static final String SQL_SELECT_BASE = """
			SELECT c.id AS contato_id, c.id_imovel, c.id_comprador, c.mensagem, c.status,
			       c.consulta_credito_autorizada, c.resultado_credito, c.data_contato,
			       i.titulo AS imovel_titulo, i.status AS imovel_status, i.id_usuario AS imovel_dono,
			       comp.nome AS comprador_nome, comp.email AS comprador_email,
			       comp.telefone AS comprador_telefone, comp.foto_perfil AS comprador_foto
			FROM contato_interesse c
			JOIN imovel i ON i.id = c.id_imovel
			JOIN usuario comp ON comp.id = c.id_comprador
			""";

	private static final String SQL_LISTAR_POR_IMOVEL = SQL_SELECT_BASE
			+ " WHERE c.id_imovel = ? ORDER BY c.data_contato DESC";

	/** Mensagens recebidas pelo anunciante, em todos os imóveis dele. */
	private static final String SQL_LISTAR_RECEBIDOS = SQL_SELECT_BASE
			+ " WHERE i.id_usuario = ? ORDER BY c.data_contato DESC";

	/** Mensagens enviadas pelo comprador. */
	private static final String SQL_LISTAR_ENVIADOS = SQL_SELECT_BASE
			+ " WHERE c.id_comprador = ? ORDER BY c.data_contato DESC";

	private static final String SQL_BUSCAR_POR_ID = SQL_SELECT_BASE + " WHERE c.id = ?";

	private static final String SQL_ATUALIZAR_STATUS = "UPDATE contato_interesse SET status = ? WHERE id = ?";

	private static final String SQL_ATUALIZAR_CREDITO = """
			UPDATE contato_interesse
			SET consulta_credito_autorizada = ?, resultado_credito = ?
			WHERE id = ?
			""";

	private static final String SQL_CONTAR_PENDENTES = """
			SELECT COUNT(*)
			FROM contato_interesse c
			JOIN imovel i ON i.id = c.id_imovel
			WHERE i.id_usuario = ? AND c.status = 'novo'
			""";

	private static final String SQL_REMOVER_POR_IMOVEL = "DELETE FROM contato_interesse WHERE id_imovel = ?";

	/**
	 * Registra uma nova mensagem de interesse e preenche o id gerado no objeto.
	 */
	public void inserir(ContatoInteresse contato) throws DAOException {
		StatusContato status = contato.getStatus() == null ? StatusContato.NOVO : contato.getStatus();
		ResultadoCredito resultadoCredito = contato.getResultadoCredito() == null
				? ResultadoCredito.NAO_SOLICITADO
				: contato.getResultadoCredito();

		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

			comando.setInt(1, contato.getIdImovel());
			comando.setInt(2, contato.getIdComprador());
			comando.setString(3, contato.getMensagem());
			comando.setString(4, ConversorEnum.paraBanco(status));
			comando.setBoolean(5, contato.isConsultaCreditoAutorizada());
			comando.setString(6, ConversorEnum.paraBanco(resultadoCredito));
			comando.executeUpdate();

			try (ResultSet chaves = comando.getGeneratedKeys()) {
				if (chaves.next()) {
					contato.setId(chaves.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao registrar o interesse no imóvel de id " + contato.getIdImovel() + ".", e);
		}
	}

	public List<ContatoInteresse> listarPorImovel(int idImovel) throws DAOException {
		return listar(SQL_LISTAR_POR_IMOVEL, "Erro ao listar os contatos do imóvel de id " + idImovel + ".", idImovel);
	}

	/**
	 * Caixa de entrada do anunciante: mensagens recebidas em todos os seus imóveis.
	 */
	public List<ContatoInteresse> listarRecebidos(int idAnunciante) throws DAOException {
		return listar(SQL_LISTAR_RECEBIDOS,
				"Erro ao listar os contatos recebidos pelo usuário de id " + idAnunciante + ".", idAnunciante);
	}

	/**
	 * Histórico do comprador: mensagens que ele enviou.
	 */
	public List<ContatoInteresse> listarEnviados(int idComprador) throws DAOException {
		return listar(SQL_LISTAR_ENVIADOS,
				"Erro ao listar os contatos enviados pelo usuário de id " + idComprador + ".", idComprador);
	}

	public Optional<ContatoInteresse> buscarPorId(int id) throws DAOException {
		List<ContatoInteresse> encontrados = listar(SQL_BUSCAR_POR_ID,
				"Erro ao buscar o contato de id " + id + ".", id);
		return encontrados.isEmpty() ? Optional.empty() : Optional.of(encontrados.get(0));
	}

	public void atualizarStatus(int idContato, StatusContato status) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_ATUALIZAR_STATUS)) {

			comando.setString(1, ConversorEnum.paraBanco(status));
			comando.setInt(2, idContato);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao atualizar o status do contato de id " + idContato + ".", e);
		}
	}

	/**
	 * Grava o resultado da verificação de crédito fictícia deste lead.
	 */
	public void atualizarCredito(int idContato, boolean autorizada, ResultadoCredito resultado) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_ATUALIZAR_CREDITO)) {

			comando.setBoolean(1, autorizada);
			comando.setString(2, ConversorEnum.paraBanco(resultado));
			comando.setInt(3, idContato);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao atualizar a verificação de crédito do contato de id " + idContato + ".",
					e);
		}
	}

	/**
	 * @return quantas mensagens ainda não foram respondidas, para o aviso no menu
	 */
	public int contarPendentes(int idAnunciante) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_CONTAR_PENDENTES)) {

			comando.setInt(1, idAnunciante);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? resultado.getInt(1) : 0;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao contar os contatos pendentes do usuário de id " + idAnunciante + ".", e);
		}
	}

	/**
	 * Remove os contatos de um imóvel. Precisa ser chamado antes de excluir o
	 * imóvel, por causa da chave estrangeira.
	 */
	public void removerPorImovel(int idImovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_REMOVER_POR_IMOVEL)) {

			comando.setInt(1, idImovel);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao remover os contatos do imóvel de id " + idImovel + ".", e);
		}
	}

	private List<ContatoInteresse> listar(String sql, String mensagemErro, int parametro) throws DAOException {
		List<ContatoInteresse> contatos = new ArrayList<>();
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql)) {

			comando.setInt(1, parametro);
			try (ResultSet resultado = comando.executeQuery()) {
				while (resultado.next()) {
					contatos.add(montarContato(resultado));
				}
			}
			return contatos;
		} catch (SQLException e) {
			throw new DAOException(mensagemErro, e);
		}
	}

	/**
	 * Converte a linha atual do ResultSet em um ContatoInteresse, com versões
	 * reduzidas do imóvel e do comprador, suficientes para a listagem.
	 */
	private ContatoInteresse montarContato(ResultSet resultado) throws SQLException {
		ContatoInteresse contato = new ContatoInteresse();
		contato.setId(resultado.getInt("contato_id"));
		contato.setIdImovel(resultado.getInt("id_imovel"));
		contato.setIdComprador(resultado.getInt("id_comprador"));
		contato.setMensagem(resultado.getString("mensagem"));
		contato.setStatus(ConversorEnum.paraEnum(StatusContato.class, resultado.getString("status")));
		contato.setConsultaCreditoAutorizada(resultado.getBoolean("consulta_credito_autorizada"));
		contato.setResultadoCredito(
				ConversorEnum.paraEnum(ResultadoCredito.class, resultado.getString("resultado_credito")));
		contato.setDataContato(LeitorResultSet.lerDataHora(resultado, "data_contato"));

		Imovel imovel = new Imovel();
		imovel.setId(resultado.getInt("id_imovel"));
		imovel.setIdUsuario(resultado.getInt("imovel_dono"));
		imovel.setTitulo(resultado.getString("imovel_titulo"));
		imovel.setStatus(ConversorEnum.paraEnum(StatusImovel.class, resultado.getString("imovel_status")));
		contato.setImovel(imovel);

		Usuario comprador = new Usuario();
		comprador.setId(resultado.getInt("id_comprador"));
		comprador.setNome(resultado.getString("comprador_nome"));
		comprador.setEmail(resultado.getString("comprador_email"));
		comprador.setTelefone(resultado.getString("comprador_telefone"));
		comprador.setFotoPerfil(resultado.getString("comprador_foto"));
		contato.setComprador(comprador);

		return contato;
	}
}
