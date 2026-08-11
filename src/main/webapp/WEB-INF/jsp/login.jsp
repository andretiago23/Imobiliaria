<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Entrar | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>

	<div class="tela-login">

		<div class="coluna-formulario">
			<form method="post" action="${pageContext.request.contextPath}/login" class="formulario-login" id="formLogin">

				<h2>Entre ou crie sua conta</h2>
				<p class="texto-apoio">Bem-vindo(a) de volta! Acesse sua conta para continuar.</p>

				<!-- Mensagem de erro vinda do LoginServlet (fica escondida sozinha se estiver vazia) -->
				<p class="alerta alerta-erro" role="alert">${erro}</p>

				<div class="campo-input campo-input-topo">
					<svg width="16" height="11" viewBox="0 0 16 11" fill="none" xmlns="http://www.w3.org/2000/svg">
						<path fill-rule="evenodd" clip-rule="evenodd"
							d="M0 .55.571 0H15.43l.57.55v9.9l-.571.55H.57L0 10.45zm1.143 1.138V9.9h13.714V1.69l-6.503 4.8h-.697zM13.749 1.1H2.25L8 5.356z"
							fill="#6B7280" />
					</svg>
					<input type="email" id="email" name="email" placeholder="E-mail" value="${email}"
						autocomplete="email" required autofocus>
				</div>

				<div class="campo-input campo-senha">
					<svg width="13" height="17" viewBox="0 0 13 17" fill="none" xmlns="http://www.w3.org/2000/svg">
						<path
							d="M13 8.5c0-.938-.729-1.7-1.625-1.7h-.812V4.25C10.563 1.907 8.74 0 6.5 0S2.438 1.907 2.438 4.25V6.8h-.813C.729 6.8 0 7.562 0 8.5v6.8c0 .938.729 1.7 1.625 1.7h9.75c.896 0 1.625-.762 1.625-1.7zM4.063 4.25c0-1.406 1.093-2.55 2.437-2.55s2.438 1.144 2.438 2.55V6.8H4.061z"
							fill="#6B7280" />
					</svg>
					<input type="password" id="senha" name="senha" placeholder="Senha"
						autocomplete="current-password" required>
					<button type="button" class="alternar-senha" id="alternarSenha" aria-label="Mostrar senha">👁</button>
				</div>

				<div class="linha-opcoes">
					<label class="opcao-lembrar">
						<input type="checkbox" id="lembrar">
						Lembrar de mim
					</label>
					<a href="#">Esqueceu a senha?</a>
				</div>

				<button type="submit" class="botao-entrar" id="botaoEntrar">Entrar</button>

				<p class="texto-cadastro">
					Ainda não tem conta?
					<a href="${pageContext.request.contextPath}/cadastro">Cadastre-se</a>
				</p>
			</form>
		</div>

		<!-- Coluna da imagem: escondida em telas pequenas, igual ao "hidden md:inline-block" do original -->
		<div class="coluna-imagem">
			<img src="${pageContext.request.contextPath}/imagens/mao-chave.jpg"
				alt="Ilustração de login">
		</div>
	</div>

	<script src="${pageContext.request.contextPath}/js/login.js"></script>
</body>
</html>
