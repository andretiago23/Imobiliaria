<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Início | Imobiliária</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>

	<header class="barra-topo">
		<a class="marca" href="${pageContext.request.contextPath}/inicio">Imobiliária</a>
		<nav class="menu">
			<a href="${pageContext.request.contextPath}/imoveis">Catálogo</a>
			<% if (((model.Usuario) session.getAttribute("usuarioLogado")).getTipoUsuario() == model.TipoUsuario.VENDEDOR) { %>
				<a href="${pageContext.request.contextPath}/meus-imoveis">Meus imóveis</a>
				<a href="${pageContext.request.contextPath}/meus-leads">Meus leads</a>
			<% } else { %>
				<a href="${pageContext.request.contextPath}/favoritos">Favoritos</a>
				<a href="${pageContext.request.contextPath}/minhas-buscas">Minhas buscas</a>
			<% } %>
			<a href="${pageContext.request.contextPath}/simulador">Simulador</a>
			<a href="${pageContext.request.contextPath}/perfil">Perfil</a>
			<span class="saudacao">Olá, ${sessionScope.usuarioLogado.nome}</span>
			<a class="botao botao-discreto" href="${pageContext.request.contextPath}/logout">Sair</a>
		</nav>
	</header>

	<main class="conteudo">

		<h1>Bem-vindo(a) de volta</h1>
		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<section class="painel-indicadores">
			<div class="indicador">
				<span class="indicador-rotulo">Reputação</span>
				<span class="indicador-valor">${reputacao}</span>
				<span class="indicador-apoio">média das suas avaliações</span>
			</div>

			<div class="indicador">
				<span class="indicador-rotulo">Avaliações recebidas</span>
				<span class="indicador-valor">${totalAvaliacoes}</span>
				<span class="indicador-apoio">total acumulado</span>
			</div>

			<div class="indicador">
				<span class="indicador-rotulo">Interesses pendentes</span>
				<span class="indicador-valor">${interessesPendentes}</span>
				<span class="indicador-apoio">aguardando sua resposta</span>
			</div>
		</section>

		<section class="bloco">
			<h2>Seus dados</h2>
			<dl class="lista-dados">
				<dt>Nome</dt>
				<dd>${sessionScope.usuarioLogado.nome}</dd>

				<dt>E-mail</dt>
				<dd>${sessionScope.usuarioLogado.email}</dd>

				<dt>Tipo de conta</dt>
				<dd>${sessionScope.usuarioLogado.tipoUsuario}</dd>
			</dl>
		</section>

	</main>

</body>
</html>
