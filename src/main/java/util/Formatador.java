package util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formatação de valores para exibição nas páginas JSP.
 *
 * O projeto não usa JSTL (não há o jar no classpath), então as páginas
 * formatam valores chamando estes métodos direto do scriptlet.
 */
public final class Formatador {

	private static final Locale LOCALE_BR = Locale.of("pt", "BR");
	private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	private Formatador() {
		// Classe utilitária: não deve ser instanciada.
	}

	public static String moeda(BigDecimal valor) {
		if (valor == null) {
			return "-";
		}
		return NumberFormat.getCurrencyInstance(LOCALE_BR).format(valor);
	}

	public static String area(double areaM2) {
		return String.format(LOCALE_BR, "%.0f m²", areaM2);
	}

	public static String data(LocalDateTime dataHora) {
		return dataHora == null ? "-" : dataHora.format(FORMATO_DATA);
	}

	public static String dataHora(LocalDateTime dataHora) {
		return dataHora == null ? "-" : dataHora.format(FORMATO_DATA_HORA);
	}
}
