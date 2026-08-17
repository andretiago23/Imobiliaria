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

/**
 * Cadastro de imobiliárias de exemplo dentro do próprio sistema.
 *
 * Como o protótipo não tem parceria real com nenhuma imobiliária, esta tela
 * fica aberta sem login: quem cria uma imobiliária recebe o código na hora e
 * repassa aos vendedores dela, que usam esse código no próprio cadastro.
 */
@WebServlet("/imobiliarias/nova")
public class ImobiliariaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/imobiliaria-nova.jsp";

	private final ImobiliariaServico imobiliariaServico = new ImobiliariaServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Imobiliaria imobiliaria = new Imobiliaria();
		imobiliaria.setNome(requisicao.getParameter("nome"));
		imobiliaria.setCnpj(requisicao.getParameter("cnpj"));
		imobiliaria.setTelefone(requisicao.getParameter("telefone"));
		imobiliaria.setEmail(requisicao.getParameter("email"));
		imobiliaria.setCidade(requisicao.getParameter("cidade"));
		imobiliaria.setEstado(requisicao.getParameter("estado"));

		try {
			String codigoGerado = imobiliariaServico.cadastrar(imobiliaria);
			requisicao.setAttribute("codigoGerado", codigoGerado);
			requisicao.setAttribute("nomeCadastrado", Html.escapar(imobiliaria.getNome()));
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
		requisicao.setAttribute("cnpj", Html.escapar(requisicao.getParameter("cnpj")));
		requisicao.setAttribute("telefone", Html.escapar(requisicao.getParameter("telefone")));
		requisicao.setAttribute("email", Html.escapar(requisicao.getParameter("email")));
		requisicao.setAttribute("cidade", Html.escapar(requisicao.getParameter("cidade")));
		requisicao.setAttribute("estado", Html.escapar(requisicao.getParameter("estado")));
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}
}
