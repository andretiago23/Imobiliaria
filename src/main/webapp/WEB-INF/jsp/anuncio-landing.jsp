<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Anuncie seu imóvel | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=38">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=38">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=38">
</head>
<body>

<% pageContext.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main>
  <!-- ===================== HERO ===================== -->
  <section class="section hero">
    <div class="map-grid" aria-hidden="true"></div>
    <div class="wrap" style="text-align:center;">
      <p class="eyebrow">Anuncie com a Habittar</p>
      <h1 class="display" style="font-size:clamp(32px,5vw,52px);max-width:800px;margin:0 auto;">Seu imóvel visto por quem <span class="hl">está procurando</span>, hoje.</h1>
      <p class="lead" style="margin:16px auto 0;">Publique em poucos minutos, escolha o plano que faz sentido pra você e só paga quando o anúncio estiver pronto para ir ao ar.</p>
      <div class="cta__actions" style="justify-content:center;margin-top:32px;">
        <details class="anunciar-dropdown">
          <summary class="btn btn--primary">
            Anunciar agora
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg>
          </summary>
          <div class="anunciar-dropdown__menu">
            <a href="${pageContext.request.contextPath}/anunciar/etapa1?tipo=proprietario">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 10.5 12 4l9 6.5V20a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/></svg>
              <span><strong>Sou proprietário</strong><small>Quero anunciar meu próprio imóvel</small></span>
            </a>
            <a href="${pageContext.request.contextPath}/anunciar/etapa1?tipo=corretor">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="3" y="7" width="18" height="14" rx="2"/><path d="M8 7V4h8v3"/></svg>
              <span><strong>Sou corretor / imobiliária</strong><small>Anuncio em nome de terceiros</small></span>
            </a>
          </div>
        </details>
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
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/></svg>
          </span>
          <div><h3 class="display">Contato direto, sem enrolação</h3><p>Quem se interessar cai direto no seu e-mail — converse e já combine a visita, sem intermediário no meio do caminho.</p></div>
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
