package model;

/**
 * Implementado pelos enums que possuem correspondência com colunas ENUM do
 * banco de dados.
 *
 * Permite que a conversão entre o texto gravado no MySQL e a constante Java
 * seja feita em um único lugar (ver util.ConversorEnum), sem repetir código
 * em cada enum.
 */
public interface ValorBanco {

	/**
	 * @return o texto exatamente como está gravado no banco (ex.: "comprador")
	 */
	String getValorBanco();

	/**
	 * @return o texto amigável para exibição nas páginas (ex.: "Comprador")
	 */
	String getRotulo();
}
