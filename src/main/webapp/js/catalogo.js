(function () {
  "use strict";

  /* ------------------------------------------------------------------
     Item 2.1 — card inteiro clicável (menos o botão "salvar", que tem
     seu próprio form/clique e não deve navegar).
     ------------------------------------------------------------------ */
  document.querySelectorAll(".card--clicavel").forEach(function (card) {
    var destino = card.dataset.href;
    if (!destino) return;

    card.addEventListener("click", function (evento) {
      if (evento.target.closest(".card__salvar")) return;
      window.location.href = destino;
    });
    card.addEventListener("keydown", function (evento) {
      if (evento.target.closest(".card__salvar")) return;
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
     Item 2.5 — área mínima: mostra "m²" ao lado assim que o usuário
     digita algo, sem exigir clique fora do campo.
     ------------------------------------------------------------------ */
  var campoArea = document.getElementById("campoArea");
  var sufixoArea = document.getElementById("sufixoArea");
  if (campoArea && sufixoArea) {
    var atualizarSufixoArea = function () {
      campoArea.value = campoArea.value.replace(/[^\d]/g, "");
      sufixoArea.hidden = campoArea.value.length === 0;
    };
    campoArea.addEventListener("input", atualizarSufixoArea);
    atualizarSufixoArea();
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
})();
