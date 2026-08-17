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
	private final ImobiliariaServico imobiliariaServico = new ImobiliariaServico();

	/**
	 * Cadastra um novo usuário.
	 *
	 * A senha recebida em texto puro é convertida em hash antes de chegar ao
	 * banco. O CPF passa pela validação matemática dos dígitos verificadores e
	 * o cadastro é recusado quando ela falha, de modo que a coluna cpf_valido
	 * só recebe registros aprovados.
	 *
	 * Quando o perfil escolhido é Vendedor, o código da imobiliária é
	 * obrigatório: é assim que o sistema confirma o vínculo, já que não há
	 * parceria real com nenhuma imobiliária neste protótipo (ver
	 * ImobiliariaServico). O CRECI é só informativo — não é exigido nem
	 * validado, fica só registrado no perfil do vendedor.
	 *
	 * @param usuario           dados preenchidos no formulário
	 * @param senhaPura         senha digitada, ainda sem hash
	 * @param codigoImobiliaria código da imobiliária informado pelo vendedor;
	 *                          ignorado quando o perfil é Comprador
	 */
	public void cadastrar(Usuario usuario, String senhaPura, String codigoImobiliaria)
			throws RegraNegocioException, DAOException {

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

		if (usuario.getTipoUsuario() == null) {
			usuario.setTipoUsuario(TipoUsuario.COMPRADOR);
		}

		if (usuario.getTipoUsuario() == TipoUsuario.VENDEDOR) {
			Imobiliaria imobiliaria = imobiliariaServico.buscarPorCodigo(codigoImobiliaria)
					.orElseThrow(() -> new RegraNegocioException(
							"Código de imobiliária inválido. Confira o código com quem te repassou."));
			usuario.setIdImobiliaria(imobiliaria.getId());
			usuario.setCreci(normalizarCreci(usuario.getCreci()));
		} else {
			usuario.setIdImobiliaria(null);
			usuario.setCreci(null);
		}

		usuario.setEmail(email);
		usuario.setCpf(cpf);
		usuario.setCpfValido(true);
		usuario.setSenha(HashSenha.gerar(senhaPura));

		usuarioDAO.inserir(usuario);
	}

	/**
	 * @return o CRECI sem espaços nas pontas, ou null se não foi informado —
	 *         o campo é opcional, só para constar no perfil do vendedor
	 */
	private String normalizarCreci(String creci) {
		if (creci == null || creci.isBlank()) {
			return null;
		}
		return creci.trim();
	}

	/**
	 * Autoriza ou revoga a consulta de crédito simulada, ajustável a qualquer
	 * momento nas configurações do perfil. A revogação não desfaz consultas já
	 * realizadas: só impede novas consultas sem uma nova autorização explícita.
	 */
	public void alterarConsentimentoCredito(int idUsuario, boolean autorizado) throws DAOException {
		usuarioDAO.atualizarConsentimentoCredito(idUsuario, autorizado);
	}

	/**
	 * Usado na tela de perfil, para exibir o nome da imobiliária do vendedor.
	 */
	public Optional<Usuario> buscarComImobiliaria(int idUsuario) throws DAOException {
		return usuarioDAO.buscarPorIdComImobiliaria(idUsuario);
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
