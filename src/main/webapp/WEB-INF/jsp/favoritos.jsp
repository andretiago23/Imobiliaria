<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Imovel" %>
<%@ page import="model.FotoImovel" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Favoritos | Imobiliária</title>
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
		<h1>Meus favoritos</h1>
		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<%
			List<Imovel> imoveis = (List<Imovel>) request.getAttribute("imoveis");
		%>
		<% if (imoveis == null || imoveis.isEmpty()) { %>
			<p class="texto-vazio">Você ainda não favoritou nenhum imóvel.</p>
		<% } else { %>
		<div class="grade-imoveis">
			<% for (Imovel imovel : imoveis) {
				FotoImovel capa = imovel.getFotoPrincipal();
				String urlFoto = capa != null ? capa.getUrlFoto() : (pageContext.getServletContext().getContextPath() + "/imagens/logo-habittar.png");
			%>
			<div class="card-imovel">
				<a href="${pageContext.request.contextPath}/imovel?id=<%= imovel.getId() %>">
					<img class="card-imovel__foto" src="<%= urlFoto %>" alt="Foto de <%= util.Html.escapar(imovel.getTitulo()) %>">
					<div class="card-imovel__corpo">
						<span class="badge badge-<%= imovel.getStatus().getValorBanco() %>"><%= imovel.getStatus().getRotulo() %></span>
						<div class="card-imovel__preco"><%= util.Formatador.moeda(imovel.getPreco()) %></div>
						<div class="card-imovel__titulo"><%= util.Html.escapar(imovel.getTitulo()) %></div>
						<div class="card-imovel__local"><%= util.Html.escapar(imovel.getCidade()) %></div>
					</div>
				</a>
				<div class="card-imovel__rodape">
					<form method="post" action="${pageContext.request.contextPath}/favoritar">
						<input type="hidden" name="idImovel" value="<%= imovel.getId() %>">
						<input type="hidden" name="origem" value="/favoritos">
						<button type="submit" class="botao botao-discreto">Remover</button>
					</form>
				</div>
			</div>
			<% } %>
		</div>
		<% } %>
	</main>
</body>
</html>
