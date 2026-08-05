package util;

/**
 * Escape de caracteres especiais de HTML.
 *
 * Necessário sempre que um valor digitado pelo usuário voltar para a tela,
 * como acontece ao reexibir um formulário que falhou na validação. Sem isso,
 * um texto contendo marcação seria interpretado pelo navegador, abrindo espaço
 * para injeção de scripts.
 */
public final class Html {

	private Html() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * @param texto valor vindo do formulário, podendo ser nulo
	 * @return o texto seguro para ser escrito dentro da página
	 */
	public static String escapar(String texto) {
		if (texto == null) {
			return "";
		}
		return texto.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
}
