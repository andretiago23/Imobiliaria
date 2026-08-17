<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.Imovel" %>
<%
	Imovel imovel = (Imovel) request.getAttribute("imovel");
	boolean edicao = imovel != null && imovel.getId() != 0;
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><%= edicao ? "Editar imóvel" : "Anunciar imóvel" %> | Imobiliária</title>
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
		<h1><%= edicao ? "Editar imóvel" : "Anunciar imóvel" %></h1>
		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<% model.Usuario usuarioLogado = (model.Usuario) session.getAttribute("usuarioLogado"); %>
		<% if (usuarioLogado != null && !usuarioLogado.podeAnunciar()) { %>
			<p class="texto-vazio">Sua conta é do tipo comprador. Só vendedores vinculados a uma imobiliária podem anunciar.</p>
		<% } else { %>
		<form method="post" action="${pageContext.request.contextPath}/anunciar" class="formulario cartao cartao-largo">
			<% if (edicao) { %><input type="hidden" name="id" value="<%= imovel.getId() %>"><% } %>

			<div class="campo">
				<label for="titulo">Título</label>
				<input type="text" id="titulo" name="titulo" value="<%= edicao ? util.Html.escapar(imovel.getTitulo()) : "" %>" required>
			</div>

			<div class="campo">
				<label for="descricao">Descrição</label>
				<textarea id="descricao" name="descricao" rows="4"><%= edicao && imovel.getDescricao() != null ? util.Html.escapar(imovel.getDescricao()) : "" %></textarea>
			</div>

			<div class="linha-campos">
				<div class="campo">
					<label for="tipo">Tipo</label>
					<select id="tipo" name="tipo" required>
						<% for (model.TipoImovel tipo : model.TipoImovel.values()) { %>
						<option value="<%= tipo.getValorBanco() %>" <%= edicao && imovel.getTipo() == tipo ? "selected" : "" %>><%= tipo.getRotulo() %></option>
						<% } %>
					</select>
				</div>
				<div class="campo">
					<label for="finalidade">Operação</label>
					<select id="finalidade" name="finalidade" required>
						<% for (model.Finalidade finalidade : model.Finalidade.values()) { %>
						<option value="<%= finalidade.getValorBanco() %>" <%= edicao && imovel.getFinalidade() == finalidade ? "selected" : "" %>><%= finalidade.getRotulo() %></option>
						<% } %>
					</select>
				</div>
			</div>

			<div class="linha-campos">
				<div class="campo">
					<label for="preco">Valor (R$)</label>
					<input type="number" id="preco" name="preco" step="0.01" min="0.01" value="<%= edicao ? imovel.getPreco() : "" %>" required>
				</div>
				<div class="campo">
					<label for="areaM2">Área (m²)</label>
					<input type="number" id="areaM2" name="areaM2" step="0.01" min="0" value="<%= edicao ? imovel.getAreaM2() : "" %>">
				</div>
				<div class="campo">
					<label for="ano">Ano de construção</label>
					<input type="number" id="ano" name="ano" min="1900" value="<%= edicao && imovel.getAno() != null ? imovel.getAno() : "" %>">
				</div>
			</div>

			<div class="linha-campos">
				<div class="campo">
					<label for="quartos">Quartos</label>
					<input type="number" id="quartos" name="quartos" min="0" value="<%= edicao ? imovel.getQuartos() : 0 %>">
				</div>
				<div class="campo">
					<label for="banheiros">Banheiros</label>
					<input type="number" id="banheiros" name="banheiros" min="0" value="<%= edicao ? imovel.getBanheiros() : 0 %>">
				</div>
				<div class="campo">
					<label for="vagasGaragem">Vagas de garagem</label>
					<input type="number" id="vagasGaragem" name="vagasGaragem" min="0" value="<%= edicao ? imovel.getVagasGaragem() : 0 %>">
				</div>
			</div>

			<div class="campo">
				<label for="endereco">Endereço</label>
				<input type="text" id="endereco" name="endereco" value="<%= edicao && imovel.getEndereco() != null ? util.Html.escapar(imovel.getEndereco()) : "" %>">
			</div>

			<div class="linha-campos">
				<div class="campo">
					<label for="cidade">Cidade</label>
					<input type="text" id="cidade" name="cidade" value="<%= edicao && imovel.getCidade() != null ? util.Html.escapar(imovel.getCidade()) : "" %>">
				</div>
				<div class="campo">
					<label for="estado">UF</label>
					<input type="text" id="estado" name="estado" maxlength="2" value="<%= edicao && imovel.getEstado() != null ? util.Html.escapar(imovel.getEstado()) : "" %>">
				</div>
				<div class="campo">
					<label for="cep">CEP</label>
					<input type="text" id="cep" name="cep" value="<%= edicao && imovel.getCep() != null ? util.Html.escapar(imovel.getCep()) : "" %>">
				</div>
			</div>

			<button type="submit" class="botao botao-principal"><%= edicao ? "Salvar alterações" : "Publicar anúncio" %></button>
		</form>
		<% } %>
	</main>
</body>
</html>
