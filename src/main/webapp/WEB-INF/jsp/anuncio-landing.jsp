<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Anuncie seu imóvel | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=14">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=14">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=14">
</head>
<body>

<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/index.jsp" aria-label="Habittar — página principal">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
    <nav class="nav__links">
      <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
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

<main>
  <!-- ===================== HERO ===================== -->
  <section class="section hero">
    <div class="map-grid" aria-hidden="true"></div>
    <div class="wrap" style="text-align:center;max-width:720px;">
      <p class="eyebrow">Anuncie com a Habittar</p>
      <h1 class="display" style="font-size:clamp(32px,5vw,52px);">Seu imóvel visto por quem <span class="hl">está procurando</span>, hoje.</h1>
      <p class="lead" style="margin:16px auto 0;">Publique em poucos minutos, escolha o plano que faz sentido pra você e só paga quando o anúncio estiver pronto para ir ao ar.</p>
      <div class="cta__actions" style="justify-content:center;margin-top:32px;">
        <a class="btn btn--primary btn--interactive" href="${pageContext.request.contextPath}/anunciar/etapa1">
          <span class="btn__label">Anunciar agora</span>
          <span class="btn__reveal" aria-hidden="true">
            Anunciar agora
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
          </span>
          <span class="btn__dot" aria-hidden="true"></span>
        </a>
        <a class="btn btn--secondary btn--interactive" href="${pageContext.request.contextPath}/planos">
          <span class="btn__label">Veja nossos planos</span>
          <span class="btn__reveal" aria-hidden="true">
            Veja nossos planos
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
          </span>
          <span class="btn__dot" aria-hidden="true"></span>
        </a>
      </div>
      <p class="micro" style="margin-top:16px;color:var(--text-secondary);">Sem compromisso até a confirmação do pagamento.</p>
    </div>
  </section>

  <!-- ===================== BENEFÍCIOS ===================== -->
  <section class="section" id="beneficios" style="padding-top:0;">
    <div class="wrap">
      <div class="cats reveal">
        <div class="cat" style="cursor:default;">
          <span class="cat__icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.2-3.2"/></svg>
          </span>
          <div><h3 class="display">Mais alcance</h3><p>Seu anúncio entra direto no catálogo buscado por quem já está olhando imóveis na sua região.</p></div>
        </div>
        <div class="cat" style="cursor:default;">
          <span class="cat__icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7Z"/><circle cx="12" cy="12" r="3"/></svg>
          </span>
          <div><h3 class="display">Visibilidade real</h3><p>Planos com posição em destaque na busca, para quem quer sair na frente.</p></div>
        </div>
        <div class="cat" style="cursor:default;">
          <span class="cat__icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3H14Z"/><path d="M7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg>
          </span>
          <div><h3 class="display">Suporte quando precisar</h3><p>Dúvidas durante o cadastro ou depois de publicar? Nosso time responde por e-mail.</p></div>
        </div>
      </div>
    </div>
  </section>
</main>

</body>
</html>
