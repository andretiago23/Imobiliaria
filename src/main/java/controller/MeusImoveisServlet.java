package controller;

import java.io.IOException;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ImovelServico;
import model.RegraNegocioException;
import model.StatusImovel;
import model.Usuario;
import util.ConversorEnum;
import util.SessaoUsuario;

/**
 * Painel do vendedor com os imóveis que ele anunciou, incluindo os já
 * negociados. Também recebe a troca de status (disponível, reservado,
 * vendido, alugado, inativo) direto da listagem.
 */
@WebServlet("/meus-imoveis")
public class MeusImoveisServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/meus-imoveis.jsp";

	private final ImovelServico imovelServico = new ImovelServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);

		Object erroPendente = requisicao.getSession().getAttribute("erroMeusImoveis");
		if (erroPendente != null) {
			requisicao.getSession().removeAttribute("erroMeusImoveis");
			requisicao.setAttribute("erro", erroPendente);
		}

		try {
			requisicao.setAttribute("imoveis", imovelServico.listarDoUsuario(usuarioLogado.getId()));
		} catch (DAOException e) {
			getServletContext().log("Falha ao listar os imóveis do usuário de id " + usuarioLogado.getId() + ".", e);
			requisicao.setAttribute("erro", "Não foi possível carregar seus imóveis agora.");
		}

		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);

		try {
			int idImovel = Integer.parseInt(requisicao.getParameter("idImovel"));
			StatusImovel novoStatus = ConversorEnum.paraEnum(StatusImovel.class, requisicao.getParameter("status"));
			imovelServico.alterarStatus(idImovel, novoStatus, usuarioLogado.getId());

		} catch (RegraNegocioException | IllegalArgumentException e) {
			requisicao.getSession().setAttribute("erroMeusImoveis", e.getMessage());
		} catch (DAOException e) {
			getServletContext().log("Falha ao alterar o status do imóvel.", e);
			requisicao.getSession().setAttribute("erroMeusImoveis",
					"Não foi possível atualizar o status agora. Tente novamente.");
		}

		resposta.sendRedirect(requisicao.getContextPath() + "/meus-imoveis");
	}
}
