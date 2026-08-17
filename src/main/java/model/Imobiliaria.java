package model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro da tabela IMOBILIARIA.
 *
 * Como o protótipo não tem parceria real com nenhuma imobiliária, cada
 * registro é criado dentro do próprio sistema (ver ImobiliariaServico) e
 * identificado por um código curto, gerado automaticamente, que o vendedor
 * usa no cadastro para provar o vínculo — sem nenhum processo externo.
 */
public class Imobiliaria {

	private int id;
	private String nome;
	private String codigo;
	private String cnpj;
	private String telefone;
	private String email;
	private String cidade;
	private String estado;
	private boolean ativa;
	private LocalDateTime dataCadastro;

	public Imobiliaria() {
	}

	public Imobiliaria(String nome, String cnpj, String telefone, String email, String cidade, String estado) {
		this.nome = nome;
		this.cnpj = cnpj;
		this.telefone = telefone;
		this.email = email;
		this.cidade = cidade;
		this.estado = estado;
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

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
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
