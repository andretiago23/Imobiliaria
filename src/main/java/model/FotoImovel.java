package model;

import java.util.Objects;

/**
 * Representa um registro da tabela FOTO_IMOVEL.
 *
 * O campo ordem controla a sequência de exibição no carrossel do anúncio.
 */
public class FotoImovel {

	private int id;
	private int idImovel;
	private String urlFoto;
	private int ordem;

	public FotoImovel() {
	}

	public FotoImovel(int idImovel, String urlFoto, int ordem) {
		this.idImovel = idImovel;
		this.urlFoto = urlFoto;
		this.ordem = ordem;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdImovel() {
		return idImovel;
	}

	public void setIdImovel(int idImovel) {
		this.idImovel = idImovel;
	}

	public String getUrlFoto() {
		return urlFoto;
	}

	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof FotoImovel outra)) {
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
		return "FotoImovel [id=" + id + ", idImovel=" + idImovel + ", ordem=" + ordem + "]";
	}
}
