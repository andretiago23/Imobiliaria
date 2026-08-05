package util;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Métodos auxiliares para ler colunas que aceitam NULL no banco.
 *
 * O ResultSet devolve zero para colunas numéricas nulas, então é necessário
 * consultar wasNull() logo em seguida. Centralizar isso evita repetir a
 * verificação em todos os DAOs.
 */
public final class LeitorResultSet {

	private LeitorResultSet() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * Lê uma coluna INT que aceita NULL.
	 */
	public static Integer lerInteiro(ResultSet resultado, String coluna) throws SQLException {
		int valor = resultado.getInt(coluna);
		return resultado.wasNull() ? null : valor;
	}

	/**
	 * Lê uma coluna DECIMAL que aceita NULL, devolvendo Double.
	 * Usado nas colunas latitude e longitude.
	 */
	public static Double lerDouble(ResultSet resultado, String coluna) throws SQLException {
		BigDecimal valor = resultado.getBigDecimal(coluna);
		return valor == null ? null : valor.doubleValue();
	}

	/**
	 * Lê uma coluna DATETIME, convertendo para a API de data e hora do Java.
	 */
	public static LocalDateTime lerDataHora(ResultSet resultado, String coluna) throws SQLException {
		Timestamp marcaTempo = resultado.getTimestamp(coluna);
		return marcaTempo == null ? null : marcaTempo.toLocalDateTime();
	}
}
