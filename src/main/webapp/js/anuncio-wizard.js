/**
 * Interações client-side do assistente de anúncio: autopreenchimento de
 * endereço a partir do CEP (ViaCEP) e o checkbox "mesmo endereço do imóvel"
 * na etapa 3. Validação de negócio continua sempre no servidor.
 */
(function () {
	"use strict";

	// -------------------------------------------------------------------
	// Etapa 1 — stepper de quartos/banheiros (botões -/+ no lugar da
	// setinha nativa do input number).
	// -------------------------------------------------------------------
	document.querySelectorAll("[data-stepper]").forEach(function (stepper) {
		var campo = stepper.querySelector("input");
		if (!campo) return;

		stepper.querySelectorAll(".wizard-stepper__botao").forEach(function (botao) {
			botao.addEventListener("click", function () {
				var passo = Number(botao.dataset.step);
				var minimo = campo.min !== "" ? Number(campo.min) : -Infinity;
				var maximo = campo.max !== "" ? Number(campo.max) : Infinity;
				var atual = campo.value === "" ? 0 : Number(campo.value);
				var novoValor = Math.min(maximo, Math.max(minimo, atual + passo));
				campo.value = novoValor;
				campo.dispatchEvent(new Event("change", { bubbles: true }));
			});
		});
	});

	function apenasDigitos(texto) {
		return (texto || "").replace(/\D/g, "");
	}

	// -------------------------------------------------------------------
	// Autopreenchimento de endereço via CEP (ViaCEP)
	// -------------------------------------------------------------------
	function ligarAutopreenchimentoCep(prefixo) {
		var campoCep = document.getElementById("cep" + prefixo);
		if (!campoCep) return;

		var status = document.getElementById("cepStatus" + prefixo);
		var campoEndereco = document.getElementById("endereco" + prefixo);
		var campoBairro = document.getElementById("bairro" + prefixo);
		var campoCidade = document.getElementById("cidade" + prefixo);
		var campoEstado = document.getElementById("estado" + prefixo);

		function buscar() {
			var cep = apenasDigitos(campoCep.value);
			if (cep.length !== 8) return;
			if (status) {
				status.textContent = "Buscando endereço...";
				status.className = "wizard-cep__status";
			}

			fetch("https://viacep.com.br/ws/" + cep + "/json/")
				.then(function (resposta) { return resposta.json(); })
				.then(function (dados) {
					if (dados.erro) {
						if (status) {
							status.textContent = "CEP não encontrado. Confira o número ou preencha manualmente.";
							status.className = "wizard-cep__status wizard-cep__status--erro";
						}
						return;
					}
					if (campoEndereco && !campoEndereco.value) campoEndereco.value = dados.logradouro || "";
					if (campoBairro && !campoBairro.value) campoBairro.value = dados.bairro || "";
					if (campoCidade) campoCidade.value = dados.localidade || "";
					if (campoEstado) campoEstado.value = dados.uf || "";
					if (status) {
						status.textContent = "Endereço encontrado — confira o número.";
						status.className = "wizard-cep__status wizard-cep__status--ok";
					}
				})
				.catch(function () {
					if (status) {
						status.textContent = "Não foi possível buscar o CEP agora. Preencha manualmente.";
						status.className = "wizard-cep__status wizard-cep__status--erro";
					}
				});
		}

		campoCep.addEventListener("blur", buscar);
	}

	ligarAutopreenchimentoCep("");
	ligarAutopreenchimentoCep("Anunciante");

	// -------------------------------------------------------------------
	// Etapa 1 — contador de caracteres da descrição (mínimo de 150). Fica
	// numa função à parte porque as características (mais abaixo) também
	// mudam o texto da descrição por JS, sem disparar o evento "input" —
	// então precisam chamar isso na mão depois de mexer no valor.
	var MINIMO_DESCRICAO = 150;
	var campoDescricao = document.getElementById("descricao");
	var contadorDescricao = document.getElementById("contadorDescricao");

	function atualizarContadorDescricao() {
		if (!campoDescricao || !contadorDescricao) return;
		var tamanho = campoDescricao.value.trim().length;
		var faltam = MINIMO_DESCRICAO - tamanho;
		contadorDescricao.textContent = faltam > 0
			? tamanho + "/" + MINIMO_DESCRICAO + " caracteres — faltam " + faltam
			: tamanho + " caracteres";
		contadorDescricao.classList.toggle("wizard-contador--ok", faltam <= 0);
	}

	if (campoDescricao) {
		campoDescricao.addEventListener("input", atualizarContadorDescricao);
		atualizarContadorDescricao();
	}

	// -------------------------------------------------------------------
	// Etapa 1 — características do imóvel somadas à descrição
	//
	// Cada checkbox marcada acrescenta uma linha "emoji Texto" na descrição;
	// desmarcar remove só aquela linha, sem mexer no resto do que a pessoa
	// já escreveu. O resumo no dropdown mostra quantas estão marcadas.
	// -------------------------------------------------------------------
	var listaCaracteristicas = document.getElementById("caracteristicas");
	if (listaCaracteristicas && campoDescricao) {
		var resumoCaracteristicas = document.getElementById("resumoCaracteristicas");
		var caixasCaracteristicas = listaCaracteristicas.querySelectorAll("input[type=checkbox]");

		function atualizarResumoCaracteristicas() {
			if (!resumoCaracteristicas) return;
			var marcadas = Array.prototype.filter.call(caixasCaracteristicas, function (c) { return c.checked; }).length;
			resumoCaracteristicas.textContent = marcadas > 0
				? marcadas + " característica" + (marcadas > 1 ? "s" : "") + " selecionada" + (marcadas > 1 ? "s" : "")
				: "Selecione as características";
		}

		caixasCaracteristicas.forEach(function (caixa) {
			var linha = caixa.dataset.emoji + " " + caixa.dataset.texto;

			caixa.addEventListener("change", function () {
				var linhas = campoDescricao.value.split("\n").filter(function (l) { return l.trim() !== ""; });

				if (caixa.checked) {
					if (linhas.indexOf(linha) === -1) {
						linhas.push(linha);
					}
				} else {
					linhas = linhas.filter(function (l) { return l !== linha; });
				}

				campoDescricao.value = linhas.join("\n");
				atualizarContadorDescricao();
				atualizarResumoCaracteristicas();
			});
		});

		atualizarResumoCaracteristicas();
	}

	// -------------------------------------------------------------------
	// Etapa 1 — fotos e vídeo enviados por AJAX: salva o arquivo na hora
	// (ou remove) sem recarregar a página inteira nem exigir os outros
	// campos obrigatórios do formulário (título, endereço etc.), que é
	// exatamente o que causava os dois bugs de antes — ver
	// controller.AnuncioWizardServlet, bloco "apenasMidia" do doPost.
	// -------------------------------------------------------------------
	var formEtapa1 = document.getElementById("formEtapa1");
	var gradeFotos = document.getElementById("gradeFotos");
	var blocoVideo = document.getElementById("blocoVideo");
	if (formEtapa1 && gradeFotos && blocoVideo) {
		var inputFotos = document.getElementById("inputFotos");
		var contadorFotos = document.getElementById("contadorFotos");
		var erroMidia = document.getElementById("erroMidia");
		var botaoProximo = document.getElementById("botaoProximo");

		function escaparAtributoMidia(texto) {
			return String(texto).replace(/&/g, "&amp;").replace(/"/g, "&quot;");
		}

		// action do form já é a URL absoluta certa (contextPath +
		// /anunciar/etapa1) — reaproveitada como base pro href de fallback
		// das remoções (funciona mesmo sem JS, embora sem AJAX).
		function marcacaoRemoverFoto(url) {
			return formEtapa1.action + "?removerFoto=" + encodeURIComponent(url);
		}

		function renderizarFotos(fotos) {
			gradeFotos.innerHTML = fotos.map(function (url) {
				var urlSegura = escaparAtributoMidia(url);
				return '<div class="wizard-midia__item">'
					+ '<img src="' + urlSegura + '" alt="">'
					+ '<a class="wizard-midia__remover" href="' + escaparAtributoMidia(marcacaoRemoverFoto(url)) + '"'
					+ ' data-remover-foto="' + urlSegura + '" title="Remover foto" aria-label="Remover foto">×</a>'
					+ '</div>';
			}).join("");
			if (contadorFotos) contadorFotos.textContent = fotos.length;
			ligarRemocaoFotos();
		}

		function renderizarVideo(video) {
			if (video) {
				var urlSegura = escaparAtributoMidia(video);
				blocoVideo.innerHTML = '<div class="wizard-midia__grade"><div class="wizard-midia__item">'
					+ '<video src="' + urlSegura + '" muted></video>'
					+ '<a class="wizard-midia__remover" href="' + escaparAtributoMidia(formEtapa1.action + "?removerVideo=1") + '"'
					+ ' data-remover-video="1" title="Remover vídeo" aria-label="Remover vídeo">×</a>'
					+ '</div></div>';
			} else {
				blocoVideo.innerHTML = '<label class="btn btn--secondary btn--sm" style="display:inline-flex;cursor:pointer;">'
					+ '+ Adicionar vídeo'
					+ '<input type="file" id="inputVideo" name="video" accept="video/mp4,video/webm,video/quicktime" class="sr-only">'
					+ '</label>';
				ligarInputVideo();
			}
			ligarRemocaoVideo();
		}

		function mostrarErroMidia(mensagem) {
			if (!erroMidia) return;
			erroMidia.hidden = !mensagem;
			erroMidia.textContent = mensagem || "";
		}

		function enviarMidia(extras) {
			var dados = new FormData(formEtapa1);
			dados.set("apenasMidia", "1");
			Object.keys(extras || {}).forEach(function (chave) { dados.set(chave, extras[chave]); });

			// Lê os arquivos escolhidos acima (no FormData) e já limpa os
			// inputs, senão uma ação seguinte (ex.: remover outra foto)
			// reenviaria os mesmos arquivos de novo sem querer.
			if (inputFotos) inputFotos.value = "";
			var inputVideoAtual = document.getElementById("inputVideo");
			if (inputVideoAtual) inputVideoAtual.value = "";

			if (botaoProximo) botaoProximo.disabled = true;
			mostrarErroMidia(null);

			return fetch(formEtapa1.action, { method: "POST", body: dados })
				.then(function (resposta) { return resposta.json(); })
				.then(function (dadosResposta) {
					renderizarFotos(dadosResposta.fotos || []);
					renderizarVideo(dadosResposta.video || null);
					mostrarErroMidia(dadosResposta.erro || null);
				})
				.catch(function () {
					mostrarErroMidia("Não foi possível enviar agora. Tente novamente.");
				})
				.finally(function () {
					if (botaoProximo) botaoProximo.disabled = false;
				});
		}

		function ligarInputFotos() {
			if (!inputFotos) return;
			inputFotos.addEventListener("change", function () {
				if (inputFotos.files && inputFotos.files.length > 0) enviarMidia();
			});
		}

		function ligarInputVideo() {
			var input = document.getElementById("inputVideo");
			if (!input) return;
			input.addEventListener("change", function () {
				if (input.files && input.files.length > 0) enviarMidia();
			});
		}

		function ligarRemocaoFotos() {
			gradeFotos.querySelectorAll("[data-remover-foto]").forEach(function (link) {
				link.addEventListener("click", function (evento) {
					evento.preventDefault();
					enviarMidia({ removerFoto: link.dataset.removerFoto });
				});
			});
		}

		function ligarRemocaoVideo() {
			blocoVideo.querySelectorAll("[data-remover-video]").forEach(function (link) {
				link.addEventListener("click", function (evento) {
					evento.preventDefault();
					enviarMidia({ removerVideo: "1" });
				});
			});
		}

		ligarInputFotos();
		ligarInputVideo();
		ligarRemocaoFotos();
		ligarRemocaoVideo();
	}

	// -------------------------------------------------------------------
	// Etapa 3 — "Utilizar mesmo endereço do imóvel"
	// -------------------------------------------------------------------
	var checkboxMesmoEndereco = document.getElementById("enderecoIgualImovel");
	if (checkboxMesmoEndereco) {
		var blocoEnderecoAnunciante = document.getElementById("blocoEnderecoAnunciante");
		var camposAnunciante = blocoEnderecoAnunciante
			? blocoEnderecoAnunciante.querySelectorAll("input")
			: [];

		function aplicarEstado() {
			var igual = checkboxMesmoEndereco.checked;
			camposAnunciante.forEach(function (campo) {
				campo.disabled = igual;
			});
			if (blocoEnderecoAnunciante) {
				blocoEnderecoAnunciante.classList.toggle("wizard-bloco--desabilitado", igual);
			}
		}

		checkboxMesmoEndereco.addEventListener("change", aplicarEstado);
		aplicarEstado();
	}
})();
