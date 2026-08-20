<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Map, java.util.Locale, java.text.NumberFormat, model.Imovel, model.StatusImovel" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Imóveis anunciados | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=34">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=34">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=34">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=34">
</head>
<body>

<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/inicio" aria-label="Habittar — catálogo">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
    <nav class="nav__links">
      <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
      <a href="${pageContext.request.contextPath}/anunciar">Anunciar</a>
      <a class="is-current" href="${pageContext.request.contextPath}/imoveis-anunciados" aria-current="page">Imóveis anunciados</a>
      <div class="avatar-menu">
        <a class="avatar" href="${pageContext.request.contextPath}/perfil" title="Meu perfil" aria-label="Meu perfil">
          <% if (((model.Usuario) session.getAttribute("usuarioLogado")).getFotoPerfil() != null
                && !((model.Usuario) session.getAttribute("usuarioLogado")).getFotoPerfil().isBlank()) { %>
            <img src="${pageContext.request.contextPath}${sessionScope.usuarioLogado.fotoPerfil}" alt="">
          <% } else { %>
            ${sessionScope.usuarioLogado.inicial}
          <% } %>
        </a>
        <div class="avatar-menu__dropdown">
          <div class="avatar-menu__dropdown-inner">
            <a href="${pageContext.request.contextPath}/perfil">Ver meu perfil</a>
            <a href="${pageContext.request.contextPath}/logout">Sair</a>
          </div>
        </div>
      </div>
    </nav>
  </div>
</header>

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
          </div>
        </div>
      </article>
      <% } %>
    </div>
  <% } %>
</main>

</body>
</html>
