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
import model.TipoAnunciante;
import util.ConversorEnum;

/**
 * Página de planos, só para consulta — acessível a partir do link "Veja
 * nossos planos" da landing de anúncio, sem iniciar o assistente de 5
 * etapas. Os mesmos dados (PlanoDAO.listarPorTipo) alimentam os cards da
 * etapa 2 do assistente (ver AnuncioWizardServlet), mas aqui mostramos todas
 * as categorias (individual e pack) do tipo escolhido, não só as compráveis
 * dentro do assistente.
 *
 * O tipo vem do parâmetro "tipo" (proprietario/corretor, o mesmo usado na
 * landing e na etapa 1 do assistente); sem parâmetro ou com valor inválido,
 * assume-se proprietário.
 */
@WebServlet("/planos")
public class PlanosServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_PLANOS = "/WEB-INF/jsp/planos.jsp";

	private final PlanoDAO planoDAO = new PlanoDAO();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		TipoAnunciante tipoAnunciante;
		try {
			tipoAnunciante = ConversorEnum.paraEnum(TipoAnunciante.class, requisicao.getParameter("tipo"));
		} catch (IllegalArgumentException e) {
			tipoAnunciante = null;
		}
		if (tipoAnunciante == null) {
			tipoAnunciante = TipoAnunciante.PROPRIETARIO;
		}

		try {
			List<Plano> planos = planoDAO.listarPorTipo(tipoAnunciante);
			requisicao.setAttribute("planos", planos);
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar os planos.", e);
			requisicao.setAttribute("erro", "Não foi possível carregar os planos agora.");
		}
		requisicao.setAttribute("tipoAnunciante", tipoAnunciante);
		requisicao.getRequestDispatcher(PAGINA_PLANOS).forward(requisicao, resposta);
	}
}
