package model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro da tabela USUARIOS.
 *
 * O campo senha guarda sempre o hash, nunca a senha em texto puro.
 */
public class Usuario {

	private int id;
	private String nome;
	private String email;
	private boolean emailConfirmado;
	private String senha;
	private String cpf;
	private boolean cpfValido;
	private String telefone;
	private boolean telefoneConfirmado;
	private String fotoPerfil;
	private String creci;
	private TipoUsuario tipoUsuario;
	private Integer idImobiliaria;
	private boolean consentimentoCredito;
	private LocalDateTime dataCadastro;

	/** Associação opcional, preenchida apenas quando o DAO fizer JOIN com imobiliaria. */
	private Imobiliaria imobiliaria;

	public Usuario() {
	}

	/**
	 * Construtor usado no cadastro, com os campos mínimos obrigatórios.
	 */
	public Usuario(String nome, String email, String senha, String cpf, TipoUsuario tipoUsuario) {
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.cpf = cpf;
		this.tipoUsuario = tipoUsuario;
	}

	/**
	 * @return true se o usuário tem permissão para publicar imóveis
	 */
	public boolean podeAnunciar() {
		return tipoUsuario != null && tipoUsuario.podeAnunciar();
	}

	/**
	 * @return true se a conta passou pelas verificações de e-mail e CPF
	 */
	public boolean estaVerificado() {
		return emailConfirmado && cpfValido;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEmailConfirmado() {
		return emailConfirmado;
	}

	public void setEmailConfirmado(boolean emailConfirmado) {
		this.emailConfirmado = emailConfirmado;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public boolean isCpfValido() {
		return cpfValido;
	}

	public void setCpfValido(boolean cpfValido) {
		this.cpfValido = cpfValido;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public boolean isTelefoneConfirmado() {
		return telefoneConfirmado;
	}

	public void setTelefoneConfirmado(boolean telefoneConfirmado) {
		this.telefoneConfirmado = telefoneConfirmado;
	}

	public String getFotoPerfil() {
		return fotoPerfil;
	}

	public void setFotoPerfil(String fotoPerfil) {
		this.fotoPerfil = fotoPerfil;
	}

	public String getCreci() {
		return creci;
	}

	public void setCreci(String creci) {
		this.creci = creci;
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public Integer getIdImobiliaria() {
		return idImobiliaria;
	}

	public void setIdImobiliaria(Integer idImobiliaria) {
		this.idImobiliaria = idImobiliaria;
	}

	public boolean isConsentimentoCredito() {
		return consentimentoCredito;
	}

	public void setConsentimentoCredito(boolean consentimentoCredito) {
		this.consentimentoCredito = consentimentoCredito;
	}

	public Imobiliaria getImobiliaria() {
		return imobiliaria;
	}

	public void setImobiliaria(Imobiliaria imobiliaria) {
		this.imobiliaria = imobiliaria;
	}

	public LocalDateTime getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(LocalDateTime dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**
	 * Dois usuários são iguais quando possuem o mesmo id.
	 * Registros ainda não gravados no banco (id igual a zero) nunca são iguais.
	 */
	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof Usuario outro)) {
			return false;
		}
		return id != 0 && id == outro.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * A senha é intencionalmente omitida para não vazar o hash em logs.
	 */
	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nome=" + nome + ", email=" + email + ", tipoUsuario=" + tipoUsuario + "]";
	}
}
