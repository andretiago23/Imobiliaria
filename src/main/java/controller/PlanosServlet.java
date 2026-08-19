package controller;

import java.io.IOException;
import java.util.List;

import dao.DAOException;
import dao.PlanoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Plano;

/**
 * Página de planos, só para consulta — acessível a partir do link "Veja
 * nossos planos" da landing de anúncio, sem iniciar o assistente de 4
 * etapas. Os mesmos dados (PlanoDAO.listar) alimentam os cards da etapa 2
 * do assistente (ver AnuncioWizardServlet).
 */
@WebServlet("/planos")
public class PlanosServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_PLANOS = "/WEB-INF/jsp/planos.jsp";

	private final PlanoDAO planoDAO = new PlanoDAO();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		try {
			List<Plano> planos = planoDAO.listar();
			requisicao.setAttribute("planos", planos);
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar os planos.", e);
			requisicao.setAttribute("erro", "Não foi possível carregar os planos agora.");
		}
		requisicao.getRequestDispatcher(PAGINA_PLANOS).forward(requisicao, resposta);
	}
}
