<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	boolean vendedorSelecionado = "vendedor".equals(request.getAttribute("tipoUsuario"));
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Criar conta | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

	<div class="tela-centralizada">
		<div class="cartao cartao-largo">

			<div class="cartao-cabecalho">
				<h1>Criar conta</h1>
				<p class="texto-apoio">Escolha o perfil de comprador ou vendedor para continuar.</p>
			</div>

			<p class="alerta alerta-erro" role="alert">${erro}</p>

			<form method="post" action="${pageContext.request.contextPath}/cadastro" class="formulario" id="formCadastro">

				<div class="campo">
					<label for="nome">Nome completo</label>
					<input type="text" id="nome" name="nome" value="${nome}" required autofocus>
				</div>

				<div class="linha-campos">
					<div class="campo">
						<label for="email">E-mail</label>
						<input type="email" id="email" name="email" value="${email}" required>
					</div>
					<div class="campo">
						<label for="cpf">CPF</label>
						<input type="text" id="cpf" name="cpf" value="${cpf}" placeholder="Somente números" maxlength="14" required>
					</div>
				</div>

				<div class="linha-campos">
					<div class="campo">
						<label for="senha">Senha</label>
						<input type="password" id="senha" name="senha" minlength="8" required autocomplete="new-password">
					</div>
					<div class="campo">
						<label for="confirmacaoSenha">Confirmar senha</label>
						<input type="password" id="confirmacaoSenha" name="confirmacaoSenha" minlength="8" required autocomplete="new-password">
					</div>
				</div>

				<div class="campo">
					<label for="telefone">Telefone (opcional)</label>
					<input type="tel" id="telefone" name="telefone" value="${telefone}">
				</div>

				<div class="campo">
					<label>Tipo de conta</label>
					<div class="linha-campos">
						<label class="opcao-lembrar">
							<input type="radio" name="tipoUsuario" value="comprador"
								<%= vendedorSelecionado ? "" : "checked" %>
								onclick="alternarCamposVendedor(false)">
							Comprador — quero encontrar um imóvel
						</label>
						<label class="opcao-lembrar">
							<input type="radio" name="tipoUsuario" value="vendedor"
								<%= vendedorSelecionado ? "checked" : "" %>
								onclick="alternarCamposVendedor(true)">
							Vendedor — anuncio imóveis por uma imobiliária
						</label>
					</div>
				</div>

				<div id="blocoVendedor" class="bloco" style="display:none">
					<h2>Dados de vendedor</h2>
					<p class="texto-apoio">
						Todo vendedor precisa estar vinculado a uma imobiliária cadastrada no sistema.
						Se você ainda não tem o código, <a href="${pageContext.request.contextPath}/imobiliarias/nova" target="_blank">cadastre a imobiliária aqui</a> e volte com o código gerado.
					</p>

					<div class="linha-campos">
						<div class="campo">
							<label for="codigoImobiliaria">Código da imobiliária</label>
							<input type="text" id="codigoImobiliaria" name="codigoImobiliaria" value="${codigoImobiliaria}" placeholder="Ex.: IMB-4F82A1">
						</div>
						<div class="campo">
							<label for="creci">CRECI (opcional)</label>
							<input type="text" id="creci" name="creci" value="${creci}" placeholder="Só para constar no seu perfil">
						</div>
					</div>
					<p class="texto-dica">O CRECI não é validado por nenhum órgão real neste protótipo — fica só como informação no seu perfil.</p>
				</div>

				<button type="submit" class="botao botao-principal">Criar conta</button>
			</form>

			<div class="cartao-rodape">
				<p>Já tem conta? <a href="${pageContext.request.contextPath}/login">Entrar</a></p>
			</div>
		</div>
	</div>

	<script>
		function alternarCamposVendedor(mostrar) {
			document.getElementById("blocoVendedor").style.display = mostrar ? "block" : "none";
			document.getElementById("codigoImobiliaria").required = mostrar;
		}
		document.addEventListener("DOMContentLoaded", function () {
			var vendedorSelecionado = document.querySelector('input[name="tipoUsuario"][value="vendedor"]').checked;
			alternarCamposVendedor(vendedorSelecionado);
		});
	</script>
</body>
</html>
