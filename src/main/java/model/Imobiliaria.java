package model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro da tabela imobiliaria.
 *
 * Como o sistema não tem parceria real com nenhuma imobiliária, cada
 * registro é criado dentro do próprio sistema (ver ImobiliariaServico) e
 * identificado por um código curto, gerado automaticamente, que o vendedor
 * usa no cadastro para provar o vínculo — sem nenhum processo externo.
 */
public class Imobiliaria {

	private int id;
	private String nome;
	private String codigo;
	private boolean ativa;
	private LocalDateTime dataCadastro;

	public Imobiliaria() {
	}

	public Imobiliaria(String nome) {
		this.nome = nome;
		this.ativa = true;
	}

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

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	public LocalDateTime getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(LocalDateTime dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof Imobiliaria outra)) {
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
		return "Imobiliaria [id=" + id + ", nome=" + nome + ", codigo=" + codigo + "]";
	}
}
