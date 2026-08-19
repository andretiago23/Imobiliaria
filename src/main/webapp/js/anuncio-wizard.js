/**
 * Interações client-side do assistente de anúncio: autopreenchimento de
 * endereço a partir do CEP (ViaCEP) e o checkbox "mesmo endereço do imóvel"
 * na etapa 3. Validação de negócio continua sempre no servidor.
 */
(function () {
	"use strict";

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
	// Etapa 1 — características do imóvel somadas à descrição
	//
	// Cada checkbox marcada acrescenta uma linha "emoji Texto" na descrição;
	// desmarcar remove só aquela linha, sem mexer no resto do que a pessoa
	// já escreveu.
	// -------------------------------------------------------------------
	var listaCaracteristicas = document.getElementById("caracteristicas");
	if (listaCaracteristicas) {
		var campoDescricao = document.getElementById("descricao");

		listaCaracteristicas.querySelectorAll("input[type=checkbox]").forEach(function (caixa) {
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
			});
		});
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
