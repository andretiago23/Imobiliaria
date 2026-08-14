<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
	Porta de entrada publica da aplicacao.

	Quem ja esta autenticado vai direto para a area logada; o restante
	ve a landing page da Habittar, sem precisar de login.
--%>
<%
  if (util.SessaoUsuario.estaAutenticado(request)) {
    response.sendRedirect(request.getContextPath() + "/inicio");
    return;
  }
  // Base de contexto para funcionar em qualquer deploy do MVC Java
  String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Habittar — Encontre onde morar com precisão</title>
  <meta name="description" content="Busque imóveis para alugar, comprar ou vender por localização real. Todos os anunciantes em um só lugar, sem burocracia.">
  <meta property="og:title" content="Habittar — Encontre onde morar com precisão">
  <meta property="og:description" content="Busque imóveis por localização real. Todos os anunciantes em um só lugar.">
  <meta property="og:type" content="website">
  <meta name="twitter:card" content="summary_large_image">

  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Space+Mono:wght@400&display=swap" rel="stylesheet">
  <link href="https://api.fontshare.com/v2/css?f[]=clash-display@600,700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="<%= ctx %>/css/habittar.css">
</head>
<body>

<!-- ============ HEADER ============ -->
<header class="header" id="header">
  <div class="wrap">
    <a class="logo" href="<%= ctx %>/">
      <svg viewBox="0 0 24 24" fill="none" aria-hidden="true">
        <path d="M12 22s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" stroke="#FF6A1A" stroke-width="1.8"/>
        <path d="M9 12.2 12 9.6l3 2.6V15a.6.6 0 0 1-.6.6H9.6A.6.6 0 0 1 9 15v-2.8Z" stroke="#FF6A1A" stroke-width="1.6" stroke-linejoin="round"/>
      </svg>
      Habittar
    </a>
    <nav class="nav">
      <a href="#destaques">Comprar</a>
      <a href="#categorias">Alugar</a>
      <a href="#diferenciais">Como funciona</a>
      <a class="btn btn--primary btn--sm" href="#anunciar">
        <svg class="pin" width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
          <path d="M12 22s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" stroke="currentColor" stroke-width="2"/>
          <circle cx="12" cy="11" r="2.4" stroke="currentColor" stroke-width="2"/>
        </svg>
        Anunciar imóvel
      </a>
    </nav>
  </div>
</header>

<main>

<!-- ============ HERO ============ -->
<section class="section section--page hero">
  <div class="cartogrid" data-parallax></div>
  <div class="wrap">
    <div class="hero__grid">
      <div>
        <span class="mono">-23.5613, -46.6820 &middot; São Paulo</span>
        <h1>Seu bairro, seu tipo de negócio, <span class="accent-word">um clique</span>.</h1>
        <p class="lead">Buscar onde morar é uma questão de precisão, não de sorte. A Habittar reúne os anunciantes da cidade em um único mapa.</p>

        <form class="search-form" action="<%= ctx %>/buscar" method="get" autocomplete="off">

          <div class="segment" role="tablist" data-segment>
            <button type="button" class="is-active" data-value="alugar">Alugar</button>
            <button type="button" data-value="comprar">Comprar</button>
            <button type="button" data-value="vender">Vender</button>
          </div>
          <input type="hidden" name="negocio" id="negocio" value="alugar">

          <div class="search">
            <span class="search__icon" aria-hidden="true">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M12 22s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" stroke="currentColor" stroke-width="2"/>
                <circle cx="12" cy="11" r="2.4" stroke="currentColor" stroke-width="2"/>
              </svg>
            </span>
            <label class="sr-only" for="q" hidden="hidden">Bairro, cidade ou região</label>
            <input id="q" name="q" type="text" placeholder="Bairro, cidade ou região — ex.: Pinheiros">

            <div class="combo" data-combobox>
              <button type="button" class="combo__field" data-combobox-toggle aria-haspopup="listbox" aria-expanded="false">
                <span class="combo__value" data-combobox-label>Tipo de imóvel</span>
                <svg class="combo__chevron" width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true">
                  <path d="M14.0607 5.49999L13.5303 6.03032L8.7071 10.8535C8.31658 11.2441 7.68341 11.2441 7.29289 10.8535L2.46966 6.03032L1.93933 5.49999L2.99999 4.43933L3.53032 4.96966L7.99999 9.43933L12.4697 4.96966L13 4.43933L14.0607 5.49999Z" fill="currentColor"/>
                </svg>
              </button>
              <input type="hidden" name="tipo" id="tipo" value="">
              <ul class="combo__list" role="listbox" data-combobox-list hidden="hidden">
                <%
                  String[][] tiposImovel = {
                    {"apartamento", "Apartamento"},
                    {"casa", "Casa"},
                    {"kitnet", "Kitnet / Studio"},
                    {"cobertura", "Cobertura"},
                    {"comercial", "Comercial"},
                    {"terreno", "Terreno"}
                  };
                  for (String[] tipo : tiposImovel) {
                %>
                <li role="option" data-value="<%= tipo[0] %>">
                  <span><%= tipo[1] %></span>
                  <svg class="combo__check" width="14" height="14" viewBox="0 0 24 24" fill="none" aria-hidden="true">
                    <path d="M4 12.6111L8.92308 17.5L20 6.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </li>
                <% } %>
              </ul>
            </div>

            <button class="btn btn--primary" type="submit">Buscar</button>
          </div>
        </form>


        <div class="hero__meta">
          <span class="mono">156 imóveis em Pinheiros</span>
          <span class="mono">42 anunciantes ativos</span>
        </div>
      </div>

      <!-- Composição: mapa estilizado com pins -->
      <div class="scene" aria-hidden="true">
        <svg viewBox="0 0 560 420">
          <defs>
            <pattern id="mapGrid" width="56" height="56" patternUnits="userSpaceOnUse">
              <path d="M56 0H0V56" fill="none" stroke="rgba(22,21,31,.10)" stroke-width="1"/>
            </pattern>
            <g id="pinOutline">
              <path d="M8 21s7-6.3 7-11.4A7 7 0 1 0 1 9.6C1 14.7 8 21 8 21Z" fill="none" stroke="currentColor" stroke-width="1.5"/>
              <circle cx="8" cy="9.4" r="2.3" fill="none" stroke="currentColor" stroke-width="1.5"/>
            </g>
          </defs>
          <rect x="0" y="0" width="560" height="420" rx="16" fill="url(#mapGrid)"/>
          <!-- quarteirões -->
          <g fill="none" stroke="rgba(91,89,103,.20)" stroke-width="1.5">
            <rect x="40" y="46" width="150" height="96" rx="10"/>
            <rect x="212" y="46" width="112" height="140" rx="10"/>
            <rect x="348" y="76" width="168" height="88" rx="10"/>
            <rect x="40" y="168" width="150" height="120" rx="10"/>
            <rect x="212" y="212" width="112" height="76" rx="10"/>
            <rect x="348" y="192" width="168" height="120" rx="10"/>
            <rect x="128" y="316" width="260" height="66" rx="10"/>
          </g>
          <!-- pins secundários (ocultos no mobile) -->
          <g color="#5B5967" opacity=".55" class="hide-mobile">
            <use href="#pinOutline" x="72" y="70"/>
            <use href="#pinOutline" x="248" y="120"/>
            <use href="#pinOutline" x="464" y="96"/>
            <use href="#pinOutline" x="96" y="230"/>
            <use href="#pinOutline" x="262" y="240"/>
            <use href="#pinOutline" x="196" y="336"/>
          </g>
          <!-- raio de busca -->
          <circle class="radar" cx="392" cy="240" r="60" fill="rgba(255,106,26,.08)" stroke="#FF6A1A" stroke-width="1.5" stroke-dasharray="6 8"/>
          <!-- pin principal -->
          <g class="pin-drop" transform="translate(376,206)">
            <path d="M16 44s16-14.4 16-26A16 16 0 1 0 0 18c0 11.6 16 26 16 26Z" fill="#FF6A1A"/>
            <circle cx="16" cy="18" r="5.6" fill="#FDF9F5"/>
          </g>
        </svg>
      </div>
    </div>
  </div>
</section>

<!-- ============ CATEGORIAS (respiro) ============ -->
<section class="section section--page" id="categorias" style="padding-top:0">
  <div class="cartogrid" data-parallax></div>
  <div class="wrap">
    <div class="cats">
      <a class="cat reveal" href="<%= ctx %>/buscar?negocio=alugar">
        <span class="cat__icon">
          <svg width="34" height="34" viewBox="0 0 24 24" fill="none"><path d="M4 10.5 12 4l8 6.5V20a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1v-9.5Z" stroke="currentColor" stroke-width="1.5"/><path d="M9.5 21v-5h5v5" stroke="currentColor" stroke-width="1.5"/></svg>
        </span>
        <div>
          <h3>Alugar</h3>
          <p>Filtre por bairro, faixa de preço e disponibilidade real — sem anúncio fantasma.</p>
        </div>
        <span class="cat__go mono">3.482 opções &rarr;</span>
      </a>
      <a class="cat reveal" href="<%= ctx %>/buscar?negocio=comprar">
        <span class="cat__icon">
          <svg width="34" height="34" viewBox="0 0 24 24" fill="none"><rect x="4" y="7" width="16" height="14" rx="1.5" stroke="currentColor" stroke-width="1.5"/><path d="M4 7 12 3l8 4M9 21v-5h6v5" stroke="currentColor" stroke-width="1.5"/></svg>
        </span>
        <div>
          <h3>Comprar</h3>
          <p>Do primeiro apê ao imóvel de investimento, com todos os anunciantes no mesmo mapa.</p>
        </div>
        <span class="cat__go mono">2.117 opções &rarr;</span>
      </a>
      <a class="cat reveal" href="<%= ctx %>/anunciar">
        <span class="cat__icon">
          <svg width="34" height="34" viewBox="0 0 24 24" fill="none"><path d="M12 21s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" stroke="currentColor" stroke-width="1.5"/><path d="M12 7.4v5.2M9.4 10h5.2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        </span>
        <div>
          <h3>Vender</h3>
          <p>Coloque seu imóvel no mapa e fale direto com quem está buscando na sua região.</p>
        </div>
        <span class="cat__go mono">anunciar &rarr;</span>
      </a>
    </div>
  </div>
</section>

<!-- costura: page -> raised -->
<div class="seam" aria-hidden="true">
  <svg viewBox="0 0 1440 96" preserveAspectRatio="none"><path d="M0,96 C420,0 1020,0 1440,96 L1440,96 L0,96 Z" fill="#FFFFFF"/></svg>
</div>

<!-- ============ IMÓVEIS EM DESTAQUE (densa) ============ -->
<section class="section section--raised" id="destaques">
  <div class="wrap">
    <div class="section-head">
      <div>
        <span class="mono">Atualizado hoje</span>
        <h2>Imóveis em destaque</h2>
      </div>
      <span class="mono">5.599 imóveis disponíveis agora</span>
    </div>

    <div class="props">
      <!-- SUBSTITUIR: foto real de imóvel (4:3 / 16:11) -->
      <article class="card card--big">
        <div class="card__media">
          <img src="<%= ctx %>/img/imovel-01.jpg" alt="Apartamento em Pinheiros, São Paulo" loading="lazy">
          <span class="badge">Aluguel</span>
        </div>
        <div class="card__body">
          <div class="card__price">R$ 4.200 <span class="mono">/mês</span></div>
          <div class="card__loc">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" stroke="currentColor" stroke-width="2"/></svg>
            Rua dos Pinheiros — Pinheiros, São Paulo
          </div>
          <div class="card__specs"><span>78 m²</span><span>2 quartos</span><span>1 vaga</span></div>
        </div>
      </article>

      <article class="card" style="grid-area:s1">
        <div class="card__media"><img src="<%= ctx %>/img/imovel-02.jpg" alt="Studio na Vila Madalena" loading="lazy"><span class="badge">Venda</span></div>
        <div class="card__body">
          <div class="card__price">R$ 615 mil</div>
          <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" stroke="currentColor" stroke-width="2"/></svg> Vila Madalena</div>
          <div class="card__specs"><span>41 m²</span><span>1 quarto</span></div>
        </div>
      </article>

      <article class="card" style="grid-area:s2">
        <div class="card__media"><img src="<%= ctx %>/img/imovel-03.jpg" alt="Apartamento na Consolação" loading="lazy"><span class="badge">Aluguel</span></div>
        <div class="card__body">
          <div class="card__price">R$ 2.850 <span class="mono">/mês</span></div>
          <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" stroke="currentColor" stroke-width="2"/></svg> Consolação</div>
          <div class="card__specs"><span>55 m²</span><span>2 quartos</span></div>
        </div>
      </article>

      <article class="card" style="grid-area:s3">
        <div class="card__media"><img src="<%= ctx %>/img/imovel-04.jpg" alt="Casa no Butantã" loading="lazy"><span class="badge">Venda</span></div>
        <div class="card__body">
          <div class="card__price">R$ 890 mil</div>
          <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" stroke="currentColor" stroke-width="2"/></svg> Butantã</div>
          <div class="card__specs"><span>132 m²</span><span>3 quartos</span><span>2 vagas</span></div>
        </div>
      </article>

      <article class="card" style="grid-area:s4">
        <div class="card__media"><img src="<%= ctx %>/img/imovel-05.jpg" alt="Apartamento no Ipiranga" loading="lazy"><span class="badge">Aluguel</span></div>
        <div class="card__body">
          <div class="card__price">R$ 1.980 <span class="mono">/mês</span></div>
          <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" stroke="currentColor" stroke-width="2"/></svg> Ipiranga</div>
          <div class="card__specs"><span>63 m²</span><span>2 quartos</span></div>
        </div>
      </article>
    </div>
  </div>
</section>

<!-- costura: raised -> page -->
<div class="seam" aria-hidden="true">
  <svg viewBox="0 0 1440 96" preserveAspectRatio="none"><path d="M0,96 C420,0 1020,0 1440,96 L1440,96 L0,96 Z" fill="#FDF9F5"/></svg>
</div>

<!-- ============ DIFERENCIAIS ============ -->
<section class="section section--page" id="diferenciais">
  <div class="cartogrid" data-parallax></div>
  <div class="wrap">

    <!-- (a) convergência de anunciantes -->
    <div class="diff" data-inview>
      <div class="reveal">
        <span class="mono">01 — Agregação</span>
        <h3>Todos os anunciantes num só lugar</h3>
        <p>Imobiliárias, corretores e proprietários convergem para o mesmo mapa. Você compara tudo sem abrir cinco abas.</p>
      </div>
      <div class="diff__scene" aria-hidden="true">
        <svg viewBox="0 0 640 380">
          <g fill="none" stroke="rgba(255,106,26,.3)" stroke-width="1" class="draw" style="--len:520">
            <path d="M70 60 C 220 90, 260 150, 320 190"/>
            <path d="M580 70 C 440 110, 400 150, 320 190"/>
            <path d="M50 300 C 190 280, 250 230, 320 190"/>
            <path d="M600 300 C 470 290, 400 240, 320 190"/>
            <path d="M320 350 C 320 300, 320 240, 320 190"/>
          </g>
          <g color="#5B5967" opacity=".6" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M70 72s9-8 9-14.6A9 9 0 1 0 61 57.4C61 64 70 72 70 72Z"/>
            <path d="M580 82s9-8 9-14.6A9 9 0 1 0 571 67.4C571 74 580 82 580 82Z"/>
            <path d="M50 312s9-8 9-14.6A9 9 0 1 0 41 297.4C41 304 50 312 50 312Z"/>
            <path d="M600 312s9-8 9-14.6A9 9 0 1 0 591 297.4C591 304 600 312 600 312Z" class="hide-mobile"/>
            <path d="M320 362s9-8 9-14.6A9 9 0 1 0 311 347.4C311 354 320 362 320 362Z" class="hide-mobile"/>
          </g>
          <g transform="translate(306,158)">
            <path d="M14 40s14-13 14-23.5A14 14 0 1 0 0 16.5C0 27 14 40 14 40Z" fill="#FF6A1A"/>
            <circle cx="14" cy="16.5" r="5" fill="#FDF9F5"/>
          </g>
        </svg>
      </div>
    </div>

    <!-- (b) busca por localização real -->
    <div class="diff" data-inview>
      <div class="reveal">
        <span class="mono">02 — Precisão</span>
        <h3>Busca por localização real</h3>
        <p>Digite o bairro e veja o raio acender: resultados por onde você quer morar, não por onde o anunciante quer vender.</p>
      </div>
      <div class="diff__scene" aria-hidden="true">
        <svg viewBox="0 0 640 380">
          <circle class="radar-expand" cx="320" cy="200" r="150" fill="rgba(255,106,26,.08)" stroke="#FF6A1A" stroke-width="1.5" stroke-dasharray="6 8"/>
          <g transform="translate(180,72)">
            <rect width="280" height="56" rx="28" fill="#FFFFFF" stroke="rgba(22,21,31,.08)"/>
            <path d="M28 38s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" fill="none" stroke="#FF6A1A" stroke-width="1.6" transform="translate(0,-6)"/>
            <rect x="52" y="24" width="150" height="8" rx="4" fill="rgba(91,89,103,.28)"/>
          </g>
          <g color="#FF6A1A" fill="none" stroke="currentColor" stroke-width="1.6">
            <path class="lit" style="transition-delay:.35s" d="M220 250s9-8 9-14.6A9 9 0 1 0 211 235.4C211 242 220 250 220 250Z"/>
            <path class="lit" style="transition-delay:.6s" d="M400 240s9-8 9-14.6A9 9 0 1 0 391 225.4C391 232 400 240 400 240Z"/>
            <path class="lit" style="transition-delay:.85s" d="M300 320s9-8 9-14.6A9 9 0 1 0 291 305.4C291 312 300 320 300 320Z"/>
            <path class="lit" style="transition-delay:1.1s" d="M440 320s9-8 9-14.6A9 9 0 1 0 431 305.4C431 312 440 320 440 320Z"/>
          </g>
        </svg>
      </div>
    </div>

    <!-- (c) linha do tempo -->
    <div class="diff" data-inview>
      <div class="reveal">
        <span class="mono">03 — Do início ao fim</span>
        <h3>Do primeiro apê à negociação com a imobiliária</h3>
        <p>Você começa buscando e termina conversando com quem decide. Sem burocracia intermediária, sem cadastro em dez portais.</p>
      </div>
      <div class="diff__scene" aria-hidden="true">
        <svg viewBox="0 0 640 200">
          <line x1="40" y1="100" x2="600" y2="100" stroke="rgba(91,89,103,.2)" stroke-width="1"/>
          <line class="draw" style="--len:560" x1="40" y1="100" x2="600" y2="100" stroke="#FF6A1A" stroke-width="2"/>
          <circle cx="40" cy="100" r="14" fill="none" stroke="#5B5967" stroke-width="1.5"/>
          <g transform="translate(556,64)">
            <rect width="48" height="52" rx="6" fill="none" stroke="#5B5967" stroke-width="1.5"/>
            <path d="M24 -4s8-7 8-13a8 8 0 1 0-16 0c0 6 8 13 8 13Z" fill="#FF6A1A" transform="translate(0,-2)"/>
          </g>
        </svg>
      </div>
    </div>

  </div>
</section>

<!-- costura: page -> inverse -->
<div class="seam" aria-hidden="true">
  <svg viewBox="0 0 1440 96" preserveAspectRatio="none"><path d="M0,96 C420,0 1020,0 1440,96 L1440,96 L0,96 Z" fill="#15141C"/></svg>
</div>

<!-- ============ PROVA SOCIAL (único bloco escuro) ============ -->
<section class="section section--inverse" id="prova" data-inview>
  <div class="cartogrid"></div>
  <div class="wrap">
    <div class="stats">
      <div>
        <div class="stat__num" data-count="5599" data-suffix="">0</div>
        <div class="stat__label">Imóveis anunciados</div>
      </div>
      <div>
        <div class="stat__num" data-count="420" data-suffix="+">0</div>
        <div class="stat__label">Anunciantes ativos</div>
      </div>
      <div>
        <div class="stat__num" data-count="1830" data-suffix="">0</div>
        <div class="stat__label">Negócios fechados</div>
      </div>
    </div>
    <blockquote class="quote">
      <p>Achei meu apartamento no bairro que eu queria em dois dias. Foi a primeira vez que a busca por localização funcionou de verdade.</p>
      <footer>Depoimento — substituir por depoimento real aprovado</footer>
    </blockquote>
  </div>
</section>

<!-- costura: inverse -> page -->
<div class="seam" aria-hidden="true">
  <svg viewBox="0 0 1440 96" preserveAspectRatio="none"><path d="M0,96 C420,0 1020,0 1440,96 L1440,96 L0,96 Z" fill="#FDF9F5"/></svg>
</div>

<!-- ============ CTA FINAL ============ -->
<section class="section section--page cta" id="anunciar" data-inview>
  <div class="cartogrid" data-parallax></div>
  <svg class="cta__pin" viewBox="0 0 120 160" fill="none" aria-hidden="true">
    <path d="M60 154s52-46.5 52-84A52 52 0 1 0 8 70c0 37.5 52 84 52 84Z" stroke="currentColor" stroke-width="4"/>
    <circle cx="60" cy="68" r="18" stroke="currentColor" stroke-width="4"/>
  </svg>
  <div class="wrap">
    <div class="cta__panel">
      <span class="mono">Anuncie grátis</span>
      <h2>Seu imóvel pode estar <span class="accent-word">no mapa</span> agora.</h2>
      <p>Publique em minutos e apareça para quem está buscando exatamente no seu bairro.</p>
      <a class="btn btn--primary" href="<%= ctx %>/anunciar">
        <svg class="pin" width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M12 22s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" stroke="currentColor" stroke-width="2"/><circle cx="12" cy="11" r="2.4" stroke="currentColor" stroke-width="2"/></svg>
        Anunciar meu imóvel
      </a>
    </div>
  </div>
</section>

</main>

<!-- ============ FOOTER ============ -->
<footer class="footer">
  <div class="wrap">
    <div class="footer__grid">
      <div>
        <a class="logo" href="<%= ctx %>/" style="color:#fff">
          <svg viewBox="0 0 24 24" fill="none" aria-hidden="true">
            <path d="M12 22s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" stroke="#FF6A1A" stroke-width="1.8"/>
            <path d="M9 12.2 12 9.6l3 2.6V15a.6.6 0 0 1-.6.6H9.6A.6.6 0 0 1 9 15v-2.8Z" stroke="#FF6A1A" stroke-width="1.6" stroke-linejoin="round"/>
          </svg>
          Habittar
        </a>
        <p style="color:#A6A3B0;max-width:34ch;margin-top:16px">Encontrar onde morar é uma questão de precisão, mostrada em tempo real.</p>
      </div>
      <div>
        <h4>Buscar</h4>
        <ul>
          <li><a href="<%= ctx %>/buscar?negocio=alugar">Apartamentos para alugar</a></li>
          <li><a href="<%= ctx %>/buscar?negocio=comprar">Imóveis à venda</a></li>
          <li><a href="<%= ctx %>/buscar?tipo=casa">Casas</a></li>
          <li><a href="<%= ctx %>/buscar?tipo=comercial">Comercial</a></li>
        </ul>
      </div>
      <div>
        <h4>Anunciantes</h4>
        <ul>
          <li><a href="<%= ctx %>/anunciar">Anunciar imóvel</a></li>
          <li><a href="<%= ctx %>/imobiliarias">Para imobiliárias</a></li>
          <li><a href="<%= ctx %>/planos">Planos</a></li>
          <li><a href="<%= ctx %>/login">Área do anunciante</a></li>
        </ul>
      </div>
      <div>
        <h4>Institucional</h4>
        <ul>
          <li><a href="<%= ctx %>/sobre">Sobre a Habittar</a></li>
          <li><a href="<%= ctx %>/ajuda">Central de ajuda</a></li>
          <li><a href="<%= ctx %>/termos">Termos de uso</a></li>
          <li><a href="<%= ctx %>/privacidade">Privacidade</a></li>
        </ul>
      </div>
    </div>
    <div class="footer__bottom">
      <span>&copy; <%= java.time.Year.now().getValue() %> Habittar. Todos os direitos reservados.</span>
      <span>CNPJ 00.000.000/0001-00</span>
    </div>
  </div>
</footer>

<script src="<%= ctx %>/js/habittar.js" defer></script>
</body>
</html>