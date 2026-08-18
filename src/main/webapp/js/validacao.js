/**
 * Máscaras e validação client-side dos formulários de conta.
 *
 * A validação real de negócio continua no servidor (nunca confiar só no
 * navegador) — isto aqui existe para dar retorno imediato, campo a campo,
 * sem precisar de um round-trip ao servidor para erros óbvios como um CPF
 * com dígito verificador errado ou as duas senhas não coincidindo.
 */
(function () {
	"use strict";

	// -------------------------------------------------------------------
	// Máscaras
	// -------------------------------------------------------------------

	function apenasDigitos(texto) {
		return (texto || "").replace(/\D/g, "");
	}

	function mascararCPF(valor) {
		var digitos = apenasDigitos(valor).slice(0, 11);
		return digitos
			.replace(/(\d{3})(\d)/, "$1.$2")
			.replace(/(\d{3})(\d)/, "$1.$2")
			.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
	}

	function mascararTelefone(valor) {
		var digitos = apenasDigitos(valor).slice(0, 11);
		if (digitos.length <= 10) {
			return digitos
				.replace(/(\d{2})(\d)/, "($1) $2")
				.replace(/(\d{4})(\d{1,4})$/, "$1-$2");
		}
		return digitos
			.replace(/(\d{2})(\d)/, "($1) $2")
			.replace(/(\d{5})(\d{1,4})$/, "$1-$2");
	}

	var MASCARAS = { cpf: mascararCPF, telefone: mascararTelefone };

	document.querySelectorAll("[data-mascara]").forEach(function (campo) {
		var aplicar = MASCARAS[campo.dataset.mascara];
		if (!aplicar) return;

		// Formata também o valor que já vier preenchido pelo servidor
		// (ex.: telefone salvo sem máscara no banco).
		if (campo.value) campo.value = aplicar(campo.value);

		campo.addEventListener("input", function () {
			var posicaoOriginal = campo.selectionStart;
			var tamanhoAntes = campo.value.length;
			campo.value = aplicar(campo.value);
			var diferenca = campo.value.length - tamanhoAntes;
			campo.setSelectionRange(posicaoOriginal + diferenca, posicaoOriginal + diferenca);
		});
	});

	// -------------------------------------------------------------------
	// Validação de CPF (mesmo algoritmo de util.ValidadorCPF no back-end)
	// -------------------------------------------------------------------

	function cpfValido(valorComMascara) {
		var d = apenasDigitos(valorComMascara);
		if (d.length !== 11 || /^(\d)\1{10}$/.test(d)) {
			return false;
		}
		function digitoVerificador(quantidade) {
			var soma = 0;
			var peso = quantidade + 1;
			for (var i = 0; i < quantidade; i++) {
				soma += Number(d[i]) * peso;
				peso--;
			}
			var resto = soma % 11;
			return resto < 2 ? 0 : 11 - resto;
		}
		return digitoVerificador(9) === Number(d[9]) && digitoVerificador(10) === Number(d[10]);
	}

	var PADRAO_EMAIL = /^[\w.+-]+@[\w-]+(\.[\w-]+)+$/;

	// -------------------------------------------------------------------
	// Regras por campo — cada uma devolve a mensagem de erro, ou "" se ok.
	// -------------------------------------------------------------------

	var REGRAS = {
		nome: function (campo) {
			var valor = campo.value.trim();
			if (!valor) return "Informe o nome.";
			if (valor.length < 3) return "O nome deve ter pelo menos 3 letras.";
			return "";
		},
		email: function (campo) {
			var valor = campo.value.trim();
			if (!valor) return "Informe o e-mail.";
			if (!PADRAO_EMAIL.test(valor)) return "Informe um e-mail válido.";
			return "";
		},
		cpf: function (campo) {
			var valor = campo.value.trim();
			if (!valor) return "Informe o CPF.";
			if (apenasDigitos(valor).length !== 11) return "O CPF deve ter 11 dígitos.";
			if (!cpfValido(valor)) return "Este CPF não é válido. Confira os números.";
			return "";
		},
		telefone: function (campo) {
			var valor = apenasDigitos(campo.value);
			if (!valor) return ""; // campo opcional
			if (valor.length < 10 || valor.length > 11) return "Informe um telefone com DDD.";
			return "";
		},
		senha: function (campo) {
			if (campo.value.length < 8) return "A senha deve ter pelo menos 8 caracteres.";
			return "";
		},
		senhaObrigatoria: function (campo) {
			if (!campo.value) return "Informe a senha.";
			return "";
		},
		confirmacaoSenha: function (campo) {
			var senha = document.getElementById("senha");
			if (senha && campo.value !== senha.value) return "As senhas não coincidem.";
			return "";
		},
		aceiteTermos: function (campo) {
			if (!campo.checked) return "É preciso concordar com a Política de Privacidade e os Termos de Uso.";
			return "";
		},
	};

	// -------------------------------------------------------------------
	// Exibição do erro embaixo do campo
	// -------------------------------------------------------------------

	function validarCampo(campo) {
		var regra = REGRAS[campo.dataset.validar];
		if (!regra || campo.disabled) return true;

		var mensagem = regra(campo);
		var spanErro = document.getElementById("erro-" + campo.id);
		var wrapper = campo.closest(".glass-input");

		if (spanErro) spanErro.textContent = mensagem;
		if (wrapper) wrapper.classList.toggle("tem-erro", Boolean(mensagem));

		return !mensagem;
	}

	document.querySelectorAll("[data-validar]").forEach(function (campo) {
		campo.addEventListener("blur", function () {
			validarCampo(campo);
		});
		// Some o erro assim que a pessoa começa a corrigir, sem esperar o blur.
		campo.addEventListener("input", function () {
			var spanErro = document.getElementById("erro-" + campo.id);
			if (spanErro && spanErro.textContent) {
				validarCampo(campo);
			}
		});
	});

	document.querySelectorAll("form").forEach(function (formulario) {
		var camposValidaveis = formulario.querySelectorAll("[data-validar]");
		if (camposValidaveis.length === 0) return;

		formulario.addEventListener("submit", function (evento) {
			var primeiroInvalido = null;

			camposValidaveis.forEach(function (campo) {
				var valido = validarCampo(campo);
				if (!valido && !primeiroInvalido) {
					primeiroInvalido = campo;
				}
			});

			if (primeiroInvalido) {
				evento.preventDefault();
				primeiroInvalido.focus();
			}
		});
	});
})();
