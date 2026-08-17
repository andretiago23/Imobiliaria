<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.ContatoInteresse" %>
<%@ page import="model.StatusContato" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Meus leads | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

	<header class="barra-topo">
		<a class="marca" href="${pageContext.request.contextPath}/">Imobiliária</a>
		<nav class="menu">
			<a href="${pageContext.request.contextPath}/meus-imoveis">Meus imóveis</a>
			<a href="${pageContext.request.contextPath}/inicio">Minha conta</a>
		</nav>
	</header>

	<main class="conteudo">
		<h1>Meus leads</h1>
		<p class="texto-apoio">Mensagens de interesse recebidas nos seus anúncios. Atualize a situação conforme a negociação avança.</p>
		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<%
			List<ContatoInteresse> leads = (List<ContatoInteresse>) request.getAttribute("leads");
		%>
		<% if (leads == null || leads.isEmpty()) { %>
			<p class="texto-vazio">Você ainda não recebeu nenhum lead.</p>
		<% } else { %>
		<div class="tabela-wrapper">
			<table class="tabela">
				<thead>
					<tr>
						<th>Imóvel</th>
						<th>Cliente</th>
						<th>Mensagem</th>
						<th>Crédito</th>
						<th>Recebido em</th>
						<th>Situação</th>
					</tr>
				</thead>
				<tbody>
					<% for (ContatoInteresse lead : leads) { %>
					<tr>
						<td><a href="${pageContext.request.contextPath}/imovel?id=<%= lead.getImovel().getId() %>"><%= util.Html.escapar(lead.getImovel().getTitulo()) %></a></td>
						<td>
							<%= util.Html.escapar(lead.getComprador().getNome()) %><br>
							<span class="texto-dica"><%= util.Html.escapar(lead.getComprador().getEmail()) %></span>
						</td>
						<td class="celula-larga"><%= util.Html.escapar(lead.getMensagem()) %></td>
						<td>
							<% if (lead.isConsultaCreditoAutorizada()) { %>
								<%= lead.getResultadoCredito().getRotulo() %>
							<% } else { %>
								<span class="texto-dica">Não solicitada</span>
							<% } %>
						</td>
						<td><%= util.Formatador.dataHora(lead.getDataContato()) %></td>
						<td>
							<form method="post" action="${pageContext.request.contextPath}/meus-leads">
								<input type="hidden" name="idContato" value="<%= lead.getId() %>">
								<select name="status" onchange="this.form.submit()">
									<% for (StatusContato status : StatusContato.values()) { %>
									<option value="<%= status.getValorBanco() %>" <%= lead.getStatus() == status ? "selected" : "" %>><%= status.getRotulo() %></option>
									<% } %>
								</select>
							</form>
						</td>
					</tr>
					<% } %>
				</tbody>
			</table>
		</div>
		<% } %>
	</main>
</body>
</html>
