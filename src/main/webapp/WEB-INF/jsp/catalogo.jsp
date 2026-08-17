<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Imovel" %>
<%@ page import="model.FotoImovel" %>
<%@ page import="model.FiltroImovel" %>
<%
	FiltroImovel filtro = (FiltroImovel) request.getAttribute("filtro");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Catálogo de imóveis | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

	<header class="barra-topo">
		<a class="marca" href="${pageContext.request.contextPath}/">Imobiliária</a>
		<nav class="menu">
			<a href="${pageContext.request.contextPath}/imoveis">Catálogo</a>
			<a href="${pageContext.request.contextPath}/simulador">Simulador</a>
			<% if (session.getAttribute("usuarioLogado") != null) { %>
				<a href="${pageContext.request.contextPath}/favoritos">Favoritos</a>
				<a href="${pageContext.request.contextPath}/inicio">Minha conta</a>
			<% } else { %>
				<a href="${pageContext.request.contextPath}/login">Entrar</a>
			<% } %>
		</nav>
	</header>

	<main class="conteudo">
		<h1>Catálogo de imóveis</h1>
		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<form class="formulario-filtros" method="get" action="${pageContext.request.contextPath}/imoveis">
			<div class="campo">
				<label for="cidade">Cidade</label>
				<input type="text" id="cidade" name="cidade" value="${filtro.cidade}">
			</div>
			<div class="campo">
				<label for="tipo">Tipo</label>
				<select id="tipo" name="tipo">
					<option value="">Qualquer</option>
					<option value="casa" <%= filtro != null && filtro.getTipo() == model.TipoImovel.CASA ? "selected" : "" %>>Casa</option>
					<option value="apartamento" <%= filtro != null && filtro.getTipo() == model.TipoImovel.APARTAMENTO ? "selected" : "" %>>Apartamento</option>
					<option value="terreno" <%= filtro != null && filtro.getTipo() == model.TipoImovel.TERRENO ? "selected" : "" %>>Terreno</option>
					<option value="comercial" <%= filtro != null && filtro.getTipo() == model.TipoImovel.COMERCIAL ? "selected" : "" %>>Comercial</option>
					<option value="rural" <%= filtro != null && filtro.getTipo() == model.TipoImovel.RURAL ? "selected" : "" %>>Rural</option>
				</select>
			</div>
			<div class="campo">
				<label for="finalidade">Operação</label>
				<select id="finalidade" name="finalidade">
					<option value="">Qualquer</option>
					<option value="venda" <%= filtro != null && filtro.getFinalidade() == model.Finalidade.VENDA ? "selected" : "" %>>Venda</option>
					<option value="aluguel" <%= filtro != null && filtro.getFinalidade() == model.Finalidade.ALUGUEL ? "selected" : "" %>>Aluguel</option>
				</select>
			</div>
			<div class="campo">
				<label for="quartosMinimo">Quartos (mín.)</label>
				<input type="number" id="quartosMinimo" name="quartosMinimo" min="0" value="${filtro.quartosMinimo}">
			</div>
			<div class="campo">
				<label for="precoMaximo">Valor máximo</label>
				<input type="number" id="precoMaximo" name="precoMaximo" min="0" step="0.01" value="${filtro.precoMaximo}">
			</div>
			<button type="submit" class="botao botao-principal">Filtrar</button>
		</form>

		<form id="formComparar" action="${pageContext.request.contextPath}/comparar" method="get" onsubmit="return prepararComparacao();">
			<input type="hidden" name="ids" id="idsComparar">

			<%
				List<Imovel> imoveis = (List<Imovel>) request.getAttribute("imoveis");
			%>
			<% if (imoveis == null || imoveis.isEmpty()) { %>
				<p class="texto-vazio">Nenhum imóvel encontrado com esses filtros.</p>
			<% } else { %>
				<div class="secao-cabecalho">
					<span class="texto-apoio"><%= imoveis.size() %> imóvel(is) encontrado(s)</span>
					<button type="submit" class="botao botao-discreto">Comparar selecionados (até 3)</button>
				</div>
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
								<div class="card-imovel__local"><%= util.Html.escapar(imovel.getCidade()) %><%= imovel.getEstado() != null ? " - " + imovel.getEstado() : "" %></div>
								<div class="card-imovel__specs">
									<span><%= util.Formatador.area(imovel.getAreaM2()) %></span>
									<span><%= imovel.getQuartos() %> qts</span>
									<span><%= imovel.getBanheiros() %> ban.</span>
									<span><%= imovel.getVagasGaragem() %> vagas</span>
								</div>
							</div>
						</a>
						<div class="card-imovel__rodape">
							<label class="selecao-comparar">
								<input type="checkbox" class="checkboxComparar" value="<%= imovel.getId() %>"> comparar
							</label>
						</div>
					</div>
					<% } %>
				</div>
			<% } %>
		</form>
	</main>

	<script>
		function prepararComparacao() {
			var marcados = document.querySelectorAll(".checkboxComparar:checked");
			if (marcados.length === 0) {
				alert("Selecione ao menos um imóvel para comparar.");
				return false;
			}
			if (marcados.length > 3) {
				alert("Selecione no máximo 3 imóveis para comparar.");
				return false;
			}
			var ids = [];
			marcados.forEach(function (checkbox) { ids.push(checkbox.value); });
			document.getElementById("idsComparar").value = ids.join(",");
			return true;
		}
	</script>
</body>
</html>
