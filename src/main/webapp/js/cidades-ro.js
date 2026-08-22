/* Autocomplete de cidade restrito aos municípios de Rondônia (o estado onde
 * a Habittar atua) — lista vinda da API pública do IBGE (dados oficiais,
 * sem chave). Busca só uma vez e guarda em memória: como são só 52
 * municípios, filtrar localmente a cada tecla é instantâneo e não depende
 * de rede depois do primeiro carregamento.
 *
 * Compartilhado entre a busca da landing (hero) e a busca do catálogo —
 * ver js/habittar.js e js/catalogo.js.
 */
window.HabittarCidadesRO = (function () {
  "use strict";

  var municipiosRO = null;
  var carregandoMunicipios = null;

  function normalizarTexto(texto) {
    return texto
      .toLowerCase()
      .normalize("NFD")
      .replace(/[̀-ͯ]/g, "");
  }

  function carregarMunicipiosRO() {
    if (municipiosRO) return Promise.resolve(municipiosRO);
    if (carregandoMunicipios) return carregandoMunicipios;

    carregandoMunicipios = fetch("https://servicodados.ibge.gov.br/api/v1/localidades/estados/RO/municipios")
      .then(function (resposta) { return resposta.ok ? resposta.json() : []; })
      .then(function (lista) {
        municipiosRO = (lista || [])
          .map(function (municipio) { return municipio.nome; })
          .sort(function (a, b) { return a.localeCompare(b, "pt-BR"); });
        return municipiosRO;
      })
      .catch(function () {
        // Sem internet ou API do IBGE fora do ar: a busca continua
        // funcionando por texto livre, só sem sugestões.
        municipiosRO = [];
        return municipiosRO;
      });
    return carregandoMunicipios;
  }

  /**
   * Liga o autocomplete num campo de texto existente.
   *
   * @param campo   input de texto onde o usuário digita a cidade
   * @param lista   <ul> vazia (hidden) onde as sugestões são renderizadas
   * @param wrap    elemento que, ao perder clique fora dele, fecha a lista
   *                (opcional — usa o próprio campo se não informado)
   */
  function ligar(campo, lista, wrap) {
    if (!campo || !lista) return;
    wrap = wrap || campo;

    function fecharSugestoes() {
      lista.hidden = true;
      lista.innerHTML = "";
    }

    function renderizarSugestoes(cidades) {
      lista.innerHTML = "";
      if (!cidades.length) {
        fecharSugestoes();
        return;
      }
      cidades.slice(0, 8).forEach(function (nome) {
        var li = document.createElement("li");
        li.innerHTML = "<span>" + nome + "</span><span class=\"micro\">Rondônia — RO</span>";
        li.addEventListener("click", function () {
          campo.value = nome;
          fecharSugestoes();
          campo.focus();
        });
        lista.appendChild(li);
      });
      lista.hidden = false;
    }

    function mostrarSugestoesParaTermo(termoDigitado) {
      carregarMunicipiosRO().then(function (cidades) {
        var termo = normalizarTexto(termoDigitado.trim());
        var filtradas = !termo
          ? cidades
          : cidades.filter(function (cidade) { return normalizarTexto(cidade).indexOf(termo) !== -1; });
        renderizarSugestoes(filtradas);
      });
    }

    campo.addEventListener("focus", function () {
      mostrarSugestoesParaTermo(campo.value);
    });
    campo.addEventListener("input", function () {
      mostrarSugestoesParaTermo(campo.value);
    });
    document.addEventListener("click", function (e) {
      if (!wrap.contains(e.target)) fecharSugestoes();
    });
    campo.addEventListener("keydown", function (e) {
      if (e.key === "Escape") fecharSugestoes();
    });
  }

  return { ligar: ligar };
})();
