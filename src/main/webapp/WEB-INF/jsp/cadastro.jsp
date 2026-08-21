<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	// Mapeia a mensagem de erro do servidor para o campo correspondente, para
	// que ela apareça embaixo do campo certo em vez de só num aviso genérico.
	// Cobre as validações que só o servidor consegue fazer (e-mail/CPF já
	// cadastrados) — o resto é barrado antes mesmo do envio pelo validacao.js.
	String erroGeral = (String) request.getAttribute("erro");
	String erroCampo = null;
	if (erroGeral != null) {
		String minusculo = erroGeral.toLowerCase();
		if (minusculo.contains("cpf") || minusculo.contains("cnpj")) {
			erroCampo = "cpf";
		} else if (minusculo.contains("e-mail") || minusculo.contains("email")) {
			erroCampo = "email";
		} else if (minusculo.contains("senha")) {
			erroCampo = "senha";
		} else if (minusculo.contains("nome")) {
			erroCampo = "nome";
		} else if (minusculo.contains("celular")) {
			erroCampo = "telefone";
		}
	}
	request.setAttribute("erroCampo", erroCampo);
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Criar conta | Habittar</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=52">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=52">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css?v=52">
</head>
<body>

<div class="auth">

	<!-- ===================== FORMULÁRIO ===================== -->
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

			<h1 class="display auth-anim auth-anim--2">
				<% if (Boolean.TRUE.equals(request.getAttribute("modoGoogle"))) { %>
					Falta pouco
				<% } else { %>
					Criar conta
				<% } %>
			</h1>

			<% if (erroCampo == null) { %>
				<p class="alerta alerta-erro auth-anim auth-anim--3" role="alert">${erro}</p>
			<% } %>

			<form method="post" action="${pageContext.request.contextPath}/cadastro" id="formCadastro" novalidate>

				<input type="hidden" name="csrf" value="${csrf}">

				<div class="auth__campo auth-anim auth-anim--3">
					<label for="nome">Nome completo</label>
					<div class="glass-input">
						<% if (Boolean.TRUE.equals(request.getAttribute("modoGoogle"))) { %>
							<input type="text" id="nome" value="${nome}" disabled>
						<% } else { %>
							<input type="text" id="nome" name="nome" placeholder="Seu nome" value="${nome}" data-validar="nome" required autofocus>
						<% } %>
					</div>
					<span class="campo-erro" id="erro-nome">${erroCampo == 'nome' ? erro : ''}</span>
				</div>

				<div class="auth__campo auth-anim auth-anim--3">
					<label for="email">E-mail</label>
					<div class="glass-input">
						<% if (Boolean.TRUE.equals(request.getAttribute("modoGoogle"))) { %>
							<input type="email" id="email" value="${email}" disabled>
						<% } else { %>
							<input type="email" id="email" name="email" placeholder="voce@email.com" value="${email}" autocomplete="email" data-validar="email" required>
						<% } %>
					</div>
					<span class="campo-erro" id="erro-email">${erroCampo == 'email' ? erro : ''}</span>
				</div>

				<div class="auth__campo auth-anim auth-anim--4">
					<label for="cpf">CPF ou CNPJ</label>
					<div class="glass-input">
						<input type="text" id="cpf" name="cpf" placeholder="Insira seu CPF/CNPJ" inputmode="numeric" maxlength="18"
							value="${cpf}" data-mascara="cpfCnpj" data-validar="cpfCnpj" required>
					</div>
					<span class="campo-erro" id="erro-cpf">${erroCampo == 'cpf' ? erro : ''}</span>
				</div>

				<div class="auth__campo auth-anim auth-anim--4">
					<label for="telefone">Celular</label>
					<div class="glass-input">
						<input type="text" id="telefone" name="telefone" placeholder="(11) 90000-0000" inputmode="numeric" maxlength="15"
							value="${telefone}" data-mascara="telefone" data-validar="telefoneObrigatorio" required>
					</div>
					<span class="campo-erro" id="erro-telefone">${erroCampo == 'telefone' ? erro : ''}</span>
				</div>

				<% if (!Boolean.TRUE.equals(request.getAttribute("modoGoogle"))) { %>
				<div class="auth__campo auth-anim auth-anim--5">
					<label for="senha">Senha</label>
					<div class="glass-input glass-input--senha">
						<input type="password" id="senha" name="senha" placeholder="Mínimo de 8 caracteres" autocomplete="new-password" data-validar="senha" minlength="8" required>
						<button type="button" class="alternar-senha" id="alternarSenha" aria-label="Mostrar senha">
							<svg class="icone-mostrar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7Z"/><circle cx="12" cy="12" r="3"/></svg>
							<svg class="icone-ocultar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 7 11 7a13.16 13.16 0 0 1-1.67 2.68M6.61 6.61C3.06 8.9 1 12 1 12s4 7 11 7a9.26 9.26 0 0 0 5.39-1.61M1 1l22 22"/><path d="M14.12 14.12a3 3 0 1 1-4.24-4.24"/></svg>
						</button>
					</div>
					<span class="campo-erro" id="erro-senha">${erroCampo == 'senha' ? erro : ''}</span>
				</div>

				<div class="auth__campo auth-anim auth-anim--5">
					<label for="confirmacaoSenha">Confirmar senha</label>
					<div class="glass-input">
						<input type="password" id="confirmacaoSenha" name="confirmacaoSenha" placeholder="Repita a senha" autocomplete="new-password" data-validar="confirmacaoSenha" minlength="8" required>
					</div>
					<span class="campo-erro" id="erro-confirmacaoSenha"></span>
				</div>
				<% } %>

				<div class="auth__campo auth__campo--consentimento auth-anim auth-anim--5">
					<label class="auth__lembrar" for="aceiteTermos" style="align-items:flex-start;">
						<input type="checkbox" id="aceiteTermos" name="aceiteTermos" data-validar="aceiteTermos"
							${aceiteTermos ? 'checked' : ''} required>
						<span>Li e concordo com a
							<a href="${pageContext.request.contextPath}/legal/habittar-psi-privacidade.pdf" target="_blank" rel="noopener">Política de Privacidade e os Termos de Uso</a>
							(LGPD).
						</span>
					</label>
					<span class="campo-erro" id="erro-aceiteTermos"></span>
				</div>

				<button type="submit" class="btn btn--primary btn--interactive auth-anim auth-anim--6" style="width:100%;margin-top:8px;" id="botaoCadastrar">
					<span class="btn__label">Criar conta</span>
					<span class="btn__reveal" aria-hidden="true">
						Criar conta
						<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
					</span>
					<span class="btn__dot" aria-hidden="true"></span>
				</button>
			</form>

			<p class="auth__rodape auth-anim auth-anim--7">
				Já tem conta?
				<a href="${pageContext.request.contextPath}/login">Entrar</a>
			</p>
		</div>
	</section>

	<!-- ===================== IMAGEM ===================== -->
	<aside class="auth__hero" aria-hidden="true">
		<div class="auth__hero-imagem">
			<img src="${pageContext.request.contextPath}/imagens/mao-chave.jpg" alt="">
		</div>
	</aside>

</div>

<script src="${pageContext.request.contextPath}/js/validacao.js?v=52"></script>
<script src="${pageContext.request.contextPath}/js/formulario.js?v=52"></script>
</body>
</html>
