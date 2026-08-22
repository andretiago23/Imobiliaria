<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Locale, java.text.NumberFormat, model.Imovel, model.Finalidade, model.StatusImovel, model.FotoImovel" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><%= request.getAttribute("imovel") != null ? util.Html.escapar(((Imovel) request.getAttribute("imovel")).getTitulo()) + " | Habittar" : "Imóvel | Habittar" %></title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=59">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=59">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=59">
</head>
<body>

<% request.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="app-main">

  <a class="voltar" href="${pageContext.request.contextPath}/inicio">
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
    Voltar ao catálogo
  </a>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <% if ("1".equals(request.getParameter("publicado"))) { %>
    <p class="alerta" style="background:#e6f5ec;border-color:#bfe3cd;color:#1c6b3f;">
      Seu imóvel foi anunciado com sucesso! Ele já está visível no catálogo.
    </p>
  <% } %>
  <% if ("1".equals(request.getParameter("visitaAgendada"))) { %>
    <p class="alerta" style="background:#e6f5ec;border-color:#bfe3cd;color:#1c6b3f;">
      Visita agendada com sucesso! O anunciante foi avisado por e-mail.
    </p>
  <% } %>

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

  <%
    java.util.List<FotoImovel> fotosImovel = imovel.getFotos();
    boolean temFotoReal = fotosImovel != null && !fotosImovel.isEmpty();
    String urlFotoPrincipal = temFotoReal ? fotosImovel.get(0).getUrlFoto()
        : util.ImagemImovel.urlIlustrativa(imovel.getTipo(), imovel.getId());
  %>
  <div class="imovel-detalhe">
    <div>
      <div class="imovel-detalhe__foto">
        <img id="fotoPrincipalImovel" src="<%= urlFotoPrincipal %>"
          alt="Foto de <%= util.Html.escapar(imovel.getTitulo()) %>">
        <% if (imovel.getIdUsuario() != ((model.Usuario) session.getAttribute("usuarioLogado")).getId()) { %>
          <form method="post" action="${pageContext.request.contextPath}/favorito" class="imovel-detalhe__salvar">
            <input type="hidden" name="csrf" value="${csrf}">
            <input type="hidden" name="idImovel" value="<%= imovel.getId() %>">
            <input type="hidden" name="destino" value="${pageContext.request.contextPath}/imovel?id=<%= imovel.getId() %>">
            <button type="submit" class="botao-salvar <%= Boolean.TRUE.equals(request.getAttribute("salvo")) ? "botao-salvar--ativo" : "" %>"
              title="<%= Boolean.TRUE.equals(request.getAttribute("salvo")) ? "Remover dos salvos" : "Salvar imóvel" %>"
              aria-label="Salvar imóvel">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="<%= Boolean.TRUE.equals(request.getAttribute("salvo")) ? "currentColor" : "none" %>" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M19 21 12 16l-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>
            </button>
          </form>
        <% } %>
      </div>
      <% if (temFotoReal && fotosImovel.size() > 1) { %>
        <div class="imovel-detalhe__miniaturas">
          <% for (FotoImovel foto : fotosImovel) { %>
            <button type="button" class="imovel-detalhe__miniatura" data-src="<%= foto.getUrlFoto() %>">
              <img src="<%= foto.getUrlFoto() %>" alt="">
            </button>
          <% } %>
        </div>
      <% } %>

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
      <p class="lead" style="max-width:none;white-space:pre-line;">
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

      <% if (imovel.getStatus() == StatusImovel.RESERVADO && !donoDoAnuncio) { %>
        <p class="alerta" style="background:#fdf3e3;border-color:#f0dba8;color:#8a6200;margin-top:16px;">
          Este imóvel já está em negociação com outro interessado, mas você ainda pode demonstrar interesse.
        </p>
      <% } %>

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

        <%
          String telefoneWhats = imovel.getDono() != null ? imovel.getDono().getTelefone() : null;
          if (telefoneWhats != null && !telefoneWhats.isBlank()) {
            String somenteDigitos = telefoneWhats.replaceAll("\\D", "");
            String numeroWhats = somenteDigitos.length() <= 11 ? "55" + somenteDigitos : somenteDigitos;
        %>
          <a class="btn btn--whatsapp" style="width:100%;margin-top:16px;" target="_blank" rel="noopener"
            href="https://wa.me/<%= numeroWhats %>?text=<%= java.net.URLEncoder.encode("Olá! Vi o imóvel \"" + imovel.getTitulo() + "\" na Habittar e gostaria de mais informações.", java.nio.charset.StandardCharsets.UTF_8) %>"
            data-whatsapp-id="<%= imovel.getId() %>" data-whatsapp-csrf="${csrf}">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><path d="M12.04 2C6.58 2 2.13 6.45 2.13 11.91c0 1.75.46 3.45 1.32 4.95L2 22l5.29-1.39a9.9 9.9 0 0 0 4.75 1.21h.01c5.46 0 9.9-4.45 9.9-9.91 0-2.65-1.03-5.14-2.9-7.01A9.82 9.82 0 0 0 12.04 2zm5.8 14.16c-.24.68-1.4 1.3-1.93 1.37-.5.08-1.11.11-1.79-.11a16.3 16.3 0 0 1-1.6-.6c-2.82-1.22-4.66-4.06-4.8-4.25-.14-.19-1.15-1.53-1.15-2.92s.72-2.07.98-2.35c.26-.29.56-.36.75-.36l.53.01c.17 0 .4-.06.62.48.24.58.8 2 .87 2.15.07.15.12.32.02.51-.1.19-.14.31-.29.48-.14.17-.3.37-.43.5-.14.14-.29.29-.13.57.17.29.75 1.25 1.62 2.02 1.11.99 2.05 1.3 2.33 1.44.29.14.46.12.63-.07.17-.19.72-.85.92-1.14.19-.29.38-.24.63-.15.26.1 1.65.79 1.93.93.29.14.48.22.55.34.07.13.07.72-.17 1.4z"/></svg>
            Entrar em contato
          </a>
        <% } %>

        <%
          java.util.List<?> disponibilidade = (java.util.List<?>) request.getAttribute("disponibilidade");
        %>
        <% if (disponibilidade != null && !disponibilidade.isEmpty()) { %>
          <a class="btn btn--secondary" style="width:100%;margin-top:10px;" href="${pageContext.request.contextPath}/imovel/visita?idImovel=<%= imovel.getId() %>">
            Agendar visita
          </a>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/interesse">
          <input type="hidden" name="csrf" value="${csrf}">
          <input type="hidden" name="idImovel" value="<%= imovel.getId() %>">

          <p class="micro" style="margin-top:16px;color:var(--text-secondary);">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="display:inline;vertical-align:-2px;margin-right:4px;" aria-hidden="true"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-10 6L2 7"/></svg>
            <%= util.Html.escapar(usuarioLogado.getEmail()) %>
          </p>
          <textarea name="mensagem" rows="3" required maxlength="500"
            style="width:100%;margin-top:8px;font-family:var(--font-sans);font-size:14px;color:var(--text-primary);background:var(--surface-page);border:1px solid var(--border-subtle);border-radius:var(--radius-sm);padding:12px 14px;resize:vertical;"
            ><%= "Olá! Quero ser contatado sobre este imóvel em " + (aluguel ? "aluguel" : "venda") + " que vi na Habittar." %></textarea>
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

<script>
  // "Entrar em contato" via WhatsApp: registra a métrica com um fetch
  // assíncrono ANTES de deixar o navegador seguir para o wa.me, sem
  // travar o clique — se o fetch não terminar a tempo, o link já foi.
  document.querySelectorAll("[data-whatsapp-id]").forEach(function (link) {
    link.addEventListener("click", function () {
      var dados = new URLSearchParams();
      dados.set("idImovel", link.dataset.whatsappId);
      dados.set("csrf", link.dataset.whatsappCsrf);
      navigator.sendBeacon
        ? navigator.sendBeacon("${pageContext.request.contextPath}/imovel/whatsapp", dados)
        : fetch("${pageContext.request.contextPath}/imovel/whatsapp", { method: "POST", body: dados, keepalive: true });
    });
  });

  // Miniaturas da galeria: clicar troca a foto principal, sem recarregar
  // a página.
  var fotoPrincipal = document.getElementById("fotoPrincipalImovel");
  document.querySelectorAll(".imovel-detalhe__miniatura").forEach(function (miniatura) {
    miniatura.addEventListener("click", function () {
      if (!fotoPrincipal) return;
      fotoPrincipal.src = miniatura.dataset.src;
      document.querySelectorAll(".imovel-detalhe__miniatura").forEach(function (outra) {
        outra.classList.toggle("is-ativa", outra === miniatura);
      });
    });
  });
</script>


</body>
</html>
