/* Habittar — interações da LP (JS puro, sem dependências) */
(function () {
  "use strict";

  var reduced = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  var isMobile = window.matchMedia("(max-width: 760px)").matches;

  /* ---- Header translúcido a partir de 40px ---- */
  var header = document.getElementById("header");
  var parallax = document.querySelectorAll("[data-parallax]");
  var ticking = false;

  function onScroll() {
    if (ticking) return;
    ticking = true;
    window.requestAnimationFrame(function () {
      var y = window.pageYOffset || document.documentElement.scrollTop;
      if (header) header.classList.toggle("is-scrolled", y > 40);
      if (!reduced) {
        var offset = Math.min(8, y * 0.02);
        for (var i = 0; i < parallax.length; i++) {
          parallax[i].style.transform = "translateY(" + offset + "px)";
        }
      }
      ticking = false;
    });
  }
  window.addEventListener("scroll", onScroll, { passive: true });
  onScroll();

  /* ---- Segmento alugar/comprar/vender ---- */
  var seg = document.querySelector("[data-segment]");
  var hidden = document.getElementById("negocio");
  if (seg) {
    seg.addEventListener("click", function (e) {
      var btn = e.target.closest("button[data-value]");
      if (!btn) return;
      seg.querySelectorAll("button").forEach(function (b) {
        b.classList.toggle("is-active", b === btn);
      });
      if (hidden) hidden.value = btn.getAttribute("data-value");
    });
  }

  /* ---- Combobox "Tipo de imóvel" ---- */
  var combo = document.querySelector("[data-combobox]");
  if (combo) {
    var toggle = combo.querySelector("[data-combobox-toggle]");
    var list = combo.querySelector("[data-combobox-list]");
    var label = combo.querySelector("[data-combobox-label]");
    var hiddenTipo = combo.querySelector("#tipo");
    var defaultLabel = label.textContent;

    function closeCombo() {
      combo.removeAttribute("data-open");
      list.hidden = true;
      toggle.setAttribute("aria-expanded", "false");
    }
    function openCombo() {
      combo.setAttribute("data-open", "");
      list.hidden = false;
      toggle.setAttribute("aria-expanded", "true");
    }

    toggle.addEventListener("click", function (e) {
      e.stopPropagation();
      if (list.hidden) openCombo(); else closeCombo();
    });

    list.addEventListener("click", function (e) {
      var opt = e.target.closest("li[data-value]");
      if (!opt) return;
      var value = opt.getAttribute("data-value");
      list.querySelectorAll("li").forEach(function (li) {
        li.classList.toggle("is-selected", li === opt);
      });
      label.textContent = opt.querySelector("span").textContent;
      if (hiddenTipo) hiddenTipo.value = value;
      closeCombo();
    });

    document.addEventListener("click", function (e) {
      if (!combo.contains(e.target)) closeCombo();
    });
    document.addEventListener("keydown", function (e) {
      if (e.key === "Escape") closeCombo();
    });
  }

  /* ---- Revelação em viewport ---- */
  var targets = document.querySelectorAll("[data-inview], .reveal");
  if (!("IntersectionObserver" in window)) {
    targets.forEach(function (el) { el.classList.add("in-view"); });
    countAll();
  } else {
    var io = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        entry.target.classList.add("in-view");
        entry.target.querySelectorAll("[data-count]").forEach(startCount);
        io.unobserve(entry.target);
      });
    }, { threshold: 0.25, rootMargin: "0px 0px -10% 0px" });
    targets.forEach(function (el) { io.observe(el); });
  }

  /* ---- Stroke draw: stagger de 150ms entre as linhas ---- */
  document.querySelectorAll("svg g.draw path").forEach(function (p, i) {
    p.style.transitionDelay = (isMobile ? 0 : i * 0.15) + "s";
  });

  /* ---- Contadores ---- */
  function startCount(el) {
    if (el.dataset.done) return;
    el.dataset.done = "1";
    var target = parseInt(el.getAttribute("data-count"), 10) || 0;
    var suffix = el.getAttribute("data-suffix") || "";
    if (reduced) { el.textContent = format(target) + suffix; return; }
    var duration = 1400, start = null;
    function step(ts) {
      if (start === null) start = ts;
      var p = Math.min((ts - start) / duration, 1);
      var eased = 1 - Math.pow(1 - p, 3); // ease-out
      el.textContent = format(Math.round(target * eased)) + suffix;
      if (p < 1) window.requestAnimationFrame(step);
    }
    window.requestAnimationFrame(step);
  }
  function countAll() {
    document.querySelectorAll("[data-count]").forEach(startCount);
  }
  function format(n) {
    return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
  }
})();