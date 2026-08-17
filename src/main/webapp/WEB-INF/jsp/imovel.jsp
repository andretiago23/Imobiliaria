<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Locale, java.text.NumberFormat, model.Imovel, model.Finalidade, model.StatusImovel" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><%= request.getAttribute("imovel") != null ? util.Html.escapar(((Imovel) request.getAttribute("imovel")).getTitulo()) + " | Habittar" : "Imóvel | Habittar" %></title>
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
      <span class="micro">Olá, ${sessionScope.usuarioLogado.nome}</span>
      <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/logout">Sair</a>
    </nav>
  </div>
</header>

<main class="app-main">

  <a class="voltar" href="${pageContext.request.contextPath}/inicio">
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
    Voltar ao catálogo
  </a>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    Imovel imovel = (Imovel) request.getAttribute("imovel");
    if (imovel != null) {
      boolean aluguel = imovel.getFinalidade() == Finalidade.ALUGUEL;
      NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
  %>
  <nav class="breadcrumb micro" aria-label="Trilha de navegação">
    <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
    <span aria-hidden="true">/</span>
    <a href="${pageContext.request.contextPath}/inicio?cidade=<%= java.net.URLEncoder.encode(imovel.getCidade(), java.nio.charset.StandardCharsets.UTF_8) %>"><%= util.Html.escapar(imovel.getCidade()) %></a>
    <span aria-hidden="true">/</span>
    <span class="breadcrumb__atual"><%= util.Html.escapar(imovel.getTitulo()) %></span>
  </nav>

  <div class="imovel-detalhe">
    <div>
      <div class="imovel-detalhe__foto">
        <img src="${pageContext.request.contextPath}/imagens/placeholder-imovel.svg"
          alt="Foto de <%= util.Html.escapar(imovel.getTitulo()) %>">
      </div>

      <div class="imovel-detalhe__badges">
        <span class="badge"><%= aluguel ? "Aluguel" : "Venda" %></span>
        <span class="badge"><%= imovel.getTipo().getRotulo() %></span>
        <% if (imovel.getStatus() != StatusImovel.ATIVO) { %>
          <span class="badge badge--status" style="position:static;"><%= imovel.getStatus().getRotulo() %></span>
        <% } %>
      </div>

      <h1 class="display"><%= util.Html.escapar(imovel.getTitulo()) %></h1>
      <p class="imovel-detalhe__endereco">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="display:inline;vertical-align:-2px;margin-right:4px;" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg>
        <%= util.Html.escapar(imovel.getEnderecoCompleto()) %><% if (imovel.getCep() != null && !imovel.getCep().isBlank()) { %> — CEP <%= util.Html.escapar(imovel.getCep()) %><% } %>
      </p>

      <div class="ficha-tecnica">
        <div>
          <div class="ficha-tecnica__valor"><%= imovel.getAreaM2() %> m²</div>
          <div class="micro">Área</div>
        </div>
        <div>
          <div class="ficha-tecnica__valor"><%= imovel.getQuartos() %></div>
          <div class="micro">Quarto(s)</div>
        </div>
        <div>
          <div class="ficha-tecnica__valor"><%= imovel.getBanheiros() %></div>
          <div class="micro">Banheiro(s)</div>
        </div>
        <div>
          <div class="ficha-tecnica__valor"><%= imovel.getVagasGaragem() %></div>
          <div class="micro">Vaga(s)</div>
        </div>
      </div>

      <h3 class="display" style="font-size:20px;">Descrição</h3>
      <p class="lead" style="max-width:none;">
        <%= imovel.getDescricao() != null && !imovel.getDescricao().isBlank()
              ? util.Html.escapar(imovel.getDescricao())
              : "Sem descrição cadastrada para este imóvel." %>
      </p>

      <% if (imovel.getDono() != null) { %>
      <h3 class="display" style="font-size:20px;margin-top:32px;">Anunciante</h3>
      <p class="micro"><%= util.Html.escapar(imovel.getDono().getNome()) %></p>
      <% } %>
    </div>

    <aside class="imovel-painel">
      <div class="imovel-painel__preco">
        <%= moeda.format(imovel.getPreco()) %>
        <% if (aluguel) { %><span class="micro">/mês</span><% } %>
      </div>
      <p class="micro" style="margin-top:6px;">Código do anúncio: HB-<%= imovel.getId() %></p>

      <button class="btn btn--primary" type="button" disabled aria-disabled="true" title="Fluxo de leads ainda não disponível neste protótipo">
        Tenho interesse
      </button>
      <p class="imovel-painel__aviso micro">O envio de interesse (com simulação de financiamento opcional) chega em uma próxima etapa do protótipo.</p>
    </aside>
  </div>
  <% } %>

</main>

</body>
</html>
