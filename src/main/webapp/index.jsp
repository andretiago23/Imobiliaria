<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Habittar | Imoveis a venda e para alugar por localizacao</title>
  <meta name="description" content="Busque imoveis por rua, bairro ou cidade no catalogo da Habittar: fotos, preco, area e ficha tecnica completa. Anuncie seu imovel e receba leads qualificados.">
  <meta property="og:title" content="Habittar | Imoveis por localizacao">
  <meta property="og:description" content="Seu bairro, seu tipo de negocio, um clique. Catalogo completo da Habittar com busca por localizacao real.">
  <meta property="og:type" content="website">
  <meta name="twitter:card" content="summary_large_image">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=18">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=18">
</head>
<body>
<!-- ===================== HEADER ===================== -->
<header class="nav">
  <div class="nav__inner">
    <a class="logo" href="#top" aria-label="Habittar — início">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
    <nav class="nav__links">
      <a href="${pageContext.request.contextPath}/inicio?finalidade=venda">Comprar</a>
      <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel">Alugar</a>
      <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
      <a href="#diferenciais">Como funciona</a>
      <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/login">Entrar</a>
      <a class="btn btn--primary btn--sm btn--interactive" href="#anunciar">
        <span class="btn__label">
          <span class="btn__pin" aria-hidden="true">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.5"/></svg>
          </span>
          Anunciar imóvel
        </span>
        <span class="btn__reveal" aria-hidden="true">
          Anunciar imóvel
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
        </span>
        <span class="btn__dot" aria-hidden="true"></span>
      </a>
    </nav>
  </div>
</header>

<main id="top">
  <% if ("1".equals(request.getParameter("contaExcluida"))) { %>
    <div class="wrap" style="padding-top:96px;">
      <p class="alerta" style="background:#e6f5ec;border:1px solid #bfe3cd;color:#1c6b3f;border-radius:12px;padding:14px 18px;">
        Sua conta foi excluída. Seus dados pessoais foram removidos, conforme a Política de Privacidade.
      </p>
    </div>
  <% } %>
  <!-- ===================== HERO ===================== -->
  <section class="section hero">
    <div class="map-grid" aria-hidden="true"></div>
    <div class="wrap hero__grid">
      <div>
        <p class="eyebrow">Portal imobiliário por localização</p>
        <h1 class="display">O imóvel certo está <span class="hl">mais perto</span> do que você imagina.</h1>

        <form class="search" action="${pageContext.request.contextPath}/inicio" method="get" role="search" id="formBuscaHero">
          <div class="segment" role="group" aria-label="Tipo de negócio">
            <button type="button" class="is-active" data-value="venda" aria-pressed="true">Comprar</button>
            <button type="button" data-value="aluguel" aria-pressed="false">Alugar</button>
            <button type="button" data-value="vender" aria-pressed="false">Vender</button>
          </div>
          <input type="hidden" id="finalidade" name="finalidade" value="venda">
          <label class="search__field">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#FF6A1A" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.5"/></svg>
            <span class="sr-only" hidden>Localização</span>
            <input type="text" name="cidade" placeholder="Digite um bairro, cidade ou rua" autocomplete="off">
          </label>
          <button class="btn btn--primary search__submit" type="submit" aria-label="Buscar imóveis">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.2-3.2"/></svg>
          </button>
        </form>
      </div>

      <div class="scene" aria-hidden="true">
        <svg viewBox="0 0 560 340" role="presentation">
          <defs>
            <pattern id="miniGrid" width="40" height="40" patternUnits="userSpaceOnUse">
              <path d="M40 0H0V40" fill="none" stroke="#16151F" stroke-opacity="0.1" stroke-width="1"/>
            </pattern>
          </defs>
          <rect x="8" y="8" width="544" height="324" rx="16" fill="url(#miniGrid)" stroke="#16151F" stroke-opacity="0.08"/>
          <!-- quarteirões -->
          <g fill="none" stroke="#5B5967" stroke-opacity="0.2" stroke-width="1.5">
            <rect x="48" y="48" width="128" height="84" rx="10"/>
            <rect x="200" y="40" width="96" height="120" rx="10"/>
            <rect x="324" y="60" width="150" height="72" rx="10"/>
            <rect x="60" y="164" width="104" height="120" rx="10"/>
            <rect x="192" y="188" width="132" height="96" rx="10"/>
            <rect x="352" y="164" width="120" height="120" rx="10"/>
          </g>
          <!-- pins secundários pousados -->
          <g fill="none" stroke="#5B5967" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" opacity="0.75">
            <path d="M96 96s6-4.6 6-9.6A6 6 0 0 0 90 86.4c0 5 6 9.6 6 9.6Z"/>
            <path d="M244 118s6-4.6 6-9.6a6 6 0 0 0-12 0c0 5 6 9.6 6 9.6Z"/>
            <path d="M420 108s6-4.6 6-9.6a6 6 0 0 0-12 0c0 5 6 9.6 6 9.6Z"/>
            <path d="M108 236s6-4.6 6-9.6a6 6 0 0 0-12 0c0 5 6 9.6 6 9.6Z"/>
            <path d="M258 258s6-4.6 6-9.6a6 6 0 0 0-12 0c0 5 6 9.6 6 9.6Z"/>
            <path d="M462 246s6-4.6 6-9.6a6 6 0 0 0-12 0c0 5 6 9.6 6 9.6Z"/>
          </g>
          <!-- raio de busca -->
          <g id="heroRadar">
            <circle cx="372" cy="168" r="60" fill="#FF6A1A" fill-opacity="0.08" stroke="#FF6A1A" stroke-opacity="0.45" stroke-width="1.5" stroke-dasharray="5 7"/>
          </g>
          <!-- pin principal -->
          <g id="heroPin">
            <path d="M372 186s16-12.4 16-25.6A16 16 0 0 0 356 160.4c0 13.2 16 25.6 16 25.6Z" fill="#FF6A1A"/>
            <circle cx="372" cy="160" r="6" fill="#FFFFFF"/>
          </g>
          <text x="392" y="206" font-family="Space Mono, monospace" font-size="11" fill="#5B5967">-23.5614, -46.6929</text>
        </svg>
      </div>
    </div>
  </section>

  <!-- ===================== CATEGORIAS ===================== -->
  <section class="section" id="categorias" style="padding-top:0">
    <div class="map-grid" aria-hidden="true"></div>
    <div class="wrap">
      <div class="cats reveal">
        <a class="cat" href="${pageContext.request.contextPath}/inicio?finalidade=venda">
          <span class="cat__icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 10.5 12 4l9 6.5V20a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/><path d="M9.5 21v-6h5v6"/></svg>
          </span>
          <div><h3 class="display">Comprar</h3><p>Apartamentos, casas e terrenos com ficha técnica completa, preço por m² e endereço exato após o login.</p></div>
          <span class="cat__arrow micro">Ver catálogo →</span>
        </a>
        <a class="cat" href="${pageContext.request.contextPath}/inicio?finalidade=aluguel">
          <span class="cat__icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="3" y="7" width="18" height="14" rx="3"/><path d="M8 7V4h8v3M3 13h18"/></svg>
          </span>
          <div><h3 class="display">Alugar</h3><p>Do primeiro apê ao imóvel da família: filtro por valor máximo, quartos mínimos e bairro real, não região genérica.</p></div>
          <span class="cat__arrow micro">Ver catálogo →</span>
        </a>
        <a class="cat" href="#anunciar">
          <span class="cat__icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><path d="M12 7.5v5M9.5 10h5"/></svg>
          </span>
          <div><h3 class="display">Vender</h3><p>Coloque seu imóvel no mapa e receba interesses qualificados, com simulação de financiamento anexada.</p></div>
          <span class="cat__arrow micro">Anunciar →</span>
        </a>
      </div>
    </div>
  </section>

  <svg class="seam seam--to-raised" viewBox="0 0 1440 96" preserveAspectRatio="none" aria-hidden="true"><path d="M0 96C360 8 1080 8 1440 96V96H0Z"/></svg>

  <!-- ===================== IMÓVEIS EM DESTAQUE ===================== -->
  <section class="section section--raised" id="imoveis" style="padding-top:24px">
    <div class="wrap">
      <div class="section__head reveal">
        <div>
          <p class="eyebrow">Catálogo</p>
          <h2 class="display">Imóveis em <span class="hl">destaque</span></h2>
        </div>
        <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/inicio">Ver mais →</a>
      </div>

      <div class="props">
        <article class="card card--feature">
          <div class="card__photo tem-foto">
            <img src="https://images.pexels.com/photos/21284473/pexels-photo-21284473.jpeg?auto=compress&cs=tinysrgb&w=800" alt="" loading="lazy">
            <span class="badge">Venda</span><span class="micro">Cód. HB-1042</span>
          </div>
          <div class="card__body">
            <div class="card__price">R$ 845.000</div>
            <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg> Pinheiros, São Paulo — SP</div>
            <div class="card__specs"><span>92 m²</span><span>3 quartos</span><span>2 banh.</span><span>2 vagas</span></div>
          </div>
        </article>
        <article class="card">
          <div class="card__photo tem-foto">
            <img src="https://images.pexels.com/photos/19239905/pexels-photo-19239905.jpeg?auto=compress&cs=tinysrgb&w=800" alt="" loading="lazy">
            <span class="badge">Aluguel</span>
          </div>
          <div class="card__body">
            <div class="card__price">R$ 3.200<span class="micro">/mês</span></div>
            <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg> Vila Madalena — SP</div>
            <div class="card__specs"><span>58 m²</span><span>2 qtos</span><span>1 vaga</span></div>
          </div>
        </article>
        <article class="card">
          <div class="card__photo tem-foto">
            <img src="https://images.pexels.com/photos/2128329/pexels-photo-2128329.jpeg?auto=compress&cs=tinysrgb&w=800" alt="" loading="lazy">
            <span class="badge">Reservado</span>
          </div>
          <div class="card__body">
            <div class="card__price">R$ 620.000</div>
            <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg> Perdizes — SP</div>
            <div class="card__specs"><span>74 m²</span><span>2 qtos</span><span>1 vaga</span></div>
          </div>
        </article>
        <article class="card">
          <div class="card__photo tem-foto">
            <img src="https://images.pexels.com/photos/18078684/pexels-photo-18078684.jpeg?auto=compress&cs=tinysrgb&w=800" alt="" loading="lazy">
            <span class="badge">Venda</span>
          </div>
          <div class="card__body">
            <div class="card__price">R$ 1.190.000</div>
            <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg> Alto de Pinheiros — SP</div>
            <div class="card__specs"><span>148 m²</span><span>4 qtos</span><span>3 vagas</span></div>
          </div>
        </article>
        <article class="card">
          <div class="card__photo tem-foto">
            <img src="https://images.pexels.com/photos/2030037/pexels-photo-2030037.jpeg?auto=compress&cs=tinysrgb&w=800" alt="" loading="lazy">
            <span class="badge">Aluguel</span>
          </div>
          <div class="card__body">
            <div class="card__price">R$ 2.150<span class="micro">/mês</span></div>
            <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg> Butantã — SP</div>
            <div class="card__specs"><span>44 m²</span><span>1 qto</span><span>1 vaga</span></div>
          </div>
        </article>
      </div>
    </div>
  </section>

  <svg class="seam seam--to-page" viewBox="0 0 1440 96" preserveAspectRatio="none" aria-hidden="true"><path d="M0 0C360 88 1080 88 1440 0V96H0Z"/></svg>

  <!-- ===================== DIFERENCIAIS ===================== -->
  <section class="section" id="diferenciais" style="padding-top:16px">
    <div class="map-grid" aria-hidden="true"></div>
    <div class="wrap">

      <!-- (a) convergência de pins -->
      <div class="diff reveal">
        <div class="diff__text">
          <p class="eyebrow">01 — Catálogo único</p>
          <h3 class="display">Todo o portfólio em <span class="hl">um só lugar</span></h3>
          <p class="lead">Nada de abrir cinco abas. Cada imóvel da Habittar entra no mesmo mapa, com o mesmo padrão de ficha e o mesmo nível de detalhe.</p>
        </div>
        <div class="scene" aria-hidden="true">
          <svg viewBox="0 0 640 420" role="presentation">
            <g fill="none" stroke="#FF6A1A" stroke-opacity="0.3" stroke-width="1">
              <path class="draw" d="M80 70C200 140 240 180 320 210"/>
              <path class="draw" d="M560 60C440 130 400 180 320 210"/>
              <path class="draw" d="M60 340C180 300 240 250 320 210"/>
              <path class="draw" d="M580 350C460 300 400 250 320 210"/>
              <path class="draw" d="M320 380C320 320 320 260 320 210"/>
            </g>
            <g fill="none" stroke="#5B5967" stroke-width="1.6" stroke-linejoin="round" opacity="0.8">
              <path d="M80 78s7-5.4 7-11.2A7 7 0 0 0 73 66.8C73 72.6 80 78 80 78Z"/>
              <path d="M560 68s7-5.4 7-11.2a7 7 0 0 0-14 0c0 5.8 7 11.2 7 11.2Z"/>
              <path d="M60 348s7-5.4 7-11.2a7 7 0 0 0-14 0c0 5.8 7 11.2 7 11.2Z"/>
              <path d="M580 358s7-5.4 7-11.2a7 7 0 0 0-14 0c0 5.8 7 11.2 7 11.2Z"/>
              <path d="M320 388s7-5.4 7-11.2a7 7 0 0 0-14 0c0 5.8 7 11.2 7 11.2Z"/>
            </g>
            <g>
              <path d="M320 232s14-11 14-22.4A14 14 0 0 0 306 209.6c0 11.4 14 22.4 14 22.4Z" fill="#FF6A1A"/>
              <circle cx="320" cy="209" r="5" fill="#fff"/>
            </g>
          </svg>
        </div>
      </div>

      <!-- (b) raio de busca -->
      <div class="diff diff--flip reveal">
        <div class="diff__text">
          <p class="eyebrow">02 — Busca por localização real</p>
          <h3 class="display">Você busca por <span class="hl">rua</span>, não por região</h3>
          <p class="lead">O raio de busca acende exatamente o que existe ao redor do ponto escolhido — com contador ao vivo e filtros de valor, quartos e operação.</p>
        </div>
        <div class="scene" aria-hidden="true">
          <svg viewBox="0 0 640 420" role="presentation">
            <g class="radar">
              <circle cx="320" cy="210" r="170" fill="#FF6A1A" fill-opacity="0.08" stroke="#FF6A1A" stroke-opacity="0.4" stroke-width="1.5" stroke-dasharray="6 8"/>
            </g>
            <rect x="180" y="188" width="280" height="48" rx="24" fill="#FFFFFF" stroke="#16151F" stroke-opacity="0.08"/>
            <path d="M208 222s6-4.8 6-9.8a6 6 0 0 0-12 0c0 5 6 9.8 6 9.8Z" fill="#FF6A1A"/>
            <rect x="226" y="205" width="150" height="8" rx="4" fill="#16151F" opacity="0.12"/>
            <text x="392" y="217" font-family="Space Mono, monospace" font-size="12" fill="#5B5967">156</text>
            <g class="fade-pin" style="transition-delay:.25s" fill="none" stroke="#FF6A1A" stroke-width="1.8"><path d="M240 108s7-5.4 7-11.2a7 7 0 0 0-14 0c0 5.8 7 11.2 7 11.2Z"/></g>
            <g class="fade-pin" style="transition-delay:.45s" fill="none" stroke="#FF6A1A" stroke-width="1.8"><path d="M420 128s7-5.4 7-11.2a7 7 0 0 0-14 0c0 5.8 7 11.2 7 11.2Z"/></g>
            <g class="fade-pin" style="transition-delay:.65s" fill="none" stroke="#FF6A1A" stroke-width="1.8"><path d="M250 320s7-5.4 7-11.2a7 7 0 0 0-14 0c0 5.8 7 11.2 7 11.2Z"/></g>
            <g class="fade-pin" style="transition-delay:.85s" fill="none" stroke="#FF6A1A" stroke-width="1.8"><path d="M430 306s7-5.4 7-11.2a7 7 0 0 0-14 0c0 5.8 7 11.2 7 11.2Z"/></g>
          </svg>
        </div>
      </div>

      <!-- (c) linha do tempo -->
      <div class="diff reveal" id="sceneTimeline">
        <div class="diff__text">
          <p class="eyebrow">03 — Do interesse à negociação</p>
          <h3 class="display">Do primeiro apê à <span class="hl">chave na mão</span></h3>
          <p class="lead">Simule o financiamento na própria ficha, envie seu interesse com a simulação anexada e fale com um corretor que já sabe o que você procura.</p>
        </div>
        <div class="scene" aria-hidden="true">
          <svg viewBox="0 0 640 200" role="presentation">
            <line x1="40" y1="100" x2="600" y2="100" stroke="#5B5967" stroke-opacity="0.2" stroke-width="1.5"/>
            <path id="timelineProgress" d="M40 100H600" stroke="#FF6A1A" stroke-width="2.5" fill="none" stroke-dashoffset="560"/>
            <circle cx="40" cy="100" r="14" fill="none" stroke="#5B5967" stroke-width="1.6"/>
            <g transform="translate(556 68)">
              <rect x="0" y="16" width="48" height="48" rx="6" fill="none" stroke="#5B5967" stroke-width="1.6"/>
              <path d="M24 14s9-6.9 9-14.4A9 9 0 0 0 15-.4C15 7.1 24 14 24 14Z" fill="#FF6A1A"/>
            </g>
            <text x="34" y="140" font-family="Space Mono, monospace" font-size="11" fill="#5B5967">busca</text>
            <text x="520" y="150" font-family="Space Mono, monospace" font-size="11" fill="#5B5967">negociação</text>
          </svg>
        </div>
      </div>

    </div>
  </section>

  <svg class="seam seam--to-inverse" viewBox="0 0 1440 96" preserveAspectRatio="none" aria-hidden="true"><path d="M0 96C360 8 1080 8 1440 96V96H0Z"/></svg>

  <!-- ===================== PROVA SOCIAL ===================== -->
  <section class="section section--inverse" data-counters style="padding-top:40px">
    <div class="map-grid" aria-hidden="true"></div>
    <div class="wrap">
      <div class="stats">
        <div>
          <div class="stat__num" data-count="1284">0</div>
          <div class="stat__label">Imóveis no catálogo</div>
        </div>
        <div>
          <div class="stat__num" data-count="3120" data-suffix="+">0</div>
          <div class="stat__label">Clientes atendidos</div>
        </div>
        <div>
          <div class="stat__num" data-count="418">0</div>
          <div class="stat__label">Negócios fechados</div>
        </div>
      </div>

      <blockquote class="quote">
        <p>Achei o apartamento em três dias buscando pela rua onde eu já queria morar. A simulação foi junto com o contato e o corretor ligou sabendo do meu orçamento.</p>
        <span>Depoimento ilustrativo — substituir por texto real aprovado</span>
      </blockquote>
    </div>
  </section>

  <svg class="seam seam--to-page" viewBox="0 0 1440 96" preserveAspectRatio="none" aria-hidden="true"><path d="M0 0C360 88 1080 88 1440 0V96H0Z"/></svg>

  <!-- ===================== CTA FINAL ===================== -->
  <section class="section cta" id="anunciar" style="padding-top:24px">
    <div class="map-grid" aria-hidden="true"></div>
    <div class="cta__pin" aria-hidden="true">
      <svg width="240" height="240" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-6 8-12.5A8 8 0 1 0 4 9.5C4 16 12 22 12 22Z"/><circle cx="12" cy="9.5" r="3"/></svg>
    </div>
    <div class="wrap">
      <div class="cta__card">
        <p class="eyebrow">Anuncie com a Habittar</p>
        <h2 class="display">Chega de esperar. <span class="hl">Anuncie agora.</span></h2>
        <p class="lead">Grátis, rápido e direto ao ponto: seu imóvel no ar em minutos, visível para quem já está procurando.</p>
        <div class="cta__actions">
          <a class="btn btn--primary btn--interactive" href="${pageContext.request.contextPath}/anunciar">
            <span class="btn__label">
              <span class="btn__pin" aria-hidden="true"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.5"/></svg></span>
              Anunciar imóvel
            </span>
            <span class="btn__reveal" aria-hidden="true">
              Anunciar imóvel
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
            </span>
            <span class="btn__dot" aria-hidden="true"></span>
          </a>
          <a class="btn btn--secondary btn--interactive" href="${pageContext.request.contextPath}/inicio">
            <span class="btn__label">Ver catálogo</span>
            <span class="btn__reveal" aria-hidden="true">
              Ver catálogo
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
            </span>
            <span class="btn__dot" aria-hidden="true"></span>
          </a>
        </div>
        <p class="micro" style="margin-top:24px">Tempo médio de publicação: 6 minutos</p>
      </div>
    </div>
  </section>
</main>

<!-- ===================== FOOTER ===================== -->
<footer class="footer">
  <div class="footer__grid">
    <div>
      <a class="logo" href="#top">
        <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
      </a>
      <p style="font-size:14px;line-height:1.6;margin-top:16px;max-width:26ch">Encontrar onde morar é uma questão de precisão.</p>
    </div>
    <div>
      <h4>Buscar</h4>
      <ul>
        <li><a href="${pageContext.request.contextPath}/inicio?tipo=apartamento&finalidade=venda">Apartamentos à venda</a></li>
        <li><a href="${pageContext.request.contextPath}/inicio?tipo=casa&finalidade=venda">Casas à venda</a></li>
        <li><a href="${pageContext.request.contextPath}/inicio?tipo=apartamento&finalidade=aluguel">Apartamentos para alugar</a></li>
        <li><a href="${pageContext.request.contextPath}/inicio?tipo=terreno">Terrenos</a></li>
        <li><a href="${pageContext.request.contextPath}/inicio?tipo=comercial">Imóveis comerciais</a></li>
      </ul>
    </div>
    <div>
      <h4>Habittar</h4>
      <ul><li><a href="#diferenciais">Como funciona</a></li><li><a href="${pageContext.request.contextPath}/anunciar">Anunciar imóvel</a></li><li><a href="${pageContext.request.contextPath}/login">Área do cliente</a></li><li><a href="${pageContext.request.contextPath}/login">Painel da imobiliária</a></li><li><a href="#top">Trabalhe conosco</a></li></ul>
    </div>
    <div>
      <h4>Institucional</h4>
      <ul><li><a href="${pageContext.request.contextPath}/legal/habittar-psi-privacidade.pdf" target="_blank" rel="noopener">Termos de uso</a></li><li><a href="${pageContext.request.contextPath}/legal/habittar-psi-privacidade.pdf" target="_blank" rel="noopener">Política de privacidade (LGPD)</a></li><li><a href="${pageContext.request.contextPath}/legal/habittar-psi-privacidade.pdf" target="_blank" rel="noopener">Segurança da informação</a></li><li><a href="#top">Contato</a></li><li><a href="#top">CRECI 00000-J</a></li></ul>
    </div>
  </div>
  <div class="footer__base">
    <span>© 2026 Habittar. Todos os direitos reservados.</span>
    <span>-23.5614, -46.6929</span>
  </div>
</footer>

<script src="${pageContext.request.contextPath}/js/habittar.js?v=18"></script>
</body>
</html>
