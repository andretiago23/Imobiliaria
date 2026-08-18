package model;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
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
	private final SecureRandom geradorAleatorio = new SecureRandom();

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
	 * ImobiliariaServico).
	 *
	 * @param usuario           dados preenchidos no formulário
	 * @param senhaPura         senha digitada, ainda sem hash
	 * @param aceitouTermos     true se a pessoa marcou a caixa de concordância
	 *                          com a Política de Privacidade e os Termos de Uso
	 *                          — sem isso, o cadastro não é aceito (LGPD, art. 8º)
	 * @param codigoImobiliaria código da imobiliária informado pelo vendedor;
	 *                          ignorado quando o perfil é Comprador
	 */
	public void cadastrar(Usuario usuario, String senhaPura, boolean aceitouTermos, String codigoImobiliaria)
			throws RegraNegocioException, DAOException {

		if (!aceitouTermos) {
			throw new RegraNegocioException(
					"É preciso concordar com a Política de Privacidade e os Termos de Uso para criar a conta.");
		}

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
		} else {
			usuario.setIdImobiliaria(null);
		}

		usuario.setEmail(email);
		usuario.setCpf(cpf);
		usuario.setCpfValido(true);
		usuario.setSenha(HashSenha.gerar(senhaPura));
		usuario.setTermosAceitosEm(LocalDateTime.now());

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
	 * Busca a conta pelo e-mail, usado para saber se um login social já tem
	 * cadastro no sistema.
	 */
	public Optional<Usuario> buscarPorEmail(String email) throws DAOException {
		if (email == null) {
			return Optional.empty();
		}
		return usuarioDAO.buscarPorEmail(ValidadorEmail.normalizar(email));
	}

	/**
	 * Conclui o cadastro de quem entrou pela primeira vez via login social
	 * (Google). O e-mail já chega confirmado pelo provedor, mas o CPF ainda é
	 * obrigatório no nosso cadastro — por isso este método é chamado depois que
	 * a pessoa preenche o restante do formulário.
	 *
	 * Como a conta não usa senha própria, é gravado um hash de uma senha
	 * aleatória que nunca é revelada a ninguém: ela existe só para satisfazer a
	 * coluna obrigatória, e não pode ser usada para autenticar.
	 */
	public void cadastrarComLoginSocial(Usuario usuario, boolean aceitouTermos, String codigoImobiliaria)
			throws RegraNegocioException, DAOException {
		String senhaAleatoria = Base64.getEncoder().encodeToString(gerarBytesAleatorios(32));
		cadastrar(usuario, senhaAleatoria, aceitouTermos, codigoImobiliaria);
	}

	private byte[] gerarBytesAleatorios(int tamanho) {
		byte[] bytes = new byte[tamanho];
		geradorAleatorio.nextBytes(bytes);
		return bytes;
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

	private static final int TAMANHO_MAXIMO_APELIDO = 60;

	/**
	 * Atualiza o apelido e o telefone exibidos no perfil. Diferente de
	 * atualizarPerfil, não mexe em nome nem e-mail — usado na tela de perfil,
	 * onde só esses dois campos ficam editáveis.
	 */
	public void atualizarApelidoETelefone(int idUsuario, String apelido, String telefone)
			throws RegraNegocioException, DAOException {

		if (apelido != null && apelido.trim().length() > TAMANHO_MAXIMO_APELIDO) {
			throw new RegraNegocioException("O apelido pode ter no máximo " + TAMANHO_MAXIMO_APELIDO + " caracteres.");
		}

		Usuario atual = usuarioDAO.buscarPorId(idUsuario)
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));

		atual.setApelido(apelido == null || apelido.isBlank() ? null : apelido.trim());
		atual.setTelefone(telefone == null || telefone.isBlank() ? null : telefone.trim());
		usuarioDAO.atualizar(atual);
	}

	/**
	 * Troca a foto de perfil pelo arquivo recém-enviado.
	 *
	 * @param caminhoFoto caminho relativo já salvo em disco (ver PerfilServlet)
	 */
	public void atualizarFotoPerfil(int idUsuario, String caminhoFoto) throws DAOException {
		usuarioDAO.atualizarFotoPerfil(idUsuario, caminhoFoto);
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

	/**
	 * Monta um relatório em texto simples com os dados pessoais do titular,
	 * para atender ao direito de acesso e portabilidade (LGPD, art. 18, I e V).
	 * Não inclui o hash da senha.
	 */
	public String exportarDados(int idUsuario) throws RegraNegocioException, DAOException {
		Usuario usuario = usuarioDAO.buscarPorId(idUsuario)
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));

		StringBuilder relatorio = new StringBuilder();
		relatorio.append("Habittar — seus dados pessoais\n");
		relatorio.append("Gerado em: ").append(LocalDateTime.now()).append("\n");
		relatorio.append("=".repeat(48)).append("\n\n");
		relatorio.append("Nome: ").append(vazioSeNulo(usuario.getNome())).append("\n");
		relatorio.append("Apelido: ").append(vazioSeNulo(usuario.getApelido())).append("\n");
		relatorio.append("E-mail: ").append(vazioSeNulo(usuario.getEmail())).append("\n");
		relatorio.append("CPF: ").append(vazioSeNulo(usuario.getCpf())).append("\n");
		relatorio.append("Telefone: ").append(vazioSeNulo(usuario.getTelefone())).append("\n");
		relatorio.append("Tipo de conta: ")
				.append(usuario.getTipoUsuario() == null ? "" : usuario.getTipoUsuario().getRotulo()).append("\n");
		relatorio.append("Conta criada em: ").append(usuario.getDataCadastro()).append("\n");
		relatorio.append("Termos de privacidade aceitos em: ").append(usuario.getTermosAceitosEm()).append("\n");
		return relatorio.toString();
	}

	private String vazioSeNulo(String valor) {
		return valor == null ? "" : valor;
	}

	/**
	 * Atende ao direito de eliminação dos dados (LGPD, art. 18, VI): remove os
	 * dados de identificação direta da conta. A linha em si é preservada —
	 * anonimizada — porque imóveis, interesses e avaliações de outras pessoas
	 * mantêm chave estrangeira para ela, e o banco não usa ON DELETE CASCADE.
	 * A conta anonimizada não consegue mais autenticar.
	 */
	public void excluirConta(int idUsuario) throws DAOException {
		String emailPlaceholder = "usuario-" + idUsuario + "@removido.habittar.invalid";
		String senhaInutilizavel = HashSenha.gerar(Base64.getEncoder().encodeToString(gerarBytesAleatorios(32)));
		usuarioDAO.anonimizar(idUsuario, emailPlaceholder, senhaInutilizavel);
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
