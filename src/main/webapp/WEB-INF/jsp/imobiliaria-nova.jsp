<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Cadastrar imobiliária | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

	<div class="tela-centralizada">
		<div class="cartao cartao-largo">

			<div class="cartao-cabecalho">
				<h1>Cadastrar imobiliária</h1>
				<p class="texto-apoio">Cadastre aqui a imobiliária de exemplo. Você recebe um código único para repassar aos vendedores dela usarem no cadastro deles.</p>
			</div>

			<p class="alerta alerta-erro" role="alert">${erro}</p>

			<% if (request.getAttribute("codigoGerado") != null) { %>
				<p>Imobiliária <strong>${nomeCadastrado}</strong> cadastrada com sucesso.</p>
				<p>Código de vínculo para os vendedores:</p>
				<span class="codigo-gerado">${codigoGerado}</span>
				<p class="texto-dica">Guarde este código — ele não é mostrado de novo automaticamente.</p>
				<div class="cartao-rodape">
					<a href="${pageContext.request.contextPath}/cadastro" class="botao botao-principal">Ir para o cadastro de vendedor</a>
				</div>
			<% } else { %>
				<form method="post" action="${pageContext.request.contextPath}/imobiliarias/nova" class="formulario">
					<div class="campo">
						<label for="nome">Nome da imobiliária</label>
						<input type="text" id="nome" name="nome" value="${nome}" required autofocus>
					</div>
					<div class="linha-campos">
						<div class="campo">
							<label for="cnpj">CNPJ (opcional)</label>
							<input type="text" id="cnpj" name="cnpj" value="${cnpj}">
						</div>
						<div class="campo">
							<label for="telefone">Telefone (opcional)</label>
							<input type="text" id="telefone" name="telefone" value="${telefone}">
						</div>
					</div>
					<div class="linha-campos">
						<div class="campo">
							<label for="email">E-mail (opcional)</label>
							<input type="email" id="email" name="email" value="${email}">
						</div>
						<div class="campo">
							<label for="cidade">Cidade (opcional)</label>
							<input type="text" id="cidade" name="cidade" value="${cidade}">
						</div>
						<div class="campo">
							<label for="estado">UF (opcional)</label>
							<input type="text" id="estado" name="estado" maxlength="2" value="${estado}">
						</div>
					</div>
					<button type="submit" class="botao botao-principal">Cadastrar e gerar código</button>
				</form>
			<% } %>
		</div>
	</div>
</body>
</html>
