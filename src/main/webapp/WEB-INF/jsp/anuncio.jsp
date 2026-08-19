<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Anunciar imóvel | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=12">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=12">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=12">
</head>
<body>

<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/inicio" aria-label="Habittar — catálogo">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
    <nav class="nav__links">
      <a href="${pageContext.request.contextPath}/inicio">Catálogo</a>
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

    <p class="alerta alerta-erro" role="alert">${erro}</p>

    <form class="filtros" method="post" action="${pageContext.request.contextPath}/anunciar" id="formAnuncio">
      <input type="hidden" name="csrf" value="${csrf}">

      <h2 style="font-size:14px;margin:0 0 14px;">Sobre o imóvel</h2>
      <div class="filtros__grade filtros__grade--2col">
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
      </div>

      <h2 style="font-size:14px;margin:28px 0 14px;">Preço e características</h2>
      <div class="filtros__grade filtros__grade--2col">
        <div class="filtros__campo">
          <label for="preco">Preço (R$)</label>
          <input type="number" id="preco" name="preco" min="0" step="0.01" placeholder="0,00" value="${preco}" required>
        </div>

        <div class="filtros__campo">
          <label for="areaM2">Área (m²)</label>
          <input type="number" id="areaM2" name="areaM2" min="0" step="0.01" placeholder="0" value="${areaM2}">
        </div>

        <div class="filtros__campo">
          <label for="quartos">Quartos</label>
          <input type="number" id="quartos" name="quartos" min="0" step="1" placeholder="0" value="${quartos}">
        </div>

        <div class="filtros__campo">
          <label for="banheiros">Banheiros</label>
          <input type="number" id="banheiros" name="banheiros" min="0" step="1" placeholder="0" value="${banheiros}">
        </div>

        <div class="filtros__campo filtros__campo--largo">
          <label for="vagasGaragem">Vagas de garagem</label>
          <input type="number" id="vagasGaragem" name="vagasGaragem" min="0" step="1" placeholder="0" value="${vagasGaragem}">
        </div>
      </div>

      <h2 style="font-size:14px;margin:28px 0 14px;">Localização</h2>
      <div class="filtros__grade filtros__grade--2col">
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

        <div class="filtros__campo filtros__campo--largo">
          <label for="cep">CEP</label>
          <input type="text" id="cep" name="cep" placeholder="00000-000" maxlength="9" value="${cep}">
        </div>
      </div>

      <h2 style="font-size:14px;margin:28px 0 14px;">Descrição</h2>
      <div class="filtros__campo">
        <label for="descricao" class="sr-only">Descrição do imóvel</label>
        <textarea id="descricao" name="descricao" rows="5" placeholder="Detalhes que ajudam quem está buscando: acabamento, condomínio, proximidade de transporte..." style="width:100%;font-family:var(--font-sans);font-size:15px;color:var(--text-primary);background:var(--surface-page);border:1px solid var(--border-subtle);border-radius:var(--radius-sm);padding:12px 14px;resize:vertical;">${descricao}</textarea>
      </div>

      <button class="btn btn--primary btn--interactive" type="submit" style="width:100%;margin-top:24px;">
        <span class="btn__label">Publicar anúncio</span>
        <span class="btn__reveal" aria-hidden="true">
          Publicar anúncio
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
        </span>
        <span class="btn__dot" aria-hidden="true"></span>
      </button>
    </form>

</main>

<script src="${pageContext.request.contextPath}/js/formulario.js?v=12"></script>
</body>
</html>
