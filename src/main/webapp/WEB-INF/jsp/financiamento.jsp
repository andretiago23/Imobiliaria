<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Calculadora de poder de compra | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tokens.css?v=60">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=60">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=60">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/financiamento.css?v=1">
</head>
<body>

<% request.setAttribute("navFixa", true); %>
<jsp:include page="/WEB-INF/jsp/fragmentos/navbar.jsp" />

<main class="app-main" style="max-width:720px;padding-top:56px;">
  <p class="eyebrow">Financiamento</p>
  <h1 class="display">Calculadora de <span class="hl">poder de compra</span></h1>
  <p class="lead" style="max-width:none;">
    Informe sua renda mensal e quanto você já tem guardado para dar de entrada — a calculadora estima
    o valor de imóvel que cabe no seu bolso, pra você direcionar sua busca no catálogo.
  </p>

  <form id="formPoderCompra" class="calc-form" autocomplete="off">
    <div class="calc-form__campo">
      <label for="campoRenda">Renda mensal</label>
      <span class="calc-form__moeda">
        <span class="calc-form__prefixo">R$</span>
        <input type="text" inputmode="numeric" id="campoRenda" placeholder="0,00">
      </span>
      <p class="micro">Considere sua renda total, sem descontos.</p>
    </div>

    <div class="calc-form__campo">
      <label for="campoEntrada">Entrada + FGTS</label>
      <span class="calc-form__moeda">
        <span class="calc-form__prefixo">R$</span>
        <input type="text" inputmode="numeric" id="campoEntrada" placeholder="0,00">
      </span>
      <p class="micro">Quanto você tem disponível para dar de entrada.</p>
    </div>

    <button type="submit" class="btn btn--primary" id="btnCalcularPoderCompra">Calcular</button>
  </form>

  <div id="resultadoPoderCompra" class="calc-resultado" hidden>
    <p class="micro">Seu potencial de compra é de um imóvel de até</p>
    <p class="calc-resultado__valor" id="valorPoderCompra">R$ 0,00</p>

    <p class="micro" style="margin-top:20px;">O valor é dividido em:</p>
    <div class="calc-resultado__linhas">
      <div class="calc-resultado__linha">
        <span>Entrada</span>
        <strong id="valorEntradaUsada">R$ 0,00</strong>
      </div>
      <div class="calc-resultado__linha">
        <span>Valor a financiar</span>
        <strong id="valorFinanciado">R$ 0,00</strong>
      </div>
      <div class="calc-resultado__linha">
        <span>ITBI e outras taxas</span>
        <strong id="valorTaxas">R$ 0,00</strong>
      </div>
    </div>

    <p id="avisoEntradaMinima" class="alerta" style="margin-top:16px;" hidden></p>

    <details class="calc-explicacao">
      <summary>Entenda o cálculo</summary>
      <p>
        A parcela do financiamento não pode comprometer mais que 30% da sua renda mensal — isso define o
        quanto o banco financiaria com base só na sua renda, considerando uma taxa de referência de 10,5% ao
        ano e prazo de até 35 anos (os parâmetros mais comuns no mercado brasileiro).
      </p>
      <p>
        Os bancos costumam exigir uma entrada de pelo menos 20% do valor do imóvel. Se sua entrada for menor
        que isso para o crédito calculado, o limite passa a ser sua entrada, não sua renda — por isso
        avisamos quanto faltaria para aproveitar todo o crédito liberado pela sua renda.
      </p>
      <p>
        Por fim, estimamos os custos com ITBI e outras taxas em cerca de 5% do valor do imóvel — um gasto
        adicional, que normalmente não entra no financiamento.
      </p>
      <p class="micro">
        Simulação ilustrativa, sem nenhum vínculo com instituição financeira — os valores reais dependem da
        sua análise de crédito.
      </p>
    </details>

    <a id="linkBuscarNoCatalogo" class="btn btn--secondary btn--interactive" style="margin-top:20px;width:100%;"
      href="${pageContext.request.contextPath}/inicio">
      <span class="btn__label">Buscar imóveis dentro desse valor</span>
      <span class="btn__reveal" aria-hidden="true">
        Buscar imóveis dentro desse valor
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
      </span>
      <span class="btn__dot" aria-hidden="true"></span>
    </a>
  </div>
</main>

<script src="${pageContext.request.contextPath}/js/financiamento.js?v=1"></script>
</body>
</html>
