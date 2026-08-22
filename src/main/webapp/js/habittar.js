/* Habittar — interações da landing page (JS puro, sem libs) */
(function () {
  "use strict";

  /* Header translúcido a partir de 40px de scroll */
  var nav = document.querySelector(".nav");
  var grids = [].slice.call(document.querySelectorAll(".map-grid"));
  var ticking = false;

  function onScroll() {
    if (ticking) return;
    ticking = true;
    window.requestAnimationFrame(function () {
      var y = window.pageYOffset || document.documentElement.scrollTop;
      /* Páginas com navFixa (fundo sólido já renderizado pelo servidor,
         data-fixa="true") não devem ter a classe removida quando a
         página carrega no topo — antes esse toggle incondicional
         desfazia o fundo sólido de todas as páginas internas assim que
         o JS rodava, mesmo com scroll 0. */
      if (nav && nav.dataset.fixa !== "true") nav.classList.toggle("is-scrolled", y > 40);
      /* Parallax sutil do grid cartográfico (máx. 8px) */
      var offset = Math.min(8, y * 0.02);
      for (var i = 0; i < grids.length; i++) {
        grids[i].style.transform = "translateY(" + offset + "px)";
      }
      ticking = false;
    });
  }
  window.addEventListener("scroll", onScroll, { passive: true });
  onScroll();

  /* Comprimento real dos paths para o efeito de desenho */
  [].forEach.call(document.querySelectorAll(".draw"), function (path) {
    try {
      var len = Math.ceil(path.getTotalLength());
      path.style.setProperty("--len", len);
    } catch (e) {
      /* elemento sem geometria — ignora */
    }
  });

  /* Revelação por viewport */
  var observed = document.querySelectorAll(".reveal, .scene-scroll, .cta, [data-counters]");
  if ("IntersectionObserver" in window) {
    var io = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry) {
          if (!entry.isIntersecting) return;
          entry.target.classList.add("in-view");
          if (entry.target.hasAttribute("data-counters")) startCounters(entry.target);
          io.unobserve(entry.target);
        });
      },
      { threshold: 0.25 }
    );
    [].forEach.call(observed, function (el) {
      io.observe(el);
    });
  } else {
    [].forEach.call(observed, function (el) {
      el.classList.add("in-view");
    });
    [].forEach.call(document.querySelectorAll("[data-counters]"), startCounters);
  }

  /* Contadores da prova social: 0 -> valor final em 1.4s (ease-out) */
  function startCounters(scope) {
    [].forEach.call(scope.querySelectorAll("[data-count]"), function (el) {
      var target = parseFloat(el.getAttribute("data-count"));
      var suffix = el.getAttribute("data-suffix") || "";
      var start = null;
      var dur = 1400;
      function step(ts) {
        if (start === null) start = ts;
        var p = Math.min(1, (ts - start) / dur);
        var eased = 1 - Math.pow(1 - p, 3);
        var value = Math.round(target * eased);
        el.textContent = value.toLocaleString("pt-BR") + suffix;
        if (p < 1) window.requestAnimationFrame(step);
      }
      window.requestAnimationFrame(step);
    });
  }

  /* Linha do tempo do diferencial (c): traço laranja ligado ao scroll */
  var timeline = document.getElementById("timelineProgress");
  var timelineBlock = document.getElementById("sceneTimeline");
  if (timeline && timelineBlock) {
    var tlLen = 0;
    try {
      tlLen = timeline.getTotalLength();
    } catch (e) {}
    timeline.style.strokeDasharray = tlLen;
    var tlTick = false;
    var updateTimeline = function () {
      if (tlTick) return;
      tlTick = true;
      window.requestAnimationFrame(function () {
        var r = timelineBlock.getBoundingClientRect();
        var p = 1 - (r.bottom - window.innerHeight * 0.4) / (r.height + window.innerHeight * 0.4);
        p = Math.max(0, Math.min(1, p));
        timeline.style.strokeDashoffset = tlLen * (1 - p);
        tlTick = false;
      });
    };
    window.addEventListener("scroll", updateTimeline, { passive: true });
    window.addEventListener("resize", updateTimeline);
    updateTimeline();
  }

  /* Segmento do formulário de busca (comprar / alugar / vender) */
  var segment = document.querySelector(".segment");
  if (segment) {
    segment.addEventListener("click", function (e) {
      var btn = e.target.closest("button");
      if (!btn) return;
      [].forEach.call(segment.querySelectorAll("button"), function (b) {
        b.classList.toggle("is-active", b === btn);
        b.setAttribute("aria-pressed", b === btn ? "true" : "false");
      });
      var hidden = document.getElementById("finalidade");
      if (hidden) hidden.value = btn.getAttribute("data-value");
    });
  }

  /* "Imóvel novo" (data-value continua "vender") não é um filtro do
     catálogo — leva para o bloco de anúncio */
  var formBusca = document.getElementById("formBuscaHero");
  if (formBusca) {
    formBusca.addEventListener("submit", function (e) {
      var hidden = document.getElementById("finalidade");
      if (hidden && hidden.value === "vender") {
        e.preventDefault();
        var alvo = document.getElementById("anunciar");
        if (alvo) alvo.scrollIntoView({ behavior: "smooth" });
      }
    });
  }

  /* Queda do pin do hero ao carregar */
  var hero = document.querySelector(".hero .scene");
  if (hero) window.requestAnimationFrame(function () {
    hero.classList.add("pin-drop");
  });

  /* Autocomplete de cidade na busca da landing, restrito aos municípios de
     Rondônia — lógica compartilhada em js/cidades-ro.js (também usada pela
     busca do catálogo, ver js/catalogo.js). */
  var campoLocalizacao = document.getElementById("campoLocalizacao");
  var listaSugestoes = document.getElementById("sugestoesLocalizacao");
  if (campoLocalizacao && listaSugestoes && window.HabittarCidadesRO) {
    window.HabittarCidadesRO.ligar(campoLocalizacao, listaSugestoes, campoLocalizacao.closest(".hero__filtro-input-wrap"));
  }

  /* Autocomplete de bairro na busca da landing, restrito por cidade quando
     o campo Cidade já está preenchido. Diferente da cidade, não existe uma
     API pública com a lista fechada de bairros do Brasil — usa o Nominatim
     (OpenStreetMap), embutindo a cidade escolhida (ou "Rondônia" como
     fallback) na busca, e filtra na resposta só os resultados cuja cidade
     bate com a escolhida (ou, sem cidade escolhida, só os de Rondônia). */
  var campoBairro = document.getElementById("campoBairro");
  var listaSugestoesBairro = document.getElementById("sugestoesBairro");
  if (campoBairro && listaSugestoesBairro) {
    var timeoutBuscaBairro = null;
    var controladorBairroAtual = null;

    var normalizarTextoBairro = function (texto) {
      return texto
        .toLowerCase()
        .normalize("NFD")
        .replace(/[̀-ͯ]/g, "");
    };

    var fecharSugestoesBairro = function () {
      listaSugestoesBairro.hidden = true;
      listaSugestoesBairro.innerHTML = "";
    };

    var buscarBairros = function (termo) {
      if (controladorBairroAtual) controladorBairroAtual.abort();
      controladorBairroAtual = new AbortController();

      var cidadeEscolhida = campoLocalizacao ? campoLocalizacao.value.trim() : "";
      var qualificador = cidadeEscolhida ? cidadeEscolhida + ", Rondônia" : "Rondônia";
      var url = "https://nominatim.openstreetmap.org/search?format=json&addressdetails=1&limit=8"
        + "&countrycodes=br&accept-language=pt-BR&q=" + encodeURIComponent(termo + ", " + qualificador);

      fetch(url, { signal: controladorBairroAtual.signal, headers: { "Accept": "application/json" } })
        .then(function (resposta) { return resposta.ok ? resposta.json() : []; })
        .then(function (resultados) {
          listaSugestoesBairro.innerHTML = "";
          var vistos = {};
          (resultados || []).forEach(function (item) {
            var endereco = item.address || {};
            // Só interessa resultado que caiu mesmo dentro de Rondônia —
            // o Nominatim às vezes devolve algo de outro estado só por
            // coincidência de nome.
            if (endereco.state !== "Rondônia") return;

            var bairro = endereco.suburb || endereco.neighbourhood || endereco.city_district;
            if (!bairro) return;
            var cidade = endereco.city || endereco.town || endereco.municipality || "";

            // Com uma cidade escolhida no campo Cidade, só sugere bairros
            // que caem mesmo nela — sem isso o Nominatim às vezes traz
            // bairros de outro município de Rondônia com nome parecido.
            if (cidadeEscolhida
                && normalizarTextoBairro(cidade).indexOf(normalizarTextoBairro(cidadeEscolhida)) === -1) {
              return;
            }

            var chave = bairro + "|" + cidade;
            if (vistos[chave]) return;
            vistos[chave] = true;

            var li = document.createElement("li");
            li.innerHTML = "<span>" + bairro + "</span>"
              + (cidade ? "<span class=\"micro\">" + cidade + " — RO</span>" : "");
            li.addEventListener("click", function () {
              campoBairro.value = bairro;
              fecharSugestoesBairro();
              campoBairro.focus();
            });
            listaSugestoesBairro.appendChild(li);
          });
          listaSugestoesBairro.hidden = listaSugestoesBairro.children.length === 0;
        })
        .catch(function () {
          // Falha de rede ou requisição cancelada: a busca continua
          // funcionando por texto livre, só sem sugestões.
        });
    };

    campoBairro.addEventListener("input", function () {
      var termo = campoBairro.value.trim();
      window.clearTimeout(timeoutBuscaBairro);
      if (termo.length < 3) {
        fecharSugestoesBairro();
        return;
      }
      timeoutBuscaBairro = window.setTimeout(function () { buscarBairros(termo); }, 350);
    });

    var campoBairroWrap = campoBairro.closest(".hero__filtro-input-wrap");
    document.addEventListener("click", function (e) {
      if (campoBairroWrap && !campoBairroWrap.contains(e.target)) fecharSugestoesBairro();
    });
    campoBairro.addEventListener("keydown", function (e) {
      if (e.key === "Escape") fecharSugestoesBairro();
    });
  }

  /* "Valor total até" — campo visível formatado com separador de milhar +
     "R$" fixo (mesmo padrão de js/catalogo.js), com o valor "cru" indo pro
     input escondido que é o que realmente é enviado no formulário. Limite
     de 999.999.999 (9 dígitos). */
  var LIMITE_VALOR_MAXIMO = 999999999;
  var precoExibido = document.getElementById("precoMaximoHeroExibido");
  var precoOculto = document.getElementById("precoMaximoHero");
  if (precoExibido && precoOculto) {
    precoExibido.addEventListener("input", function () {
      var digitos = precoExibido.value.replace(/\D/g, "");
      var numero = digitos ? Math.min(Number(digitos), LIMITE_VALOR_MAXIMO) : null;
      precoOculto.value = numero || "";
      precoExibido.value = numero ? new Intl.NumberFormat("pt-BR").format(numero) : "";
    });
  }
})();