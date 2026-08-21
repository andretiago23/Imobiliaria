<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Map, java.util.Locale, java.text.NumberFormat, model.Imovel, model.StatusImovel" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Imóveis anunciados | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=46">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=46">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=46">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=46">
</head>
<body>

<% pageContext.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="app-main" style="max-width:960px;">
  <div class="app-header">
    <div>
      <p class="eyebrow">Painel do anunciante</p>
      <h1 class="display">Imóveis <span class="hl">anunciados</span></h1>
    </div>
    <a class="btn btn--primary btn--sm" href="${pageContext.request.contextPath}/anunciar">+ Novo anúncio</a>
  </div>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    List<Imovel> imoveis = (List<Imovel>) request.getAttribute("imoveis");
    Map<Integer, Integer> leadsPorImovel = (Map<Integer, Integer>) request.getAttribute("leadsPorImovel");
    Map<Integer, Integer> visitasPorImovel = (Map<Integer, Integer>) request.getAttribute("visitasPorImovel");
    NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    if (imoveis == null || imoveis.isEmpty()) {
  %>
    <div class="estado-vazio">
      <p>Você ainda não publicou nenhum imóvel.</p>
      <a class="btn btn--primary btn--sm" style="margin-top:12px;" href="${pageContext.request.contextPath}/anunciar">Anunciar imóvel</a>
    </div>
  <% } else { %>
    <div class="anunciados-lista">
      <% for (Imovel imovel : imoveis) {
           long dias = imovel.getDiasSemAtualizacao();
           String corBadge = imovel.getCorBadgeAtualizacao();
           int leads = leadsPorImovel != null && leadsPorImovel.containsKey(imovel.getId()) ? leadsPorImovel.get(imovel.getId()) : 0;
           int visitas = visitasPorImovel != null && visitasPorImovel.containsKey(imovel.getId()) ? visitasPorImovel.get(imovel.getId()) : 0;
      %>
      <article class="anunciado-card">
        <div class="anunciado-card__foto">
          <img src="<%= util.ImagemImovel.urlIlustrativa(imovel.getTipo(), imovel.getId()) %>" alt="" loading="lazy">
        </div>
        <div class="anunciado-card__corpo">
          <div class="anunciado-card__topo">
            <div>
              <span class="badge badge--status" style="position:static;"><%= imovel.getStatus().getRotulo() %></span>
              <span class="anunciado-card__badge-atualizacao anunciado-card__badge-atualizacao--<%= corBadge %>">
                Última atualização há <%= dias %> dia(s)
              </span>
            </div>
            <p class="anunciado-card__preco"><%= moeda.format(imovel.getPreco()) %></p>
          </div>
          <p class="anunciado-card__titulo"><%= util.Html.escapar(imovel.getTitulo()) %></p>
          <p class="micro" style="color:var(--text-secondary);"><%= util.Html.escapar(imovel.getEnderecoCompleto()) %></p>

          <div class="anunciado-card__metricas">
            <div><strong><%= imovel.getVisualizacoes() %></strong><span>Visualizações</span></div>
            <div><strong><%= leads %></strong><span>Interessados</span></div>
            <div><strong><%= imovel.getContatosWhatsapp() %></strong><span>Contatos WhatsApp</span></div>
            <div><strong><%= visitas %></strong><span>Visitas agendadas</span></div>
          </div>

          <div class="anunciado-card__acoes">
            <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/imovel?id=<%= imovel.getId() %>">Ver anúncio</a>
            <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/editar-imovel?id=<%= imovel.getId() %>">Editar</a>

            <form method="post" action="${pageContext.request.contextPath}/imoveis-anunciados" style="display:inline-flex;gap:8px;">
              <input type="hidden" name="csrf" value="${csrf}">
              <input type="hidden" name="idImovel" value="<%= imovel.getId() %>">
              <select name="status" class="anunciado-card__select-status" onchange="this.form.requestSubmit()">
                <option value="ativo" <%= imovel.getStatus() == StatusImovel.ATIVO ? "selected" : "" %>>Disponível</option>
                <option value="reservado" <%= imovel.getStatus() == StatusImovel.RESERVADO ? "selected" : "" %>>Reservado</option>
                <option value="<%= imovel.getFinalidade() == model.Finalidade.ALUGUEL ? "alugado" : "vendido" %>"
                  <%= (imovel.getStatus() == StatusImovel.VENDIDO || imovel.getStatus() == StatusImovel.ALUGADO) ? "selected" : "" %>>
                  <%= imovel.getFinalidade() == model.Finalidade.ALUGUEL ? "Alugado" : "Vendido" %>
                </option>
                <option value="inativo" <%= imovel.getStatus() == StatusImovel.INATIVO ? "selected" : "" %>>Pausado</option>
              </select>
            </form>

            <!-- Item 6.1: excluir imóvel, com confirmação -->
            <button type="button" class="btn-icone btn-icone--perigo" title="Excluir imóvel"
              aria-label="Excluir imóvel"
              data-excluir-imovel="<%= imovel.getId() %>">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 6h18"/><path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/></svg>
            </button>
          </div>
        </div>
      </article>
      <% } %>
    </div>
  <% } %>
</main>

<!-- Item 6.1: modal de confirmação de exclusão -->
<dialog id="modalExcluirImovel" class="modal-confirmacao">
  <form method="dialog" class="modal-confirmacao__corpo">
    <h2>Excluir imóvel</h2>
    <p>Tem certeza que deseja excluir esse imóvel? Essa ação não pode ser desfeita.</p>
    <div class="modal-confirmacao__acoes">
      <button type="submit" value="cancelar" class="btn btn--secondary btn--sm">Cancelar</button>
      <button type="submit" value="confirmar" class="btn btn--perigo btn--sm">Excluir</button>
    </div>
  </form>
</dialog>

<form method="post" action="${pageContext.request.contextPath}/imoveis-anunciados" id="formExcluirImovel" hidden>
  <input type="hidden" name="csrf" value="${csrf}">
  <input type="hidden" name="acao" value="excluir">
  <input type="hidden" name="idImovel" id="idImovelExcluir" value="">
</form>

<script>
  (function () {
    var modal = document.getElementById("modalExcluirImovel");
    var formExcluir = document.getElementById("formExcluirImovel");
    var campoId = document.getElementById("idImovelExcluir");

    document.querySelectorAll("[data-excluir-imovel]").forEach(function (botao) {
      botao.addEventListener("click", function () {
        campoId.value = botao.getAttribute("data-excluir-imovel");
        modal.showModal();
      });
    });

    modal.addEventListener("close", function () {
      if (modal.returnValue === "confirmar") {
        formExcluir.requestSubmit();
      }
    });
  })();
</script>

</body>
</html>
