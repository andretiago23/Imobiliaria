<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.ContatoInteresse" %>
<%
	ContatoInteresse lead = (ContatoInteresse) request.getAttribute("lead");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Interesse registrado | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

	<div class="tela-centralizada">
		<div class="cartao">
			<div class="cartao-cabecalho">
				<h1>Interesse registrado!</h1>
				<p class="texto-apoio">O vendedor responsável pelo imóvel recebeu sua mensagem e vai entrar em contato.</p>
			</div>

			<% if (lead != null) { %>
			<dl class="lista-dados">
				<dt>Situação</dt><dd><span class="badge badge-ativo"><%= lead.getStatus().getRotulo() %></span></dd>
				<% if (lead.isConsultaCreditoAutorizada()) { %>
				<dt>Verificação de crédito</dt>
				<dd><%= lead.getResultadoCredito().getRotulo() %></dd>
				<% } %>
			</dl>
			<% if (lead.isConsultaCreditoAutorizada()) { %>
			<p class="texto-dica">Simulação ilustrativa e verificação fictícia. Sujeitas à análise real da instituição financeira e do birô de crédito.</p>
			<% } %>
			<% } %>

			<div class="cartao-rodape">
				<a href="${pageContext.request.contextPath}/imoveis" class="botao botao-principal">Voltar ao catálogo</a>
			</div>
		</div>
	</div>
</body>
</html>
