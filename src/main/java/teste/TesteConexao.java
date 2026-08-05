package teste;

import java.sql.Connection;
import java.sql.SQLException;

import dao.ConnectionFactory;
import dao.DAOException;

/**
 * Classe auxiliar para validar a configuração de acesso ao banco.
 *
 * Execute no Eclipse com: botão direito > Run As > Java Application.
 */
public class TesteConexao {

	public static void main(String[] args) {
		try (Connection conexao = ConnectionFactory.obterConexao()) {
			System.out.println("Conexao estabelecida com sucesso!");
			System.out.println("Banco...: " + conexao.getMetaData().getDatabaseProductName()
					+ " " + conexao.getMetaData().getDatabaseProductVersion());
			System.out.println("Schema..: " + conexao.getCatalog());
			System.out.println("Driver..: " + conexao.getMetaData().getDriverVersion());
		} catch (DAOException | SQLException e) {
			System.err.println("Erro ao conectar no banco de dados:");
			e.printStackTrace();
		}
	}
}
