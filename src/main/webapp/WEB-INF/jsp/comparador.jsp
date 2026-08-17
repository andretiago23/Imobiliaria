<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Imovel" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Comparar imóveis | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

	<header class="barra-topo">
		<a class="marca" href="${pageContext.request.contextPath}/">Imobiliária</a>
		<nav class="menu">
			<a href="${pageContext.request.contextPath}/imoveis">Catálogo</a>
			<a href="${pageContext.request.contextPath}/inicio">Minha conta</a>
		</nav>
	</header>

	<main class="conteudo">
		<h1>Comparar imóveis</h1>
		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<%
			List<Imovel> imoveis = (List<Imovel>) request.getAttribute("imoveis");
		%>
		<% if (imoveis != null && !imoveis.isEmpty()) { %>
		<div class="tabela-wrapper">
			<table class="tabela">
				<thead>
					<tr>
						<th>Característica</th>
						<% for (Imovel imovel : imoveis) { %>
						<th><a href="${pageContext.request.contextPath}/imovel?id=<%= imovel.getId() %>"><%= util.Html.escapar(imovel.getTitulo()) %></a></th>
						<% } %>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Preço</td>
						<% for (Imovel imovel : imoveis) { %><td><%= util.Formatador.moeda(imovel.getPreco()) %></td><% } %>
					</tr>
					<tr>
						<td>Área</td>
						<% for (Imovel imovel : imoveis) { %><td><%= util.Formatador.area(imovel.getAreaM2()) %></td><% } %>
					</tr>
					<tr>
						<td>Preço por m²</td>
						<% for (Imovel imovel : imoveis) { %><td><%= util.Formatador.moeda(imovel.getPrecoPorM2()) %></td><% } %>
					</tr>
					<tr>
						<td>Quartos</td>
						<% for (Imovel imovel : imoveis) { %><td><%= imovel.getQuartos() %></td><% } %>
					</tr>
					<tr>
						<td>Banheiros</td>
						<% for (Imovel imovel : imoveis) { %><td><%= imovel.getBanheiros() %></td><% } %>
					</tr>
					<tr>
						<td>Vagas de garagem</td>
						<% for (Imovel imovel : imoveis) { %><td><%= imovel.getVagasGaragem() %></td><% } %>
					</tr>
					<tr>
						<td>Ano</td>
						<% for (Imovel imovel : imoveis) { %><td><%= imovel.getAno() != null ? imovel.getAno() : "-" %></td><% } %>
					</tr>
				</tbody>
			</table>
		</div>
		<% } %>

		<p><a href="${pageContext.request.contextPath}/imoveis">← Voltar ao catálogo</a></p>
	</main>
</body>
</html>
