/* Calculadora de poder de compra — simulação ilustrativa, client-side.
 *
 * Metodologia (parâmetros de referência, sem vínculo com nenhuma
 * instituição financeira real):
 *   - a parcela do financiamento não passa de 30% da renda mensal;
 *   - taxa de juros de referência: 10,5% ao ano;
 *   - prazo de referência: 35 anos (420 meses), o teto usual do SFH;
 *   - entrada mínima exigida pelos bancos: 20% do valor do imóvel;
 *   - ITBI e outras taxas: cerca de 5% do valor do imóvel (custo à parte,
 *     não entra no financiamento).
 *
 * A partir da renda, calculamos o valor máximo financiável (pela
 * capacidade de pagamento da parcela). Se a entrada disponível já cobre os
 * 20% mínimos exigidos para esse financiamento, ela toda vira poder de
 * compra extra (financiamento + entrada). Se a entrada for menor que o
 * mínimo exigido, quem limita o valor do imóvel passa a ser a entrada, não
 * a renda — e avisamos quanto faltaria para aproveitar o crédito inteiro.
 */
(function () {
  "use strict";

  var PERCENTUAL_MAXIMO_PARCELA = 0.30;
  var TAXA_JUROS_ANUAL = 0.105;
  var PRAZO_MESES = 420;
  var PERCENTUAL_ENTRADA_MINIMA = 0.20;
  var PERCENTUAL_TAXAS = 0.05;

  var form = document.getElementById("formPoderCompra");
  if (!form) return;

  var campoRenda = document.getElementById("campoRenda");
  var campoEntrada = document.getElementById("campoEntrada");
  var resultado = document.getElementById("resultadoPoderCompra");
  var avisoEntradaMinima = document.getElementById("avisoEntradaMinima");
  var linkBuscar = document.getElementById("linkBuscarNoCatalogo");

  // Mesmo padrão de máscara de dinheiro do catálogo (ver
  // configurarCampoPreco em js/catalogo.js): os dígitos digitados são reais
  // inteiros, formatados com separador de milhar enquanto o usuário digita.
  function configurarCampoMoeda(campo) {
    campo.addEventListener("input", function () {
      var digitos = campo.value.replace(/\D/g, "");
      campo.value = digitos ? new Intl.NumberFormat("pt-BR").format(Number(digitos)) : "";
    });
  }
  configurarCampoMoeda(campoRenda);
  configurarCampoMoeda(campoEntrada);

  function valorNumerico(campo) {
    var digitos = campo.value.replace(/\D/g, "");
    return digitos ? Number(digitos) : 0;
  }

  function formatarMoeda(valor) {
    return valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
  }

  // Valor presente de uma série de parcelas iguais (fórmula padrão de
  // financiamento pela Tabela Price).
  function valorFinanciavelPelaRenda(rendaMensal) {
    var parcelaMaxima = rendaMensal * PERCENTUAL_MAXIMO_PARCELA;
    var taxaMensal = Math.pow(1 + TAXA_JUROS_ANUAL, 1 / 12) - 1;
    var fator = (1 - Math.pow(1 + taxaMensal, -PRAZO_MESES)) / taxaMensal;
    return parcelaMaxima * fator;
  }

  form.addEventListener("submit", function (evento) {
    evento.preventDefault();

    var renda = valorNumerico(campoRenda);
    var entradaDisponivel = valorNumerico(campoEntrada);
    if (renda <= 0) {
      campoRenda.focus();
      return;
    }

    var financiadoMaximoPelaRenda = valorFinanciavelPelaRenda(renda);
    var entradaNecessariaParaMaximo = financiadoMaximoPelaRenda * PERCENTUAL_ENTRADA_MINIMA / (1 - PERCENTUAL_ENTRADA_MINIMA);

    var valorFinanciado, entradaUsada;
    var faltaParaMaximoPotencial = 0;

    if (entradaDisponivel >= entradaNecessariaParaMaximo) {
      // Entrada dá e sobra: usa todo o crédito que a renda permite, e o
      // excedente da entrada vira poder de compra extra.
      valorFinanciado = financiadoMaximoPelaRenda;
      entradaUsada = entradaDisponivel;
    } else {
      // Entrada é o fator limitante: o imóvel não pode passar do ponto em
      // que essa entrada ainda representa os 20% mínimos exigidos.
      var totalPossivelComEssaEntrada = entradaDisponivel / PERCENTUAL_ENTRADA_MINIMA;
      entradaUsada = entradaDisponivel;
      valorFinanciado = totalPossivelComEssaEntrada - entradaDisponivel;
      faltaParaMaximoPotencial = entradaNecessariaParaMaximo - entradaDisponivel;
    }

    var valorImovel = entradaUsada + valorFinanciado;
    var taxas = valorImovel * PERCENTUAL_TAXAS;

    document.getElementById("valorPoderCompra").textContent = formatarMoeda(valorImovel);
    document.getElementById("valorEntradaUsada").textContent = formatarMoeda(entradaUsada);
    document.getElementById("valorFinanciado").textContent = formatarMoeda(valorFinanciado);
    document.getElementById("valorTaxas").textContent = formatarMoeda(taxas);

    if (faltaParaMaximoPotencial > 0.01) {
      avisoEntradaMinima.hidden = false;
      avisoEntradaMinima.textContent = "Para aproveitar todo o crédito que sua renda permite, você precisaria de mais "
        + formatarMoeda(faltaParaMaximoPotencial) + " de entrada.";
    } else {
      avisoEntradaMinima.hidden = true;
    }

    linkBuscar.href = linkBuscar.href.split("?")[0] + "?precoMaximo=" + Math.round(valorImovel);

    resultado.hidden = false;
    resultado.scrollIntoView({ behavior: "smooth", block: "start" });
  });
})();
