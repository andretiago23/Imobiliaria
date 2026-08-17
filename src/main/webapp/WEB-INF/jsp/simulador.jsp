<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.SimulacaoFinanciamento" %>
<%
	SimulacaoFinanciamento resultado = (SimulacaoFinanciamento) request.getAttribute("resultado");
	Object idImovelOrigem = request.getAttribute("idImovelOrigem");
	Object valorImovelSugerido = request.getAttribute("valorImovelSugerido");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Simulador de financiamento | Imobiliária</title>
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
		<h1>Simulador de financiamento</h1>
		<p class="texto-apoio">Sistema SAC (amortização constante). Taxas fictícias, só para ilustração do protótipo.</p>
		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<form method="post" action="${pageContext.request.contextPath}/simulador" class="formulario cartao cartao-largo">
			<% if (idImovelOrigem != null) { %><input type="hidden" name="idImovelOrigem" value="<%= idImovelOrigem %>"><% } %>

			<div class="campo">
				<label for="valorImovel">Valor do imóvel (R$)</label>
				<input type="number" id="valorImovel" name="valorImovel" step="0.01" min="0.01"
					value="<%= valorImovelSugerido != null ? valorImovelSugerido : "" %>" required>
			</div>

			<div class="linha-campos">
				<div class="campo">
					<label for="entradaPercentual">Entrada (% do valor)</label>
					<input type="number" id="entradaPercentual" name="entradaPercentual" step="0.1" min="0" max="99" placeholder="Ex.: 20">
				</div>
				<div class="campo">
					<label for="valorEntrada">Ou entrada em valor (R$)</label>
					<input type="number" id="valorEntrada" name="valorEntrada" step="0.01" min="0" placeholder="Preencha só um dos dois campos">
				</div>
			</div>

			<div class="linha-campos">
				<div class="campo">
					<label for="prazoAnos">Prazo (anos)</label>
					<input type="number" id="prazoAnos" name="prazoAnos" min="${prazoMinimo}" max="${prazoMaximo}" value="30" required>
					<span class="texto-dica">Entre ${prazoMinimo} e ${prazoMaximo} anos</span>
				</div>
				<div class="campo">
					<label for="instituicaoReferencia">Instituição de referência</label>
					<input type="text" id="instituicaoReferencia" name="instituicaoReferencia" placeholder="Ex.: Banco fictício da simulação">
				</div>
			</div>

			<button type="submit" class="botao botao-principal">Calcular simulação</button>
		</form>

		<% if (resultado != null) { %>
		<div class="bloco secao">
			<h2>Resultado da simulação</h2>
			<div class="painel-indicadores">
				<div class="indicador">
					<span class="indicador-rotulo">Valor financiado</span>
					<span class="indicador-valor"><%= util.Formatador.moeda(resultado.getValorFinanciado()) %></span>
				</div>
				<div class="indicador">
					<span class="indicador-rotulo">Parcela inicial estimada</span>
					<span class="indicador-valor"><%= util.Formatador.moeda(resultado.getParcelaInicial()) %></span>
				</div>
				<div class="indicador">
					<span class="indicador-rotulo">Total de juros estimado</span>
					<span class="indicador-valor"><%= util.Formatador.moeda(resultado.getTotalJuros()) %></span>
				</div>
				<div class="indicador">
					<span class="indicador-rotulo">Prazo</span>
					<span class="indicador-valor"><%= resultado.getPrazoAnos() %> anos</span>
				</div>
			</div>
			<p class="texto-dica">Simulação ilustrativa. Sujeita à análise de crédito da instituição financeira. Não há integração real com bancos.</p>

			<% if (idImovelOrigem != null) { %>
			<p><a href="${pageContext.request.contextPath}/imovel?id=<%= idImovelOrigem %>" class="botao botao-principal">Voltar ao imóvel e anexar esta simulação</a></p>
			<% } %>
		</div>
		<% } %>
	</main>
</body>
</html>
