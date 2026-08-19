<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Confirmação de status | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=23">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=23">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=23">
</head>
<body>

<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/index.jsp" aria-label="Habittar — página principal">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
  </div>
</header>

<main class="wizard-main" style="max-width:560px;">
  <div class="pagamento-card">
    <% if (Boolean.TRUE.equals(request.getAttribute("sucesso"))) { %>
      <div class="pagamento-card__icone">
        <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 6 9 17l-5-5"/></svg>
      </div>
      <h1 class="display" style="font-size:24px;">Obrigado por confirmar!</h1>
      <p class="lead" style="max-width:none;margin-top:8px;">Seu anúncio continua ativo no catálogo. Vamos te avisar de novo daqui a um tempo.</p>
    <% } else { %>
      <h1 class="display" style="font-size:24px;">Link inválido ou expirado</h1>
      <p class="lead" style="max-width:none;margin-top:8px;">Esse link de confirmação já foi usado ou não existe mais. Se seu anúncio saiu do catálogo, reative direto em "Imóveis anunciados".</p>
    <% } %>
    <a class="btn btn--primary" style="margin-top:20px;" href="${pageContext.request.contextPath}/imoveis-anunciados">Ir para meus imóveis</a>
  </div>
</main>

</body>
</html>
