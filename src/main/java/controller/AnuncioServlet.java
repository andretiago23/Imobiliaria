package controller;

import java.io.IOException;
import java.math.BigDecimal;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Finalidade;
import model.Imovel;
import model.ImovelServico;
import model.RegraNegocioException;
import model.TipoImovel;
import model.Usuario;
import util.ConversorEnum;
import util.Html;
import util.SessaoUsuario;
import util.TokenCsrf;

/**
 * Publicação de um novo anúncio de imóvel.
 *
 * Só usuários do tipo "vendedor" podem publicar — quem está autenticado mas é
 * do tipo "comprador" recebe uma tela explicando como liberar o recurso, em
 * vez do formulário.
 */
@WebServlet("/anunciar")
public class AnuncioServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_ANUNCIO = "/WEB-INF/jsp/anuncio.jsp";

	private final ImovelServico imovelServico = new ImovelServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);
		if (!usuario.podeAnunciar()) {
			requisicao.setAttribute("semPermissao", true);
			requisicao.getRequestDispatcher(PAGINA_ANUNCIO).forward(requisicao, resposta);
			return;
		}

		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA_ANUNCIO).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);
		if (!usuario.podeAnunciar()) {
			requisicao.setAttribute("semPermissao", true);
			requisicao.getRequestDispatcher(PAGINA_ANUNCIO).forward(requisicao, resposta);
			return;
		}

		if (!TokenCsrf.valido(requisicao)) {
			reexibirFormulario(requisicao, resposta, "Sua sessão expirou. Preencha o formulário novamente.");
			return;
		}

		try {
			Imovel imovel = montarImovel(requisicao);
			imovelServico.publicar(imovel, usuario);
			resposta.sendRedirect(requisicao.getContextPath() + "/imovel?id=" + imovel.getId());

		} catch (RegraNegocioException e) {
			reexibirFormulario(requisicao, resposta, e.getMessage());
		} catch (NumberFormatException e) {
			reexibirFormulario(requisicao, resposta, "Confira os valores numéricos informados (preço, área, quartos...).");
		} catch (IllegalArgumentException e) {
			reexibirFormulario(requisicao, resposta, "Confira os campos de tipo e negócio selecionados.");
		} catch (DAOException e) {
			getServletContext().log("Falha ao publicar o imóvel.", e);
			reexibirFormulario(requisicao, resposta,
					"Não foi possível publicar o anúncio agora. Tente novamente em instantes.");
		}
	}

	private Imovel montarImovel(HttpServletRequest requisicao) {
		Imovel imovel = new Imovel();
		imovel.setTitulo(requisicao.getParameter("titulo"));
		imovel.setDescricao(requisicao.getParameter("descricao"));
		imovel.setTipo(ConversorEnum.paraEnum(TipoImovel.class, requisicao.getParameter("tipo")));
		imovel.setFinalidade(ConversorEnum.paraEnum(Finalidade.class, requisicao.getParameter("finalidade")));
		imovel.setPreco(parseDecimal(requisicao.getParameter("preco")));
		imovel.setAreaM2(parseDouble(requisicao.getParameter("areaM2")));
		imovel.setQuartos(parseInteiro(requisicao.getParameter("quartos")));
		imovel.setBanheiros(parseInteiro(requisicao.getParameter("banheiros")));
		imovel.setVagasGaragem(parseInteiro(requisicao.getParameter("vagasGaragem")));
		imovel.setEndereco(requisicao.getParameter("endereco"));
		imovel.setCidade(requisicao.getParameter("cidade"));
		imovel.setEstado(requisicao.getParameter("estado"));
		imovel.setCep(requisicao.getParameter("cep"));
		return imovel;
	}

	private BigDecimal parseDecimal(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		return new BigDecimal(valor.trim().replace(",", "."));
	}

	private double parseDouble(String valor) {
		if (valor == null || valor.isBlank()) {
			return 0;
		}
		return Double.parseDouble(valor.trim().replace(",", "."));
	}

	private int parseInteiro(String valor) {
		if (valor == null || valor.isBlank()) {
			return 0;
		}
		return Integer.parseInt(valor.trim());
	}

	private void reexibirFormulario(HttpServletRequest requisicao, HttpServletResponse resposta, String mensagemErro)
			throws ServletException, IOException {

		requisicao.setAttribute("erro", mensagemErro);
		requisicao.setAttribute("titulo", Html.escapar(requisicao.getParameter("titulo")));
		requisicao.setAttribute("descricao", Html.escapar(requisicao.getParameter("descricao")));
		requisicao.setAttribute("tipo", Html.escapar(requisicao.getParameter("tipo")));
		requisicao.setAttribute("finalidade", Html.escapar(requisicao.getParameter("finalidade")));
		requisicao.setAttribute("preco", Html.escapar(requisicao.getParameter("preco")));
		requisicao.setAttribute("areaM2", Html.escapar(requisicao.getParameter("areaM2")));
		requisicao.setAttribute("quartos", Html.escapar(requisicao.getParameter("quartos")));
		requisicao.setAttribute("banheiros", Html.escapar(requisicao.getParameter("banheiros")));
		requisicao.setAttribute("vagasGaragem", Html.escapar(requisicao.getParameter("vagasGaragem")));
		requisicao.setAttribute("endereco", Html.escapar(requisicao.getParameter("endereco")));
		requisicao.setAttribute("cidade", Html.escapar(requisicao.getParameter("cidade")));
		requisicao.setAttribute("estado", Html.escapar(requisicao.getParameter("estado")));
		requisicao.setAttribute("cep", Html.escapar(requisicao.getParameter("cep")));
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA_ANUNCIO).forward(requisicao, resposta);
	}
}
