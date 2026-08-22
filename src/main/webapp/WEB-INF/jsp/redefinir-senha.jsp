<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Redefinir senha | Habittar</title>
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
		</div>

		<div class="auth__form-inner">

			<% if (!Boolean.TRUE.equals(request.getAttribute("tokenValido"))) { %>

				<h1 class="display auth-anim auth-anim--2">Link inválido ou expirado</h1>
				<p class="lead auth-anim auth-anim--3" style="max-width:none;margin-top:8px;">
					Esse link de redefinição já foi usado, ou já passou das 2 horas de validade. Peça um novo.
				</p>
				<a class="btn btn--primary btn--interactive auth-anim auth-anim--4" style="width:100%;margin-top:24px;" href="${pageContext.request.contextPath}/esqueci-senha">
					<span class="btn__label">Pedir novo link</span>
					<span class="btn__reveal" aria-hidden="true">
						Pedir novo link
						<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
					</span>
					<span class="btn__dot" aria-hidden="true"></span>
				</a>

			<% } else { %>

				<h1 class="display auth-anim auth-anim--2">Escolha uma senha nova</h1>

				<p class="alerta alerta-erro auth-anim auth-anim--3" role="alert">${erro}</p>

				<form method="post" action="${pageContext.request.contextPath}/redefinir-senha" id="formRedefinirSenha" novalidate style="margin-top:8px;">
					<input type="hidden" name="csrf" value="${csrf}">
					<input type="hidden" name="token" value="${token}">

					<div class="auth__campo auth-anim auth-anim--3">
						<label for="senha">Nova senha</label>
						<div class="glass-input glass-input--senha">
							<input type="password" id="senha" name="senha" placeholder="Pelo menos 8 caracteres"
								autocomplete="new-password" data-validar="senha" required>
							<button type="button" class="alternar-senha" aria-label="Mostrar senha">
								<svg class="icone-mostrar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7Z"/><circle cx="12" cy="12" r="3"/></svg>
								<svg class="icone-ocultar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 7 11 7a13.16 13.16 0 0 1-1.67 2.68M6.61 6.61C3.06 8.9 1 12 1 12s4 7 11 7a9.26 9.26 0 0 0 5.39-1.61M1 1l22 22"/><path d="M14.12 14.12a3 3 0 1 1-4.24-4.24"/></svg>
							</button>
						</div>
						<span class="campo-erro" id="erro-senha"></span>
					</div>

					<div class="auth__campo auth-anim auth-anim--4">
						<label for="confirmarSenha">Confirmar nova senha</label>
						<div class="glass-input glass-input--senha">
							<input type="password" id="confirmarSenha" name="confirmarSenha" placeholder="Repita a senha"
								autocomplete="new-password" data-validar="confirmacaoSenha" required>
							<button type="button" class="alternar-senha" aria-label="Mostrar senha">
								<svg class="icone-mostrar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7Z"/><circle cx="12" cy="12" r="3"/></svg>
								<svg class="icone-ocultar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 7 11 7a13.16 13.16 0 0 1-1.67 2.68M6.61 6.61C3.06 8.9 1 12 1 12s4 7 11 7a9.26 9.26 0 0 0 5.39-1.61M1 1l22 22"/><path d="M14.12 14.12a3 3 0 1 1-4.24-4.24"/></svg>
							</button>
						</div>
						<span class="campo-erro" id="erro-confirmarSenha"></span>
					</div>

					<button type="submit" class="btn btn--primary btn--interactive auth-anim auth-anim--5" style="width:100%;margin-top:8px;">
						<span class="btn__label">Salvar nova senha</span>
						<span class="btn__reveal" aria-hidden="true">
							Salvar nova senha
							<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
						</span>
						<span class="btn__dot" aria-hidden="true"></span>
					</button>
				</form>

			<% } %>
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
