<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.LinkedHashMap, java.util.Map, java.util.Locale, java.text.NumberFormat,
                  model.Imovel, model.VisitaSlot, model.Finalidade, model.FotoImovel" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Agendar visita | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=60">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=64">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=60">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=60">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/agendar-visita.css?v=1">
</head>
<body>

<% request.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="app-main" style="max-width:1040px;">
  <%
    Imovel imovel = (Imovel) request.getAttribute("imovel");
  %>
  <a class="voltar" href="${pageContext.request.contextPath}/imovel?id=<%= imovel != null ? imovel.getId() : "" %>">
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
    Voltar ao imóvel
  </a>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    @SuppressWarnings("unchecked")
    List<VisitaSlot> slots = (List<VisitaSlot>) request.getAttribute("slots");
  %>
  <% if (slots == null || slots.isEmpty()) { %>
    <div class="estado-vazio" style="margin-top:20px;">
      <p>O anunciante ainda não abriu horários para visita neste imóvel.</p>
      <p class="micro">Envie uma mensagem pelo "Tenho interesse" para combinar diretamente.</p>
    </div>
  <% } else {
       Map<java.time.LocalDate, List<VisitaSlot>> porDia = new LinkedHashMap<>();
       for (VisitaSlot slot : slots) {
         porDia.computeIfAbsent(slot.data(), k -> new java.util.ArrayList<>()).add(slot);
       }
       String[] abreviacaoDiaSemana = { "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom" };
       NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
       NumberFormat area = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
       area.setMaximumFractionDigits(1);
       boolean aluguel = imovel.getFinalidade() == Finalidade.ALUGUEL;
       FotoImovel fotoCapa = imovel.getFotoPrincipal();
       String urlFoto = fotoCapa != null ? fotoCapa.getUrlFoto()
           : util.ImagemImovel.urlIlustrativa(imovel.getTipo(), imovel.getId());
  %>
    <div class="visita-layout">

      <!-- ===================== CARD DO FORMULÁRIO ===================== -->
      <div class="visita-card">
        <h1 class="display visita-card__titulo">Quando você quer visitar este imóvel?</h1>

        <form method="post" action="${pageContext.request.contextPath}/imovel/visita?idImovel=<%= imovel.getId() %>" id="formVisita">
          <input type="hidden" name="csrf" value="${csrf}">
          <input type="hidden" name="idImovel" value="<%= imovel.getId() %>">

          <p class="visita-rotulo">Escolha um dia</p>
          <div class="visita-dias-nav">
            <button type="button" class="visita-dias-seta" id="setaDiasAnterior" aria-label="Dias anteriores">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
            </button>
            <div class="visita-dias" id="listaDias">
              <% int indiceDia = 0;
                 for (java.time.LocalDate dia : porDia.keySet()) { %>
                <input type="radio" name="diaEscolhido" id="dia<%= indiceDia %>" class="visita-dia-input"
                  data-alvo="horarios<%= indiceDia %>" <%= indiceDia == 0 ? "checked" : "" %>>
                <label for="dia<%= indiceDia %>" class="visita-dia">
                  <span class="visita-dia__semana"><%= abreviacaoDiaSemana[dia.getDayOfWeek().getValue() - 1] %></span>
                  <span class="visita-dia__numero"><%= dia.getDayOfMonth() %></span>
                </label>
              <% indiceDia++; } %>
            </div>
            <button type="button" class="visita-dias-seta" id="setaDiasProxima" aria-label="Próximos dias">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m9 18 6-6-6-6"/></svg>
            </button>
          </div>

          <p class="visita-rotulo">Escolha um horário</p>
          <% indiceDia = 0;
             for (Map.Entry<java.time.LocalDate, List<VisitaSlot>> entrada : porDia.entrySet()) { %>
            <div class="visita-horarios" id="horarios<%= indiceDia %>" <%= indiceDia == 0 ? "" : "hidden" %>>
              <% for (VisitaSlot slot : entrada.getValue()) { %>
                <label class="visita-horario">
                  <input type="radio" name="slot" value="<%= slot.chave() %>" required>
                  <span><%= slot.horaInicio() %></span>
                </label>
              <% } %>
            </div>
          <% indiceDia++; } %>

          <div class="visita-card__rodape">
            <button class="btn btn--primary btn--interactive" type="submit" style="width:100%;">
              <span class="btn__label">Confirmar visita</span>
              <span class="btn__reveal" aria-hidden="true">
                Confirmar visita
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
              </span>
              <span class="btn__dot" aria-hidden="true"></span>
            </button>
          </div>
        </form>
      </div>

      <!-- ===================== RESUMO DO IMÓVEL ===================== -->
      <aside class="visita-resumo">
        <div class="visita-resumo__foto">
          <img src="<%= urlFoto %>" alt="Foto de <%= util.Html.escapar(imovel.getTitulo()) %>">
        </div>
        <p class="micro" style="text-transform:uppercase;letter-spacing:0.04em;"><%= imovel.getTipo().getRotulo() %></p>
        <p class="visita-resumo__preco">
          <%= moeda.format(imovel.getPreco()) %>
          <% if (aluguel) { %><span class="micro">/mês</span><% } %>
        </p>
        <p class="visita-resumo__specs">
          <%= area.format(imovel.getAreaM2()) %> m² · <%= imovel.getQuartos() %> quarto(s) · <%= imovel.getVagasGaragem() %> vaga(s)
        </p>
        <p class="visita-resumo__endereco"><%= util.Html.escapar(imovel.getEnderecoCompleto()) %></p>
      </aside>
    </div>
  <% } %>
</main>

<script src="${pageContext.request.contextPath}/js/agendar-visita.js?v=1"></script>
</body>
</html>
