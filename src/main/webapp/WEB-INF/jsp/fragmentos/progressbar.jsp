<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Barra de progresso do assistente de anúncio. Espera o atributo de
     requisição "etapaAtual" (1 a 4), setado pelo AnuncioWizardServlet antes
     do forward. Reaproveitada nas 4 telas via <jsp:include>. --%>
<%
  int etapaAtual = (Integer) request.getAttribute("etapaAtual");
  String[] rotulos = { "O que anunciar", "Plano", "Seus dados", "Pagamento" };
%>
<div class="wizard-progress" role="group" aria-label="Progresso do anúncio">
  <div class="wizard-progress__trilha">
    <div class="wizard-progress__preenchida" style="width:<%= (etapaAtual - 1) * 100 / 3 %>%;"></div>
  </div>
  <div class="wizard-progress__passos">
    <% for (int passo = 1; passo <= 4; passo++) {
      String estado = passo < etapaAtual ? "concluido" : (passo == etapaAtual ? "atual" : "pendente");
    %>
      <div class="wizard-progress__passo wizard-progress__passo--<%= estado %>">
        <span class="wizard-progress__bolha">
          <% if (passo < etapaAtual) { %>
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 6 9 17l-5-5"/></svg>
          <% } else { %>
            <%= passo %>
          <% } %>
        </span>
        <span class="wizard-progress__rotulo micro"><%= rotulos[passo - 1] %></span>
      </div>
    <% } %>
  </div>
  <p class="micro wizard-progress__contador">Etapa <%= etapaAtual %> de 4</p>
</div>
