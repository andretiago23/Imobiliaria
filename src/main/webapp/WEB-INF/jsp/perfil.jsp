<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Locale, java.text.NumberFormat, model.Usuario, model.Imovel, model.StatusImovel" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Meu perfil | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=40">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=40">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=40">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/perfil.css?v=40">
</head>
<body>

<%
  Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
  boolean temFoto = usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isBlank();
%>

<% pageContext.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="app-main" style="max-width:900px;">

  <!-- Item 5.9: botão Voltar -->
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

  <div class="perfil-grid">

    <!-- ===================== DADOS DA CONTA ===================== -->
    <section class="perfil-card">
      <div class="perfil-card__cabecalho">
        <h2>Seus dados</h2>
        <button type="button" class="botao-editar" id="botaoEditarDados" aria-label="Editar seus dados" title="Editar">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/></svg>
        </button>
      </div>
      <form method="post" action="${pageContext.request.contextPath}/perfil" class="perfil-dados" id="formDados">
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
          <label for="telefone">Telefone</label>
          <input type="text" id="telefone" name="telefone" placeholder="(11) 90000-0000" maxlength="15"
            data-mascara="telefone" value="${sessionScope.usuarioLogado.telefone}" readonly>
        </div>

        <button class="btn btn--primary btn--sm" type="submit" id="botaoSalvarDados" hidden>Salvar dados</button>
      </form>
    </section>

    <!-- ===================== IMÓVEIS SALVOS ===================== -->
    <section class="perfil-card">
      <h2>Imóveis salvos</h2>
      <%
        List<Imovel> salvos = (List<Imovel>) request.getAttribute("salvos");
        if (salvos == null || salvos.isEmpty()) {
      %>
        <div class="estado-vazio">
          <p>Você ainda não salvou nenhum imóvel.</p>
          <a class="btn btn--primary btn--sm" style="margin-top:12px;" href="${pageContext.request.contextPath}/inicio">Ver catálogo</a>
        </div>
      <% } else {
          NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
          for (Imovel salvo : salvos) {
      %>
        <!-- Item 5.7: só foto, preço e localização -->
        <a class="perfil-item perfil-item--salvo" href="${pageContext.request.contextPath}/imovel?id=<%= salvo.getId() %>">
          <img class="perfil-item__foto" src="<%= util.ImagemImovel.urlIlustrativa(salvo.getTipo(), salvo.getId()) %>" alt="">
          <div>
            <p class="perfil-item__titulo"><%= moeda.format(salvo.getPreco()) %></p>
            <p class="micro"><%= util.Html.escapar(salvo.getEnderecoCompleto()) %></p>
          </div>
        </a>
      <% }
        } %>
    </section>
  </div>

</main>

<script src="${pageContext.request.contextPath}/js/validacao.js?v=40"></script>
<script src="${pageContext.request.contextPath}/js/formulario.js?v=40"></script>
<script>
  // Item 5.3: campo de telefone começa travado; o ícone de lápis destrava
  // e mostra o botão "Salvar dados".
  (function () {
    var botaoEditar = document.getElementById("botaoEditarDados");
    var telefone = document.getElementById("telefone");
    var botaoSalvar = document.getElementById("botaoSalvarDados");
    if (!botaoEditar || !telefone || !botaoSalvar) return;

    botaoEditar.addEventListener("click", function () {
      telefone.removeAttribute("readonly");
      telefone.focus();
      botaoSalvar.hidden = false;
    });
  })();
</script>
</body>
</html>
