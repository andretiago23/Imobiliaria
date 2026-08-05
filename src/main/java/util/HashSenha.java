package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Geração e conferência do hash das senhas.
 *
 * Usa PBKDF2 com HMAC-SHA256, disponível no próprio JDK, sem necessidade de
 * biblioteca externa. Cada senha recebe um salt aleatório próprio, o que
 * impede que duas senhas iguais gerem o mesmo hash.
 *
 * O texto gravado na coluna senha tem o formato:
 *
 * <pre>
 * iteracoes:saltEmBase64:hashEmBase64
 * </pre>
 *
 * Guardar as iterações junto permite aumentar esse número no futuro sem
 * invalidar as senhas já cadastradas.
 */
public final class HashSenha {

	private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
	private static final int ITERACOES = 210_000;
	private static final int TAMANHO_SALT_BYTES = 16;
	private static final int TAMANHO_HASH_BITS = 256;
	private static final String SEPARADOR = ":";
	private static final int TOTAL_PARTES = 3;

	private static final SecureRandom GERADOR_ALEATORIO = new SecureRandom();

	private HashSenha() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * Converte a senha em texto puro no hash que deve ser gravado no banco.
	 *
	 * @param senha senha digitada pelo usuário
	 * @return o hash completo, com iterações e salt embutidos
	 */
	public static String gerar(String senha) {
		if (senha == null || senha.isEmpty()) {
			throw new IllegalArgumentException("A senha não pode ser vazia.");
		}

		byte[] salt = new byte[TAMANHO_SALT_BYTES];
		GERADOR_ALEATORIO.nextBytes(salt);
		byte[] hash = calcular(senha, salt, ITERACOES);

		Base64.Encoder codificador = Base64.getEncoder();
		return ITERACOES + SEPARADOR + codificador.encodeToString(salt) + SEPARADOR
				+ codificador.encodeToString(hash);
	}

	/**
	 * Confere se a senha digitada corresponde ao hash gravado.
	 *
	 * @param senhaDigitada  senha informada na tela de login
	 * @param hashArmazenado conteúdo da coluna senha
	 * @return true se a senha estiver correta
	 */
	public static boolean verificar(String senhaDigitada, String hashArmazenado) {
		if (senhaDigitada == null || hashArmazenado == null) {
			return false;
		}

		String[] partes = hashArmazenado.split(SEPARADOR);
		if (partes.length != TOTAL_PARTES) {
			return false;
		}

		try {
			int iteracoes = Integer.parseInt(partes[0]);
			Base64.Decoder decodificador = Base64.getDecoder();
			byte[] salt = decodificador.decode(partes[1]);
			byte[] hashEsperado = decodificador.decode(partes[2]);
			byte[] hashCalculado = calcular(senhaDigitada, salt, iteracoes);

			// Comparação em tempo constante, para não vazar informação pelo
			// tempo de resposta.
			return MessageDigest.isEqual(hashEsperado, hashCalculado);
		} catch (IllegalArgumentException e) {
			// Hash gravado em formato inválido: trata como senha incorreta.
			return false;
		}
	}

	private static byte[] calcular(String senha, byte[] salt, int iteracoes) {
		PBEKeySpec especificacao = new PBEKeySpec(senha.toCharArray(), salt, iteracoes, TAMANHO_HASH_BITS);
		try {
			return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificacao).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException("Falha ao calcular o hash da senha.", e);
		} finally {
			especificacao.clearPassword();
		}
	}
}
