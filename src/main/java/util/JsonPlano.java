package util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Leitor mínimo de objetos JSON de um único nível, sem depender de nenhuma
 * biblioteca externa.
 *
 * Serve apenas para ler as respostas simples e conhecidas da API do Google
 * (token e userinfo), que são sempre um objeto plano de string/número/
 * booleano — não é um parser JSON genérico e não trata objetos ou listas
 * aninhadas.
 */
public final class JsonPlano {

	private JsonPlano() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * @param json texto JSON de um objeto plano, ex.: {"nome":"Ana","idade":30}
	 * @return mapa com os valores como String, na ordem em que aparecem
	 */
	public static Map<String, String> lerObjeto(String json) {
		Map<String, String> valores = new LinkedHashMap<>();
		if (json == null) {
			return valores;
		}

		int posicao = json.indexOf('{');
		int fim = json.lastIndexOf('}');
		if (posicao < 0 || fim < 0 || fim <= posicao) {
			return valores;
		}

		int cursor = posicao + 1;
		while (cursor < fim) {
			cursor = pularEspacosEVirgulas(json, cursor, fim);
			if (cursor >= fim || json.charAt(cursor) != '"') {
				break;
			}

			int[] chaveFim = new int[1];
			String chave = lerString(json, cursor, chaveFim);
			cursor = pularEspacos(json, chaveFim[0]);

			if (cursor >= fim || json.charAt(cursor) != ':') {
				break;
			}
			cursor = pularEspacos(json, cursor + 1);

			int[] valorFim = new int[1];
			String valor = lerValor(json, cursor, valorFim);
			cursor = valorFim[0];

			valores.put(chave, valor);
		}
		return valores;
	}

	private static String lerValor(String json, int inicio, int[] fimSaida) {
		char primeiro = json.charAt(inicio);
		if (primeiro == '"') {
			return lerString(json, inicio, fimSaida);
		}
		// número, booleano ou null: lê até a próxima vírgula ou fechamento de objeto
		int cursor = inicio;
		while (cursor < json.length() && json.charAt(cursor) != ',' && json.charAt(cursor) != '}') {
			cursor++;
		}
		fimSaida[0] = cursor;
		return json.substring(inicio, cursor).trim();
	}

	/**
	 * Lê uma string JSON entre aspas, tratando os escapes básicos (\", \\, \/,
	 * \n, \t, \r e \\uXXXX).
	 */
	private static String lerString(String json, int inicioComAspas, int[] fimSaida) {
		StringBuilder texto = new StringBuilder();
		int cursor = inicioComAspas + 1;
		while (cursor < json.length() && json.charAt(cursor) != '"') {
			char atual = json.charAt(cursor);
			if (atual == '\\' && cursor + 1 < json.length()) {
				char proximo = json.charAt(cursor + 1);
				switch (proximo) {
					case '"' -> texto.append('"');
					case '\\' -> texto.append('\\');
					case '/' -> texto.append('/');
					case 'n' -> texto.append('\n');
					case 't' -> texto.append('\t');
					case 'r' -> texto.append('\r');
					case 'u' -> {
						String hex = json.substring(cursor + 2, cursor + 6);
						texto.append((char) Integer.parseInt(hex, 16));
						cursor += 4;
					}
					default -> texto.append(proximo);
				}
				cursor += 2;
			} else {
				texto.append(atual);
				cursor++;
			}
		}
		fimSaida[0] = cursor + 1;
		return texto.toString();
	}

	private static int pularEspacos(String texto, int posicao) {
		int cursor = posicao;
		while (cursor < texto.length() && Character.isWhitespace(texto.charAt(cursor))) {
			cursor++;
		}
		return cursor;
	}

	private static int pularEspacosEVirgulas(String texto, int posicao, int limite) {
		int cursor = posicao;
		while (cursor < limite && (Character.isWhitespace(texto.charAt(cursor)) || texto.charAt(cursor) == ',')) {
			cursor++;
		}
		return cursor;
	}
}
