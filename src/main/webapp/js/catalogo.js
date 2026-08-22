(function () {
  "use strict";

  /* Autocomplete de cidade na barra de busca do catálogo — mesma lógica
     (e mesma lista, restrita a Rondônia) da busca da landing, ver
     js/cidades-ro.js. */
  var campoLocalizacaoCatalogo = document.getElementById("campoLocalizacaoCatalogo");
  var sugestoesLocalizacaoCatalogo = document.getElementById("sugestoesLocalizacaoCatalogo");
  if (campoLocalizacaoCatalogo && sugestoesLocalizacaoCatalogo && window.HabittarCidadesRO) {
    window.HabittarCidadesRO.ligar(
      campoLocalizacaoCatalogo,
      sugestoesLocalizacaoCatalogo,
      campoLocalizacaoCatalogo.closest(".catalogo-busca__campo")
    );
  }

  /* ------------------------------------------------------------------
     Item 2.1 — card inteiro clicável (menos o botão "salvar", que tem
     seu próprio form/clique e não deve navegar).
     ------------------------------------------------------------------ */
  document.querySelectorAll(".card--clicavel").forEach(function (card) {
    var destino = card.dataset.href;
    if (!destino) return;

    // Com o comparador de imóveis ativo, o card inteiro vira uma área de
    // seleção — clicar nele não deve navegar pro detalhe (só o botão
    // "Selecionar imóvel" reage, ver a seção do comparador mais abaixo).
    var emModoComparador = function () {
      var grade = card.closest(".catalogo__grade");
      return grade && grade.classList.contains("modo-comparador");
    };

    card.addEventListener("click", function (evento) {
      if (evento.target.closest(".card__salvar") || evento.target.closest(".card__comparar-btn")) return;
      if (emModoComparador()) return;
      window.location.href = destino;
    });
    card.addEventListener("keydown", function (evento) {
      if (evento.target.closest(".card__salvar") || evento.target.closest(".card__comparar-btn")) return;
      if (emModoComparador()) return;
      if (evento.key === "Enter" || evento.key === " ") {
        evento.preventDefault();
        window.location.href = destino;
      }
    });
  });

  /* ------------------------------------------------------------------
     Item x — a barra de filtros busca sozinha ao mudar qualquer campo,
     sem precisar clicar em "Buscar imóveis". Rádios/pílulas disparam na
     hora; campos de texto (localização, área, preço) esperam uma pausa
     de digitação antes de enviar, pra não recarregar a cada tecla.
     ------------------------------------------------------------------ */
  var formFiltros = document.getElementById("formFiltros");
  if (formFiltros) {
    var enviar = function () {
      formFiltros.requestSubmit
        ? formFiltros.requestSubmit()
        : formFiltros.submit();
    };

    formFiltros.querySelectorAll('input[type="radio"]').forEach(function (input) {
      input.addEventListener("change", enviar);
    });

    var timeoutBusca = null;
    var enviarComPausa = function () {
      window.clearTimeout(timeoutBusca);
      timeoutBusca = window.setTimeout(enviar, 600);
    };

    var campoCidade = formFiltros.querySelector('input[name="cidade"]');
    var campoEstado = formFiltros.querySelector('input[name="estado"]');
    if (campoCidade) campoCidade.addEventListener("input", enviarComPausa);
    if (campoEstado) campoEstado.addEventListener("input", enviarComPausa);

    var campoArea = document.getElementById("campoArea");
    if (campoArea) campoArea.addEventListener("input", enviarComPausa);

    var precoMinimoExibido = document.getElementById("precoMinimoExibido");
    var precoMaximoExibido = document.getElementById("precoMaximoExibido");
    if (precoMinimoExibido) precoMinimoExibido.addEventListener("input", enviarComPausa);
    if (precoMaximoExibido) precoMaximoExibido.addEventListener("input", enviarComPausa);
  }

  /* ------------------------------------------------------------------
     Item 2.5 — área mínima: só dígitos (o "m²" ao lado é fixo no HTML,
     não depende mais de ter algo digitado).
     ------------------------------------------------------------------ */
  var campoArea = document.getElementById("campoArea");
  if (campoArea) {
    campoArea.addEventListener("input", function () {
      campoArea.value = campoArea.value.replace(/[^\d]/g, "");
    });
  }

  /* ------------------------------------------------------------------
     Item 2.6 — preço: "R$" fixo (já é HTML estático) + formatação de
     milhar automática enquanto digita. O valor visível fica só com os
     dígitos formatados; o valor numérico "cru" (sem pontuação) vai pro
     input escondido que realmente é enviado no formulário.
     ------------------------------------------------------------------ */
  function configurarCampoPreco(idExibido, idOculto) {
    var exibido = document.getElementById(idExibido);
    var oculto = document.getElementById(idOculto);
    if (!exibido || !oculto) return;

    var formatar = function () {
      var digitos = exibido.value.replace(/\D/g, "");
      oculto.value = digitos;
      exibido.value = digitos ? new Intl.NumberFormat("pt-BR").format(Number(digitos)) : "";
    };

    // Pré-preenche o campo visível a partir do valor já salvo no filtro
    if (oculto.value) {
      exibido.value = new Intl.NumberFormat("pt-BR").format(Number(oculto.value));
    }

    exibido.addEventListener("input", formatar);
  }
  configurarCampoPreco("precoMinimoExibido", "precoMinimo");
  configurarCampoPreco("precoMaximoExibido", "precoMaximo");

  /* ------------------------------------------------------------------
     Comparador de imóveis: modo de seleção nos cards (até 3) + modal em
     2 passos (escolher quais informações comparar → ver a comparação).
     Tudo client-side: os dados de cada imóvel já vêm embutidos no card
     num <script type="application/json">, sem chamada nenhuma ao servidor.
     ------------------------------------------------------------------ */
  (function () {
    var grade = document.querySelector(".catalogo__grade");
    var btnAbrir = document.getElementById("btnAbrirComparador");
    var banner = document.getElementById("bannerComparador");
    var btnCancelar = document.getElementById("btnCancelarComparador");
    var btnCompararAgora = document.getElementById("btnCompararAgora");
    var contador = document.getElementById("contadorComparador");
    var modal = document.getElementById("modalComparador");
    if (!grade || !btnAbrir || !modal) return;

    var LIMITE = 3;
    var selecionados = []; // ids (string) na ordem em que foram clicados

    function dadosDoCard(id) {
      var card = grade.querySelector('[data-imovel-id="' + id + '"]');
      var script = card && card.querySelector(".dados-comparador");
      if (!script) return null;
      try {
        return JSON.parse(script.textContent);
      } catch (e) {
        return null;
      }
    }

    function atualizarContador() {
      contador.textContent = selecionados.length + " de " + LIMITE + " selecionados";
      btnCompararAgora.disabled = selecionados.length < 2;
    }

    function atualizarBotoesCards() {
      grade.querySelectorAll("[data-comparar-btn]").forEach(function (botao) {
        var id = botao.dataset.imovelId;
        var ativo = selecionados.indexOf(id) !== -1;
        botao.classList.toggle("is-selecionado", ativo);
        botao.textContent = ativo ? "Selecionado" : "Selecionar imóvel";
        botao.disabled = !ativo && selecionados.length >= LIMITE;
      });
    }

    function entrarModoComparador() {
      grade.classList.add("modo-comparador");
      banner.hidden = false;
      btnAbrir.hidden = true;
    }

    function sairModoComparador() {
      grade.classList.remove("modo-comparador");
      banner.hidden = true;
      btnAbrir.hidden = false;
      selecionados = [];
      atualizarContador();
      atualizarBotoesCards();
    }

    btnAbrir.addEventListener("click", entrarModoComparador);
    btnCancelar.addEventListener("click", sairModoComparador);

    grade.addEventListener("click", function (evento) {
      var botao = evento.target.closest("[data-comparar-btn]");
      if (!botao) return;
      evento.preventDefault();
      evento.stopPropagation();

      var id = botao.dataset.imovelId;
      var indice = selecionados.indexOf(id);
      if (indice !== -1) {
        selecionados.splice(indice, 1);
      } else if (selecionados.length < LIMITE) {
        selecionados.push(id);
      }
      atualizarContador();
      atualizarBotoesCards();
    });

    // --- Passo 1: quais informações comparar ---
    var passoCampos = document.getElementById("passoCamposComparador");
    var passoResultado = document.getElementById("passoResultadoComparador");
    var camposCheckboxes = Array.prototype.slice.call(modal.querySelectorAll(".campo-comparador"));
    var verComparacaoBtn = document.getElementById("verComparacao");
    var tabela = document.getElementById("tabelaComparador");

    function algumCampoMarcado() {
      return camposCheckboxes.some(function (cb) { return cb.checked; });
    }
    function atualizarBotaoVerComparacao() {
      verComparacaoBtn.disabled = !algumCampoMarcado();
    }
    camposCheckboxes.forEach(function (cb) {
      cb.addEventListener("change", atualizarBotaoVerComparacao);
    });

    btnCompararAgora.addEventListener("click", function () {
      passoCampos.hidden = false;
      passoResultado.hidden = true;
      atualizarBotaoVerComparacao();
      modal.showModal();
    });

    document.getElementById("fecharModalComparador").addEventListener("click", function () { modal.close(); });
    document.getElementById("cancelarCamposComparador").addEventListener("click", function () { modal.close(); });
    document.getElementById("fecharComparacao").addEventListener("click", function () { modal.close(); });
    document.getElementById("voltarCamposComparador").addEventListener("click", function () {
      passoResultado.hidden = true;
      passoCampos.hidden = false;
    });

    // --- Passo 2: monta a comparação lado a lado ---
    // im.titulo/endereco/descricao vêm de texto livre (cadastro do imóvel),
    // e viram innerHTML aqui embaixo — precisam ser escapados como HTML,
    // não só como JSON (o escape do lado do servidor só cobre a string
    // JSON em si, ver util.Json).
    function escaparHtml(texto) {
      var div = document.createElement("div");
      div.textContent = texto == null ? "" : String(texto);
      return div.innerHTML;
    }

    function formatarMoeda(valor) {
      return valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
    }
    function formatarArea(valor) {
      return valor.toLocaleString("pt-BR", { maximumFractionDigits: 1 }) + " m²";
    }

    function linhaFoto(imoveis) {
      var html = '<div class="comparador-linha comparador-linha--foto"><div class="comparador-linha__rotulo"></div>';
      imoveis.forEach(function (im) {
        html += '<div class="comparador-coluna">'
          + '<img src="' + escaparHtml(im.foto) + '" alt="">'
          + '<p class="comparador-coluna__titulo">' + escaparHtml(im.titulo) + '</p>'
          + '<p class="micro">' + escaparHtml(im.endereco) + '</p>'
          + '</div>';
      });
      return html + '</div>';
    }

    // formatarDiffMaior/Menor recebem a diferença já em módulo (positiva) e
    // devolvem o texto completo da linha, ex.: "R$ 1.000,00 mais caro".
    function linhaNumerica(rotulo, imoveis, valorDe, formatarValor, formatarDiffMaior, formatarDiffMenor) {
      var referencia = valorDe(imoveis[0]);
      var html = '<div class="comparador-linha"><div class="comparador-linha__rotulo">' + rotulo + '</div>';
      imoveis.forEach(function (im, indice) {
        var valor = valorDe(im);
        html += '<div class="comparador-coluna"><div class="comparador-valor">' + formatarValor(valor) + '</div>';
        if (indice > 0) {
          var diferenca = valor - referencia;
          if (diferenca > 0) {
            html += '<div class="comparador-diff comparador-diff--maior">' + formatarDiffMaior(diferenca) + '</div>';
          } else if (diferenca < 0) {
            html += '<div class="comparador-diff comparador-diff--menor">' + formatarDiffMenor(Math.abs(diferenca)) + '</div>';
          }
        }
        html += '</div>';
      });
      return html + '</div>';
    }

    function linhaTexto(rotulo, imoveis, valorDe) {
      var html = '<div class="comparador-linha comparador-linha--texto"><div class="comparador-linha__rotulo">' + rotulo + '</div>';
      imoveis.forEach(function (im) {
        html += '<div class="comparador-coluna"><p class="comparador-texto">' + escaparHtml(valorDe(im)) + '</p></div>';
      });
      return html + '</div>';
    }

    function pluralComContagem(quantidade, singular, plural) {
      return quantidade + " " + (quantidade === 1 ? singular : plural);
    }

    verComparacaoBtn.addEventListener("click", function () {
      var imoveis = selecionados.map(dadosDoCard).filter(Boolean);
      if (imoveis.length < 2) return;

      var camposSelecionados = camposCheckboxes.filter(function (cb) { return cb.checked; })
        .map(function (cb) { return cb.value; });

      var html = '<div class="comparador-grid" style="--colunas:' + imoveis.length + '">';
      html += linhaFoto(imoveis);

      if (camposSelecionados.indexOf("preco") !== -1) {
        html += linhaNumerica("Valor", imoveis, function (im) { return im.precoValor; }, formatarMoeda,
          function (diferenca) { return formatarMoeda(diferenca) + " mais caro"; },
          function (diferenca) { return formatarMoeda(diferenca) + " mais barato"; });
      }
      if (camposSelecionados.indexOf("quartos") !== -1) {
        html += linhaNumerica("Quartos", imoveis, function (im) { return im.quartos; }, function (v) { return v; },
          function (diferenca) { return pluralComContagem(diferenca, "quarto a mais", "quartos a mais"); },
          function (diferenca) { return pluralComContagem(diferenca, "quarto a menos", "quartos a menos"); });
      }
      if (camposSelecionados.indexOf("banheiros") !== -1) {
        html += linhaNumerica("Banheiros", imoveis, function (im) { return im.banheiros; }, function (v) { return v; },
          function (diferenca) { return pluralComContagem(diferenca, "banheiro a mais", "banheiros a mais"); },
          function (diferenca) { return pluralComContagem(diferenca, "banheiro a menos", "banheiros a menos"); });
      }
      if (camposSelecionados.indexOf("area") !== -1) {
        html += linhaNumerica("Área", imoveis, function (im) { return im.areaValor; }, formatarArea,
          function (diferenca) { return formatarArea(diferenca) + " maior"; },
          function (diferenca) { return formatarArea(diferenca) + " menor"; });
      }
      if (camposSelecionados.indexOf("descricao") !== -1) {
        html += linhaTexto("Descrição", imoveis, function (im) { return im.descricao; });
      }

      html += "</div>";
      tabela.innerHTML = html;
      passoCampos.hidden = true;
      passoResultado.hidden = false;
    });
  })();
})();
