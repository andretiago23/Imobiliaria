<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Entrar | Habittar</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=3">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css?v=3">
</head>
<body>

<div class="auth">

	<!-- ===================== FORMULÁRIO ===================== -->
	<section class="auth__form">

		<div class="auth__brand auth-anim auth-anim--1">
			<a class="auth__logo" href="${pageContext.request.contextPath}/index.jsp">
				<img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
			</a>
			<a class="voltar" href="${pageContext.request.contextPath}/index.jsp" style="margin-left:auto;">
				<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
				Voltar
			</a>
		</div>

		<div class="auth__form-inner">

			<h1 class="display auth-anim auth-anim--2">Entrar</h1>

			<p class="alerta alerta-erro auth-anim auth-anim--3" role="alert">${erro}${param.erroGoogle}</p>

			<form method="post" action="${pageContext.request.contextPath}/login" id="formLogin" novalidate>

				<input type="hidden" name="redirecionar" value="${redirecionar}">
				<input type="hidden" name="csrf" value="${csrf}">

				<div class="auth__campo auth-anim auth-anim--3">
					<label for="email">E-mail</label>
					<div class="glass-input">
						<input type="email" id="email" name="email" placeholder="voce@email.com" value="${email}"
							autocomplete="email" data-validar="email" required autofocus>
					</div>
					<span class="campo-erro" id="erro-email"></span>
				</div>

				<div class="auth__campo auth-anim auth-anim--4">
					<label for="senha">Senha</label>
					<div class="glass-input glass-input--senha">
						<input type="password" id="senha" name="senha" placeholder="Sua senha"
							autocomplete="current-password" data-validar="senhaObrigatoria" required>
						<button type="button" class="alternar-senha" id="alternarSenha" aria-label="Mostrar senha">
							<svg class="icone-mostrar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7Z"/><circle cx="12" cy="12" r="3"/></svg>
							<svg class="icone-ocultar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 7 11 7a13.16 13.16 0 0 1-1.67 2.68M6.61 6.61C3.06 8.9 1 12 1 12s4 7 11 7a9.26 9.26 0 0 0 5.39-1.61M1 1l22 22"/><path d="M14.12 14.12a3 3 0 1 1-4.24-4.24"/></svg>
						</button>
					</div>
					<span class="campo-erro" id="erro-senha"></span>
				</div>

				<div class="auth__linha auth-anim auth-anim--5">
					<label class="auth__lembrar">
						<input type="checkbox" id="lembrar">
						Lembrar de mim
					</label>
					<a href="#">Esqueceu a senha?</a>
				</div>

				<button type="submit" class="btn btn--primary auth-anim auth-anim--5" style="width:100%;" id="botaoEntrar">Entrar</button>
			</form>

			<div class="auth__divisor auth-anim auth-anim--6">
				<span class="micro">ou continue com</span>
			</div>

			<a class="auth__social auth-anim auth-anim--6" href="${pageContext.request.contextPath}/auth/google?redirecionar=${redirecionar}">
				<svg width="18" height="18" viewBox="0 0 48 48" aria-hidden="true">
					<path fill="#FFC107" d="M43.611 20.083H42V20H24v8h11.303c-1.649 4.657-6.08 8-11.303 8-6.627 0-12-5.373-12-12s5.373-12 12-12c3.059 0 5.842 1.154 7.961 3.039l5.657-5.657C34.046 6.053 29.268 4 24 4 12.955 4 4 12.955 4 24s8.955 20 20 20 20-8.955 20-20c0-2.641-.21-5.236-.611-7.743z"/>
					<path fill="#FF3D00" d="M6.306 14.691l6.571 4.819C14.655 15.108 18.961 12 24 12c3.059 0 5.842 1.154 7.961 3.039l5.657-5.657C34.046 6.053 29.268 4 24 4 16.318 4 9.656 8.337 6.306 14.691z"/>
					<path fill="#4CAF50" d="M24 44c5.166 0 9.86-1.977 13.409-5.192l-6.19-5.238C29.211 35.091 26.715 36 24 36c-5.202 0-9.619-3.317-11.283-7.946l-6.522 5.025C9.505 39.556 16.227 44 24 44z"/>
					<path fill="#1976D2" d="M43.611 20.083H42V20H24v8h11.303c-.792 2.237-2.231 4.166-4.087 5.571l6.19 5.238C42.022 35.026 44 30.038 44 24c0-2.641-.21-5.236-.611-7.743z"/>
				</svg>
				Continuar com Google
			</a>

			<p class="auth__rodape auth-anim auth-anim--7">
				Ainda não tem conta?
				<a href="${pageContext.request.contextPath}/cadastro">Cadastre-se</a>
			</p>
		</div>
	</section>

	<!-- ===================== IMAGEM + DEPOIMENTOS ===================== -->
	<aside class="auth__hero" aria-hidden="true">
		<div class="auth__hero-imagem">
			<img src="${pageContext.request.contextPath}/imagens/mao-chave.jpg" alt="">
		</div>
	</aside>

</div>

<script src="${pageContext.request.contextPath}/js/validacao.js?v=3"></script>
<script src="${pageContext.request.contextPath}/js/formulario.js?v=3"></script>
</body>
</html>
