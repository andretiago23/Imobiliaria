<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.RascunhoAnuncio, java.net.URLEncoder, java.nio.charset.StandardCharsets" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>O que anunciar | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=63">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=64">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=63">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=63">
</head>
<body>

<% request.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="wizard-main">
  <% request.setAttribute("etapaAtual", 1); %>
  <jsp:include page="/WEB-INF/jsp/fragmentos/progressbar.jsp" />

  <p class="eyebrow wizard-etapa__eyebrow">Etapa 1 de 5</p>
  <h1 class="display wizard-etapa__titulo">O que você deseja fazer?</h1>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    RascunhoAnuncio rascunho = (RascunhoAnuncio) request.getAttribute("rascunho");
  %>

  <form method="post" action="${pageContext.request.contextPath}/anunciar/etapa1" id="formEtapa1"
    enctype="multipart/form-data">
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
        <label for="preco">Preço</label>
        <input type="text" id="preco" name="preco" inputmode="decimal" placeholder="R$ 0,00" maxlength="18"
          data-mascara="moeda" value="<%= rascunho.getPreco() == null ? "" : rascunho.getPreco() %>" required>
      </div>
      <div class="filtros__campo filtros__campo--largo">
        <div class="wizard-specs-grade">
          <div class="filtros__campo">
            <label for="areaM2">Área</label>
            <input type="text" id="areaM2" name="areaM2" inputmode="decimal" placeholder="0 m²" maxlength="12"
              data-mascara="area" value="<%= rascunho.getAreaM2() == 0 ? "" : rascunho.getAreaM2() %>">
          </div>
          <div class="filtros__campo">
            <label for="quartos">Quartos</label>
            <div class="wizard-stepper" data-stepper>
              <button type="button" class="wizard-stepper__botao" data-step="-1" aria-label="Diminuir quartos">−</button>
              <input type="number" id="quartos" name="quartos" min="0" max="20" step="1" inputmode="numeric"
                value="<%= rascunho.getQuartos() %>">
              <button type="button" class="wizard-stepper__botao" data-step="1" aria-label="Aumentar quartos">+</button>
            </div>
          </div>
          <div class="filtros__campo">
            <label for="banheiros">Banheiros</label>
            <div class="wizard-stepper" data-stepper>
              <button type="button" class="wizard-stepper__botao" data-step="-1" aria-label="Diminuir banheiros">−</button>
              <input type="number" id="banheiros" name="banheiros" min="0" max="20" step="1" inputmode="numeric"
                value="<%= rascunho.getBanheiros() %>">
              <button type="button" class="wizard-stepper__botao" data-step="1" aria-label="Aumentar banheiros">+</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="filtros__campo" style="margin-top:16px;">
      <label for="descricao">Descrição</label>
      <textarea id="descricao" name="descricao" rows="5" minlength="150" maxlength="1000" required
        placeholder="Detalhes que ajudam quem está buscando: acabamento, condomínio, proximidade de transporte..."
        style="width:100%;font-family:var(--font-sans);font-size:15px;color:var(--text-primary);background:var(--surface-page);border:1px solid var(--border-subtle);border-radius:var(--radius-sm);padding:12px 14px;resize:vertical;"><%= rascunho.getDescricao() == null ? "" : util.Html.escapar(rascunho.getDescricao()) %></textarea>
      <p class="wizard-contador" id="contadorDescricao"></p>
    </div>

    <h2 style="font-size:14px;margin:28px 0 4px;">Características do imóvel</h2>
    <p class="micro" style="margin:0 0 12px;color:var(--text-secondary);">Selecione as que se aplicam — elas entram automaticamente na descrição.</p>
    <details class="wizard-caracteristicas-dropdown">
      <summary>
        <span id="resumoCaracteristicas">Selecione as características</span>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg>
      </summary>
      <div class="wizard-caracteristicas" id="caracteristicas">
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🐕" data-texto="Aceita animais">
          <span class="filtro-checkbox__caixa"></span> Aceita animais
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🛋️" data-texto="Mobiliado">
          <span class="filtro-checkbox__caixa"></span> Mobiliado
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="❄️" data-texto="Ar-condicionado">
          <span class="filtro-checkbox__caixa"></span> Ar-condicionado
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🚪" data-texto="Armário embutido">
          <span class="filtro-checkbox__caixa"></span> Armário embutido
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🌇" data-texto="Varanda">
          <span class="filtro-checkbox__caixa"></span> Varanda
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🏊" data-texto="Piscina">
          <span class="filtro-checkbox__caixa"></span> Piscina
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🔥" data-texto="Churrasqueira">
          <span class="filtro-checkbox__caixa"></span> Churrasqueira
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🔐" data-texto="Portaria 24h">
          <span class="filtro-checkbox__caixa"></span> Portaria 24h
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🛗" data-texto="Elevador">
          <span class="filtro-checkbox__caixa"></span> Elevador
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🏋️" data-texto="Academia">
          <span class="filtro-checkbox__caixa"></span> Academia
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🏘️" data-texto="Condomínio fechado">
          <span class="filtro-checkbox__caixa"></span> Condomínio fechado
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🧺" data-texto="Área de serviço">
          <span class="filtro-checkbox__caixa"></span> Área de serviço
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="☎️" data-texto="Interfone">
          <span class="filtro-checkbox__caixa"></span> Interfone
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🔋" data-texto="Gerador">
          <span class="filtro-checkbox__caixa"></span> Gerador
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="☀️" data-texto="Energia solar">
          <span class="filtro-checkbox__caixa"></span> Energia solar
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🎪" data-texto="Playground">
          <span class="filtro-checkbox__caixa"></span> Playground
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🎉" data-texto="Salão de festas">
          <span class="filtro-checkbox__caixa"></span> Salão de festas
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🏀" data-texto="Quadra poliesportiva">
          <span class="filtro-checkbox__caixa"></span> Quadra poliesportiva
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="🚧" data-texto="Portão eletrônico">
          <span class="filtro-checkbox__caixa"></span> Portão eletrônico
        </label>
        <label class="filtro-checkbox">
          <input type="checkbox" data-emoji="💼" data-texto="Escritório/home office">
          <span class="filtro-checkbox__caixa"></span> Escritório/home office
        </label>
      </div>
    </details>

    <h2 style="font-size:14px;margin:28px 0 4px;">Fotos e vídeo do imóvel</h2>
    <p class="micro" style="margin:0 0 12px;color:var(--text-secondary);">
      Obrigatório: pelo menos <%= RascunhoAnuncio.MINIMO_FOTOS %> fotos, para dar mais confiança a quem está buscando. O vídeo é opcional.
    </p>
    <p class="alerta alerta-erro" id="erroMidia" role="alert" hidden></p>

    <div class="wizard-midia">
      <p class="micro" style="margin:0 0 8px;font-weight:600;">
        Fotos (<span id="contadorFotos"><%= rascunho.getFotos().size() %></span>/<%= RascunhoAnuncio.MINIMO_FOTOS %> no mínimo)
      </p>
      <div class="wizard-midia__grade" id="gradeFotos">
        <% for (String urlFoto : rascunho.getFotos()) { %>
          <div class="wizard-midia__item">
            <img src="<%= urlFoto %>" alt="">
            <a class="wizard-midia__remover"
              href="${pageContext.request.contextPath}/anunciar/etapa1?removerFoto=<%= URLEncoder.encode(urlFoto, StandardCharsets.UTF_8) %>"
              data-remover-foto="<%= util.Html.escapar(urlFoto) %>" title="Remover foto" aria-label="Remover foto">×</a>
          </div>
        <% } %>
      </div>
      <label class="btn btn--secondary btn--sm" style="display:inline-flex;margin-top:4px;cursor:pointer;">
        + Adicionar fotos
        <input type="file" id="inputFotos" name="fotos" accept="image/jpeg,image/png,image/webp" multiple class="sr-only">
      </label>
    </div>

    <div class="wizard-midia" style="margin-top:20px;">
      <p class="micro" style="margin:0 0 8px;font-weight:600;">Vídeo (opcional)</p>
      <div id="blocoVideo">
        <% if (rascunho.getVideo() != null && !rascunho.getVideo().isBlank()) { %>
          <div class="wizard-midia__grade">
            <div class="wizard-midia__item">
              <video src="<%= rascunho.getVideo() %>" muted></video>
              <a class="wizard-midia__remover"
                href="${pageContext.request.contextPath}/anunciar/etapa1?removerVideo=1"
                data-remover-video="1" title="Remover vídeo" aria-label="Remover vídeo">×</a>
            </div>
          </div>
        <% } else { %>
          <label class="btn btn--secondary btn--sm" style="display:inline-flex;cursor:pointer;">
            + Adicionar vídeo
            <input type="file" id="inputVideo" name="video" accept="video/mp4,video/webm,video/quicktime" class="sr-only">
          </label>
        <% } %>
      </div>
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
        <input type="text" id="endereco" name="endereco" placeholder="Preenchido automaticamente pelo CEP" maxlength="200"
          value="<%= rascunho.getEndereco() == null ? "" : util.Html.escapar(rascunho.getEndereco()) %>" required>
      </div>
      <div class="filtros__campo">
        <label for="numero">Número</label>
        <input type="text" id="numero" name="numero" placeholder="Ex.: 120" maxlength="10"
          value="<%= rascunho.getNumero() == null ? "" : util.Html.escapar(rascunho.getNumero()) %>" required>
      </div>
      <div class="filtros__campo">
        <label for="bairro">Bairro</label>
        <input type="text" id="bairro" name="bairro" placeholder="Preenchido automaticamente pelo CEP" maxlength="100"
          value="<%= rascunho.getBairro() == null ? "" : util.Html.escapar(rascunho.getBairro()) %>" required>
      </div>
      <div class="filtros__campo">
        <label for="cidade">Cidade</label>
        <input type="text" id="cidade" name="cidade" readonly maxlength="100"
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

<script src="${pageContext.request.contextPath}/js/validacao.js?v=63"></script>
<script src="${pageContext.request.contextPath}/js/formulario.js?v=63"></script>
<script src="${pageContext.request.contextPath}/js/anuncio-wizard.js?v=63"></script>
</body>
</html>
