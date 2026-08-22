<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Locale, java.text.NumberFormat, model.Imovel, model.Finalidade" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Catálogo | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=60">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=64">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=60">
</head>
<body>

<!-- ===================== HEADER ===================== -->
<% request.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="app-main app-main--catalogo">
  <div class="map-grid" aria-hidden="true"></div>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <form method="get" action="${pageContext.request.contextPath}/inicio" class="catalogo-layout" id="formFiltros">

    <!-- ===================== SIDEBAR DE FILTROS (local único, item 2.3) ===================== -->
    <aside class="filtro-sidebar">

      <div class="filtro-tabs" role="group" aria-label="Tipo de negócio">
        <input type="radio" name="finalidade" value="" id="fTodos" class="filtro-tab-input" ${empty filtro.finalidade ? 'checked' : ''}>
        <label for="fTodos" class="filtro-tab">Todos</label>
        <input type="radio" name="finalidade" value="venda" id="fVenda" class="filtro-tab-input" ${filtro.finalidade == 'VENDA' ? 'checked' : ''}>
        <label for="fVenda" class="filtro-tab">Comprar</label>
        <input type="radio" name="finalidade" value="aluguel" id="fAluguel" class="filtro-tab-input" ${filtro.finalidade == 'ALUGUEL' ? 'checked' : ''}>
        <label for="fAluguel" class="filtro-tab">Alugar</label>
      </div>

      <div class="filtro-secao">
        <h3>Tipo de imóvel</h3>
        <label class="filtro-checkbox">
          <input type="radio" name="tipo" value="" ${empty filtro.tipo ? 'checked' : ''}>
          <span class="filtro-checkbox__caixa"></span> Todos os tipos
        </label>
        <label class="filtro-checkbox">
          <input type="radio" name="tipo" value="apartamento" ${filtro.tipo == 'APARTAMENTO' ? 'checked' : ''}>
          <span class="filtro-checkbox__caixa"></span> Apartamento
        </label>
        <label class="filtro-checkbox">
          <input type="radio" name="tipo" value="casa" ${filtro.tipo == 'CASA' ? 'checked' : ''}>
          <span class="filtro-checkbox__caixa"></span> Casa
        </label>
        <label class="filtro-checkbox">
          <input type="radio" name="tipo" value="terreno" ${filtro.tipo == 'TERRENO' ? 'checked' : ''}>
          <span class="filtro-checkbox__caixa"></span> Terreno
        </label>
        <label class="filtro-checkbox">
          <input type="radio" name="tipo" value="comercial" ${filtro.tipo == 'COMERCIAL' ? 'checked' : ''}>
          <span class="filtro-checkbox__caixa"></span> Imóvel comercial
        </label>
        <label class="filtro-checkbox">
          <input type="radio" name="tipo" value="rural" ${filtro.tipo == 'RURAL' ? 'checked' : ''}>
          <span class="filtro-checkbox__caixa"></span> Imóvel rural
        </label>
      </div>

      <div class="filtro-secao">
        <h3>Quartos</h3>
        <div class="pills">
          <input type="radio" name="quartosMinimo" value="" id="q0" class="pill-input" ${empty filtro.quartosMinimo ? 'checked' : ''}>
          <label for="q0" class="pill">Todos</label>
          <input type="radio" name="quartosMinimo" value="1" id="q1" class="pill-input" ${filtro.quartosMinimo == 1 ? 'checked' : ''}>
          <label for="q1" class="pill">1+</label>
          <input type="radio" name="quartosMinimo" value="2" id="q2" class="pill-input" ${filtro.quartosMinimo == 2 ? 'checked' : ''}>
          <label for="q2" class="pill">2+</label>
          <input type="radio" name="quartosMinimo" value="3" id="q3" class="pill-input" ${filtro.quartosMinimo == 3 ? 'checked' : ''}>
          <label for="q3" class="pill">3+</label>
          <input type="radio" name="quartosMinimo" value="4" id="q4" class="pill-input" ${filtro.quartosMinimo == 4 ? 'checked' : ''}>
          <label for="q4" class="pill">4+</label>
        </div>
      </div>

      <div class="filtro-secao">
        <h3>Banheiros</h3>
        <div class="pills">
          <input type="radio" name="banheirosMinimo" value="" id="b0" class="pill-input" ${empty filtro.banheirosMinimo ? 'checked' : ''}>
          <label for="b0" class="pill">Todos</label>
          <input type="radio" name="banheirosMinimo" value="1" id="b1" class="pill-input" ${filtro.banheirosMinimo == 1 ? 'checked' : ''}>
          <label for="b1" class="pill">1+</label>
          <input type="radio" name="banheirosMinimo" value="2" id="b2" class="pill-input" ${filtro.banheirosMinimo == 2 ? 'checked' : ''}>
          <label for="b2" class="pill">2+</label>
          <input type="radio" name="banheirosMinimo" value="3" id="b3" class="pill-input" ${filtro.banheirosMinimo == 3 ? 'checked' : ''}>
          <label for="b3" class="pill">3+</label>
          <input type="radio" name="banheirosMinimo" value="4" id="b4" class="pill-input" ${filtro.banheirosMinimo == 4 ? 'checked' : ''}>
          <label for="b4" class="pill">4+</label>
        </div>
      </div>

      <!-- Item 2.5: área vira campo numérico com "m²" fixo ao lado -->
      <div class="filtro-secao">
        <h3>Área mínima</h3>
        <div class="filtro-area">
          <input type="text" inputmode="numeric" maxlength="5" id="campoArea" name="areaMinima" placeholder="Qualquer" value="${filtro.areaMinima}">
          <span class="filtro-area__sufixo">m²</span>
        </div>
      </div>

      <!-- Item 2.6: "R$" fixo + formatação de milhar -->
      <div class="filtro-secao">
        <h3>Preço</h3>
        <div class="filtro-preco">
          <label>
            <span class="micro">Mínimo</span>
            <span class="filtro-preco__campo">
              <span class="filtro-preco__prefixo">R$</span>
              <input type="text" inputmode="numeric" class="campo-preco" id="precoMinimoExibido" placeholder="0" autocomplete="off">
              <input type="hidden" name="precoMinimo" id="precoMinimo" value="${filtro.precoMinimo}">
            </span>
          </label>
          <label>
            <span class="micro">Máximo</span>
            <span class="filtro-preco__campo">
              <span class="filtro-preco__prefixo">R$</span>
              <input type="text" inputmode="numeric" class="campo-preco" id="precoMaximoExibido" placeholder="0" autocomplete="off">
              <input type="hidden" name="precoMaximo" id="precoMaximo" value="${filtro.precoMaximo}">
            </span>
          </label>
        </div>
      </div>

      <div class="filtro-sidebar__acoes">
        <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/inicio">Limpar</a>
        <button class="btn btn--primary btn--sm" type="submit">Buscar imóveis</button>
      </div>
    </aside>

    <!-- ===================== CONTEÚDO ===================== -->
    <div class="catalogo-main">

      <%
        List<Imovel> imoveis = (List<Imovel>) request.getAttribute("imoveis");
        if (imoveis == null) {
          imoveis = java.util.Collections.emptyList();
        }
        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        NumberFormat area = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        area.setMaximumFractionDigits(1);
      %>

      <!-- Localização em destaque, fora da sidebar, em cima da grade —
           mesmo autocomplete de cidade da hero (js/cidades-ro.js), restrito
           aos municípios de Rondônia. -->
      <div class="catalogo-busca">
        <div class="catalogo-busca__campo">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.5"/></svg>
          <input type="text" id="campoLocalizacaoCatalogo" name="cidade" placeholder="Cidade — ex.: Porto Velho" value="${filtro.cidade}" autocomplete="off">
          <ul id="sugestoesLocalizacaoCatalogo" class="search__sugestoes" hidden></ul>
        </div>
        <button class="btn btn--primary catalogo-busca__botao" type="submit" aria-label="Buscar">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.2-3.2"/></svg>
        </button>
      </div>
      <p class="catalogo-busca__contagem"><%= imoveis.size() %> encontrado(s)</p>

      <!-- Comparador de imóveis: modo de seleção nos cards + modal de
           comparação, tudo client-side (ver js/catalogo.js). -->
      <button type="button" id="btnAbrirComparador" class="btn btn--secondary" style="margin-bottom:20px;">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 3v18h18"/><path d="M7 15v3"/><path d="M12 10v8"/><path d="M17 6v12"/></svg>
        Comparador de imóveis
      </button>
      <div id="bannerComparador" class="comparador-banner" hidden>
        <p>Selecione até 3 imóveis para serem comparados lado a lado.</p>
        <div class="comparador-banner__acoes">
          <span id="contadorComparador" class="micro">0 de 3 selecionados</span>
          <button type="button" id="btnCancelarComparador" class="btn btn--secondary btn--sm">Cancelar</button>
          <button type="button" id="btnCompararAgora" class="btn btn--primary btn--sm" disabled>Comparar</button>
        </div>
      </div>

      <p class="alerta alerta-erro" role="alert">${erroCatalogo}</p>

      <% if (imoveis.isEmpty()) { %>
        <div class="estado-vazio">
          <p>Nenhum imóvel encontrado com esses filtros.</p>
          <p class="micro">Tente ampliar a faixa de preço ou remover algum filtro.</p>
        </div>
      <% } else {
           java.util.Set<Integer> idsFavoritados = (java.util.Set<Integer>) request.getAttribute("idsFavoritados");
      %>
        <div class="catalogo__grade">
          <% for (Imovel imovel : imoveis) {
            boolean aluguel = imovel.getFinalidade() == Finalidade.ALUGUEL;
            boolean salvo = idsFavoritados != null && idsFavoritados.contains(imovel.getId());
            model.FotoImovel fotoCapa = imovel.getFotoPrincipal();
            String urlFotoCard = fotoCapa != null ? fotoCapa.getUrlFoto()
                : util.ImagemImovel.urlIlustrativa(imovel.getTipo(), imovel.getId());
            String descricaoComparador = imovel.getDescricao() != null && !imovel.getDescricao().isBlank()
                ? imovel.getDescricao() : "Sem descrição cadastrada.";
            String jsonComparador = "{"
                + "\"titulo\":\"" + util.Json.escapar(imovel.getTitulo()) + "\","
                + "\"endereco\":\"" + util.Json.escapar(imovel.getEnderecoCompleto()) + "\","
                + "\"foto\":\"" + util.Json.escapar(urlFotoCard) + "\","
                + "\"precoFmt\":\"" + util.Json.escapar(moeda.format(imovel.getPreco())) + "\","
                + "\"precoValor\":" + imovel.getPreco() + ","
                + "\"quartos\":" + imovel.getQuartos() + ","
                + "\"banheiros\":" + imovel.getBanheiros() + ","
                + "\"areaValor\":" + imovel.getAreaM2() + ","
                + "\"descricao\":\"" + util.Json.escapar(descricaoComparador) + "\""
                + "}";
          %>
          <!-- Item 2.1: card inteiro é clicável (article com data-href + JS),
               o botão "salvar" continua funcionando à parte via stopPropagation.
               Comparador de imóveis: data-imovel-id + o <script> com os dados
               alimentam js/catalogo.js sem precisar de nenhuma chamada extra
               ao servidor. -->
          <article class="card card--clicavel" data-href="${pageContext.request.contextPath}/imovel?id=<%= imovel.getId() %>"
            data-imovel-id="<%= imovel.getId() %>" tabindex="0" role="link">
            <script type="application/json" class="dados-comparador"><%= jsonComparador %></script>
            <div class="card__photo tem-foto">
              <img src="<%= urlFotoCard %>"
                alt="Foto de <%= util.Html.escapar(imovel.getTitulo()) %>" loading="lazy">
              <button type="button" class="card__comparar-btn" data-comparar-btn data-imovel-id="<%= imovel.getId() %>">
                Selecionar imóvel
              </button>
              <form method="post" action="${pageContext.request.contextPath}/favorito" class="card__salvar">
                <input type="hidden" name="csrf" value="${csrf}">
                <input type="hidden" name="idImovel" value="<%= imovel.getId() %>">
                <input type="hidden" name="destino" value="${pageContext.request.contextPath}/inicio">
                <button type="submit" class="botao-salvar <%= salvo ? "botao-salvar--ativo" : "" %>" aria-label="Salvar imóvel">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="<%= salvo ? "currentColor" : "none" %>" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M19 21 12 16l-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>
                </button>
              </form>
            </div>
            <div class="card__body">
              <div class="card__price">
                <%= moeda.format(imovel.getPreco()) %>
                <% if (aluguel) { %><span class="micro">/mês</span><% } %>
              </div>
              <p class="card__endereco"><%= util.Html.escapar(imovel.getEnderecoCompleto()) %></p>
              <div class="card__specs">
                <span title="Área">
                  <svg class="ficha-icone" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M8 3H5a2 2 0 0 0-2 2v3"/><path d="M21 8V5a2 2 0 0 0-2-2h-3"/><path d="M3 16v3a2 2 0 0 0 2 2h3"/><path d="M16 21h3a2 2 0 0 0 2-2v-3"/></svg>
                  <%= area.format(imovel.getAreaM2()) %> m²
                </span>
                <span title="Quartos">
                  <svg class="ficha-icone" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M2 4v16"/><path d="M2 8h18a2 2 0 0 1 2 2v10"/><path d="M2 17h20"/><path d="M6 8v9"/></svg>
                  <%= imovel.getQuartos() %>
                </span>
                <span title="Banheiros">
                  <svg class="ficha-icone" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9 6 7.5 2.5"/><path d="M6 10V6.5A2.5 2.5 0 0 1 8.5 4c1 0 1.8.5 2.2 1.4"/><path d="M3 10h18"/><path d="M5 10v3a7 7 0 0 0 14 0v-3"/><path d="M8 20v1"/><path d="M16 20v1"/></svg>
                  <%= imovel.getBanheiros() %>
                </span>
              </div>
            </div>
          </article>
          <% } %>
        </div>
      <% } %>
    </div>
  </form>

</main>

<!-- Comparador de imóveis: dialog nativo, 2 passos (campos → resultado),
     tudo montado no cliente a partir dos <script type="application/json">
     de cada card — ver js/catalogo.js. -->
<dialog id="modalComparador" class="modal-comparador">
  <div class="modal-comparador__corpo">
    <button type="button" class="modal-comparador__fechar" id="fecharModalComparador" aria-label="Fechar">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
    </button>

    <div id="passoCamposComparador">
      <h2 class="display" style="font-size:20px;">Selecione as informações que serão comparadas</h2>
      <div class="comparador-campos">
        <label class="filtro-checkbox">
          <input type="checkbox" class="campo-comparador" value="preco" checked>
          <span class="filtro-checkbox__caixa"></span> Valor
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" class="campo-comparador" value="quartos" checked>
          <span class="filtro-checkbox__caixa"></span> Quantidade de quartos
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" class="campo-comparador" value="banheiros" checked>
          <span class="filtro-checkbox__caixa"></span> Quantidade de banheiros
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" class="campo-comparador" value="area" checked>
          <span class="filtro-checkbox__caixa"></span> Área
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" class="campo-comparador" value="descricao">
          <span class="filtro-checkbox__caixa"></span> Descrição
        </label>
      </div>
      <div class="modal-comparador__acoes">
        <button type="button" class="btn btn--secondary" id="cancelarCamposComparador">Cancelar</button>
        <button type="button" class="btn btn--primary" id="verComparacao">Comparar</button>
      </div>
    </div>

    <div id="passoResultadoComparador" hidden>
      <h2 class="display" style="font-size:20px;">Comparação de imóveis</h2>
      <div id="tabelaComparador"></div>
      <div class="modal-comparador__acoes">
        <button type="button" class="btn btn--secondary" id="voltarCamposComparador">Voltar</button>
        <button type="button" class="btn btn--primary" id="fecharComparacao">Fechar</button>
      </div>
    </div>
  </div>
</dialog>

<script src="${pageContext.request.contextPath}/js/cidades-ro.js?v=60"></script>
<script src="${pageContext.request.contextPath}/js/catalogo.js?v=60"></script>
</body>
</html>
