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
		if (minusculo.contains("cpf")) {
			erroCampo = "cpf";
		} else if (minusculo.contains("e-mail") || minusculo.contains("email")) {
			erroCampo = "email";
		} else if (minusculo.contains("senha")) {
			erroCampo = "senha";
		} else if (minusculo.contains("nome")) {
			erroCampo = "nome";
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
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>

<div class="auth">

	<!-- ===================== FORMULÁRIO ===================== -->
	<section class="auth__form">
		<div class="auth__form-inner">

			<a class="voltar auth-anim auth-anim--1" href="${pageContext.request.contextPath}/login">
				<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
				Voltar
			</a>

			<a class="auth__logo auth-anim auth-anim--1" href="${pageContext.request.contextPath}/index.jsp">
				<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#FF6A1A" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
					<path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/>
					<path d="M9 11.2 12 8.8l3 2.4V14h-6z"/>
				</svg>
				Habittar
			</a>

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
					<label for="cpf">CPF</label>
					<div class="glass-input">
						<input type="text" id="cpf" name="cpf" placeholder="000.000.000-00" inputmode="numeric" maxlength="14"
							value="${cpf}" data-mascara="cpf" data-validar="cpf" required>
					</div>
					<span class="campo-erro" id="erro-cpf">${erroCampo == 'cpf' ? erro : ''}</span>
				</div>

				<div class="auth__campo auth-anim auth-anim--4">
					<label for="telefone">Telefone <span class="micro">(opcional)</span></label>
					<div class="glass-input">
						<input type="text" id="telefone" name="telefone" placeholder="(11) 90000-0000" inputmode="numeric" maxlength="15"
							value="${telefone}" data-mascara="telefone" data-validar="telefone">
					</div>
					<span class="campo-erro" id="erro-telefone"></span>
				</div>

				<div class="auth__campo auth-anim auth-anim--4">
					<label for="tipoUsuario">Tipo de conta</label>
					<div class="glass-input">
						<select id="tipoUsuario" name="tipoUsuario" style="width:100%;background:transparent;border:0;padding:14px 16px;font-family:var(--font-sans);font-size:15px;color:var(--text-primary);">
							<option value="comprador" ${tipoUsuario == 'comprador' ? 'selected' : ''}>Comprador — quero buscar imóveis</option>
							<option value="vendedor" ${tipoUsuario == 'vendedor' ? 'selected' : ''}>Vendedor / imobiliária — quero anunciar imóveis</option>
						</select>
					</div>
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

				<button type="submit" class="btn btn--primary auth-anim auth-anim--6" style="width:100%;margin-top:8px;" id="botaoCadastrar">Criar conta</button>
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

<script src="${pageContext.request.contextPath}/js/validacao.js"></script>
<script src="${pageContext.request.contextPath}/js/formulario.js"></script>
</body>
</html>
