package model;

import util.ValidadorCPF;

/**
 * Verificação de crédito simulada.
 *
 * Não existe integração real com nenhum birô (Serasa, SPC ou similar). O
 * resultado é calculado deterministicamente a partir do CPF, só para que a
 * demonstração seja consistente entre execuções, e nunca revela valor de
 * dívida, credor ou qualquer outro dado financeiro — só um dos dois rótulos
 * previstos no PROJECT_SPEC (seção 28).
 */
public class CreditoServico {

	private static final int DIVISOR_FICTICIO = 3;

	/**
	 * @param cpf CPF do cliente, só chamado depois que ele autorizou a consulta
	 * @return NOME_REGULAR ou RESTRICAO, nunca NAO_SOLICITADO
	 */
	public ResultadoCredito consultar(String cpf) {
		String digitos = ValidadorCPF.apenasDigitos(cpf);
		int soma = digitos.chars().map(Character::getNumericValue).sum();
		return soma % DIVISOR_FICTICIO == 0 ? ResultadoCredito.RESTRICAO : ResultadoCredito.NOME_REGULAR;
	}
}
