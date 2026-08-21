<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% response.setStatus(404); %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Página não encontrada | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=40">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=40">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=40">
</head>
<body>
<main class="app-main" style="max-width:520px;text-align:center;padding-top:96px;">
  <p class="eyebrow">Erro 404</p>
  <h1 class="display">Essa página não existe</h1>
  <p class="lead" style="max-width:none;">O endereço pode ter mudado ou o imóvel já não está mais disponível.</p>
  <a class="btn btn--primary" style="margin-top:24px;" href="${pageContext.request.contextPath}/inicio">Voltar ao catálogo</a>
</main>
</body>
</html>
