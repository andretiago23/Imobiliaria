<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Financiamento | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=3">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=3">
</head>
<body>

<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/inicio" aria-label="Habittar — catálogo">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
    <nav class="nav__links">
      <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
      <% if (session.getAttribute("usuarioLogado") != null) { %>
        <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/logout">Sair</a>
      <% } else { %>
        <a class="btn btn--primary btn--sm" href="${pageContext.request.contextPath}/login">Entrar</a>
      <% } %>
    </nav>
  </div>
</header>

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
