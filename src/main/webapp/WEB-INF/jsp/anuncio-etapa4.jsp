<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Locale, java.text.NumberFormat, model.Plano, model.RascunhoAnuncio" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Confirme seu anúncio | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=17">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=17">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=17">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=17">
</head>
<body>

<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/index.jsp" aria-label="Habittar — página principal">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
    <nav class="nav__links">
      <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
    </nav>
  </div>
</header>

<main class="wizard-main">
  <% request.setAttribute("etapaAtual", 4); %>
  <jsp:include page="/WEB-INF/jsp/fragmentos/progressbar.jsp" />

  <p class="eyebrow wizard-etapa__eyebrow">Etapa 4 de 4</p>
  <h1 class="display wizard-etapa__titulo">Confira antes de pagar</h1>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    RascunhoAnuncio rascunho = (RascunhoAnuncio) request.getAttribute("rascunho");
    Plano plano = (Plano) request.getAttribute("plano");
    NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    boolean aluguel = rascunho.getFinalidade() == model.Finalidade.ALUGUEL;
  %>

  <div class="wizard-resumo">
    <div class="wizard-resumo__secao">
      <p class="wizard-resumo__rotulo">Tipo de anúncio</p>
      <p class="wizard-resumo__valor"><%= aluguel ? "Aluguel" : "Venda" %> — <%= util.Html.escapar(rascunho.getTitulo()) %></p>
    </div>
    <div class="wizard-resumo__secao">
      <p class="wizard-resumo__rotulo">Endereço do imóvel</p>
      <p class="wizard-resumo__valor"><%= util.Html.escapar(rascunho.enderecoImovelResumido()) %></p>
    </div>
    <div class="wizard-resumo__secao">
      <p class="wizard-resumo__rotulo">Plano escolhido</p>
      <% if (plano != null) { %>
        <p class="wizard-resumo__valor"><%= util.Html.escapar(plano.getNome()) %> — <%= plano.getDuracaoDias() %> dias no ar</p>
        <p class="wizard-resumo__preco"><%= moeda.format(plano.getPreco()) %></p>
      <% } %>
    </div>
    <div class="wizard-resumo__secao">
      <p class="wizard-resumo__rotulo">Dados do anunciante</p>
      <p class="wizard-resumo__valor"><%= util.Html.escapar(rascunho.getNomeAnunciante()) %></p>
      <p class="micro" style="margin-top:4px;color:var(--text-secondary);">
        <%= util.Html.escapar(rascunho.getCpfCnpjAnunciante()) %> · <%= util.Html.escapar(rascunho.getCelularAnunciante()) %>
      </p>
    </div>
  </div>

  <form method="post" action="${pageContext.request.contextPath}/anunciar/etapa4">
    <input type="hidden" name="csrf" value="${csrf}">
    <div class="wizard-acoes">
      <a class="btn btn--secondary" href="${pageContext.request.contextPath}/anunciar/etapa3">Voltar</a>
      <button class="btn btn--primary btn--interactive" type="submit">
        <span class="btn__label">Ir para pagamento</span>
        <span class="btn__reveal" aria-hidden="true">
          Ir para pagamento
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
        </span>
        <span class="btn__dot" aria-hidden="true"></span>
      </button>
    </div>
  </form>
</main>

</body>
</html>
