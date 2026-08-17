document.addEventListener("DOMContentLoaded", function () {

	// 1) Mostrar/ocultar senha ao clicar no ícone de olho.
	//    Cada botão controla o campo de senha mais próximo dentro do mesmo
	//    agrupamento, então a mesma lógica serve para qualquer formulário
	//    (login, cadastro, etc.) sem precisar repetir o script.
	document.querySelectorAll(".alternar-senha").forEach(function (botao) {
		var campoSenha = botao.closest(".glass-input--senha").querySelector("input");
		if (!campoSenha) return;

		botao.addEventListener("click", function () {
			var visivel = campoSenha.type === "text";
			campoSenha.type = visivel ? "password" : "text";
			botao.classList.toggle("esta-visivel", !visivel);
			botao.setAttribute("aria-label", visivel ? "Mostrar senha" : "Ocultar senha");
		});
	});

	// 2) Evita duplo envio do formulário (clique duplo ou duplo Enter),
	//    desabilitando o botão de type="submit" assim que o formulário é
	//    enviado.
	document.querySelectorAll("form").forEach(function (formulario) {
		var botaoEnviar = formulario.querySelector("button[type=submit]");
		if (!botaoEnviar) return;

		formulario.addEventListener("submit", function (evento) {
			// Se outro script (ex.: validacao.js) já bloqueou o envio por causa
			// de um campo inválido, não trava o botão num "Enviando..." falso.
			if (evento.defaultPrevented) return;
			botaoEnviar.disabled = true;
			botaoEnviar.dataset.textoOriginal = botaoEnviar.textContent;
			botaoEnviar.textContent = "Enviando...";
		});
	});
});
