<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Set, java.util.HashSet, model.Imovel, model.DisponibilidadeVisita, model.DiaSemana, model.TipoImovel, model.Finalidade" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Editar imóvel | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=35">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=35">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=35">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=35">
</head>
<body>

<header class="nav is-scrolled">
  <div class="nav__inner">
    <a class="logo" href="${pageContext.request.contextPath}/inicio" aria-label="Habittar — catálogo">
      <img src="${pageContext.request.contextPath}/imagens/logo-habittar.png" alt="Habittar">
    </a>
  </div>
</header>

<main class="app-main" style="max-width:760px;">
  <a class="voltar" href="${pageContext.request.contextPath}/imoveis-anunciados">
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m15 18-6-6 6-6"/></svg>
    Voltar
  </a>

  <div class="app-header">
    <div>
      <p class="eyebrow">Editar anúncio</p>
      <h1 class="display">Atualize seu imóvel</h1>
    </div>
  </div>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    Imovel imovel = (Imovel) request.getAttribute("imovel");
    List<DisponibilidadeVisita> disponibilidade = (List<DisponibilidadeVisita>) request.getAttribute("disponibilidade");
    Set<String> diasMarcados = new HashSet<>();
    String horaInicioAtual = "09:00";
    String horaFimAtual = "18:00";
    if (disponibilidade != null) {
      for (DisponibilidadeVisita janela : disponibilidade) {
        diasMarcados.add(janela.getDiaSemana().name());
        horaInicioAtual = janela.getHoraInicio().toString();
        horaFimAtual = janela.getHoraFim().toString();
      }
    }
    // Mesmo cuidado do assistente: se por algum motivo o imóvel ainda não
    // tem disponibilidade salva (ex.: anúncio antigo, de antes dessa
    // funcionalidade), não deixa os dias vazios com o horário já
    // preenchido — pré-marca segunda a sexta como sugestão.
    if (diasMarcados.isEmpty()) {
      diasMarcados.add("SEG");
      diasMarcados.add("TER");
      diasMarcados.add("QUA");
      diasMarcados.add("QUI");
      diasMarcados.add("SEX");
    }

    if (imovel != null) {
  %>
  <form method="post" action="${pageContext.request.contextPath}/editar-imovel?id=<%= imovel.getId() %>">
    <input type="hidden" name="csrf" value="${csrf}">

    <h2 style="font-size:14px;margin:0 0 14px;">Sobre o imóvel</h2>
    <div class="filtros__grade filtros__grade--2col">
      <div class="filtros__campo filtros__campo--largo">
        <label for="titulo">Título do anúncio</label>
        <input type="text" id="titulo" name="titulo" maxlength="200" required
          value="<%= util.Html.escapar(imovel.getTitulo()) %>">
      </div>
      <div class="filtros__campo">
        <label for="tipo">Tipo</label>
        <select id="tipo" name="tipo" required>
          <option value="casa" <%= imovel.getTipo() == TipoImovel.CASA ? "selected" : "" %>>Casa</option>
          <option value="apartamento" <%= imovel.getTipo() == TipoImovel.APARTAMENTO ? "selected" : "" %>>Apartamento</option>
          <option value="terreno" <%= imovel.getTipo() == TipoImovel.TERRENO ? "selected" : "" %>>Terreno</option>
          <option value="comercial" <%= imovel.getTipo() == TipoImovel.COMERCIAL ? "selected" : "" %>>Imóvel comercial</option>
          <option value="rural" <%= imovel.getTipo() == TipoImovel.RURAL ? "selected" : "" %>>Imóvel rural</option>
        </select>
      </div>
      <div class="filtros__campo">
        <label for="finalidade">Negócio</label>
        <select id="finalidade" name="finalidade" required>
          <option value="venda" <%= imovel.getFinalidade() == Finalidade.VENDA ? "selected" : "" %>>Venda</option>
          <option value="aluguel" <%= imovel.getFinalidade() == Finalidade.ALUGUEL ? "selected" : "" %>>Aluguel</option>
        </select>
      </div>
      <div class="filtros__campo">
        <label for="preco">Preço</label>
        <input type="text" id="preco" name="preco" data-mascara="moeda" required value="<%= imovel.getPreco() %>">
      </div>
      <div class="filtros__campo filtros__campo--largo">
        <label for="areaM2">Área</label>
        <input type="text" id="areaM2" name="areaM2" data-mascara="area" value="<%= imovel.getAreaM2() %>">
      </div>
      <div class="filtros__campo">
        <label for="quartos">Quartos</label>
        <input type="number" id="quartos" name="quartos" min="0" value="<%= imovel.getQuartos() %>">
      </div>
      <div class="filtros__campo">
        <label for="banheiros">Banheiros</label>
        <input type="number" id="banheiros" name="banheiros" min="0" value="<%= imovel.getBanheiros() %>">
      </div>
    </div>

    <div class="filtros__campo" style="margin-top:16px;">
      <label for="descricao">Descrição</label>
      <textarea id="descricao" name="descricao" rows="4" maxlength="1000"
        style="width:100%;font-family:var(--font-sans);font-size:15px;color:var(--text-primary);background:var(--surface-page);border:1px solid var(--border-subtle);border-radius:var(--radius-sm);padding:12px 14px;resize:vertical;"><%= imovel.getDescricao() == null ? "" : util.Html.escapar(imovel.getDescricao()) %></textarea>
    </div>

    <h2 style="font-size:14px;margin:28px 0 14px;">Endereço</h2>
    <div class="filtros__grade filtros__grade--2col">
      <div class="filtros__campo filtros__campo--largo">
        <label for="endereco">Endereço</label>
        <input type="text" id="endereco" name="endereco" maxlength="255" value="<%= imovel.getEndereco() == null ? "" : util.Html.escapar(imovel.getEndereco()) %>">
      </div>
      <div class="filtros__campo">
        <label for="cidade">Cidade</label>
        <input type="text" id="cidade" name="cidade" maxlength="100" value="<%= imovel.getCidade() == null ? "" : util.Html.escapar(imovel.getCidade()) %>">
      </div>
      <div class="filtros__campo">
        <label for="estado">Estado</label>
        <input type="text" id="estado" name="estado" maxlength="2" value="<%= imovel.getEstado() == null ? "" : util.Html.escapar(imovel.getEstado()) %>">
      </div>
      <div class="filtros__campo filtros__campo--largo">
        <label for="cep">CEP</label>
        <input type="text" id="cep" name="cep" maxlength="9" value="<%= imovel.getCep() == null ? "" : util.Html.escapar(imovel.getCep()) %>">
      </div>
    </div>

    <h2 style="font-size:14px;margin:28px 0 4px;">Disponibilidade para visitas</h2>
    <div class="wizard-dias">
      <% for (DiaSemana dia : DiaSemana.values()) { %>
        <label class="wizard-dia">
          <input type="checkbox" name="diaSemana" value="<%= dia.name() %>" <%= diasMarcados.contains(dia.name()) ? "checked" : "" %>>
          <span><%= dia.getRotulo().substring(0, 3) %></span>
        </label>
      <% } %>
    </div>
    <div class="filtros__grade filtros__grade--2col" style="margin-top:14px;">
      <div class="filtros__campo">
        <label for="horaInicio">Das</label>
        <input type="time" id="horaInicio" name="horaInicio" value="<%= horaInicioAtual %>">
      </div>
      <div class="filtros__campo">
        <label for="horaFim">Até</label>
        <input type="time" id="horaFim" name="horaFim" value="<%= horaFimAtual %>">
      </div>
    </div>

    <button class="btn btn--primary btn--interactive" type="submit" style="width:100%;margin-top:24px;">
      <span class="btn__label">Salvar alterações</span>
      <span class="btn__reveal" aria-hidden="true">
        Salvar alterações
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
      </span>
      <span class="btn__dot" aria-hidden="true"></span>
    </button>
  </form>
  <% } %>
</main>

<script src="${pageContext.request.contextPath}/js/validacao.js?v=35"></script>
</body>
</html>
