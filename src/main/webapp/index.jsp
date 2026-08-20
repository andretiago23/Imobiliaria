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
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=34">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=34">
</head>
<body>
<!-- ===================== HEADER ===================== -->
<header class="nav">
  <div class="nav__inner">
    <a class="logo" href="#top" aria-label="Habittar — início">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
    <nav class="nav__links">
      <div class="nav-dropdown">
        <button type="button" class="nav-dropdown__trigger">
          Comprar
          <svg class="nav-dropdown__chevron" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg>
        </button>
        <div class="nav-dropdown__panel">
          <p class="nav-dropdown__titulo">Cidade</p>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda&cidade=S%C3%A3o+Paulo">São Paulo</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda&cidade=Rio+de+Janeiro">Rio de Janeiro</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda&cidade=Belo+Horizonte">Belo Horizonte</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda&cidade=Vit%C3%B3ria">Vitória</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda&cidade=Curitiba">Curitiba</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda&cidade=Porto+Alegre">Porto Alegre</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=venda" class="nav-dropdown__ver-todos">Ver todos os imóveis</a>
        </div>
      </div>
      <div class="nav-dropdown">
        <button type="button" class="nav-dropdown__trigger">
          Alugar
          <svg class="nav-dropdown__chevron" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg>
        </button>
        <div class="nav-dropdown__panel">
          <p class="nav-dropdown__titulo">Cidade</p>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel&cidade=S%C3%A3o+Paulo">São Paulo</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel&cidade=Rio+de+Janeiro">Rio de Janeiro</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel&cidade=Belo+Horizonte">Belo Horizonte</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel&cidade=Vit%C3%B3ria">Vitória</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel&cidade=Curitiba">Curitiba</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel&cidade=Porto+Alegre">Porto Alegre</a>
          <a href="${pageContext.request.contextPath}/inicio?finalidade=aluguel" class="nav-dropdown__ver-todos">Ver todos os imóveis</a>
        </div>
      </div>
      <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
      <a href="${pageContext.request.contextPath}/financiamento">Financiamento</a>
      <% if (session.getAttribute("usuarioLogado") != null) { %>
        <div class="avatar-menu">
          <a class="avatar-com-seta" href="${pageContext.request.contextPath}/perfil" title="Meu perfil" aria-label="Meu perfil">
            <span class="avatar">
              <% if (((model.Usuario) session.getAttribute("usuarioLogado")).getFotoPerfil() != null
                    && !((model.Usuario) session.getAttribute("usuarioLogado")).getFotoPerfil().isBlank()) { %>
                <img src="${pageContext.request.contextPath}${sessionScope.usuarioLogado.fotoPerfil}" alt="">
              <% } else { %>
                ${sessionScope.usuarioLogado.inicial}
              <% } %>
            </span>
            <svg class="nav-dropdown__chevron" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg>
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
      <% } else { %>
        <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/login">Entrar</a>
      <% } %>
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
      <div class="hero__busca">
        <h1 class="display">O imóvel certo está <span class="hl">mais perto</span> do que você imagina.</h1>

        <div class="segment" role="group" aria-label="Tipo de negócio">
          <button type="button" class="is-active" data-value="venda" aria-pressed="true">Comprar</button>
          <button type="button" data-value="aluguel" aria-pressed="false">Alugar</button>
          <button type="button" data-value="vender" aria-pressed="false">Imóvel novo</button>
        </div>
        <form class="search" action="${pageContext.request.contextPath}/inicio" method="get" role="search" id="formBuscaHero" autocomplete="off">
          <input type="hidden" id="finalidade" name="finalidade" value="venda">
          <label class="search__field search__field--tipo">
            <span class="sr-only" hidden>Tipo de imóvel</span>
            <select name="tipo" id="tipoImovel">
              <option value="">Tipo de imóvel</option>
              <option value="apartamento">Apartamento</option>
              <option value="casa">Casa</option>
              <option value="terreno">Terreno</option>
              <option value="comercial">Imóvel comercial</option>
              <option value="rural">Imóvel rural</option>
            </select>
          </label>
          <label class="search__field search__field--local">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#FF6A1A" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.5"/></svg>
            <span class="sr-only" hidden>Localização</span>
            <input type="text" id="campoLocalizacao" name="cidade" placeholder="Busque por rua, bairro ou cidade" autocomplete="off">
            <ul id="sugestoesLocalizacao" class="search__sugestoes" hidden></ul>
          </label>
          <button class="btn btn--primary search__submit" type="submit" aria-label="Buscar imóveis">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.2-3.2"/></svg>
          </button>
        </form>
      </div>

      <div class="scene" aria-hidden="true">
        <img src="${pageContext.request.contextPath}/imagens/familiaHeroSection.jpg" alt="" loading="lazy">
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
        <a class="card card--feature" href="${pageContext.request.contextPath}/imovel?id=1">
          <div class="card__photo tem-foto">
            <img src="https://images.pexels.com/photos/21284473/pexels-photo-21284473.jpeg?auto=compress&cs=tinysrgb&w=800" alt="" loading="lazy">
            <span class="badge">Venda</span><span class="micro">Cód. HB-1042</span>
          </div>
          <div class="card__body">
            <div class="card__price">R$ 845.000</div>
            <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg> Pinheiros, São Paulo — SP</div>
            <div class="card__specs"><span>92 m²</span><span>3 quartos</span><span>2 banh.</span><span>2 vagas</span></div>
          </div>
        </a>
        <a class="card" href="${pageContext.request.contextPath}/imovel?id=2">
          <div class="card__photo tem-foto">
            <img src="https://images.pexels.com/photos/19239905/pexels-photo-19239905.jpeg?auto=compress&cs=tinysrgb&w=800" alt="" loading="lazy">
            <span class="badge">Aluguel</span>
          </div>
          <div class="card__body">
            <div class="card__price">R$ 3.200<span class="micro">/mês</span></div>
            <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg> Vila Madalena — SP</div>
            <div class="card__specs"><span>58 m²</span><span>2 qtos</span><span>1 vaga</span></div>
          </div>
        </a>
        <a class="card" href="${pageContext.request.contextPath}/imovel?id=3">
          <div class="card__photo tem-foto">
            <img src="https://images.pexels.com/photos/2128329/pexels-photo-2128329.jpeg?auto=compress&cs=tinysrgb&w=800" alt="" loading="lazy">
            <span class="badge">Reservado</span>
          </div>
          <div class="card__body">
            <div class="card__price">R$ 620.000</div>
            <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg> Perdizes — SP</div>
            <div class="card__specs"><span>74 m²</span><span>2 qtos</span><span>1 vaga</span></div>
          </div>
        </a>
        <a class="card" href="${pageContext.request.contextPath}/imovel?id=4">
          <div class="card__photo tem-foto">
            <img src="https://images.pexels.com/photos/18078684/pexels-photo-18078684.jpeg?auto=compress&cs=tinysrgb&w=800" alt="" loading="lazy">
            <span class="badge">Venda</span>
          </div>
          <div class="card__body">
            <div class="card__price">R$ 1.190.000</div>
            <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg> Alto de Pinheiros — SP</div>
            <div class="card__specs"><span>148 m²</span><span>4 qtos</span><span>3 vagas</span></div>
          </div>
        </a>
        <a class="card" href="${pageContext.request.contextPath}/imovel?id=5">
          <div class="card__photo tem-foto">
            <img src="https://images.pexels.com/photos/2030037/pexels-photo-2030037.jpeg?auto=compress&cs=tinysrgb&w=800" alt="" loading="lazy">
            <span class="badge">Aluguel</span>
          </div>
          <div class="card__body">
            <div class="card__price">R$ 2.150<span class="micro">/mês</span></div>
            <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg> Butantã — SP</div>
            <div class="card__specs"><span>44 m²</span><span>1 qto</span><span>1 vaga</span></div>
          </div>
        </a>
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
          <img src="${pageContext.request.contextPath}/imagens/imagem-01.jpg" alt="" loading="lazy">
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
          <img src="${pageContext.request.contextPath}/imagens/imagem-02.png" alt="" loading="lazy" style="object-fit:contain;background:var(--surface-page);">
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
          <img src="${pageContext.request.contextPath}/imagens/imagem-03.jpg" alt="" loading="lazy">
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
      <p class="micro" style="text-align:center;opacity:.6;margin-top:8px;">Números ilustrativos</p>

      <blockquote class="quote">
        <p>Achei o apartamento em três dias buscando pela rua onde eu já queria morar. A simulação foi junto com o contato e o corretor ligou sabendo do meu orçamento.</p>
        <span>Depoimento ilustrativo</span>
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
      <h4>Habittar</h4>
      <ul><li><a href="${pageContext.request.contextPath}/anunciar">Anunciar imóvel</a></li><li><a href="${pageContext.request.contextPath}/inicio">Catálogo</a></li></ul>
    </div>
    <div>
      <h4>Institucional</h4>
      <ul><li><a href="${pageContext.request.contextPath}/legal/habittar-psi-privacidade.pdf" target="_blank" rel="noopener">Termos de uso</a></li><li><a href="${pageContext.request.contextPath}/legal/habittar-psi-privacidade.pdf" target="_blank" rel="noopener">Política de privacidade</a></li><li><a href="https://wa.me/5569992450697" target="_blank" rel="noopener">Contato</a></li></ul>
    </div>
  </div>
</footer>

<script src="${pageContext.request.contextPath}/js/habittar.js?v=34"></script>
</body>
</html>
