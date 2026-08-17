<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.Usuario" %>
<%
	Usuario usuario = (Usuario) request.getAttribute("usuario");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Meu perfil | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

	<header class="barra-topo">
		<a class="marca" href="${pageContext.request.contextPath}/">Imobiliária</a>
		<nav class="menu">
			<a href="${pageContext.request.contextPath}/inicio">Minha conta</a>
		</nav>
	</header>

	<main class="conteudo">
		<h1>Meu perfil</h1>
		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<% if (usuario != null) { %>
		<div class="bloco secao">
			<h2>Meus dados</h2>
			<dl class="lista-dados">
				<dt>Nome</dt><dd><%= util.Html.escapar(usuario.getNome()) %></dd>
				<dt>E-mail</dt><dd><%= util.Html.escapar(usuario.getEmail()) %></dd>
				<dt>Tipo de conta</dt><dd><%= usuario.getTipoUsuario().getRotulo() %></dd>
				<% if (usuario.getTipoUsuario() == model.TipoUsuario.VENDEDOR) { %>
				<dt>CRECI</dt><dd><%= usuario.getCreci() != null ? util.Html.escapar(usuario.getCreci()) : "Não informado" %></dd>
				<dt>Imobiliária</dt>
				<dd><%= usuario.getImobiliaria() != null ? util.Html.escapar(usuario.getImobiliaria().getNome()) + " (código " + usuario.getImobiliaria().getCodigo() + ")" : "-" %></dd>
				<% } %>
			</dl>
		</div>

		<div class="bloco secao">
			<h2>Verificação de crédito</h2>
			<p class="texto-apoio">
				Controla se você autoriza a consulta simulada de crédito ao demonstrar interesse em um imóvel.
				Revogar aqui não desfaz consultas já feitas, só impede novas sem uma autorização explícita de novo.
			</p>
			<form method="post" action="${pageContext.request.contextPath}/perfil">
				<label class="opcao-lembrar">
					<input type="checkbox" name="consentimentoCredito" <%= usuario.isConsentimentoCredito() ? "checked" : "" %> onchange="this.form.submit()">
					Autorizo a consulta simulada de crédito
				</label>
			</form>
		</div>
		<% } %>
	</main>
</body>
</html>
