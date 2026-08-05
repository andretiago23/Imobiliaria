<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Entrar | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body class="tela-centralizada">

	<main class="cartao">
		<header class="cartao-cabecalho">
			<h1>Entrar</h1>
			<p class="texto-apoio">Acesse sua conta para anunciar e favoritar imóveis.</p>
		</header>

		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<form method="post" action="${pageContext.request.contextPath}/login" class="formulario">
			<div class="campo">
				<label for="email">E-mail</label>
				<input type="email" id="email" name="email" value="${email}"
					autocomplete="email" required autofocus>
			</div>

			<div class="campo">
				<label for="senha">Senha</label>
				<input type="password" id="senha" name="senha"
					autocomplete="current-password" required>
			</div>

			<button type="submit" class="botao botao-principal">Entrar</button>
		</form>

		<footer class="cartao-rodape">
			<p>
				Ainda não tem conta?
				<a href="${pageContext.request.contextPath}/cadastro">Cadastre-se</a>
			</p>
		</footer>
	</main>

</body>
</html>
