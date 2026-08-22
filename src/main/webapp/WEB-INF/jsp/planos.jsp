<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Locale, java.text.NumberFormat, model.Plano, model.CategoriaPlano, model.TipoAnunciante, util.FormatoPlano" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Planos para anunciar | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=56">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=56">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=56">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=56">
</head>
<body>

<% request.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<%
  TipoAnunciante tipoAnunciante = (TipoAnunciante) request.getAttribute("tipoAnunciante");
  boolean corretor = tipoAnunciante == TipoAnunciante.CORRETOR;
%>

<main class="app-main" style="max-width:1040px;">

  <a class="voltar" href="${pageContext.request.contextPath}/anunciar">
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
    Voltar
  </a>

  <div class="app-header">
    <div>
      <p class="eyebrow">Planos <%= corretor ? "para corretor / imobiliária" : "para proprietário" %></p>
      <h1 class="display">Escolha como seu imóvel vai <span class="hl">aparecer</span></h1>
    </div>
  </div>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    List<Plano> planos = (List<Plano>) request.getAttribute("planos");
    NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    if (planos != null) {

      List<Plano> packs = new java.util.ArrayList<>();
      List<Plano> individuais = new java.util.ArrayList<>();
      for (Plano plano : planos) {
        (plano.getCategoria() == CategoriaPlano.PACK ? packs : individuais).add(plano);
      }
  %>

  <% if (corretor && !packs.isEmpty()) { %>
    <!-- Abas Packs/Individual, só pra corretor/imobiliária -->
    <input type="radio" name="abaPlanos" id="abaPack" class="planos-tabs__input" checked>
    <input type="radio" name="abaPlanos" id="abaIndividual" class="planos-tabs__input">
    <div class="planos-tabs__nav">
      <label for="abaPack">Packs</label>
      <label for="abaIndividual">Individual</label>
    </div>

    <div class="planos-tabs__painel" data-painel="pack">
      <div class="planos-grade">
        <% for (Plano plano : packs) { %>
          <%@ include file="/WEB-INF/jsp/fragmentos/plano-card.jspf" %>
        <% } %>
      </div>
    </div>
    <div class="planos-tabs__painel" data-painel="individual">
      <div class="planos-grade">
        <% for (Plano plano : individuais) { %>
          <%@ include file="/WEB-INF/jsp/fragmentos/plano-card.jspf" %>
        <% } %>
      </div>
    </div>
  <% } else { %>
    <!-- Proprietário: só planos individuais, sem abas -->
    <div class="planos-grade">
      <% for (Plano plano : individuais) { %>
        <%@ include file="/WEB-INF/jsp/fragmentos/plano-card.jspf" %>
      <% } %>
    </div>
  <% } %>

  <p class="micro" style="margin-top:24px;color:var(--text-secondary);">
    Duração de um mês igual a 30 dias. Planos marcados como "com renovação automática" são cobrados de novo ao final do período, até o cancelamento.
  </p>
  <% } %>

</main>

</body>
</html>
