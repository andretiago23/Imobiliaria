package model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro da tabela FAVORITO.
 *
 * O campo imovel é preenchido apenas quando o DAO fizer a consulta com JOIN,
 * para montar a lista de favoritos do usuário sem consultas adicionais.
 */
public class Favorito {

	private int id;
	private int idUsuario;
	private int idImovel;
	private LocalDateTime dataAdicao;

	private Imovel imovel;

	public Favorito() {
	}

	public Favorito(int idUsuario, int idImovel) {
		this.idUsuario = idUsuario;
		this.idImovel = idImovel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public int getIdImovel() {
		return idImovel;
	}

	public void setIdImovel(int idImovel) {
		this.idImovel = idImovel;
	}

	public LocalDateTime getDataAdicao() {
		return dataAdicao;
	}

	public void setDataAdicao(LocalDateTime dataAdicao) {
		this.dataAdicao = dataAdicao;
	}

	public Imovel getImovel() {
		return imovel;
	}

	public void setImovel(Imovel imovel) {
		this.imovel = imovel;
	}

	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof Favorito outro)) {
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
		return "Favorito [id=" + id + ", idUsuario=" + idUsuario + ", idImovel=" + idImovel + "]";
	}
}
