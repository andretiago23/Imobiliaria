<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Criar conta | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body class="tela-centralizada">

	<main class="cartao cartao-largo">
		<header class="cartao-cabecalho">
			<h1>Criar conta</h1>
			<p class="texto-apoio">Preencha seus dados para começar a usar a plataforma.</p>
		</header>

		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<form method="post" action="${pageContext.request.contextPath}/cadastro" class="formulario">

			<div class="campo">
				<label for="nome">Nome completo</label>
				<input type="text" id="nome" name="nome" value="${nome}" maxlength="100"
					autocomplete="name" required autofocus>
			</div>

			<div class="campo">
				<label for="email">E-mail</label>
				<input type="email" id="email" name="email" value="${email}" maxlength="150"
					autocomplete="email" required>
			</div>

			<div class="linha-campos">
				<div class="campo">
					<label for="cpf">CPF</label>
					<input type="text" id="cpf" name="cpf" value="${cpf}" maxlength="14"
						inputmode="numeric" placeholder="000.000.000-00" required>
					<small class="texto-dica">Somente números ou com pontuação.</small>
				</div>

				<div class="campo">
					<label for="telefone">Telefone <span class="texto-dica">(opcional)</span></label>
					<input type="tel" id="telefone" name="telefone" value="${telefone}"
						maxlength="20" autocomplete="tel" placeholder="(00) 00000-0000">
				</div>
			</div>

			<div class="campo">
				<label for="tipoUsuario">Como você vai usar a plataforma?</label>
				<select id="tipoUsuario" name="tipoUsuario" required>
					<option value="ambos" ${tipoUsuario == 'ambos' or empty tipoUsuario ? 'selected' : ''}>
						Comprar e anunciar imóveis
					</option>
					<option value="comprador" ${tipoUsuario == 'comprador' ? 'selected' : ''}>
						Apenas comprar ou alugar
					</option>
					<option value="vendedor" ${tipoUsuario == 'vendedor' ? 'selected' : ''}>
						Apenas anunciar imóveis
					</option>
				</select>
			</div>

			<div class="linha-campos">
				<div class="campo">
					<label for="senha">Senha</label>
					<input type="password" id="senha" name="senha" minlength="8"
						autocomplete="new-password" required>
					<small class="texto-dica">No mínimo 8 caracteres.</small>
				</div>

				<div class="campo">
					<label for="confirmacaoSenha">Confirmar senha</label>
					<input type="password" id="confirmacaoSenha" name="confirmacaoSenha"
						minlength="8" autocomplete="new-password" required>
				</div>
			</div>

			<button type="submit" class="botao botao-principal">Criar conta</button>
		</form>

		<footer class="cartao-rodape">
			<p>
				Já tem conta?
				<a href="${pageContext.request.contextPath}/login">Entrar</a>
			</p>
		</footer>
	</main>

</body>
</html>
