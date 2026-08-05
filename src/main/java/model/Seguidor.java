package model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro da tabela SEGUIDOR.
 *
 * Liga o usuário que segue (idSeguidor) ao usuário seguido (idSeguido).
 * Os campos usuarioSeguidor e usuarioSeguido são preenchidos apenas quando o
 * DAO fizer a consulta com JOIN, para montar as listas de seguidores.
 */
public class Seguidor {

	private int id;
	private int idSeguidor;
	private int idSeguido;
	private LocalDateTime dataInicio;

	private Usuario usuarioSeguidor;
	private Usuario usuarioSeguido;

	public Seguidor() {
	}

	public Seguidor(int idSeguidor, int idSeguido) {
		this.idSeguidor = idSeguidor;
		this.idSeguido = idSeguido;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdSeguidor() {
		return idSeguidor;
	}

	public void setIdSeguidor(int idSeguidor) {
		this.idSeguidor = idSeguidor;
	}

	public int getIdSeguido() {
		return idSeguido;
	}

	public void setIdSeguido(int idSeguido) {
		this.idSeguido = idSeguido;
	}

	public LocalDateTime getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(LocalDateTime dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Usuario getUsuarioSeguidor() {
		return usuarioSeguidor;
	}

	public void setUsuarioSeguidor(Usuario usuarioSeguidor) {
		this.usuarioSeguidor = usuarioSeguidor;
	}

	public Usuario getUsuarioSeguido() {
		return usuarioSeguido;
	}

	public void setUsuarioSeguido(Usuario usuarioSeguido) {
		this.usuarioSeguido = usuarioSeguido;
	}

	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof Seguidor outro)) {
			return false;
		}
		return id != 0 && id == outro.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Seguidor [id=" + id + ", idSeguidor=" + idSeguidor + ", idSeguido=" + idSeguido + "]";
	}
}
