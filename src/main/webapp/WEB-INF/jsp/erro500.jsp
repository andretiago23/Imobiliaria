<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%
	// A exceção não é exibida ao usuário — só fica registrada no log do
	// servidor, com um código curto para localizar o incidente sem expor
	// detalhes internos (versão do framework, caminhos do servidor etc.).
	String codigoIncidente = Long.toString(System.currentTimeMillis(), 36).toUpperCase();
	if (exception != null) {
		getServletContext().log("Erro não tratado [" + codigoIncidente + "]", exception);
	}
	response.setStatus(500);
%>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Algo deu errado | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=41">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=41">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=41">
</head>
<body>
<main class="app-main" style="max-width:520px;text-align:center;padding-top:96px;">
  <p class="eyebrow">Erro inesperado</p>
  <h1 class="display">Algo não saiu como devia</h1>
  <p class="lead" style="max-width:none;">Nossa equipe já foi avisada. Tente novamente em instantes — se o problema continuar, volte para o catálogo.</p>
  <p class="micro">Código do incidente: <%= codigoIncidente %></p>
  <a class="btn btn--primary" style="margin-top:24px;" href="${pageContext.request.contextPath}/inicio">Voltar ao catálogo</a>
</main>
</body>
</html>
