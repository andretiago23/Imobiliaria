<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="robots" content="noindex, nofollow">
  <title>Cadastrar imobiliária | Habittar</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/habittar.css?v=2">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/catalogo.css?v=2">
</head>
<body>

<main class="app-main" style="max-width:480px;padding-top:96px;">
  <p class="eyebrow">Painel interno</p>
  <h1 class="display" style="margin-bottom:24px;">Cadastrar imobiliária</h1>

  <% if (request.getAttribute("codigoGerado") != null) { %>
    <p class="alerta alerta-sucesso" role="status">
      Imobiliária <strong>${nomeCadastrado}</strong> cadastrada. Repasse o código abaixo aos vendedores dela —
      é o que eles vão digitar no cadastro para provar o vínculo.
    </p>
    <p style="font-size:28px;font-weight:700;letter-spacing:2px;margin:16px 0;">${codigoGerado}</p>
  <% } %>

  <% if (request.getAttribute("erro") != null) { %>
    <p class="alerta alerta-erro" role="alert">${erro}</p>
  <% } %>

  <form method="post" action="${pageContext.request.contextPath}/imobiliarias/nova">
    <input type="hidden" name="csrf" value="${csrf}">

    <div class="auth__campo">
      <label for="nome">Nome da imobiliária</label>
      <div class="glass-input">
        <input type="text" id="nome" name="nome" placeholder="Nome" value="${nome}" required autofocus>
      </div>
    </div>

    <button type="submit" class="btn btn--primary" style="width:100%;margin-top:16px;">Cadastrar e gerar código</button>
  </form>
</main>

</body>
</html>
