<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.RascunhoAnuncio" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Seus dados | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=18">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=18">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=18">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wizard.css?v=18">
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
  <% request.setAttribute("etapaAtual", 3); %>
  <jsp:include page="/WEB-INF/jsp/fragmentos/progressbar.jsp" />

  <p class="eyebrow wizard-etapa__eyebrow">Etapa 3 de 5</p>
  <h1 class="display wizard-etapa__titulo">Dados do anunciante</h1>

  <p class="alerta alerta-erro" role="alert">${erro}</p>

  <%
    RascunhoAnuncio rascunho = (RascunhoAnuncio) request.getAttribute("rascunho");
    boolean enderecoIgual = rascunho.isEnderecoAnuncianteIgualImovel();
  %>

  <form method="post" action="${pageContext.request.contextPath}/anunciar/etapa3" id="formEtapa3" novalidate>
    <input type="hidden" name="csrf" value="${csrf}">

    <h2 style="font-size:14px;margin:0 0 14px;">Quem está anunciando</h2>
    <div class="filtros__grade filtros__grade--2col">
      <div class="filtros__campo filtros__campo--largo">
        <label for="nomeAnunciante">Nome completo</label>
        <input type="text" id="nomeAnunciante" name="nomeAnunciante" data-validar="nome"
          value="<%= rascunho.getNomeAnunciante() == null ? "" : util.Html.escapar(rascunho.getNomeAnunciante()) %>" required>
        <span class="campo-erro" id="erro-nomeAnunciante"></span>
      </div>
      <div class="filtros__campo">
        <label for="cpfCnpjAnunciante">CPF ou CNPJ</label>
        <input type="text" id="cpfCnpjAnunciante" name="cpfCnpjAnunciante" placeholder="Insira seu CPF/CNPJ" inputmode="numeric" maxlength="18"
          data-mascara="cpfCnpj" data-validar="cpfCnpj"
          value="<%= rascunho.getCpfCnpjAnunciante() == null ? "" : util.Html.escapar(rascunho.getCpfCnpjAnunciante()) %>" required>
        <span class="campo-erro" id="erro-cpfCnpjAnunciante"></span>
      </div>
      <div class="filtros__campo">
        <label for="celularAnunciante">Celular</label>
        <input type="text" id="celularAnunciante" name="celularAnunciante" placeholder="(11) 90000-0000" inputmode="numeric" maxlength="15"
          data-mascara="telefone" data-validar="telefoneObrigatorio"
          value="<%= rascunho.getCelularAnunciante() == null ? "" : util.Html.escapar(rascunho.getCelularAnunciante()) %>" required>
        <span class="campo-erro" id="erro-celularAnunciante"></span>
      </div>
    </div>

    <h2 style="font-size:14px;margin:28px 0 4px;">Endereço do anunciante</h2>
    <label class="wizard-checkbox" for="enderecoIgualImovel">
      <input type="checkbox" id="enderecoIgualImovel" name="enderecoIgualImovel" <%= enderecoIgual ? "checked" : "" %>>
      Utilizar o mesmo endereço do imóvel
    </label>

    <div id="blocoEnderecoAnunciante" class="<%= enderecoIgual ? "wizard-bloco--desabilitado" : "" %>">
      <div class="wizard-cep">
        <div class="filtros__campo">
          <label for="cepAnunciante">CEP</label>
          <input type="text" id="cepAnunciante" name="cepAnunciante" placeholder="00000-000" inputmode="numeric" maxlength="9"
            data-mascara="cep" value="<%= rascunho.getCepAnunciante() == null ? "" : rascunho.getCepAnunciante() %>" <%= enderecoIgual ? "disabled" : "" %>>
          <p class="wizard-cep__status" id="cepStatusAnunciante"></p>
        </div>
      </div>
      <div class="filtros__grade filtros__grade--2col">
        <div class="filtros__campo filtros__campo--largo">
          <label for="enderecoAnunciante">Rua</label>
          <input type="text" id="enderecoAnunciante" name="enderecoAnunciante"
            value="<%= rascunho.getEnderecoAnunciante() == null ? "" : util.Html.escapar(rascunho.getEnderecoAnunciante()) %>" <%= enderecoIgual ? "disabled" : "" %>>
        </div>
        <div class="filtros__campo">
          <label for="numeroAnunciante">Número</label>
          <input type="text" id="numeroAnunciante" name="numeroAnunciante"
            value="<%= rascunho.getNumeroAnunciante() == null ? "" : util.Html.escapar(rascunho.getNumeroAnunciante()) %>" <%= enderecoIgual ? "disabled" : "" %>>
        </div>
        <div class="filtros__campo">
          <label for="bairroAnunciante">Bairro</label>
          <input type="text" id="bairroAnunciante" name="bairroAnunciante"
            value="<%= rascunho.getBairroAnunciante() == null ? "" : util.Html.escapar(rascunho.getBairroAnunciante()) %>" <%= enderecoIgual ? "disabled" : "" %>>
        </div>
        <div class="filtros__campo">
          <label for="cidadeAnunciante">Cidade</label>
          <input type="text" id="cidadeAnunciante" name="cidadeAnunciante" readonly
            value="<%= rascunho.getCidadeAnunciante() == null ? "" : util.Html.escapar(rascunho.getCidadeAnunciante()) %>" <%= enderecoIgual ? "disabled" : "" %>>
        </div>
        <div class="filtros__campo">
          <label for="estadoAnunciante">Estado</label>
          <input type="text" id="estadoAnunciante" name="estadoAnunciante" maxlength="2" readonly
            value="<%= rascunho.getEstadoAnunciante() == null ? "" : util.Html.escapar(rascunho.getEstadoAnunciante()) %>" <%= enderecoIgual ? "disabled" : "" %>>
        </div>
      </div>
    </div>

    <div class="wizard-acoes">
      <a class="btn btn--secondary" href="${pageContext.request.contextPath}/anunciar/etapa2">Voltar</a>
      <button class="btn btn--primary btn--interactive" type="submit">
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

<script src="${pageContext.request.contextPath}/js/validacao.js?v=18"></script>
<script src="${pageContext.request.contextPath}/js/anuncio-wizard.js?v=18"></script>
</body>
</html>
