package model;

import java.math.BigDecimal;

/**
 * Representa um registro da tabela PLANO: as opções pagas para publicar um
 * anúncio, escolhidas na etapa 2 do assistente de anúncio.
 */
public class Plano {

	private int id;
	private String nome;
	private BigDecimal preco;
	private int duracaoDias;
	private int limiteFotos;
	private String descricao;
	private boolean destaque;
	private int ordem;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	public int getDuracaoDias() {
		return duracaoDias;
	}

	public void setDuracaoDias(int duracaoDias) {
		this.duracaoDias = duracaoDias;
	}

	public int getLimiteFotos() {
		return limiteFotos;
	}

	public void setLimiteFotos(int limiteFotos) {
		this.limiteFotos = limiteFotos;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return true para o plano que deve ser exibido como recomendado nos
	 *         cards de comparação (ex.: "Mais popular")
	 */
	public boolean isDestaque() {
		return destaque;
	}

	public void setDestaque(boolean destaque) {
		this.destaque = destaque;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}
}
