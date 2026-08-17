<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.Imovel" %>
<%@ page import="model.FotoImovel" %>
<%@ page import="model.Usuario" %>
<%
	Imovel imovel = (Imovel) request.getAttribute("imovel");
	boolean temSimulacaoPendente = session.getAttribute(controller.SimuladorServlet.ATRIBUTO_SIMULACAO) != null;
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= imovel != null ? util.Html.escapar(imovel.getTitulo()) : "Imóvel" %> | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

	<header class="barra-topo">
		<a class="marca" href="${pageContext.request.contextPath}/">Imobiliária</a>
		<nav class="menu">
			<a href="${pageContext.request.contextPath}/imoveis">Catálogo</a>
			<a href="${pageContext.request.contextPath}/favoritos">Favoritos</a>
			<a href="${pageContext.request.contextPath}/inicio">Minha conta</a>
		</nav>
	</header>

	<main class="conteudo">
		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<% if (imovel != null) {
			Usuario dono = imovel.getDono();
		%>
		<div class="secao-cabecalho">
			<div>
				<span class="badge badge-<%= imovel.getStatus().getValorBanco() %>"><%= imovel.getStatus().getRotulo() %></span>
				<h1><%= util.Html.escapar(imovel.getTitulo()) %></h1>
				<p class="texto-apoio">Código do imóvel: #<%= imovel.getId() %> · Anunciado em <%= util.Formatador.data(imovel.getDataPublicacao()) %></p>
			</div>
			<form method="post" action="${pageContext.request.contextPath}/favoritar">
				<input type="hidden" name="idImovel" value="<%= imovel.getId() %>">
				<input type="hidden" name="origem" value="/imovel?id=<%= imovel.getId() %>">
				<button type="submit" class="botao botao-discreto">
					<%= Boolean.TRUE.equals(request.getAttribute("jaFavoritado")) ? "♥ Remover dos favoritos" : "♡ Favoritar" %>
				</button>
			</form>
		</div>

		<% if (!imovel.getFotos().isEmpty()) { %>
		<div class="grade-imoveis secao">
			<% for (FotoImovel foto : imovel.getFotos()) { %>
			<img class="card-imovel__foto" src="<%= foto.getUrlFoto() %>" alt="Foto do imóvel">
			<% } %>
		</div>
		<% } %>

		<div class="painel-indicadores">
			<div class="indicador">
				<span class="indicador-rotulo">Valor</span>
				<span class="indicador-valor"><%= util.Formatador.moeda(imovel.getPreco()) %></span>
				<span class="indicador-apoio"><%= imovel.getFinalidade().getRotulo() %></span>
			</div>
			<div class="indicador">
				<span class="indicador-rotulo">Área</span>
				<span class="indicador-valor"><%= util.Formatador.area(imovel.getAreaM2()) %></span>
				<span class="indicador-apoio"><%= util.Formatador.moeda(imovel.getPrecoPorM2()) %> / m²</span>
			</div>
			<div class="indicador">
				<span class="indicador-rotulo">Cômodos</span>
				<span class="indicador-valor"><%= imovel.getQuartos() %> qts · <%= imovel.getBanheiros() %> ban.</span>
				<span class="indicador-apoio"><%= imovel.getVagasGaragem() %> vaga(s) de garagem</span>
			</div>
		</div>

		<div class="bloco secao">
			<h2>Ficha técnica</h2>
			<dl class="lista-dados">
				<dt>Tipo</dt><dd><%= imovel.getTipo().getRotulo() %></dd>
				<dt>Operação</dt><dd><%= imovel.getFinalidade().getRotulo() %></dd>
				<dt>Endereço</dt><dd><%= util.Html.escapar(imovel.getEnderecoCompleto()) %></dd>
				<dt>CEP</dt><dd><%= imovel.getCep() != null ? util.Html.escapar(imovel.getCep()) : "-" %></dd>
				<dt>Ano de construção</dt><dd><%= imovel.getAno() != null ? imovel.getAno() : "-" %></dd>
				<dt>Vendedor responsável</dt><dd><%= dono != null ? util.Html.escapar(dono.getNome()) : "-" %><%= dono != null && dono.getCreci() != null ? " (CRECI " + util.Html.escapar(dono.getCreci()) + ")" : "" %></dd>
				<dt>Imobiliária vinculada</dt>
				<dd><%= dono != null && dono.getImobiliaria() != null ? util.Html.escapar(dono.getImobiliaria().getNome()) : "-" %></dd>
			</dl>
		</div>

		<div class="bloco secao">
			<h2>Descrição</h2>
			<p><%= imovel.getDescricao() != null ? util.Html.escapar(imovel.getDescricao()) : "Sem descrição informada." %></p>
		</div>

		<div class="bloco secao">
			<h2>Tenho interesse</h2>
			<p class="texto-apoio">O envio desta mensagem é o que gera o lead para o vendedor — só olhar o anúncio não gera nada.</p>
			<form method="post" action="${pageContext.request.contextPath}/interesse" class="formulario">
				<input type="hidden" name="idImovel" value="<%= imovel.getId() %>">

				<div class="campo">
					<label for="mensagem">Mensagem para o vendedor</label>
					<textarea id="mensagem" name="mensagem" rows="3" required placeholder="Ex.: Gostaria de agendar uma visita."></textarea>
				</div>

				<label class="opcao-lembrar">
					<input type="checkbox" name="autorizarCredito">
					Autorizo a consulta do meu CPF em birô de crédito para agilizar minha análise (simulada, sem integração real)
				</label>

				<% if (temSimulacaoPendente) { %>
				<label class="opcao-lembrar">
					<input type="checkbox" name="anexarSimulacao" checked>
					Enviar minha última simulação de financiamento junto
				</label>
				<% } else { %>
				<p class="texto-dica">Quer anexar uma simulação de financiamento? <a href="${pageContext.request.contextPath}/simulador?idImovelOrigem=<%= imovel.getId() %>&valorImovel=<%= imovel.getPreco() %>">Simule antes de enviar</a>.</p>
				<% } %>

				<button type="submit" class="botao botao-principal" <%= !imovel.estaDisponivel() ? "disabled" : "" %>>Tenho interesse</button>
				<% if (!imovel.estaDisponivel()) { %><p class="texto-dica">Este anúncio não está mais aceitando novos interesses.</p><% } %>
			</form>
		</div>

		<p><a href="${pageContext.request.contextPath}/comparar?ids=<%= imovel.getId() %>">Comparar este imóvel com outros →</a></p>

		<% } else { %>
			<p class="texto-vazio">Imóvel não encontrado.</p>
		<% } %>
	</main>
</body>
</html>
