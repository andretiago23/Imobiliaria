<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Financiamento | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css">
</head>
<body>

<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/inicio" aria-label="Habittar — catálogo">
      <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#FF6A1A" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
        <path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/>
        <path d="M9 11.2 12 8.8l3 2.4V14h-6z"/>
      </svg>
      Habittar
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
