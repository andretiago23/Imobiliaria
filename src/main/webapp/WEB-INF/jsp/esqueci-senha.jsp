<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Esqueci minha senha | Habittar</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=67">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=67">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css?v=67">
</head>
<body>

<div class="auth">

	<section class="auth__form">

		<div class="auth__brand auth-anim auth-anim--1">
			<a class="auth__logo" href="${pageContext.request.contextPath}/index.jsp">
				<img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
			</a>
			<a class="voltar" href="${pageContext.request.contextPath}/login" style="margin-left:auto;">
				<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
				Voltar
			</a>
		</div>

		<div class="auth__form-inner">

			<% if (Boolean.TRUE.equals(request.getAttribute("enviado"))) { %>

				<h1 class="display auth-anim auth-anim--2">Verifique seu e-mail</h1>
				<p class="lead auth-anim auth-anim--3" style="max-width:none;margin-top:8px;">
					Se houver uma conta cadastrada com esse e-mail, mandamos um link pra você escolher uma senha nova.
					Confira também a caixa de spam.
				</p>
				<a class="btn btn--primary btn--interactive auth-anim auth-anim--4" style="width:100%;margin-top:24px;" href="${pageContext.request.contextPath}/login">
					<span class="btn__label">Voltar para o login</span>
					<span class="btn__reveal" aria-hidden="true">
						Voltar para o login
						<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
					</span>
					<span class="btn__dot" aria-hidden="true"></span>
				</a>

			<% } else { %>

				<h1 class="display auth-anim auth-anim--2">Esqueceu a senha?</h1>
				<p class="lead auth-anim auth-anim--3" style="max-width:none;margin-top:8px;">
					Informe o e-mail da sua conta — vamos mandar um link pra você escolher uma senha nova.
				</p>

				<form method="post" action="${pageContext.request.contextPath}/esqueci-senha" id="formEsqueciSenha" novalidate style="margin-top:24px;">
					<input type="hidden" name="csrf" value="${csrf}">

					<div class="auth__campo auth-anim auth-anim--3">
						<label for="email">E-mail</label>
						<div class="glass-input">
							<input type="email" id="email" name="email" placeholder="voce@email.com"
								autocomplete="email" data-validar="email" required autofocus>
						</div>
						<span class="campo-erro" id="erro-email"></span>
					</div>

					<button type="submit" class="btn btn--primary btn--interactive auth-anim auth-anim--4" style="width:100%;margin-top:8px;">
						<span class="btn__label">Enviar link</span>
						<span class="btn__reveal" aria-hidden="true">
							Enviar link
							<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
						</span>
						<span class="btn__dot" aria-hidden="true"></span>
					</button>
				</form>

			<% } %>

			<p class="auth__rodape auth-anim auth-anim--7">
				Lembrou a senha?
				<a href="${pageContext.request.contextPath}/login">Entrar</a>
			</p>
		</div>
	</section>

	<aside class="auth__hero" aria-hidden="true">
		<div class="auth__hero-imagem">
			<img src="${pageContext.request.contextPath}/imagens/mao-chave.jpg" alt="">
		</div>
	</aside>

</div>

<script src="${pageContext.request.contextPath}/js/validacao.js?v=67"></script>
<script src="${pageContext.request.contextPath}/js/formulario.js?v=67"></script>
</body>
</html>
