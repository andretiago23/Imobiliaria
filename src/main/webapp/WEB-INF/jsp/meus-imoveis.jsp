<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Imovel" %>
<%@ page import="model.StatusImovel" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Meus imóveis | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

	<header class="barra-topo">
		<a class="marca" href="${pageContext.request.contextPath}/">Imobiliária</a>
		<nav class="menu">
			<a href="${pageContext.request.contextPath}/anunciar">Anunciar imóvel</a>
			<a href="${pageContext.request.contextPath}/meus-leads">Meus leads</a>
			<a href="${pageContext.request.contextPath}/inicio">Minha conta</a>
		</nav>
	</header>

	<main class="conteudo">
		<div class="secao-cabecalho">
			<h1>Meus imóveis</h1>
			<a href="${pageContext.request.contextPath}/anunciar" class="botao botao-principal">Anunciar novo imóvel</a>
		</div>
		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<%
			List<Imovel> imoveis = (List<Imovel>) request.getAttribute("imoveis");
		%>
		<% if (imoveis == null || imoveis.isEmpty()) { %>
			<p class="texto-vazio">Você ainda não anunciou nenhum imóvel.</p>
		<% } else { %>
		<div class="tabela-wrapper">
			<table class="tabela">
				<thead>
					<tr>
						<th>Título</th>
						<th>Cidade</th>
						<th>Valor</th>
						<th>Status</th>
						<th>Publicado em</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<% for (Imovel imovel : imoveis) { %>
					<tr>
						<td><%= util.Html.escapar(imovel.getTitulo()) %></td>
						<td><%= imovel.getCidade() != null ? util.Html.escapar(imovel.getCidade()) : "-" %></td>
						<td><%= util.Formatador.moeda(imovel.getPreco()) %></td>
						<td>
							<form method="post" action="${pageContext.request.contextPath}/meus-imoveis" style="display:inline-flex;gap:6px;align-items:center">
								<input type="hidden" name="idImovel" value="<%= imovel.getId() %>">
								<select name="status" onchange="this.form.submit()">
									<% for (StatusImovel status : StatusImovel.values()) { %>
									<option value="<%= status.getValorBanco() %>" <%= imovel.getStatus() == status ? "selected" : "" %>><%= status.getRotulo() %></option>
									<% } %>
								</select>
							</form>
						</td>
						<td><%= util.Formatador.data(imovel.getDataPublicacao()) %></td>
						<td><a href="${pageContext.request.contextPath}/anunciar?id=<%= imovel.getId() %>">Editar</a></td>
					</tr>
					<% } %>
				</tbody>
			</table>
		</div>
		<% } %>
	</main>
</body>
</html>
