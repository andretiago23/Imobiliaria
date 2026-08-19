package controller;

import java.io.IOException;

import dao.ConfirmacaoStatusDAO;
import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Endpoint que o link "Confirmar" do e-mail periódico "ainda está
 * disponível?" chama (ver util.AgendadorStatusImovel). Marca a confirmação
 * como respondida, o que reseta o prazo de 15+7 dias sem nenhuma outra
 * mudança precisar acontecer no imóvel — ele simplesmente não vira
 * PENDENTE_CONFIRMACAO na próxima varredura do job.
 *
 * Rota deliberadamente fora do FiltroAutenticacao (ver CAMINHOS_LIVRES):
 * precisa funcionar a partir de um clique direto no e-mail, sem exigir
 * login — o token de uso único já garante que só quem recebeu aquele
 * e-mail específico consegue confirmar aquele imóvel específico.
 */
@WebServlet("/confirmar-status")
public class ConfirmacaoStatusServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/confirmar-status.jsp";

	private final ConfirmacaoStatusDAO confirmacaoStatusDAO = new ConfirmacaoStatusDAO();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		String token = requisicao.getParameter("token");
		boolean sucesso = false;

		if (token != null && !token.isBlank()) {
			try {
				var confirmacao = confirmacaoStatusDAO.buscarIdImovelPorToken(token);
				if (confirmacao.isPresent()) {
					confirmacaoStatusDAO.marcarRespondido(confirmacao.get()[0]);
					sucesso = true;
				}
			} catch (DAOException e) {
				getServletContext().log("Falha ao confirmar status via token.", e);
			}
		}

		requisicao.setAttribute("sucesso", sucesso);
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}
}
