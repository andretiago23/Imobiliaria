<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.RascunhoAnuncio" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>O que anunciar | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=15">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=15">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=15">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=15">
</head>
<body>

<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/index.jsp" aria-label="Habittar — página principal">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
    <nav class="nav__links">
      <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
    </nav>
  </div>
</header>

<main class="wizard-main">
  <% request.setAttribute("etapaAtual", 1); %>
  <jsp:include page="/WEB-INF/jsp/fragmentos/progressbar.jsp" />

  <p class="eyebrow wizard-etapa__eyebrow">Etapa 1 de 4</p>
  <h1 class="display wizard-etapa__titulo">O que você deseja fazer?</h1>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    RascunhoAnuncio rascunho = (RascunhoAnuncio) request.getAttribute("rascunho");
  %>

  <form method="post" action="${pageContext.request.contextPath}/anunciar/etapa1" id="formEtapa1">
    <input type="hidden" name="csrf" value="${csrf}">

    <div class="wizard-opcoes" role="radiogroup" aria-label="Tipo de negócio">
      <input type="radio" name="finalidade" value="venda" id="finalidadeVenda" class="wizard-opcao-input"
        <%= rascunho.getFinalidade() == model.Finalidade.VENDA ? "checked" : "" %> required>
      <label for="finalidadeVenda" class="wizard-opcao">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 10.5 12 4l9 6.5V20a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/></svg>
        <strong>Vender</strong>
        <span>Quero vender este imóvel</span>
      </label>

      <input type="radio" name="finalidade" value="aluguel" id="finalidadeAluguel" class="wizard-opcao-input"
        <%= rascunho.getFinalidade() == model.Finalidade.ALUGUEL ? "checked" : "" %> required>
      <label for="finalidadeAluguel" class="wizard-opcao">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="3" y="7" width="18" height="14" rx="2"/><path d="M8 7V4h8v3"/></svg>
        <strong>Alugar</strong>
        <span>Quero alugar este imóvel</span>
      </label>
    </div>

    <h2 style="font-size:14px;margin:0 0 14px;">Sobre o imóvel</h2>
    <div class="filtros__grade filtros__grade--2col">
      <div class="filtros__campo filtros__campo--largo">
        <label for="titulo">Título do anúncio</label>
        <input type="text" id="titulo" name="titulo" placeholder="Ex.: Apartamento reformado em Pinheiros"
          value="<%= rascunho.getTitulo() == null ? "" : util.Html.escapar(rascunho.getTitulo()) %>" required maxlength="200">
      </div>
      <div class="filtros__campo">
        <label for="tipo">Tipo</label>
        <select id="tipo" name="tipo" required>
          <option value="">Selecione</option>
          <option value="casa" <%= rascunho.getTipo() == model.TipoImovel.CASA ? "selected" : "" %>>Casa</option>
          <option value="apartamento" <%= rascunho.getTipo() == model.TipoImovel.APARTAMENTO ? "selected" : "" %>>Apartamento</option>
          <option value="terreno" <%= rascunho.getTipo() == model.TipoImovel.TERRENO ? "selected" : "" %>>Terreno</option>
          <option value="comercial" <%= rascunho.getTipo() == model.TipoImovel.COMERCIAL ? "selected" : "" %>>Imóvel comercial</option>
          <option value="rural" <%= rascunho.getTipo() == model.TipoImovel.RURAL ? "selected" : "" %>>Imóvel rural</option>
        </select>
      </div>
      <div class="filtros__campo">
        <label for="preco">Preço (R$)</label>
        <input type="number" id="preco" name="preco" min="0" step="0.01" placeholder="0,00"
          value="<%= rascunho.getPreco() == null ? "" : rascunho.getPreco() %>" required>
      </div>
      <div class="filtros__campo">
        <label for="areaM2">Área (m²)</label>
        <input type="number" id="areaM2" name="areaM2" min="0" step="0.01" placeholder="0"
          value="<%= rascunho.getAreaM2() == 0 ? "" : rascunho.getAreaM2() %>">
      </div>
      <div class="filtros__campo">
        <label for="quartos">Quartos</label>
        <input type="number" id="quartos" name="quartos" min="0" step="1" placeholder="0"
          value="<%= rascunho.getQuartos() == 0 ? "" : rascunho.getQuartos() %>">
      </div>
      <div class="filtros__campo">
        <label for="banheiros">Banheiros</label>
        <input type="number" id="banheiros" name="banheiros" min="0" step="1" placeholder="0"
          value="<%= rascunho.getBanheiros() == 0 ? "" : rascunho.getBanheiros() %>">
      </div>
      <div class="filtros__campo filtros__campo--largo">
        <label for="vagasGaragem">Vagas de garagem</label>
        <input type="number" id="vagasGaragem" name="vagasGaragem" min="0" step="1" placeholder="0"
          value="<%= rascunho.getVagasGaragem() == 0 ? "" : rascunho.getVagasGaragem() %>">
      </div>
    </div>

    <div class="filtros__campo" style="margin-top:16px;">
      <label for="descricao">Descrição</label>
      <textarea id="descricao" name="descricao" rows="4" placeholder="Detalhes que ajudam quem está buscando: acabamento, condomínio, proximidade de transporte..."
        style="width:100%;font-family:var(--font-sans);font-size:15px;color:var(--text-primary);background:var(--surface-page);border:1px solid var(--border-subtle);border-radius:var(--radius-sm);padding:12px 14px;resize:vertical;"><%= rascunho.getDescricao() == null ? "" : util.Html.escapar(rascunho.getDescricao()) %></textarea>
    </div>

    <h2 style="font-size:14px;margin:28px 0 14px;">Endereço do imóvel</h2>
    <div class="wizard-cep">
      <div class="filtros__campo">
        <label for="cep">CEP</label>
        <input type="text" id="cep" name="cep" placeholder="00000-000" inputmode="numeric" maxlength="9"
          data-mascara="cep" value="<%= rascunho.getCep() == null ? "" : rascunho.getCep() %>" required>
        <p class="wizard-cep__status" id="cepStatus"></p>
      </div>
    </div>
    <div class="filtros__grade filtros__grade--2col">
      <div class="filtros__campo filtros__campo--largo">
        <label for="endereco">Rua</label>
        <input type="text" id="endereco" name="endereco" placeholder="Preenchido automaticamente pelo CEP"
          value="<%= rascunho.getEndereco() == null ? "" : util.Html.escapar(rascunho.getEndereco()) %>" required>
      </div>
      <div class="filtros__campo">
        <label for="numero">Número</label>
        <input type="text" id="numero" name="numero" placeholder="Ex.: 120"
          value="<%= rascunho.getNumero() == null ? "" : util.Html.escapar(rascunho.getNumero()) %>" required>
      </div>
      <div class="filtros__campo">
        <label for="bairro">Bairro</label>
        <input type="text" id="bairro" name="bairro" placeholder="Preenchido automaticamente pelo CEP"
          value="<%= rascunho.getBairro() == null ? "" : util.Html.escapar(rascunho.getBairro()) %>" required>
      </div>
      <div class="filtros__campo">
        <label for="cidade">Cidade</label>
        <input type="text" id="cidade" name="cidade" readonly
          value="<%= rascunho.getCidade() == null ? "" : util.Html.escapar(rascunho.getCidade()) %>">
      </div>
      <div class="filtros__campo">
        <label for="estado">Estado</label>
        <input type="text" id="estado" name="estado" maxlength="2" readonly
          value="<%= rascunho.getEstado() == null ? "" : util.Html.escapar(rascunho.getEstado()) %>">
      </div>
    </div>

    <div class="wizard-acoes">
      <button class="btn btn--primary btn--interactive" type="submit" id="botaoProximo">
        <span class="btn__label">Próximo</span>
        <span class="btn__reveal" aria-hidden="true">
          Próximo
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
        </span>
        <span class="btn__dot" aria-hidden="true"></span>
      </button>
    </div>
  </form>
</main>

<script src="${pageContext.request.contextPath}/js/formulario.js?v=15"></script>
<script src="${pageContext.request.contextPath}/js/anuncio-wizard.js?v=15"></script>
</body>
</html>
