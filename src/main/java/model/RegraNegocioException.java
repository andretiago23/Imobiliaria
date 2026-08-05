package model;

/**
 * Exceção lançada quando uma regra de negócio é violada.
 *
 * Diferente da DAOException, que indica falha técnica, a mensagem desta
 * exceção é escrita para ser exibida diretamente ao usuário na página.
 */
public class RegraNegocioException extends Exception {

	private static final long serialVersionUID = 1L;

	public RegraNegocioException(String mensagem) {
		super(mensagem);
	}
}
