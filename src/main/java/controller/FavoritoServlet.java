package controller;

import java.io.IOException;

import dao.DAOException;
import dao.FavoritoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Usuario;
import util.SessaoUsuario;
import util.TokenCsrf;

/**
 * Ícone "salvar" no card do catálogo e na página de detalhe: favorita/
 * desfavorita um imóvel para a aba "Imóveis salvos" do perfil. A regra em si
 * (impedir duplicata) já é garantida pela UNIQUE KEY da tabela favorito —
 * FavoritoDAO.alternar só decide se insere ou remove.
 */
@WebServlet("/favorito")
public class FavoritoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final FavoritoDAO favoritoDAO = new FavoritoDAO();

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Integer idImovel = idValido(requisicao.getParameter("idImovel"));
		String destino = requisicao.getParameter("destino");
		String redirecionarPara = (destino != null && !destino.isBlank())
				? destino
				: requisicao.getContextPath() + "/imovel?id=" + idImovel;

		if (idImovel == null || !TokenCsrf.valido(requisicao)) {
			resposta.sendRedirect(redirecionarPara);
			return;
		}

		Usuario usuario = SessaoUsuario.obter(requisicao);
		try {
			favoritoDAO.alternar(usuario.getId(), idImovel);
		} catch (DAOException e) {
			getServletContext().log("Falha ao favoritar o imóvel " + idImovel + ".", e);
		}

		resposta.sendRedirect(redirecionarPara);
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
