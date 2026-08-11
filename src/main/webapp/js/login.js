document.addEventListener("DOMContentLoaded", function () {

	// 1) Mostrar/ocultar senha ao clicar no ícone de olho
	var botaoAlternarSenha = document.getElementById("alternarSenha");
	var campoSenha = document.getElementById("senha");

	if (botaoAlternarSenha && campoSenha) {
		botaoAlternarSenha.addEventListener("click", function () {
			var visivel = campoSenha.type === "text";
			campoSenha.type = visivel ? "password" : "text";
			botaoAlternarSenha.setAttribute("aria-label", visivel ? "Mostrar senha" : "Ocultar senha");
			botaoAlternarSenha.textContent = visivel ? "👁" : "🙈";
		});
	}

	// 2) Evita duplo envio do formulário (clique duplo ou duplo Enter),
	//    desabilitando o botão assim que o formulário é submetido.
	var formulario = document.getElementById("formLogin");
	var botaoEntrar = document.getElementById("botaoEntrar");

	if (formulario && botaoEntrar) {
		formulario.addEventListener("submit", function () {
			botaoEntrar.disabled = true;
			botaoEntrar.textContent = "Entrando...";
		});
	}
});
