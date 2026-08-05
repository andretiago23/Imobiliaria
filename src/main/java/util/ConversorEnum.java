package util;

import model.ValorBanco;

/**
 * Converte valores de colunas ENUM do MySQL para constantes Java e vice-versa.
 *
 * Centraliza essa lógica para que nenhum DAO precise repetir laços de
 * comparação de texto.
 *
 * Uso típico dentro de um DAO:
 *
 * <pre>
 * TipoUsuario tipo = ConversorEnum.paraEnum(TipoUsuario.class, rs.getString("tipo_usuario"));
 * comando.setString(1, ConversorEnum.paraBanco(usuario.getTipoUsuario()));
 * </pre>
 */
public final class ConversorEnum {

	private ConversorEnum() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * Converte o texto lido do banco na constante correspondente.
	 *
	 * @param tipoEnum classe do enum desejado
	 * @param valor    texto gravado na coluna, podendo ser nulo
	 * @return a constante correspondente, ou null se o valor for nulo/vazio
	 * @throws IllegalArgumentException se o texto não corresponder a nenhuma constante
	 */
	public static <E extends Enum<E> & ValorBanco> E paraEnum(Class<E> tipoEnum, String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		for (E constante : tipoEnum.getEnumConstants()) {
			if (constante.getValorBanco().equalsIgnoreCase(valor.trim())) {
				return constante;
			}
		}
		throw new IllegalArgumentException(
				"Valor inválido para " + tipoEnum.getSimpleName() + ": " + valor);
	}

	/**
	 * Converte a constante no texto que deve ser gravado no banco.
	 *
	 * @param valor constante do enum, podendo ser nula
	 * @return o texto do banco, ou null se a constante for nula
	 */
	public static String paraBanco(ValorBanco valor) {
		return valor == null ? null : valor.getValorBanco();
	}
}
