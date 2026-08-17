package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Imovel;
import model.ImovelServico;

/**
 * Comparador de imóveis: até 3 ao mesmo tempo, com preço por m² calculado
 * automaticamente (Imovel.getPrecoPorM2).
 *
 * Recebe os ids pela querystring (?ids=12,45,78), preenchida pelas
 * checkboxes de "comparar" no catálogo — não precisa de carrinho em sessão.
 */
@WebServlet("/comparar")
public class ComparadorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final int LIMITE_COMPARACAO = 3;

	private static final String PAGINA = "/WEB-INF/jsp/comparador.jsp";

	private final ImovelServico imovelServico = new ImovelServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		String idsParametro = requisicao.getParameter("ids");
		List<Imovel> imoveis = new ArrayList<>();

		if (idsParametro != null && !idsParametro.isBlank()) {
			String[] ids = idsParametro.split(",");
			try {
				for (String idTexto : ids) {
					if (imoveis.size() >= LIMITE_COMPARACAO) {
						break;
					}
					Optional<Imovel> imovel = imovelServico.buscarParaExibicao(Integer.parseInt(idTexto.trim()));
					imovel.ifPresent(imoveis::add);
				}
			} catch (DAOException e) {
				getServletContext().log("Falha ao carregar imóveis para comparação.", e);
				requisicao.setAttribute("erro", "Não foi possível carregar os imóveis para comparar agora.");
			} catch (NumberFormatException e) {
				requisicao.setAttribute("erro", "Lista de imóveis para comparar inválida.");
			}
		}

		if (imoveis.isEmpty() && requisicao.getAttribute("erro") == null) {
			requisicao.setAttribute("erro", "Selecione até " + LIMITE_COMPARACAO + " imóveis no catálogo para comparar.");
		}

		requisicao.setAttribute("imoveis", imoveis);
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}
}
