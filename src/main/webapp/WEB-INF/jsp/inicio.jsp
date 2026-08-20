<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Locale, java.text.NumberFormat, model.Imovel, model.Finalidade, model.StatusImovel" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Catálogo | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=36">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=36">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=36">
</head>
<body>

<!-- ===================== HEADER ===================== -->
<% pageContext.setAttribute("navFixa", true); %>
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
        <h3>Localização</h3>
        <div class="filtro-localizacao">
          <input type="text" name="cidade" placeholder="Cidade ou bairro — ex.: Pinheiros" value="${filtro.cidade}">
          <input type="text" name="estado" placeholder="UF" maxlength="2" class="filtro-localizacao__uf" value="${filtro.estado}">
        </div>
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

      <div class="filtro-secao">
        <h3>Vagas de garagem</h3>
        <div class="pills">
          <input type="radio" name="vagasMinimo" value="" id="v0" class="pill-input" ${empty filtro.vagasMinimo ? 'checked' : ''}>
          <label for="v0" class="pill">Todas</label>
          <input type="radio" name="vagasMinimo" value="1" id="v1" class="pill-input" ${filtro.vagasMinimo == 1 ? 'checked' : ''}>
          <label for="v1" class="pill">1+</label>
          <input type="radio" name="vagasMinimo" value="2" id="v2" class="pill-input" ${filtro.vagasMinimo == 2 ? 'checked' : ''}>
          <label for="v2" class="pill">2+</label>
          <input type="radio" name="vagasMinimo" value="3" id="v3" class="pill-input" ${filtro.vagasMinimo == 3 ? 'checked' : ''}>
          <label for="v3" class="pill">3+</label>
        </div>
      </div>

      <!-- Item 2.5: área vira campo numérico com "m²" dinâmico ao lado -->
      <div class="filtro-secao">
        <h3>Área mínima</h3>
        <div class="filtro-area">
          <input type="text" inputmode="numeric" maxlength="5" id="campoArea" name="areaMinima" placeholder="Qualquer" value="${filtro.areaMinima}">
          <span class="filtro-area__sufixo" id="sufixoArea" hidden>m²</span>
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
        java.util.Map<String, String> rotulosFiltro = new java.util.LinkedHashMap<>();
        rotulosFiltro.put("cidade", "Cidade: ");
        rotulosFiltro.put("estado", "Estado: ");
        rotulosFiltro.put("finalidade", "Negócio: ");
        rotulosFiltro.put("tipo", "Tipo: ");
        rotulosFiltro.put("quartosMinimo", "Quartos ≥ ");
        rotulosFiltro.put("banheirosMinimo", "Banheiros ≥ ");
        rotulosFiltro.put("vagasMinimo", "Vagas ≥ ");
        rotulosFiltro.put("areaMinima", "Área ≥ ");
        rotulosFiltro.put("precoMinimo", "Preço mín. ");
        rotulosFiltro.put("precoMaximo", "Preço máx. ");
        java.util.List<String> chips = new java.util.ArrayList<>();
        for (String chave : rotulosFiltro.keySet()) {
          String valor = request.getParameter(chave);
          if (valor != null && !valor.isBlank()) {
            String href = "?";
            for (String outraChave : rotulosFiltro.keySet()) {
              if (outraChave.equals(chave)) continue;
              String outroValor = request.getParameter(outraChave);
              if (outroValor != null && !outroValor.isBlank()) {
                href += outraChave + "=" + java.net.URLEncoder.encode(outroValor, java.nio.charset.StandardCharsets.UTF_8) + "&";
              }
            }
            chips.add("<a class=\"chip\" href=\"" + util.Html.escapar(href) + "\">"
                + util.Html.escapar(rotulosFiltro.get(chave)) + util.Html.escapar(valor)
                + " <span aria-hidden=\"true\">×</span></a>");
          }
        }
      %>
      <% if (!chips.isEmpty()) { %>
        <div class="filtros-ativos">
          <% for (String chip : chips) { %><%= chip %><% } %>
          <a class="chip chip--limpar" href="${pageContext.request.contextPath}/inicio">Limpar tudo</a>
        </div>
      <% } %>

      <%
        List<Imovel> imoveis = (List<Imovel>) request.getAttribute("imoveis");
        if (imoveis == null) {
          imoveis = java.util.Collections.emptyList();
        }
        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        NumberFormat area = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        area.setMaximumFractionDigits(1);
      %>
      <div class="catalogo__cabecalho">
        <h1 class="display" style="font-size:26px;">Imóveis <span class="hl">disponíveis</span></h1>
        <span class="micro"><%= imoveis.size() %> encontrado(s)</span>
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
            boolean reservado = imovel.getStatus() == StatusImovel.RESERVADO;
            boolean salvo = idsFavoritados != null && idsFavoritados.contains(imovel.getId());
          %>
          <!-- Item 2.1: card inteiro é clicável (article com data-href + JS),
               o botão "salvar" continua funcionando à parte via stopPropagation -->
          <article class="card card--clicavel" data-href="${pageContext.request.contextPath}/imovel?id=<%= imovel.getId() %>" tabindex="0" role="link">
            <div class="card__photo tem-foto">
              <img src="<%= util.ImagemImovel.urlIlustrativa(imovel.getTipo(), imovel.getId()) %>"
                alt="Foto ilustrativa de <%= util.Html.escapar(imovel.getTitulo()) %>" loading="lazy">
              <span class="badge"><%= aluguel ? "Aluguel" : "Venda" %></span>
              <% if (reservado) { %>
                <span class="badge badge--reservado">Reservado</span>
              <% } %>
              <% if (session.getAttribute("usuarioLogado") != null) { %>
                <form method="post" action="${pageContext.request.contextPath}/favorito" class="card__salvar">
                  <input type="hidden" name="csrf" value="${csrf}">
                  <input type="hidden" name="idImovel" value="<%= imovel.getId() %>">
                  <input type="hidden" name="destino" value="${pageContext.request.contextPath}/inicio">
                  <button type="submit" class="botao-salvar <%= salvo ? "botao-salvar--ativo" : "" %>" aria-label="Salvar imóvel">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="<%= salvo ? "currentColor" : "none" %>" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M19 21 12 16l-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>
                  </button>
                </form>
              <% } %>
            </div>
            <div class="card__body">
              <div class="card__price">
                <%= moeda.format(imovel.getPreco()) %>
                <% if (aluguel) { %><span class="micro">/mês</span><% } %>
              </div>
              <p style="margin:6px 0 0;font-weight:600;"><%= util.Html.escapar(imovel.getTitulo()) %></p>
              <div class="card__loc">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/></svg>
                <%
                  String cidade = imovel.getCidade();
                  String estado = imovel.getEstado();
                  boolean temCidade = cidade != null && !cidade.isBlank();
                  boolean temEstado = estado != null && !estado.isBlank();
                %>
                <% if (temCidade && temEstado) { %>
                  <%= util.Html.escapar(cidade) %> — <%= util.Html.escapar(estado) %>
                <% } else if (temCidade) { %>
                  <%= util.Html.escapar(cidade) %>
                <% } else if (temEstado) { %>
                  <%= util.Html.escapar(estado) %>
                <% } else { %>
                  Localização não informada
                <% } %>
              </div>
              <div class="card__specs">
                <span title="Área">
                  <svg class="ficha-icone" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M8 3H5a2 2 0 0 0-2 2v3"/><path d="M21 8V5a2 2 0 0 0-2-2h-3"/><path d="M3 16v3a2 2 0 0 0 2 2h3"/><path d="M16 21h3a2 2 0 0 0 2-2v-3"/></svg>
                  <%= area.format(imovel.getAreaM2()) %> m²
                </span>
                <span title="Quartos">
                  <svg class="ficha-icone" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M2 4v16"/><path d="M2 8h18a2 2 0 0 1 2 2v10"/><path d="M2 17h20"/><path d="M6 8v9"/></svg>
                  <%= imovel.getQuartos() %> qto(s)
                </span>
                <span title="Banheiros">
                  <svg class="ficha-icone" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 2.7 17.66 8.4A8 8 0 1 1 6.34 8.4Z"/></svg>
                  <%= imovel.getBanheiros() %> banh.
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

<footer class="footer" style="padding:32px 24px;text-align:center;">
  <span class="micro">© 2026 Habittar. Todos os direitos reservados.</span>
</footer>

<script src="${pageContext.request.contextPath}/js/catalogo.js?v=36"></script>
</body>
</html>
