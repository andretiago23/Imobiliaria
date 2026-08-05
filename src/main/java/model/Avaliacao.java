package model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro da tabela AVALIACAO.
 *
 * Um usuário avalia outro, opcionalmente no contexto de um imóvel negociado.
 * O campo avaliador é preenchido apenas quando o DAO fizer a consulta com JOIN.
 */
public class Avaliacao {

	public static final int NOTA_MINIMA = 1;
	public static final int NOTA_MAXIMA = 5;

	private int id;
	private int idAvaliador;
	private int idAvaliado;
	private Integer idImovel;
	private int nota;
	private String comentario;
	private LocalDateTime dataAvaliacao;

	private Usuario avaliador;

	public Avaliacao() {
	}

	public Avaliacao(int idAvaliador, int idAvaliado, int nota, String comentario) {
		this.idAvaliador = idAvaliador;
		this.idAvaliado = idAvaliado;
		this.comentario = comentario;
		setNota(nota);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdAvaliador() {
		return idAvaliador;
	}

	public void setIdAvaliador(int idAvaliador) {
		this.idAvaliador = idAvaliador;
	}

	public int getIdAvaliado() {
		return idAvaliado;
	}

	public void setIdAvaliado(int idAvaliado) {
		this.idAvaliado = idAvaliado;
	}

	/**
	 * @return o imóvel que deu contexto à negociação, ou null se não houver
	 */
	public Integer getIdImovel() {
		return idImovel;
	}

	public void setIdImovel(Integer idImovel) {
		this.idImovel = idImovel;
	}

	public int getNota() {
		return nota;
	}

	/**
	 * @throws IllegalArgumentException se a nota estiver fora do intervalo de 1 a 5
	 */
	public void setNota(int nota) {
		if (nota < NOTA_MINIMA || nota > NOTA_MAXIMA) {
			throw new IllegalArgumentException(
					"A nota deve estar entre " + NOTA_MINIMA + " e " + NOTA_MAXIMA + ". Recebido: " + nota);
		}
		this.nota = nota;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public LocalDateTime getDataAvaliacao() {
		return dataAvaliacao;
	}

	public void setDataAvaliacao(LocalDateTime dataAvaliacao) {
		this.dataAvaliacao = dataAvaliacao;
	}

	public Usuario getAvaliador() {
		return avaliador;
	}

	public void setAvaliador(Usuario avaliador) {
		this.avaliador = avaliador;
	}

	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof Avaliacao outra)) {
			return false;
		}
		return id != 0 && id == outra.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Avaliacao [id=" + id + ", idAvaliador=" + idAvaliador + ", idAvaliado=" + idAvaliado
				+ ", nota=" + nota + "]";
	}
}
