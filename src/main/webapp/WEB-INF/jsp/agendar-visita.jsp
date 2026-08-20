<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.LinkedHashMap, java.util.Map, model.Imovel, model.VisitaSlot" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Agendar visita | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=38">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=38">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=38">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=38">
</head>
<body>

<% pageContext.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="app-main" style="max-width:640px;">
  <%
    Imovel imovel = (Imovel) request.getAttribute("imovel");
  %>
  <a class="voltar" href="${pageContext.request.contextPath}/imovel?id=<%= imovel != null ? imovel.getId() : "" %>">
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
    Voltar ao imóvel
  </a>

  <div class="app-header">
    <div>
      <p class="eyebrow">Agendar visita</p>
      <h1 class="display" style="font-size:26px;"><%= imovel != null ? util.Html.escapar(imovel.getTitulo()) : "" %></h1>
    </div>
  </div>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    @SuppressWarnings("unchecked")
    List<VisitaSlot> slots = (List<VisitaSlot>) request.getAttribute("slots");

    if (slots == null || slots.isEmpty()) {
  %>
    <div class="estado-vazio">
      <p>O anunciante ainda não abriu horários para visita neste imóvel.</p>
      <p class="micro">Envie uma mensagem pelo "Tenho interesse" para combinar diretamente.</p>
    </div>
  <% } else {
       Map<String, List<VisitaSlot>> porDia = new LinkedHashMap<>();
       for (VisitaSlot slot : slots) {
         String rotuloDia = slot.data().getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("pt", "BR"))
             + ", " + slot.data().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM"));
         porDia.computeIfAbsent(rotuloDia, k -> new java.util.ArrayList<>()).add(slot);
       }
  %>
    <form method="post" action="${pageContext.request.contextPath}/imovel/visita?idImovel=<%= imovel.getId() %>">
      <input type="hidden" name="csrf" value="${csrf}">
      <input type="hidden" name="idImovel" value="<%= imovel.getId() %>">

      <div class="wizard-visita-dias">
        <% for (Map.Entry<String, List<VisitaSlot>> entrada : porDia.entrySet()) { %>
          <div class="wizard-visita-dia">
            <h3><%= entrada.getKey() %></h3>
            <div class="wizard-visita-horarios">
              <% for (VisitaSlot slot : entrada.getValue()) { %>
                <label class="wizard-visita-horario">
                  <input type="radio" name="slot" value="<%= slot.chave() %>" required>
                  <span><%= slot.horaInicio() %></span>
                </label>
              <% } %>
            </div>
          </div>
        <% } %>
      </div>

      <div class="wizard-acoes">
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
  <% } %>
</main>

</body>
</html>
