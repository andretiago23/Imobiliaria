package util;

/**
 * Escape mínimo para embutir texto dentro de uma string JSON escrita à mão
 * em JSP (ver o bloco de dados do comparador de imóveis em inicio.jsp).
 *
 * Não é um serializador genérico — só escapa o suficiente para um valor de
 * string dentro de aspas duplas ficar seguro: aspas, barra invertida, quebras
 * de linha e a sequência "&lt;/" (que fecharia a tag &lt;script&gt; onde o JSON
 * é embutido, se não escapada).
 */
public final class Json {

	private Json() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * @param texto valor a embutir como string JSON, podendo ser nulo
	 * @return o texto pronto para ficar entre aspas duplas num JSON
	 */
	public static String escapar(String texto) {
		if (texto == null) {
			return "";
		}
		return texto
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\r\n", "\\n")
				.replace("\n", "\\n")
				.replace("\r", "\\n")
				.replace("</", "<\\/");
	}
}
