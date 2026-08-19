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

	// Pontua como CPF enquanto a pessoa digita até 11 dígitos; a partir do
	// 12º dígito, reformata automaticamente como CNPJ — a mesma caixa serve
	// para pessoa física ou jurídica sem precisar de um seletor à parte.
	function mascararCpfCnpj(valor) {
		var digitos = apenasDigitos(valor).slice(0, 14);
		if (digitos.length <= 11) {
			return mascararCPF(digitos);
		}
		return digitos
			.replace(/(\d{2})(\d)/, "$1.$2")
			.replace(/(\d{3})(\d)/, "$1.$2")
			.replace(/(\d{3})(\d)/, "$1/$2")
			.replace(/(\d{4})(\d{1,2})$/, "$1-$2");
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

	function mascararCEP(valor) {
		var digitos = apenasDigitos(valor).slice(0, 8);
		return digitos.replace(/(\d{5})(\d{1,3})$/, "$1-$2");
	}

	// Trata "R$" na frente e pontuação de milhar/decimal enquanto a pessoa
	// digita (os dígitos digitados viram centavos, de trás para frente —
	// mesmo comportamento de app de banco).
	function mascararMoeda(valor) {
		var digitos = apenasDigitos(valor).slice(0, 12);
		if (!digitos) return "";
		var numero = parseInt(digitos, 10) / 100;
		return "R$ " + numero.toLocaleString("pt-BR", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
	}

	// Aceita um valor já digitado com ponto (ex.: vindo do servidor como
	// "70.5") ou vírgula, mantém só um separador decimal e acrescenta "m²".
	function mascararArea(valor) {
		var limpo = (valor || "").replace(/m²/gi, "").replace(/\./g, ",").replace(/[^\d,]/g, "");
		var partes = limpo.split(",");
		if (partes.length > 1) {
			limpo = partes[0] + "," + partes.slice(1).join("").slice(0, 2);
		}
		return limpo ? limpo + " m²" : "";
	}

	var MASCARAS = {
		cpf: mascararCPF,
		cpfCnpj: mascararCpfCnpj,
		telefone: mascararTelefone,
		cep: mascararCEP,
		moeda: mascararMoeda,
		area: mascararArea,
	};

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

	// Mesmo algoritmo de util.ValidadorCNPJ no back-end.
	function cnpjValido(valorComMascara) {
		var d = apenasDigitos(valorComMascara);
		if (d.length !== 14 || /^(\d)\1{13}$/.test(d)) {
			return false;
		}
		function digitoVerificador(pesos) {
			var soma = 0;
			for (var i = 0; i < pesos.length; i++) {
				soma += Number(d[i]) * pesos[i];
			}
			var resto = soma % 11;
			return resto < 2 ? 0 : 11 - resto;
		}
		var d1 = digitoVerificador([5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]);
		var d2 = digitoVerificador([6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]);
		return d1 === Number(d[12]) && d2 === Number(d[13]);
	}

	function cpfCnpjValido(valorComMascara) {
		var d = apenasDigitos(valorComMascara);
		return d.length === 11 ? cpfValido(valorComMascara) : cnpjValido(valorComMascara);
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
		cpfCnpj: function (campo) {
			var valor = campo.value.trim();
			var tamanho = apenasDigitos(valor).length;
			if (!valor) return "Informe seu CPF ou CNPJ.";
			if (tamanho !== 11 && tamanho !== 14) return "Informe um CPF (11 dígitos) ou CNPJ (14 dígitos) completo.";
			if (!cpfCnpjValido(valor)) {
				return tamanho === 11 ? "Este CPF não é válido. Confira os números." : "Este CNPJ não é válido. Confira os números.";
			}
			return "";
		},
		telefone: function (campo) {
			var valor = apenasDigitos(campo.value);
			if (!valor) return ""; // campo opcional
			if (valor.length < 10 || valor.length > 11) return "Informe um telefone com DDD.";
			return "";
		},
		telefoneObrigatorio: function (campo) {
			var valor = apenasDigitos(campo.value);
			if (!valor) return "Informe seu celular.";
			if (valor.length < 10 || valor.length > 11) return "Informe um celular com DDD.";
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
