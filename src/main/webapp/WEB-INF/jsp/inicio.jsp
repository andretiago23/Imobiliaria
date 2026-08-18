<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Locale, java.text.NumberFormat, model.Imovel, model.Finalidade, model.StatusImovel" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Catálogo | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=2">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=2">
</head>
<body>

<!-- ===================== HEADER ===================== -->
<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/inicio" aria-label="Habittar — catálogo">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
    <nav class="nav__links">
      <a href="${pageContext.request.contextPath}/index.jsp">Início</a>
      <a class="${filtro.finalidade == 'VENDA' ? 'is-current' : ''}" href="${pageContext.request.contextPath}/inicio?finalidade=venda" ${filtro.finalidade == 'VENDA' ? 'aria-current="page"' : ''}>Comprar</a>
      <a class="${filtro.finalidade == 'ALUGUEL' ? 'is-current' : ''}" href="${pageContext.request.contextPath}/inicio?finalidade=aluguel" ${filtro.finalidade == 'ALUGUEL' ? 'aria-current="page"' : ''}>Alugar</a>
      <a href="${pageContext.request.contextPath}/anunciar">Anunciar</a>
      <a href="${pageContext.request.contextPath}/financiamento">Financiamento</a>
      <% if (session.getAttribute("usuarioLogado") != null) { %>
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
      <% } else { %>
        <a class="btn btn--primary btn--sm" href="${pageContext.request.contextPath}/login">Entrar</a>
      <% } %>
    </nav>
  </div>
</header>

<main class="app-main app-main--catalogo">
  <div class="map-grid" aria-hidden="true"></div>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <form method="get" action="${pageContext.request.contextPath}/inicio" class="catalogo-layout">

    <!-- ===================== SIDEBAR DE FILTROS ===================== -->
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
        <h3>Preço</h3>
        <div class="filtro-preco">
          <label>
            <span class="micro">Mínimo</span>
            <input type="number" name="precoMinimo" min="0" step="1000" placeholder="R$ 0" value="${filtro.precoMinimo}">
          </label>
          <label>
            <span class="micro">Máximo</span>
            <input type="number" name="precoMaximo" min="0" step="1000" placeholder="R$ 0" value="${filtro.precoMaximo}">
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

      <div class="busca-topo">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.2-3.2"/></svg>
        <input type="text" name="cidade" placeholder="Cidade ou bairro — ex.: Pinheiros, São Paulo" value="${filtro.cidade}">
        <input type="text" name="estado" placeholder="UF" maxlength="2" class="busca-topo__uf" value="${filtro.estado}">
        <button class="btn btn--primary btn--sm" type="submit">Buscar</button>
      </div>

      <%
        java.util.Map<String, String> rotulosFiltro = new java.util.LinkedHashMap<>();
        rotulosFiltro.put("cidade", "Cidade: ");
        rotulosFiltro.put("estado", "Estado: ");
        rotulosFiltro.put("finalidade", "Negócio: ");
        rotulosFiltro.put("tipo", "Tipo: ");
        rotulosFiltro.put("quartosMinimo", "Quartos ≥ ");
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
      <% } else { %>
        <div class="catalogo__grade">
          <% for (Imovel imovel : imoveis) {
            boolean aluguel = imovel.getFinalidade() == Finalidade.ALUGUEL;
            boolean reservado = imovel.getStatus() == StatusImovel.RESERVADO;
          %>
          <article class="card">
            <div class="card__photo tem-foto">
              <img src="<%= util.ImagemImovel.urlIlustrativa(imovel.getTipo(), imovel.getId()) %>"
                alt="Foto ilustrativa de <%= util.Html.escapar(imovel.getTitulo()) %>" loading="lazy">
              <span class="badge"><%= aluguel ? "Aluguel" : "Venda" %></span>
              <% if (reservado) { %>
                <span class="badge badge--reservado">Reservado</span>
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
                <%= util.Html.escapar(imovel.getCidade()) %> — <%= util.Html.escapar(imovel.getEstado()) %>
              </div>
              <div class="card__specs">
                <span><%= imovel.getAreaM2() %> m²</span>
                <span><%= imovel.getQuartos() %> qto(s)</span>
                <span><%= imovel.getBanheiros() %> banh.</span>
                <span><%= imovel.getVagasGaragem() %> vaga(s)</span>
              </div>
            </div>
            <a class="btn btn--secondary btn--sm card__link" href="${pageContext.request.contextPath}/imovel?id=<%= imovel.getId() %>">Ver detalhes</a>
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

</body>
</html>
