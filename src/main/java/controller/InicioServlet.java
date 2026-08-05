package controller;

import java.io.IOException;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.InteracaoServico;
import model.Usuario;
import util.SessaoUsuario;

/**
 * Página inicial de quem está autenticado.
 *
 * Por enquanto mostra apenas o resumo da conta. A listagem de imóveis será
 * acrescentada junto com o feed.
 */
@WebServlet("/inicio")
public class InicioServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_INICIO = "/WEB-INF/jsp/inicio.jsp";

	private final InteracaoServico interacaoServico = new InteracaoServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);

		try {
			// A nota é formatada aqui para que o JSP cuide apenas da apresentação,
			// sem precisar de biblioteca de tags para arredondar o número.
			double reputacao = interacaoServico.calcularReputacao(usuario.getId());
			requisicao.setAttribute("reputacao", String.format("%.1f", reputacao));
			requisicao.setAttribute("totalAvaliacoes", interacaoServico.contarAvaliacoes(usuario.getId()));
			requisicao.setAttribute("interessesPendentes",
					interacaoServico.contarInteressesPendentes(usuario.getId()));
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar o resumo da conta.", e);
			requisicao.setAttribute("erro", "Não foi possível carregar seus dados agora.");
		}

		requisicao.getRequestDispatcher(PAGINA_INICIO).forward(requisicao, resposta);
	}
}
