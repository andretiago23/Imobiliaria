/* Agendar visita: trocar o dia selecionado mostra só os horários daquele
 * dia — os das outras datas ficam escondidos (e com a seleção de horário
 * limpa, pra não submeter sem querer um slot de um dia diferente do
 * escolhido por último). */
(function () {
  "use strict";

  var form = document.getElementById("formVisita");
  if (!form) return;

  // Setas de navegação: rolam a lista de dias um "passo" (~3 pílulas) por
  // clique, pra frente ou pra trás.
  var listaDias = document.getElementById("listaDias");
  var setaAnterior = document.getElementById("setaDiasAnterior");
  var setaProxima = document.getElementById("setaDiasProxima");
  var passoRolagem = 3 * 66; // 3 pílulas (56px + 10px de gap) por clique
  if (listaDias && setaAnterior && setaProxima) {
    setaAnterior.addEventListener("click", function () {
      listaDias.scrollBy({ left: -passoRolagem, behavior: "smooth" });
    });
    setaProxima.addEventListener("click", function () {
      listaDias.scrollBy({ left: passoRolagem, behavior: "smooth" });
    });
  }

  var diasInputs = form.querySelectorAll(".visita-dia-input");
  diasInputs.forEach(function (input) {
    input.addEventListener("change", function () {
      form.querySelectorAll(".visita-horarios").forEach(function (bloco) {
        var ativo = bloco.id === input.dataset.alvo;
        bloco.hidden = !ativo;
        if (!ativo) {
          bloco.querySelectorAll('input[name="slot"]').forEach(function (radio) {
            radio.checked = false;
          });
        }
      });
    });
  });
})();
