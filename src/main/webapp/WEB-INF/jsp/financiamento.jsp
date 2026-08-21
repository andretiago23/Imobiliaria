<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Financiamento | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=52">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=52">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=52">
</head>
<body>

<% request.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="app-main" style="max-width:640px;text-align:center;padding-top:96px;">
  <p class="eyebrow">Financiamento</p>
  <h1 class="display">O simulador está a caminho</h1>
  <p class="lead" style="max-width:none;">
    Em breve você vai poder simular o valor financiado, a parcela estimada e o total de juros
    diretamente na ficha de cada imóvel — sempre com o aviso de que é uma simulação ilustrativa,
    sujeita à análise de crédito da instituição financeira.
  </p>
  <a class="btn btn--primary" style="margin-top:24px;" href="${pageContext.request.contextPath}/inicio">Voltar ao catálogo</a>
</main>

</body>
</html>
