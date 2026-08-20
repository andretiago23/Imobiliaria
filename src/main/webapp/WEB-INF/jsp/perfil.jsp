<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Locale, java.text.NumberFormat, model.Usuario, model.Imovel, model.ContatoInteresse, model.StatusImovel" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Meu perfil | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=35">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=35">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=35">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/perfil.css?v=35">
</head>
<body>

<%
  Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
  boolean temFoto = usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isBlank();
%>

<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/inicio" aria-label="Habittar — catálogo">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
    <nav class="nav__links">
      <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
      <a href="${pageContext.request.contextPath}/imoveis-anunciados">Imóveis anunciados</a>
      <div class="avatar-menu">
        <a class="avatar" href="${pageContext.request.contextPath}/perfil" title="Meu perfil" aria-label="Meu perfil">
          <% if (temFoto) { %>
            <img src="${pageContext.request.contextPath}${sessionScope.usuarioLogado.fotoPerfil}" alt="">
          <% } else { %>
            ${sessionScope.usuarioLogado.inicial}
          <% } %>
        </a>
        <div class="avatar-menu__dropdown">
          <div class="avatar-menu__dropdown-inner">
            <a href="${pageContext.request.contextPath}/perfil">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 21a8 8 0 1 0-16 0"/><circle cx="12" cy="8" r="5"/></svg>
              Ver meu perfil
            </a>
            <a href="${pageContext.request.contextPath}/logout">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="m16 17 5-5-5-5"/><path d="M21 12H9"/></svg>
              Sair
            </a>
          </div>
        </div>
      </div>
    </nav>
  </div>
</header>

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

    <!-- ===================== MEUS IMÓVEIS (se houver algum anunciado) ===================== -->
    <section class="perfil-card">
      <h2>Meus imóveis</h2>
      <p class="alerta alerta-erro" role="alert">${erroSecao}</p>
      <%
        List<Imovel> imoveis = (List<Imovel>) request.getAttribute("imoveis");
        if (imoveis == null || imoveis.isEmpty()) {
      %>
        <div class="estado-vazio">
          <p>Você ainda não publicou nenhum imóvel.</p>
          <p class="micro" style="margin-top:6px;">Qualquer conta pode anunciar — não precisa mudar de perfil.</p>
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
        %>
        <a class="btn btn--secondary btn--sm" style="margin-top:16px;width:100%;" href="${pageContext.request.contextPath}/anunciar">+ Novo anúncio</a>
      <% } %>
    </section>

    <!-- ===================== MEUS INTERESSES ===================== -->
    <section class="perfil-card">
      <h2>Meus interesses</h2>
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
        for (Imovel salvo : salvos) {
      %>
        <a class="perfil-item" href="${pageContext.request.contextPath}/imovel?id=<%= salvo.getId() %>">
          <div>
            <p class="perfil-item__titulo"><%= util.Html.escapar(salvo.getTitulo()) %></p>
            <p class="micro"><%= util.Html.escapar(salvo.getEnderecoCompleto()) %></p>
          </div>
          <span class="badge <%= salvo.getStatus() == model.StatusImovel.ATIVO ? "" : "badge--status" %>" style="position:static;">
            <%= salvo.getStatus().getRotulo() %>
          </span>
        </a>
      <% }
        } %>
    </section>
  </div>

  <!-- ===================== PRIVACIDADE E DADOS (LGPD) ===================== -->
  <section class="perfil-card" style="margin-top:24px;">
    <h2>Privacidade e dados</h2>
    <p class="micro" style="margin:0 0 16px;">
      Você concordou com a
      <a href="${pageContext.request.contextPath}/legal/habittar-psi-privacidade.pdf" target="_blank" rel="noopener">Política de Privacidade e os Termos de Uso</a>
      <% if (usuario.getTermosAceitosEm() != null) { %>
        em <%= usuario.getTermosAceitosEm().toLocalDate() %>.
      <% } else { %>
        — ainda não temos o registro desse aceite para esta conta (criada antes deste controle existir).
      <% } %>
    </p>

    <div class="perfil-privacidade__acoes">
      <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/perfil?acao=exportar">
        Baixar meus dados
      </a>
    </div>

    <details class="perfil-excluir">
      <summary>Excluir minha conta</summary>
      <p class="micro" style="margin:12px 0;">
        Isso remove seu nome, apelido, telefone e foto de perfil, e desativa o login. Seus anúncios,
        interesses e avaliações permanecem como histórico, sem dados que te identifiquem — assim como
        já informado no envio de interesse. Essa ação não pode ser desfeita.
      </p>
      <form method="post" action="${pageContext.request.contextPath}/perfil">
        <input type="hidden" name="acao" value="excluir">
        <input type="hidden" name="csrf" value="${csrf}">
        <label for="confirmacaoExclusao" class="micro">Digite <b>EXCLUIR</b> para confirmar</label>
        <div style="display:flex;gap:8px;margin-top:6px;">
          <input type="text" id="confirmacaoExclusao" name="confirmacao" placeholder="EXCLUIR" style="flex:1;">
          <button class="btn" type="submit" style="background:#d64545;color:#fff;flex:0 0 auto;">Excluir conta</button>
        </div>
      </form>
    </details>
  </section>

</main>

<script src="${pageContext.request.contextPath}/js/validacao.js?v=35"></script>
<script src="${pageContext.request.contextPath}/js/formulario.js?v=35"></script>
</body>
</html>
