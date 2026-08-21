<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Locale, java.text.NumberFormat, model.Anuncio, model.Imovel, model.Plano" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Confirmar pagamento | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=44">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=44">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=44">
</head>
<body>

<% pageContext.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="wizard-main" style="max-width:560px;">
  <%
    Anuncio anuncio = (Anuncio) request.getAttribute("anuncio");
    Imovel imovel = (Imovel) request.getAttribute("imovel");
    Plano plano = (Plano) request.getAttribute("plano");
    NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
  %>

  <div class="pagamento-card">
    <div class="pagamento-card__icone">
      <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="2" y="5" width="20" height="14" rx="2"/><path d="M2 10h20"/></svg>
    </div>
    <p class="eyebrow">Último passo</p>
    <h1 class="display" style="font-size:26px;">Confirmar pagamento</h1>
    <p class="lead" style="max-width:none;margin:8px 0 24px;">
      <% if (imovel != null) { %>Anúncio: <strong><%= util.Html.escapar(imovel.getTitulo()) %></strong><br><% } %>
      <% if (plano != null) { %>Plano <%= util.Html.escapar(plano.getNome()) %> — <span class="wizard-resumo__preco" style="font-size:20px;"><%= moeda.format(plano.getPreco()) %></span><% } %>
    </p>

    <form method="post" action="${pageContext.request.contextPath}/anunciar/pagamento?id=<%= anuncio.getId() %>">
      <input type="hidden" name="csrf" value="${csrf}">
      <button class="btn btn--primary btn--interactive" type="submit" style="width:100%;">
        <span class="btn__label">Confirmar pagamento</span>
        <span class="btn__reveal" aria-hidden="true">
          Confirmar pagamento
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
        </span>
        <span class="btn__dot" aria-hidden="true"></span>
      </button>
    </form>

    <p class="pagamento-card__aviso">
      Simulação: nenhum valor real é cobrado. Em produção, este botão levaria a um gateway de pagamento de verdade, e a confirmação viria por um retorno automático dele.
    </p>
  </div>
</main>

</body>
</html>
