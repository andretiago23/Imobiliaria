package model;

import java.util.Optional;

import dao.DAOException;
import dao.UsuarioDAO;
import util.HashSenha;
import util.ValidadorCPF;
import util.ValidadorEmail;

/**
 * Regras de negócio ligadas ao usuário: cadastro, autenticação e alteração de
 * dados.
 *
 * Concentra aqui as validações e o tratamento da senha para que os Servlets
 * fiquem responsáveis apenas pelo fluxo da requisição.
 */
public class UsuarioServico {

	private static final int TAMANHO_MINIMO_SENHA = 8;
	private static final int TAMANHO_CPF = 11;

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();

	/**
	 * Cadastra um novo usuário.
	 *
	 * A senha recebida em texto puro é convertida em hash antes de chegar ao
	 * banco. O CPF passa pela validação matemática dos dígitos verificadores e
	 * o cadastro é recusado quando ela falha, de modo que a coluna cpf_valido
	 * só recebe registros aprovados.
	 *
	 * @param usuario   dados preenchidos no formulário
	 * @param senhaPura senha digitada, ainda sem hash
	 */
	public void cadastrar(Usuario usuario, String senhaPura) throws RegraNegocioException, DAOException {
		validarNome(usuario.getNome());
		validarSenha(senhaPura);

		String email = ValidadorEmail.normalizar(usuario.getEmail());
		if (!ValidadorEmail.isValido(email)) {
			throw new RegraNegocioException("Informe um endereço de e-mail válido.");
		}
		if (usuarioDAO.emailJaCadastrado(email)) {
			throw new RegraNegocioException("Este e-mail já está cadastrado.");
		}

		String cpf = ValidadorCPF.apenasDigitos(usuario.getCpf());
		if (cpf.length() != TAMANHO_CPF) {
			throw new RegraNegocioException("O CPF deve conter 11 dígitos.");
		}
		if (!ValidadorCPF.isValido(cpf)) {
			throw new RegraNegocioException("O CPF informado é inválido. Confira os números digitados.");
		}
		if (usuarioDAO.cpfJaCadastrado(cpf)) {
			throw new RegraNegocioException("Este CPF já está cadastrado.");
		}

		usuario.setEmail(email);
		usuario.setCpf(cpf);
		usuario.setCpfValido(true);
		usuario.setSenha(HashSenha.gerar(senhaPura));

		if (usuario.getTipoUsuario() == null) {
			usuario.setTipoUsuario(TipoUsuario.AMBOS);
		}

		usuarioDAO.inserir(usuario);
	}

	/**
	 * Confere as credenciais informadas na tela de login.
	 *
	 * Devolve Optional vazio tanto para e-mail inexistente quanto para senha
	 * incorreta, de propósito: a tela não deve revelar qual dos dois falhou.
	 *
	 * @return o usuário autenticado, ou Optional vazio se as credenciais não conferirem
	 */
	public Optional<Usuario> autenticar(String email, String senha) throws DAOException {
		if (email == null || senha == null) {
			return Optional.empty();
		}

		Optional<Usuario> encontrado = usuarioDAO.buscarPorEmail(ValidadorEmail.normalizar(email));
		if (encontrado.isEmpty()) {
			return Optional.empty();
		}

		Usuario usuario = encontrado.get();
		return HashSenha.verificar(senha, usuario.getSenha()) ? Optional.of(usuario) : Optional.empty();
	}

	/**
	 * Atualiza os dados de perfil, garantindo que o novo e-mail não pertença a
	 * outra conta.
	 */
	public void atualizarPerfil(Usuario usuario) throws RegraNegocioException, DAOException {
		validarNome(usuario.getNome());

		Usuario atual = usuarioDAO.buscarPorId(usuario.getId())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));

		String email = ValidadorEmail.normalizar(usuario.getEmail());
		if (!ValidadorEmail.isValido(email)) {
			throw new RegraNegocioException("Informe um endereço de e-mail válido.");
		}
		if (!email.equals(atual.getEmail()) && usuarioDAO.emailJaCadastrado(email)) {
			throw new RegraNegocioException("Este e-mail já está cadastrado por outro usuário.");
		}

		usuario.setEmail(email);
		usuarioDAO.atualizar(usuario);
	}

	/**
	 * Troca a senha, exigindo a confirmação da senha atual.
	 */
	public void alterarSenha(int idUsuario, String senhaAtual, String novaSenha)
			throws RegraNegocioException, DAOException {

		Usuario usuario = usuarioDAO.buscarPorId(idUsuario)
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));

		if (!HashSenha.verificar(senhaAtual, usuario.getSenha())) {
			throw new RegraNegocioException("A senha atual está incorreta.");
		}

		validarSenha(novaSenha);
		usuarioDAO.atualizarSenha(idUsuario, HashSenha.gerar(novaSenha));
	}

	private void validarNome(String nome) throws RegraNegocioException {
		if (nome == null || nome.isBlank()) {
			throw new RegraNegocioException("Informe o nome.");
		}
	}

	private void validarSenha(String senha) throws RegraNegocioException {
		if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
			throw new RegraNegocioException(
					"A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
		}
	}
}
