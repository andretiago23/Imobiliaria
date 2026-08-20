package model;

import java.util.Objects;

/**
 * Representa um registro da tabela VIDEO_IMOVEL.
 *
 * Espelha FotoImovel: um imóvel pode ter mais de um vídeo (a exigência da
 * revisão de UX é de pelo menos 1, ver RascunhoAnuncio.etapa1Completa()), com
 * a mesma ideia de ordem de exibição.
 */
public class VideoImovel {

	private int id;
	private int idImovel;
	private String urlVideo;
	private int ordem;

	public VideoImovel() {
	}

	public VideoImovel(int idImovel, String urlVideo, int ordem) {
		this.idImovel = idImovel;
		this.urlVideo = urlVideo;
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

	public String getUrlVideo() {
		return urlVideo;
	}

	public void setUrlVideo(String urlVideo) {
		this.urlVideo = urlVideo;
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
		if (!(objeto instanceof VideoImovel outro)) {
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
		return "VideoImovel [id=" + id + ", idImovel=" + idImovel + ", ordem=" + ordem + "]";
	}
}
