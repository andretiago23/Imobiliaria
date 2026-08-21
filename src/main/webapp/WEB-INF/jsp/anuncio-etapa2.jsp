<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Locale, java.text.NumberFormat, model.Plano, model.RascunhoAnuncio" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Escolha seu plano | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=52">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=52">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=52">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=52">
</head>
<body>

<% request.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="wizard-main" style="max-width:920px;">
  <% request.setAttribute("etapaAtual", 2); %>
  <jsp:include page="/WEB-INF/jsp/fragmentos/progressbar.jsp" />

  <p class="eyebrow wizard-etapa__eyebrow">Etapa 2 de 5</p>
  <h1 class="display wizard-etapa__titulo">Escolha seu plano</h1>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    RascunhoAnuncio rascunho = (RascunhoAnuncio) request.getAttribute("rascunho");
    List<Plano> planos = (List<Plano>) request.getAttribute("planos");
    NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
  %>

  <form method="post" action="${pageContext.request.contextPath}/anunciar/etapa2">
    <input type="hidden" name="csrf" value="${csrf}">

    <div class="planos-grade">
      <% if (planos != null) { for (Plano plano : planos) {
           java.math.BigDecimal meses = new java.math.BigDecimal(plano.getDuracaoDias())
               .divide(new java.math.BigDecimal(30), 4, java.math.RoundingMode.HALF_UP);
           java.math.BigDecimal valorMensal = plano.getPreco().divide(meses, 2, java.math.RoundingMode.HALF_UP);
      %>
        <input type="radio" name="idPlano" value="<%= plano.getId() %>" id="plano<%= plano.getId() %>" class="plano-input"
          <%= rascunho.getIdPlano() != null && rascunho.getIdPlano() == plano.getId() ? "checked" : "" %> required>
        <label for="plano<%= plano.getId() %>" class="plano-card">
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
        </label>
      <% } } %>
    </div>

    <div class="wizard-acoes">
      <a class="btn btn--secondary" href="${pageContext.request.contextPath}/anunciar/etapa1">Voltar</a>
      <button class="btn btn--primary btn--interactive" type="submit">
        <span class="btn__label">Próximo</span>
        <span class="btn__reveal" aria-hidden="true">
          Próximo
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
        </span>
        <span class="btn__dot" aria-hidden="true"></span>
      </button>
    </div>
  </form>
</main>

</body>
</html>
