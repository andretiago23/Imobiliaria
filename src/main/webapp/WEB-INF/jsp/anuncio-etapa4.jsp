<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.RascunhoAnuncio, model.DisponibilidadeVisita, model.DiaSemana, java.util.Set, java.util.HashSet" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Disponibilidade para visitas | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=39">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=39">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=39">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=39">
</head>
<body>

<% pageContext.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="wizard-main">
  <% request.setAttribute("etapaAtual", 4); %>
  <jsp:include page="/WEB-INF/jsp/fragmentos/progressbar.jsp" />

  <p class="eyebrow wizard-etapa__eyebrow">Etapa 4 de 5</p>
  <h1 class="display wizard-etapa__titulo">Quando você aceita visitas?</h1>
  <p class="lead" style="max-width:none;margin:0 0 24px;">Marque os dias da semana e o intervalo de horário em que costuma estar disponível. O comprador só vai conseguir agendar dentro dessa janela.</p>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    RascunhoAnuncio rascunho = (RascunhoAnuncio) request.getAttribute("rascunho");
    Set<String> diasMarcados = new HashSet<>();
    String horaInicioAtual = "09:00";
    String horaFimAtual = "18:00";
    for (DisponibilidadeVisita janela : rascunho.getDisponibilidade()) {
      diasMarcados.add(janela.getDiaSemana().name());
      horaInicioAtual = janela.getHoraInicio().toString();
      horaFimAtual = janela.getHoraFim().toString();
    }
    // Na primeira visita a esta etapa (nenhum dia ainda marcado), pré-marca
    // segunda a sexta: o horário já vem preenchido com um padrão (09h-18h),
    // e deixar os dias vazios dava a falsa impressão de que a etapa já
    // estava pronta, resultando no erro "marque ao menos um dia" ao avançar.
    if (diasMarcados.isEmpty()) {
      diasMarcados.add("SEG");
      diasMarcados.add("TER");
      diasMarcados.add("QUA");
      diasMarcados.add("QUI");
      diasMarcados.add("SEX");
    }
  %>

  <form method="post" action="${pageContext.request.contextPath}/anunciar/etapa4">
    <input type="hidden" name="csrf" value="${csrf}">

    <h2 style="font-size:14px;margin:0 0 14px;">Dias da semana</h2>
    <div class="wizard-dias">
      <% for (DiaSemana dia : DiaSemana.values()) { %>
        <label class="wizard-dia">
          <input type="checkbox" name="diaSemana" value="<%= dia.name() %>" <%= diasMarcados.contains(dia.name()) ? "checked" : "" %>>
          <span><%= dia.getRotulo().substring(0, 3) %></span>
        </label>
      <% } %>
    </div>

    <h2 style="font-size:14px;margin:28px 0 14px;">Horário</h2>
    <div class="filtros__grade filtros__grade--2col">
      <div class="filtros__campo">
        <label for="horaInicio">Das</label>
        <input type="time" id="horaInicio" name="horaInicio" value="<%= horaInicioAtual %>" required>
      </div>
      <div class="filtros__campo">
        <label for="horaFim">Até</label>
        <input type="time" id="horaFim" name="horaFim" value="<%= horaFimAtual %>" required>
      </div>
    </div>

    <div class="wizard-acoes">
      <a class="btn btn--secondary" href="${pageContext.request.contextPath}/anunciar/etapa3">Voltar</a>
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
