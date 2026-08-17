<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Anunciar imóvel | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css">
</head>
<body>

<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/inicio" aria-label="Habittar — catálogo">
      <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#FF6A1A" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
        <path d="M12 21s7-5.2 7-11a7 7 0 1 0-14 0c0 5.8 7 11 7 11Z"/>
        <path d="M9 11.2 12 8.8l3 2.4V14h-6z"/>
      </svg>
      Habittar
    </a>
    <nav class="nav__links">
      <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
      <a class="avatar" href="${pageContext.request.contextPath}/perfil" title="Meu perfil" aria-label="Meu perfil">
        <% if (((model.Usuario) session.getAttribute("usuarioLogado")).getFotoPerfil() != null
              && !((model.Usuario) session.getAttribute("usuarioLogado")).getFotoPerfil().isBlank()) { %>
          <img src="${pageContext.request.contextPath}${sessionScope.usuarioLogado.fotoPerfil}" alt="">
        <% } else { %>
          ${sessionScope.usuarioLogado.inicial}
        <% } %>
      </a>
      <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/logout">Sair</a>
    </nav>
  </div>
</header>

<main class="app-main" style="max-width:760px;">

  <a class="voltar" href="${pageContext.request.contextPath}/inicio">
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
    Voltar ao catálogo
  </a>

  <div class="app-header">
    <div>
      <p class="eyebrow">Painel do anunciante</p>
      <h1 class="display">Anunciar <span class="hl">imóvel</span></h1>
    </div>
  </div>

  <% if (Boolean.TRUE.equals(request.getAttribute("semPermissao"))) { %>

    <div class="estado-vazio">
      <p><strong>Sua conta é do tipo comprador.</strong></p>
      <p class="texto-apoio">Para publicar imóveis, altere o tipo da sua conta para "Vendedor / imobiliária" no cadastro.</p>
      <a class="btn btn--primary" style="margin-top:16px;" href="${pageContext.request.contextPath}/inicio">Voltar ao catálogo</a>
    </div>

  <% } else { %>

    <p class="alerta alerta-erro" role="alert">${erro}</p>

    <form class="filtros" method="post" action="${pageContext.request.contextPath}/anunciar" id="formAnuncio">
      <input type="hidden" name="csrf" value="${csrf}">

      <div class="filtros__grade">

        <div class="filtros__campo filtros__campo--largo">
          <label for="titulo">Título do anúncio</label>
          <input type="text" id="titulo" name="titulo" placeholder="Ex.: Apartamento reformado em Pinheiros" value="${titulo}" required maxlength="200">
        </div>

        <div class="filtros__campo">
          <label for="tipo">Tipo</label>
          <select id="tipo" name="tipo" required>
            <option value="">Selecione</option>
            <option value="casa" ${tipo == 'casa' ? 'selected' : ''}>Casa</option>
            <option value="apartamento" ${tipo == 'apartamento' ? 'selected' : ''}>Apartamento</option>
            <option value="terreno" ${tipo == 'terreno' ? 'selected' : ''}>Terreno</option>
            <option value="comercial" ${tipo == 'comercial' ? 'selected' : ''}>Imóvel comercial</option>
            <option value="rural" ${tipo == 'rural' ? 'selected' : ''}>Imóvel rural</option>
          </select>
        </div>

        <div class="filtros__campo">
          <label for="finalidade">Negócio</label>
          <select id="finalidade" name="finalidade" required>
            <option value="">Selecione</option>
            <option value="venda" ${finalidade == 'venda' ? 'selected' : ''}>Venda</option>
            <option value="aluguel" ${finalidade == 'aluguel' ? 'selected' : ''}>Aluguel</option>
          </select>
        </div>

        <div class="filtros__campo">
          <label for="preco">Preço (R$)</label>
          <input type="number" id="preco" name="preco" min="0" step="0.01" value="${preco}" required>
        </div>

        <div class="filtros__campo">
          <label for="areaM2">Área (m²)</label>
          <input type="number" id="areaM2" name="areaM2" min="0" step="0.01" value="${areaM2}">
        </div>

        <div class="filtros__campo">
          <label for="quartos">Quartos</label>
          <input type="number" id="quartos" name="quartos" min="0" step="1" value="${quartos}">
        </div>

        <div class="filtros__campo">
          <label for="banheiros">Banheiros</label>
          <input type="number" id="banheiros" name="banheiros" min="0" step="1" value="${banheiros}">
        </div>

        <div class="filtros__campo">
          <label for="vagasGaragem">Vagas de garagem</label>
          <input type="number" id="vagasGaragem" name="vagasGaragem" min="0" step="1" value="${vagasGaragem}">
        </div>

        <div class="filtros__campo filtros__campo--largo">
          <label for="endereco">Endereço</label>
          <input type="text" id="endereco" name="endereco" placeholder="Rua, número" value="${endereco}">
        </div>

        <div class="filtros__campo">
          <label for="cidade">Cidade</label>
          <input type="text" id="cidade" name="cidade" value="${cidade}">
        </div>

        <div class="filtros__campo">
          <label for="estado">Estado</label>
          <input type="text" id="estado" name="estado" placeholder="UF" maxlength="2" value="${estado}">
        </div>

        <div class="filtros__campo">
          <label for="cep">CEP</label>
          <input type="text" id="cep" name="cep" placeholder="00000-000" maxlength="9" value="${cep}">
        </div>

        <div class="filtros__campo filtros__campo--largo">
          <label for="descricao">Descrição</label>
          <textarea id="descricao" name="descricao" rows="5" placeholder="Detalhes que ajudam quem está buscando: acabamento, condomínio, proximidade de transporte..." style="width:100%;font-family:var(--font-sans);font-size:15px;color:var(--text-primary);background:var(--surface-page);border:1px solid var(--border-subtle);border-radius:var(--radius-sm);padding:12px 14px;resize:vertical;">${descricao}</textarea>
        </div>

        <div class="filtros__campo filtros__campo--largo">
          <button class="btn btn--primary" type="submit">Publicar anúncio</button>
        </div>
      </div>
    </form>

  <% } %>

</main>

<script src="${pageContext.request.contextPath}/js/formulario.js"></script>
</body>
</html>
