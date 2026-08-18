package controller;

import java.io.IOException;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Imobiliaria;
import model.ImobiliariaServico;
import model.RegraNegocioException;
import util.Html;
import util.TokenCsrf;

/**
 * Cadastro de imobiliárias, para gerar o código de afiliação que os
 * vendedores digitam no próprio cadastro (ver CadastroServlet).
 *
 * Não há link para esta URL em nenhuma tela do sistema — quem administra as
 * imobiliárias acessa direto pelo endereço. Como o sistema não tem parceria
 * real com nenhuma imobiliária, não há um cadastro de administrador separado
 * só para isso; o acesso à URL já funciona como controle suficiente para o
 * escopo do protótipo.
 */
@WebServlet("/imobiliarias/nova")
public class ImobiliariaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/imobiliaria-nova.jsp";

	private final ImobiliariaServico imobiliariaServico = new ImobiliariaServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		if (!TokenCsrf.valido(requisicao)) {
			reexibirFormulario(requisicao, resposta, "Sua sessão expirou. Preencha o formulário novamente.");
			return;
		}

		try {
			Imobiliaria imobiliaria = new Imobiliaria(requisicao.getParameter("nome"));
			String codigoGerado = imobiliariaServico.cadastrar(imobiliaria);

			requisicao.setAttribute("codigoGerado", codigoGerado);
			requisicao.setAttribute("nomeCadastrado", Html.escapar(imobiliaria.getNome()));
			requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
			requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);

		} catch (RegraNegocioException e) {
			reexibirFormulario(requisicao, resposta, e.getMessage());
		} catch (DAOException e) {
			getServletContext().log("Falha ao cadastrar a imobiliária.", e);
			reexibirFormulario(requisicao, resposta, "Não foi possível concluir o cadastro agora. Tente novamente.");
		}
	}

	private void reexibirFormulario(HttpServletRequest requisicao, HttpServletResponse resposta, String mensagemErro)
			throws ServletException, IOException {

		requisicao.setAttribute("erro", mensagemErro);
		requisicao.setAttribute("nome", Html.escapar(requisicao.getParameter("nome")));
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}
}
