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
      if (nav) nav.classList.toggle("is-scrolled", y > 40);
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

  /* Autocomplete de rua/bairro/cidade na busca da landing, via Nominatim
     (OpenStreetMap) — API pública, sem chave. Restrito ao Brasil e com
     debounce pra não disparar uma requisição a cada tecla. */
  var campoLocalizacao = document.getElementById("campoLocalizacao");
  var listaSugestoes = document.getElementById("sugestoesLocalizacao");
  if (campoLocalizacao && listaSugestoes) {
    var timeoutBusca = null;
    var controladorAtual = null;

    var fecharSugestoes = function () {
      listaSugestoes.hidden = true;
      listaSugestoes.innerHTML = "";
    };

    var buscarSugestoes = function (termo) {
      if (controladorAtual) controladorAtual.abort();
      controladorAtual = new AbortController();

      var url = "https://nominatim.openstreetmap.org/search?format=json&addressdetails=1&limit=6"
        + "&countrycodes=br&accept-language=pt-BR&q=" + encodeURIComponent(termo);

      fetch(url, { signal: controladorAtual.signal, headers: { "Accept": "application/json" } })
        .then(function (resposta) { return resposta.ok ? resposta.json() : []; })
        .then(function (resultados) {
          listaSugestoes.innerHTML = "";
          if (!resultados || !resultados.length) {
            fecharSugestoes();
            return;
          }
          // Nominatim costuma devolver o mesmo logradouro várias vezes (um
          // ponto por trecho da rua) — mantém só a primeira ocorrência de
          // cada combinação rua+cidade pra não repetir sugestão idêntica.
          var vistos = {};
          resultados.forEach(function (item) {
            var endereco = item.address || {};
            var principal = endereco.road || endereco.suburb || endereco.city
              || endereco.town || endereco.village || item.display_name.split(",")[0];
            var cidade = endereco.city || endereco.town || endereco.village || endereco.municipality || "";
            var estado = endereco.state || "";

            var chave = principal + "|" + cidade;
            if (vistos[chave]) return;
            vistos[chave] = true;

            var li = document.createElement("li");
            li.innerHTML = "<span>" + principal + "</span>"
              + (cidade ? "<span class=\"micro\">" + cidade + (estado ? " — " + estado : "") + "</span>" : "");
            li.addEventListener("click", function () {
              campoLocalizacao.value = cidade ? (principal + ", " + cidade) : principal;
              fecharSugestoes();
              campoLocalizacao.focus();
            });
            listaSugestoes.appendChild(li);
          });
          listaSugestoes.hidden = false;
        })
        .catch(function () {
          // Falha de rede ou requisição cancelada (nova busca no meio): a
          // busca continua funcionando por texto livre, sem autocomplete.
        });
    };

    campoLocalizacao.addEventListener("input", function () {
      var termo = campoLocalizacao.value.trim();
      window.clearTimeout(timeoutBusca);
      if (termo.length < 3) {
        fecharSugestoes();
        return;
      }
      timeoutBusca = window.setTimeout(function () { buscarSugestoes(termo); }, 350);
    });

    document.addEventListener("click", function (e) {
      if (!e.target.closest(".search__field--local")) fecharSugestoes();
    });
    campoLocalizacao.addEventListener("keydown", function (e) {
      if (e.key === "Escape") fecharSugestoes();
    });
  }
})();