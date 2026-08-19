package controller;

import java.io.IOException;

import dao.DAOException;
import dao.ImovelDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Endpoint chamado via fetch assíncrono pelo botão "Entrar em contato"
 * (WhatsApp) da página de detalhe, antes do redirecionamento para o
 * wa.me — incrementa a métrica "contatos via WhatsApp" do imóvel sem
 * atrasar o clique. Não bloqueia nem falha a navegação: se o fetch não
 * chegar a tempo (usuário já saiu da página), a métrica simplesmente não
 * é contada dessa vez.
 */
@WebServlet("/imovel/whatsapp")
public class WhatsAppClickServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final ImovelDAO imovelDAO = new ImovelDAO();

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Integer idImovel = idValido(requisicao.getParameter("idImovel"));
		if (idImovel != null) {
			try {
				imovelDAO.incrementarContatoWhatsapp(idImovel);
			} catch (DAOException e) {
				getServletContext().log("Falha ao registrar contato via WhatsApp do imóvel " + idImovel + ".", e);
			}
		}
		resposta.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	private Integer idValido(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		try {
			return Integer.valueOf(valor);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
