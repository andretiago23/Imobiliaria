<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
	Porta de entrada da aplicação.

	Redireciona para a página inicial. Quem não estiver autenticado é enviado
	ao login pelo FiltroAutenticacao.
--%>
<% response.sendRedirect(request.getContextPath() + "/inicio"); %>
