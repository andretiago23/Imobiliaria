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
		<a href="${pageContext.request.contextPath}/inicio" class="marca">Imobiliária</a>
		<nav class="menu">
			<span class="saudacao">Olá, ${usuarioLogado.nome}</span>
			<a href="${pageContext.request.contextPath}/logout" class="botao botao-discreto">Sair</a>
		</nav>
	</header>

	<main class="conteudo">
		<p class="alerta alerta-erro" role="alert">${erro}</p>

		<h1>Sua conta</h1>

		<section class="painel-indicadores">
			<article class="indicador">
				<span class="indicador-rotulo">Tipo de conta</span>
				<strong class="indicador-valor">${usuarioLogado.tipoUsuario.rotulo}</strong>
			</article>

			<article class="indicador">
				<span class="indicador-rotulo">Reputação</span>
				<strong class="indicador-valor">${reputacao}</strong>
				<span class="indicador-apoio">${totalAvaliacoes} avaliações</span>
			</article>

			<article class="indicador">
				<span class="indicador-rotulo">Mensagens pendentes</span>
				<strong class="indicador-valor">${interessesPendentes}</strong>
			</article>
		</section>

		<section class="bloco">
			<h2>Dados cadastrais</h2>
			<dl class="lista-dados">
				<dt>Nome</dt>
				<dd>${usuarioLogado.nome}</dd>

				<dt>E-mail</dt>
				<dd>
					${usuarioLogado.email}
					<span class="etiqueta ${usuarioLogado.emailConfirmado ? 'etiqueta-ok' : 'etiqueta-pendente'}">
						${usuarioLogado.emailConfirmado ? 'confirmado' : 'não confirmado'}
					</span>
				</dd>

				<dt>Telefone</dt>
				<dd>${empty usuarioLogado.telefone ? 'não informado' : usuarioLogado.telefone}</dd>
			</dl>
		</section>
	</main>

</body>
</html>
