<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Locale, java.text.NumberFormat, model.Usuario, model.Imovel, model.ContatoInteresse, model.StatusImovel" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Meu perfil | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/perfil.css">
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
      <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
      <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/logout">Sair</a>
    </nav>
  </div>
</header>

<%
  Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
  boolean temFoto = usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isBlank();
%>

<main class="app-main" style="max-width:900px;">

  <a class="voltar" href="${pageContext.request.contextPath}/inicio">
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
    Voltar ao catálogo
  </a>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <!-- ===================== CABEÇALHO DO PERFIL ===================== -->
  <section class="perfil-hero">
    <form method="post" action="${pageContext.request.contextPath}/perfil" enctype="multipart/form-data" id="formFoto">
      <input type="hidden" name="acao" value="foto">
      <input type="hidden" name="csrf" value="${csrf}">
      <label for="foto" class="perfil-avatar-upload" title="Alterar foto de perfil">
        <span class="avatar avatar--grande">
          <% if (temFoto) { %>
            <img src="${pageContext.request.contextPath}${sessionScope.usuarioLogado.fotoPerfil}" alt="">
          <% } else { %>
            ${sessionScope.usuarioLogado.inicial}
          <% } %>
        </span>
        <span class="perfil-avatar-upload__overlay">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M14.5 4h-5L7 7H4a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h16a1 1 0 0 0 1-1V8a1 1 0 0 0-1-1h-3l-2.5-3Z"/><circle cx="12" cy="13" r="3.5"/></svg>
          Alterar foto
        </span>
      </label>
      <input type="file" id="foto" name="foto" accept="image/jpeg,image/png,image/webp"
        class="sr-only" onchange="this.form.requestSubmit()">
    </form>

    <div class="perfil-hero__info">
      <span class="badge" style="position:static;">${sessionScope.usuarioLogado.tipoUsuario.rotulo}</span>
      <h1 class="display">${sessionScope.usuarioLogado.nomeExibicao}</h1>
      <p class="micro">${sessionScope.usuarioLogado.email}</p>
    </div>
  </section>

  <!-- ===================== RESUMO DA CONTA ===================== -->
  <section class="resumo-conta">
    <div class="resumo-conta__item">
      <div class="resumo-conta__valor">${reputacao}</div>
      <div class="resumo-conta__rotulo micro">Sua reputação</div>
    </div>
    <div class="resumo-conta__item">
      <div class="resumo-conta__valor">${totalAvaliacoes}</div>
      <div class="resumo-conta__rotulo micro">Avaliações recebidas</div>
    </div>
    <div class="resumo-conta__item">
      <div class="resumo-conta__valor">${interessesPendentes}</div>
      <div class="resumo-conta__rotulo micro">Interesses pendentes</div>
    </div>
  </section>

  <div class="perfil-grid">

    <!-- ===================== DADOS DA CONTA ===================== -->
    <section class="perfil-card">
      <h2>Seus dados</h2>
      <form method="post" action="${pageContext.request.contextPath}/perfil" class="perfil-dados">
        <input type="hidden" name="acao" value="perfil">
        <input type="hidden" name="csrf" value="${csrf}">

        <div class="filtros__campo">
          <label>Nome completo</label>
          <input type="text" value="${sessionScope.usuarioLogado.nome}" disabled>
        </div>

        <div class="filtros__campo">
          <label>E-mail</label>
          <input type="text" value="${sessionScope.usuarioLogado.email}" disabled>
        </div>

        <div class="filtros__campo">
          <label for="apelido">Apelido <span class="micro">(como quer ser chamado)</span></label>
          <input type="text" id="apelido" name="apelido" maxlength="60" placeholder="Opcional" value="${sessionScope.usuarioLogado.apelido}">
        </div>

        <div class="filtros__campo">
          <label for="telefone">Telefone</label>
          <input type="text" id="telefone" name="telefone" placeholder="(11) 90000-0000" maxlength="15"
            data-mascara="telefone" value="${sessionScope.usuarioLogado.telefone}">
        </div>

        <button class="btn btn--primary btn--sm" type="submit">Salvar dados</button>
      </form>
    </section>

    <!-- ===================== SEÇÃO ESPECÍFICA DO TIPO DE CONTA ===================== -->
    <section class="perfil-card">
      <% if (usuario.podeAnunciar()) { %>

        <h2>Painel de imóveis</h2>
        <p class="alerta alerta-erro" role="alert">${erroSecao}</p>
        <%
          List<Imovel> imoveis = (List<Imovel>) request.getAttribute("imoveis");
          if (imoveis == null || imoveis.isEmpty()) {
        %>
          <div class="estado-vazio">
            <p>Você ainda não publicou nenhum imóvel.</p>
            <a class="btn btn--primary btn--sm" style="margin-top:12px;" href="${pageContext.request.contextPath}/anunciar">Anunciar imóvel</a>
          </div>
        <% } else {
          NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
          for (Imovel imovel : imoveis) {
        %>
          <a class="perfil-item" href="${pageContext.request.contextPath}/imovel?id=<%= imovel.getId() %>">
            <div>
              <p class="perfil-item__titulo"><%= util.Html.escapar(imovel.getTitulo()) %></p>
              <p class="micro"><%= moeda.format(imovel.getPreco()) %></p>
            </div>
            <span class="badge <%= imovel.getStatus() == StatusImovel.ATIVO ? "" : "badge--status" %>" style="position:static;">
              <%= imovel.getStatus().getRotulo() %>
            </span>
          </a>
        <% }
          } %>
        <a class="btn btn--secondary btn--sm" style="margin-top:16px;width:100%;" href="${pageContext.request.contextPath}/anunciar">+ Novo anúncio</a>

      <% } else { %>

        <h2>Meus interesses</h2>
        <p class="alerta alerta-erro" role="alert">${erroSecao}</p>
        <%
          List<ContatoInteresse> interesses = (List<ContatoInteresse>) request.getAttribute("interesses");
          if (interesses == null || interesses.isEmpty()) {
        %>
          <div class="estado-vazio">
            <p>Você ainda não demonstrou interesse em nenhum imóvel.</p>
            <a class="btn btn--primary btn--sm" style="margin-top:12px;" href="${pageContext.request.contextPath}/inicio">Ver catálogo</a>
          </div>
        <% } else {
          for (ContatoInteresse interesse : interesses) {
        %>
          <a class="perfil-item" href="${pageContext.request.contextPath}/imovel?id=<%= interesse.getIdImovel() %>">
            <div>
              <p class="perfil-item__titulo"><%= util.Html.escapar(interesse.getImovel().getTitulo()) %></p>
              <p class="micro">Enviado em <%= interesse.getDataContato() != null ? interesse.getDataContato().toLocalDate() : "" %></p>
            </div>
            <span class="badge" style="position:static;"><%= interesse.getStatus().getRotulo() %></span>
          </a>
        <% }
          } %>

      <% } %>
    </section>
  </div>

</main>

<script src="${pageContext.request.contextPath}/js/validacao.js"></script>
<script src="${pageContext.request.contextPath}/js/formulario.js"></script>
</body>
</html>
