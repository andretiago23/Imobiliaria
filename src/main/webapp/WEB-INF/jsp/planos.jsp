<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Locale, java.text.NumberFormat, model.Plano" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Planos para anunciar | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=41">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=41">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=41">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=41">
</head>
<body>

<% pageContext.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="app-main" style="max-width:960px;">

  <a class="voltar" href="${pageContext.request.contextPath}/anunciar">
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
    Voltar
  </a>

  <div class="app-header">
    <div>
      <p class="eyebrow">Planos</p>
      <h1 class="display">Escolha como seu imóvel vai <span class="hl">aparecer</span></h1>
    </div>
  </div>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    List<Plano> planos = (List<Plano>) request.getAttribute("planos");
    NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    if (planos != null) {
  %>
  <div class="planos-grade">
    <% for (Plano plano : planos) {
         java.math.BigDecimal meses = new java.math.BigDecimal(plano.getDuracaoDias())
             .divide(new java.math.BigDecimal(30), 4, java.math.RoundingMode.HALF_UP);
         java.math.BigDecimal valorMensal = plano.getPreco().divide(meses, 2, java.math.RoundingMode.HALF_UP);
    %>
      <div class="plano-card" style="cursor:default;">
        <% if (plano.isDestaque()) { %><span class="plano-card__selo">Mais popular</span><% } %>
        <p class="plano-card__nome"><%= util.Html.escapar(plano.getNome()) %></p>
        <p class="plano-card__preco"><%= moeda.format(plano.getPreco()) %> <span>total</span></p>
        <p class="plano-card__mensal">Equivale a <strong><%= moeda.format(valorMensal) %>/mês</strong> — anúncio ativo por <%= plano.getDuracaoDias() %> dias</p>
        <p class="plano-card__descricao"><%= util.Html.escapar(plano.getDescricao()) %></p>
        <ul class="plano-card__lista">
          <li>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 6 9 17l-5-5"/></svg>
            Até <%= plano.getLimiteFotos() %> fotos no anúncio
          </li>
          <li>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 6 9 17l-5-5"/></svg>
            <%= plano.getDuracaoDias() %> dias no ar
          </li>
        </ul>
      </div>
    <% } %>
  </div>
  <div style="text-align:center;margin-top:40px;">
    <a class="btn btn--primary btn--interactive" href="${pageContext.request.contextPath}/anunciar/etapa1">
      <span class="btn__label">Anunciar agora</span>
      <span class="btn__reveal" aria-hidden="true">
        Anunciar agora
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
      </span>
      <span class="btn__dot" aria-hidden="true"></span>
    </a>
  </div>
  <% } %>

</main>

</body>
</html>
