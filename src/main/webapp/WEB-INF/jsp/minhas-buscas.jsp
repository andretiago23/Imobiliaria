<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.BuscaSalva" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Minhas buscas | Imobiliária</title>
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
		<h1>Minhas buscas</h1>
		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<div class="bloco secao">
			<h2>Salvar uma nova busca</h2>
			<form method="post" action="${pageContext.request.contextPath}/minhas-buscas" class="formulario">
				<input type="hidden" name="acao" value="salvar">
				<div class="campo">
					<label for="nome">Nome da busca</label>
					<input type="text" id="nome" name="nome" placeholder="Ex.: Apê 2 quartos em Pinheiros" required>
				</div>
				<div class="linha-campos">
					<div class="campo">
						<label for="tipo">Tipo</label>
						<select id="tipo" name="tipo">
							<option value="">Qualquer</option>
							<% for (model.TipoImovel tipo : model.TipoImovel.values()) { %>
							<option value="<%= tipo.getValorBanco() %>"><%= tipo.getRotulo() %></option>
							<% } %>
						</select>
					</div>
					<div class="campo">
						<label for="finalidade">Operação</label>
						<select id="finalidade" name="finalidade">
							<option value="">Qualquer</option>
							<option value="venda">Venda</option>
							<option value="aluguel">Aluguel</option>
						</select>
					</div>
					<div class="campo">
						<label for="cidade">Cidade</label>
						<input type="text" id="cidade" name="cidade">
					</div>
				</div>
				<div class="linha-campos">
					<div class="campo">
						<label for="quartosMinimo">Quartos (mín.)</label>
						<input type="number" id="quartosMinimo" name="quartosMinimo" min="0">
					</div>
					<div class="campo">
						<label for="precoMaximo">Valor máximo</label>
						<input type="number" id="precoMaximo" name="precoMaximo" step="0.01" min="0">
					</div>
				</div>
				<label class="opcao-lembrar">
					<input type="checkbox" name="alertaAtivo">
					Avisar por e-mail quando aparecer imóvel novo com esse perfil
				</label>
				<button type="submit" class="botao botao-principal">Salvar busca</button>
			</form>
		</div>

		<%
			List<BuscaSalva> buscas = (List<BuscaSalva>) request.getAttribute("buscas");
		%>
		<% if (buscas == null || buscas.isEmpty()) { %>
			<p class="texto-vazio">Você ainda não salvou nenhuma busca.</p>
		<% } else { %>
		<div class="tabela-wrapper">
			<table class="tabela">
				<thead>
					<tr><th>Nome</th><th>Critérios</th><th>Alerta</th><th>Criada em</th><th></th></tr>
				</thead>
				<tbody>
					<% for (BuscaSalva busca : buscas) { %>
					<tr>
						<td><%= util.Html.escapar(busca.getNome()) %></td>
						<td class="celula-larga">
							<%= busca.getTipo() != null ? busca.getTipo().getRotulo() + " · " : "" %>
							<%= busca.getFinalidade() != null ? busca.getFinalidade().getRotulo() + " · " : "" %>
							<%= busca.getCidade() != null ? util.Html.escapar(busca.getCidade()) + " · " : "" %>
							<%= busca.getQuartosMinimo() != null ? busca.getQuartosMinimo() + "+ qts · " : "" %>
							<%= busca.getPrecoMaximo() != null ? "até " + util.Formatador.moeda(busca.getPrecoMaximo()) : "" %>
						</td>
						<td><span class="badge <%= busca.isAlertaAtivo() ? "badge-ativo" : "badge-inativo" %>"><%= busca.isAlertaAtivo() ? "Ativo" : "Pausado" %></span></td>
						<td><%= util.Formatador.data(busca.getDataCriacao()) %></td>
						<td>
							<form method="post" action="${pageContext.request.contextPath}/minhas-buscas" style="display:inline">
								<input type="hidden" name="id" value="<%= busca.getId() %>">
								<input type="hidden" name="acao" value="<%= busca.isAlertaAtivo() ? "pausar" : "reativar" %>">
								<button type="submit" class="botao botao-discreto"><%= busca.isAlertaAtivo() ? "Pausar" : "Reativar" %></button>
							</form>
							<form method="post" action="${pageContext.request.contextPath}/minhas-buscas" style="display:inline">
								<input type="hidden" name="id" value="<%= busca.getId() %>">
								<input type="hidden" name="acao" value="excluir">
								<button type="submit" class="botao botao-discreto">Excluir</button>
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
