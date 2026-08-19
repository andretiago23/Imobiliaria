<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Locale, java.text.NumberFormat, model.Imovel, model.Finalidade, model.StatusImovel" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><%= request.getAttribute("imovel") != null ? util.Html.escapar(((Imovel) request.getAttribute("imovel")).getTitulo()) + " | Habittar" : "Imóvel | Habittar" %></title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=15">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=15">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=15">
</head>
<body>

<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/inicio" aria-label="Habittar — catálogo">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
    <nav class="nav__links">
      <div class="avatar-menu">
        <a class="avatar" href="${pageContext.request.contextPath}/perfil" title="Meu perfil" aria-label="Meu perfil">
          <% if (session.getAttribute("usuarioLogado") != null
                && ((model.Usuario) session.getAttribute("usuarioLogado")).getFotoPerfil() != null
                && !((model.Usuario) session.getAttribute("usuarioLogado")).getFotoPerfil().isBlank()) { %>
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
      NumberFormat area = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
      area.setMaximumFractionDigits(1);
  %>
  <nav class="breadcrumb micro" aria-label="Trilha de navegação">
    <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
    <% if (imovel.getCidade() != null && !imovel.getCidade().isBlank()) { %>
      <span aria-hidden="true">/</span>
      <a href="${pageContext.request.contextPath}/inicio?cidade=<%= java.net.URLEncoder.encode(imovel.getCidade(), java.nio.charset.StandardCharsets.UTF_8) %>"><%= util.Html.escapar(imovel.getCidade()) %></a>
    <% } %>
    <span aria-hidden="true">/</span>
    <span class="breadcrumb__atual"><%= util.Html.escapar(imovel.getTitulo()) %></span>
  </nav>

  <div class="imovel-detalhe">
    <div>
      <div class="imovel-detalhe__foto">
        <img src="<%= util.ImagemImovel.urlIlustrativa(imovel.getTipo(), imovel.getId()) %>"
          alt="Foto ilustrativa de <%= util.Html.escapar(imovel.getTitulo()) %>">
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
          <svg class="ficha-icone" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M8 3H5a2 2 0 0 0-2 2v3"/><path d="M21 8V5a2 2 0 0 0-2-2h-3"/><path d="M3 16v3a2 2 0 0 0 2 2h3"/><path d="M16 21h3a2 2 0 0 0 2-2v-3"/></svg>
          <div class="ficha-tecnica__valor"><%= area.format(imovel.getAreaM2()) %> m²</div>
          <div class="micro">Área</div>
        </div>
        <div>
          <svg class="ficha-icone" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M2 4v16"/><path d="M2 8h18a2 2 0 0 1 2 2v10"/><path d="M2 17h20"/><path d="M6 8v9"/></svg>
          <div class="ficha-tecnica__valor"><%= imovel.getQuartos() %></div>
          <div class="micro">Quarto(s)</div>
        </div>
        <div>
          <svg class="ficha-icone" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 2.7 17.66 8.4A8 8 0 1 1 6.34 8.4Z"/></svg>
          <div class="ficha-tecnica__valor"><%= imovel.getBanheiros() %></div>
          <div class="micro">Banheiro(s)</div>
        </div>
        <div>
          <svg class="ficha-icone" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M14 16H9m10 0h3v-3.15a1 1 0 0 0-.84-.99L16 11l-2.7-3.6a1 1 0 0 0-.8-.4H5.24a2 2 0 0 0-1.8 1.1l-.8 1.63A6 6 0 0 0 2 12.42V16h2"/><circle cx="6.5" cy="16.5" r="2.5"/><circle cx="16.5" cy="16.5" r="2.5"/></svg>
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

      <%
        model.Usuario usuarioLogado = (model.Usuario) session.getAttribute("usuarioLogado");
        boolean donoDoAnuncio = usuarioLogado != null && usuarioLogado.getId() == imovel.getIdUsuario();
      %>

      <% if ("1".equals(request.getParameter("interesseEnviado"))) { %>
        <p class="alerta" style="background:#e6f5ec;border-color:#bfe3cd;color:#1c6b3f;margin-top:16px;">
          Interesse enviado! O anunciante vai receber seus dados de contato.
        </p>
      <% } else if (request.getParameter("erroInteresse") != null) { %>
        <p class="alerta alerta-erro" style="margin-top:16px;"><%= util.Html.escapar(request.getParameter("erroInteresse")) %></p>
      <% } %>

      <% if (donoDoAnuncio) { %>
        <p class="imovel-painel__aviso micro">Este é o seu próprio anúncio.</p>
      <% } else { %>
        <form method="post" action="${pageContext.request.contextPath}/interesse">
          <input type="hidden" name="csrf" value="${csrf}">
          <input type="hidden" name="idImovel" value="<%= imovel.getId() %>">
          <textarea name="mensagem" rows="3" required maxlength="500"
            placeholder="Escreva uma mensagem para o anunciante (ex.: horários para visita, dúvidas sobre o imóvel)."
            style="width:100%;margin-top:16px;font-family:var(--font-sans);font-size:14px;color:var(--text-primary);background:var(--surface-page);border:1px solid var(--border-subtle);border-radius:var(--radius-sm);padding:12px 14px;resize:vertical;"></textarea>
          <button class="btn btn--primary btn--interactive" type="submit" style="width:100%;margin-top:12px;">
            <span class="btn__label">Tenho interesse</span>
            <span class="btn__reveal" aria-hidden="true">
              Tenho interesse
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
            </span>
            <span class="btn__dot" aria-hidden="true"></span>
          </button>
        </form>
        <p class="imovel-painel__aviso micro">O anunciante recebe seu nome, e-mail e a mensagem enviada.</p>
      <% } %>
    </aside>
  </div>

  <%
    @SuppressWarnings("unchecked")
    java.util.List<Imovel> similares = (java.util.List<Imovel>) request.getAttribute("similares");
    if (similares != null && !similares.isEmpty()) {
  %>
  <section class="imovel-similares">
    <h2 class="display" style="font-size:22px;">Similares na mesma <span class="hl">região</span></h2>
    <div class="catalogo__grade">
      <% for (Imovel parecido : similares) {
        boolean aluguelParecido = parecido.getFinalidade() == Finalidade.ALUGUEL;
      %>
      <article class="card">
        <div class="card__photo tem-foto">
          <img src="<%= util.ImagemImovel.urlIlustrativa(parecido.getTipo(), parecido.getId()) %>"
            alt="Foto ilustrativa de <%= util.Html.escapar(parecido.getTitulo()) %>" loading="lazy">
          <span class="badge"><%= aluguelParecido ? "Aluguel" : "Venda" %></span>
        </div>
        <div class="card__body">
          <div class="card__price">
            <%= moeda.format(parecido.getPreco()) %>
            <% if (aluguelParecido) { %><span class="micro">/mês</span><% } %>
          </div>
          <p style="margin:6px 0 0;font-weight:600;"><%= util.Html.escapar(parecido.getTitulo()) %></p>
          <div class="card__specs">
            <span><%= area.format(parecido.getAreaM2()) %> m²</span>
            <span><%= parecido.getQuartos() %> qto(s)</span>
          </div>
        </div>
        <a class="btn btn--secondary btn--sm card__link" href="${pageContext.request.contextPath}/imovel?id=<%= parecido.getId() %>">Ver detalhes</a>
      </article>
      <% } %>
    </div>
  </section>
  <% } %>
  <% } %>

</main>

</body>
</html>
