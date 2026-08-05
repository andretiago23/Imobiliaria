package dao;

/**
 * Exceção lançada quando ocorre alguma falha no acesso ao banco de dados.
 *
 * Encapsula a SQLException original para que as camadas superiores
 * (Controller e View) não precisem conhecer detalhes de JDBC.
 */
public class DAOException extends Exception {

	private static final long serialVersionUID = 1L;

	public DAOException(String mensagem) {
		super(mensagem);
	}

	public DAOException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
