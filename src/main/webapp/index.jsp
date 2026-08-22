<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Locale, java.text.NumberFormat, model.Imovel, model.Finalidade, model.StatusImovel" %>
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
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=52">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=64">
</head>
<body>
<!-- ===================== HEADER ===================== -->
<% request.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

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
    <div class="hero__foto">
      <img src="${pageContext.request.contextPath}/imagens/familiaHeroSection.jpg?v=2" alt="" loading="lazy">
    </div>

    <div class="hero__wrap">
      <div class="hero__filtro-card">
        <div class="hero__cartao-pill" role="group" aria-label="O que você quer fazer">
          <span class="is-active">Buscar imóveis</span>
          <a href="${pageContext.request.contextPath}/anunciar">Anunciar imóvel</a>
        </div>

        <h1 class="display hero__cartao-titulo">Encontre um lugar para chamar de seu</h1>

        <div class="segment hero__filtro-tabs" role="group" aria-label="Tipo de negócio">
          <button type="button" class="is-active" data-value="aluguel" aria-pressed="true">Alugar</button>
          <button type="button" data-value="venda" aria-pressed="false">Comprar</button>
        </div>

        <form class="hero__filtro-form" action="${pageContext.request.contextPath}/inicio" method="get" role="search" id="formBuscaHero" autocomplete="off">
          <input type="hidden" id="finalidade" name="finalidade" value="aluguel">

          <label class="hero__filtro-campo" for="campoLocalizacao">
            Cidade
            <span class="hero__filtro-input-wrap search__field--local">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#FF6A1A" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.5"/></svg>
              <input type="text" id="campoLocalizacao" name="cidade" placeholder="Busque por cidade" autocomplete="off">
              <ul id="sugestoesLocalizacao" class="search__sugestoes" hidden></ul>
            </span>
          </label>

          <label class="hero__filtro-campo" for="campoBairro">
            Bairro
            <span class="hero__filtro-input-wrap search__field--local">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#FF6A1A" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 10.5 12 4l9 6.5V20a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/><path d="M9.5 21v-6h5v6"/></svg>
              <input type="text" id="campoBairro" name="bairro" placeholder="Busque por bairro" autocomplete="off">
              <ul id="sugestoesBairro" class="search__sugestoes" hidden></ul>
            </span>
          </label>

          <div class="hero__filtro-linha">
            <label class="hero__filtro-campo" for="precoMaximoHeroExibido">
              Valor total até
              <span class="hero__filtro-preco">
                <span class="hero__filtro-preco__prefixo">R$</span>
                <input type="text" inputmode="numeric" id="precoMaximoHeroExibido" placeholder="0" autocomplete="off">
                <input type="hidden" name="precoMaximo" id="precoMaximoHero">
              </span>
            </label>
            <label class="hero__filtro-campo" for="quartosMinimoHero">
              Quartos
              <select name="quartosMinimo" id="quartosMinimoHero" class="hero__filtro-select">
                <option value="">Nº de quartos</option>
                <option value="1">1+</option>
                <option value="2">2+</option>
                <option value="3">3+</option>
                <option value="4">4+</option>
              </select>
            </label>
          </div>

          <button class="btn btn--primary hero__filtro-submit" type="submit">Buscar imóveis</button>
        </form>
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
  <%
    // Só entram aqui os imóveis realmente "Disponível" (status ATIVO) —
    // um reservado, vendido ou alugado não deveria aparecer como destaque
    // da página inicial. listarAtivos traz ativo+reservado juntos (mesma
    // consulta usada no catálogo /inicio), então filtramos aqui.
    List<Imovel> imoveisDestaque = List.of();
    try {
        List<Imovel> candidatos = new dao.ImovelDAO().listarAtivos(20);
        java.util.List<Imovel> disponiveis = new java.util.ArrayList<>();
        for (Imovel candidato : candidatos) {
            if (candidato.getStatus() == StatusImovel.ATIVO) {
                disponiveis.add(candidato);
            }
            if (disponiveis.size() == 5) break;
        }
        new dao.FotoImovelDAO().carregarFotos(disponiveis);
        imoveisDestaque = disponiveis;
    } catch (dao.DAOException e) {
        getServletContext().log("Falha ao carregar imóveis em destaque na página inicial.", e);
    }
    NumberFormat moedaDestaque = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
  %>
  <% if (!imoveisDestaque.isEmpty()) { %>
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
        <% for (int i = 0; i < imoveisDestaque.size(); i++) {
          Imovel imovelDestaque = imoveisDestaque.get(i);
          boolean aluguelDestaque = imovelDestaque.getFinalidade() == Finalidade.ALUGUEL;
          model.FotoImovel fotoCapaDestaque = imovelDestaque.getFotoPrincipal();
          String urlFotoDestaque = fotoCapaDestaque != null ? fotoCapaDestaque.getUrlFoto()
              : util.ImagemImovel.urlIlustrativa(imovelDestaque.getTipo(), imovelDestaque.getId());
        %>
        <a class="card <%= i == 0 ? "card--feature" : "" %>" href="${pageContext.request.contextPath}/imovel?id=<%= imovelDestaque.getId() %>">
          <div class="card__photo tem-foto">
            <img src="<%= urlFotoDestaque %>" alt="Foto de <%= util.Html.escapar(imovelDestaque.getTitulo()) %>" loading="lazy">
            <span class="badge"><%= aluguelDestaque ? "Aluguel" : "Venda" %></span>
          </div>
          <div class="card__body">
            <div class="card__price">
              <%= moedaDestaque.format(imovelDestaque.getPreco()) %>
              <% if (aluguelDestaque) { %><span class="micro">/mês</span><% } %>
            </div>
            <div class="card__loc"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg> <%= util.Html.escapar(imovelDestaque.getEnderecoCompleto()) %></div>
            <div class="card__specs"><span><%= imovelDestaque.getAreaM2() %> m²</span><span><%= imovelDestaque.getQuartos() %> quartos</span><span><%= imovelDestaque.getBanheiros() %> banh.</span><span><%= imovelDestaque.getVagasGaragem() %> vagas</span></div>
          </div>
        </a>
        <% } %>
      </div>
    </div>
  </section>
  <% } %>

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
      <ul><li><a href="${pageContext.request.contextPath}/legal/habittar-termos-de-uso.pdf" target="_blank" rel="noopener">Termos de uso</a></li><li><a href="${pageContext.request.contextPath}/legal/habittar-psi-privacidade.pdf" target="_blank" rel="noopener">Política de privacidade</a></li><li><a href="https://wa.me/5569992450697" target="_blank" rel="noopener">Contato</a></li></ul>
    </div>
  </div>
</footer>

<script src="${pageContext.request.contextPath}/js/cidades-ro.js?v=62"></script>
<script src="${pageContext.request.contextPath}/js/habittar.js?v=62"></script>
</body>
</html>
