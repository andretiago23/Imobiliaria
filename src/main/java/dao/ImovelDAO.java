package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.Finalidade;
import model.FiltroImovel;
import model.Imovel;
import model.StatusImovel;
import model.TipoImovel;
import model.Usuario;
import util.ConversorEnum;
import util.LeitorResultSet;

/**
 * Acesso à tabela imovel.
 *
 * Todas as consultas fazem JOIN com a tabela usuario para já trazer os dados
 * do anunciante, evitando uma consulta extra por imóvel na montagem do feed.
 * As fotos não são carregadas aqui: use FotoImovelDAO para isso.
 */
public class ImovelDAO {

	private static final String SQL_INSERIR = """
			INSERT INTO imovel (id_usuario, titulo, descricao, tipo, finalidade, preco, area_m2,
			                    quartos, banheiros, vagas_garagem, endereco, cidade, estado, cep,
			                    latitude, longitude, status)
			VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			""";

	private static final String SQL_ATUALIZAR = """
			UPDATE imovel
			SET titulo = ?, descricao = ?, tipo = ?, finalidade = ?, preco = ?, area_m2 = ?,
			    quartos = ?, banheiros = ?, vagas_garagem = ?, endereco = ?, cidade = ?,
			    estado = ?, cep = ?, latitude = ?, longitude = ?, status = ?
			WHERE id = ?
			""";

	/**
	 * Lista de colunas reaproveitada por outros DAOs que precisam devolver
	 * imóveis. Os apelidos i (imovel) e u (usuario dono) precisam ser mantidos.
	 */
	static final String COLUNAS = """
			i.id, i.id_usuario, i.titulo, i.descricao, i.tipo, i.finalidade, i.preco,
			i.area_m2, i.quartos, i.banheiros, i.vagas_garagem, i.endereco, i.cidade,
			i.estado, i.cep, i.latitude, i.longitude, i.status, i.data_publicacao,
			u.nome AS dono_nome, u.email AS dono_email, u.telefone AS dono_telefone,
			u.foto_perfil AS dono_foto
			""";

	private static final String SQL_SELECT_BASE = "SELECT " + COLUNAS + """
			FROM imovel i
			JOIN usuario u ON u.id = i.id_usuario
			""";

	private static final String SQL_BUSCAR_POR_ID = SQL_SELECT_BASE + " WHERE i.id = ?";

	private static final String SQL_LISTAR_POR_USUARIO = SQL_SELECT_BASE
			+ " WHERE i.id_usuario = ? ORDER BY i.data_publicacao DESC";

	private static final String SQL_LISTAR_ATIVOS = SQL_SELECT_BASE
			+ " WHERE i.status = 'ativo' ORDER BY i.data_publicacao DESC LIMIT ?";

	private static final String SQL_ATUALIZAR_STATUS = "UPDATE imovel SET status = ? WHERE id = ?";

	private static final String SQL_REMOVER = "DELETE FROM imovel WHERE id = ?";

	private static final String SQL_CONTAR_POR_USUARIO = "SELECT COUNT(*) FROM imovel WHERE id_usuario = ?";

	/**
	 * Publica um novo anúncio e preenche o id gerado no próprio objeto.
	 */
	public void inserir(Imovel imovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

			comando.setInt(1, imovel.getIdUsuario());
			preencherDadosImovel(comando, imovel, 2);
			comando.executeUpdate();

			try (ResultSet chaves = comando.getGeneratedKeys()) {
				if (chaves.next()) {
					imovel.setId(chaves.getInt(1));
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao inserir o imóvel " + imovel.getTitulo() + ".", e);
		}
	}

	public void atualizar(Imovel imovel) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_ATUALIZAR)) {

			int proximaPosicao = preencherDadosImovel(comando, imovel, 1);
			comando.setInt(proximaPosicao, imovel.getId());
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao atualizar o imóvel de id " + imovel.getId() + ".", e);
		}
	}

	public Optional<Imovel> buscarPorId(int id) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_BUSCAR_POR_ID)) {

			comando.setInt(1, id);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? Optional.of(montarImovel(resultado)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar o imóvel de id " + id + ".", e);
		}
	}

	/**
	 * Lista os anúncios de um usuário, incluindo os inativos e já negociados.
	 */
	public List<Imovel> listarPorUsuario(int idUsuario) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_POR_USUARIO)) {

			comando.setInt(1, idUsuario);
			return executarConsulta(comando);
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar os imóveis do usuário de id " + idUsuario + ".", e);
		}
	}

	/**
	 * Lista os anúncios ativos mais recentes, usados na página inicial.
	 *
	 * @param limite quantidade máxima de imóveis retornados
	 */
	public List<Imovel> listarAtivos(int limite) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_ATIVOS)) {

			comando.setInt(1, limite);
			return executarConsulta(comando);
		} catch (SQLException e) {
			throw new DAOException("Erro ao listar os imóveis ativos.", e);
		}
	}

	/**
	 * Busca imóveis ativos aplicando somente os filtros preenchidos.
	 *
	 * A consulta é montada dinamicamente, mas todos os valores continuam sendo
	 * enviados como parâmetros, sem concatenação de dados na SQL.
	 */
	public List<Imovel> buscarComFiltros(FiltroImovel filtro) throws DAOException {
		StringBuilder sql = new StringBuilder(SQL_SELECT_BASE).append(" WHERE i.status = 'ativo'");
		List<Object> parametros = new ArrayList<>();

		if (textoPreenchido(filtro.getCidade())) {
			sql.append(" AND i.cidade LIKE ?");
			parametros.add("%" + filtro.getCidade().trim() + "%");
		}
		if (textoPreenchido(filtro.getEstado())) {
			sql.append(" AND i.estado = ?");
			parametros.add(filtro.getEstado().trim());
		}
		if (filtro.getTipo() != null) {
			sql.append(" AND i.tipo = ?");
			parametros.add(ConversorEnum.paraBanco(filtro.getTipo()));
		}
		if (filtro.getFinalidade() != null) {
			sql.append(" AND i.finalidade = ?");
			parametros.add(ConversorEnum.paraBanco(filtro.getFinalidade()));
		}
		if (filtro.getPrecoMinimo() != null) {
			sql.append(" AND i.preco >= ?");
			parametros.add(filtro.getPrecoMinimo());
		}
		if (filtro.getPrecoMaximo() != null) {
			sql.append(" AND i.preco <= ?");
			parametros.add(filtro.getPrecoMaximo());
		}
		if (filtro.getQuartosMinimo() != null) {
			sql.append(" AND i.quartos >= ?");
			parametros.add(filtro.getQuartosMinimo());
		}
		sql.append(" ORDER BY i.data_publicacao DESC");

		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(sql.toString())) {

			for (int posicao = 0; posicao < parametros.size(); posicao++) {
				comando.setObject(posicao + 1, parametros.get(posicao));
			}
			return executarConsulta(comando);
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar imóveis com filtros.", e);
		}
	}

	/**
	 * Usado quando o anúncio é marcado como vendido, alugado ou inativo.
	 */
	public void atualizarStatus(int idImovel, StatusImovel status) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_ATUALIZAR_STATUS)) {

			comando.setString(1, ConversorEnum.paraBanco(status));
			comando.setInt(2, idImovel);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao atualizar o status do imóvel de id " + idImovel + ".", e);
		}
	}

	public void remover(int id) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_REMOVER)) {

			comando.setInt(1, id);
			comando.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("Erro ao remover o imóvel de id " + id + ".", e);
		}
	}

	public int contarPorUsuario(int idUsuario) throws DAOException {
		try (Connection conexao = ConnectionFactory.obterConexao();
				PreparedStatement comando = conexao.prepareStatement(SQL_CONTAR_POR_USUARIO)) {

			comando.setInt(1, idUsuario);
			try (ResultSet resultado = comando.executeQuery()) {
				return resultado.next() ? resultado.getInt(1) : 0;
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao contar os imóveis do usuário de id " + idUsuario + ".", e);
		}
	}

	private List<Imovel> executarConsulta(PreparedStatement comando) throws SQLException {
		List<Imovel> imoveis = new ArrayList<>();
		try (ResultSet resultado = comando.executeQuery()) {
			while (resultado.next()) {
				imoveis.add(montarImovel(resultado));
			}
		}
		return imoveis;
	}

	/**
	 * Preenche os campos comuns ao INSERT e ao UPDATE.
	 *
	 * @param posicao posição inicial do parâmetro no comando
	 * @return a próxima posição livre
	 */
	private int preencherDadosImovel(PreparedStatement comando, Imovel imovel, int posicao) throws SQLException {
		comando.setString(posicao++, imovel.getTitulo());
		comando.setString(posicao++, imovel.getDescricao());
		comando.setString(posicao++, ConversorEnum.paraBanco(imovel.getTipo()));
		comando.setString(posicao++, ConversorEnum.paraBanco(imovel.getFinalidade()));
		comando.setBigDecimal(posicao++, imovel.getPreco());
		comando.setDouble(posicao++, imovel.getAreaM2());
		comando.setInt(posicao++, imovel.getQuartos());
		comando.setInt(posicao++, imovel.getBanheiros());
		comando.setInt(posicao++, imovel.getVagasGaragem());
		comando.setString(posicao++, imovel.getEndereco());
		comando.setString(posicao++, imovel.getCidade());
		comando.setString(posicao++, imovel.getEstado());
		comando.setString(posicao++, imovel.getCep());
		comando.setObject(posicao++, imovel.getLatitude());
		comando.setObject(posicao++, imovel.getLongitude());
		comando.setString(posicao++, ConversorEnum.paraBanco(imovel.getStatus()));
		return posicao;
	}

	/**
	 * Converte a linha atual do ResultSet em um objeto Imovel, já com o dono.
	 *
	 * Visível no pacote para que os demais DAOs possam reaproveitar a montagem
	 * em consultas com JOIN, desde que usem as colunas da constante COLUNAS.
	 */
	static Imovel montarImovel(ResultSet resultado) throws SQLException {
		Imovel imovel = new Imovel();
		imovel.setId(resultado.getInt("id"));
		imovel.setIdUsuario(resultado.getInt("id_usuario"));
		imovel.setTitulo(resultado.getString("titulo"));
		imovel.setDescricao(resultado.getString("descricao"));
		imovel.setTipo(ConversorEnum.paraEnum(TipoImovel.class, resultado.getString("tipo")));
		imovel.setFinalidade(ConversorEnum.paraEnum(Finalidade.class, resultado.getString("finalidade")));
		imovel.setPreco(resultado.getBigDecimal("preco"));
		imovel.setAreaM2(resultado.getDouble("area_m2"));
		imovel.setQuartos(resultado.getInt("quartos"));
		imovel.setBanheiros(resultado.getInt("banheiros"));
		imovel.setVagasGaragem(resultado.getInt("vagas_garagem"));
		imovel.setEndereco(resultado.getString("endereco"));
		imovel.setCidade(resultado.getString("cidade"));
		imovel.setEstado(resultado.getString("estado"));
		imovel.setCep(resultado.getString("cep"));
		imovel.setLatitude(LeitorResultSet.lerDouble(resultado, "latitude"));
		imovel.setLongitude(LeitorResultSet.lerDouble(resultado, "longitude"));
		imovel.setStatus(ConversorEnum.paraEnum(StatusImovel.class, resultado.getString("status")));
		imovel.setDataPublicacao(LeitorResultSet.lerDataHora(resultado, "data_publicacao"));
		imovel.setDono(montarDono(resultado));
		return imovel;
	}

	/**
	 * Monta um Usuario com os dados do anunciante trazidos pelo JOIN.
	 * Contém apenas o necessário para exibição, sem a senha.
	 */
	private static Usuario montarDono(ResultSet resultado) throws SQLException {
		Usuario dono = new Usuario();
		dono.setId(resultado.getInt("id_usuario"));
		dono.setNome(resultado.getString("dono_nome"));
		dono.setEmail(resultado.getString("dono_email"));
		dono.setTelefone(resultado.getString("dono_telefone"));
		dono.setFotoPerfil(resultado.getString("dono_foto"));
		return dono;
	}

	private boolean textoPreenchido(String texto) {
		return texto != null && !texto.isBlank();
	}
}
