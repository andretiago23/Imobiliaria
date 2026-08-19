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
 * Qualquer usuário autenticado pode publicar — comprador ou vendedor, cliente
 * "comum" ou imobiliária. Quem publica se torna automaticamente o
 * proprietário do anúncio (imovel.id_usuario), sem precisar mudar de tipo de
 * conta nem passar por um novo cadastro. A permissão para editar ou excluir
 * um anúncio depois é sempre checada comparando id_usuario com quem está
 * logado (ImovelServico.garantirPosse), nunca pelo tipo de conta.
 */
@WebServlet("/anunciar")
public class AnuncioServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_ANUNCIO = "/WEB-INF/jsp/anuncio.jsp";

	private final ImovelServico imovelServico = new ImovelServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA_ANUNCIO).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);

		if (!TokenCsrf.valido(requisicao)) {
			reexibirFormulario(requisicao, resposta, "Sua sessão expirou. Preencha o formulário novamente.");
			return;
		}

		try {
			Imovel imovel = montarImovel(requisicao);
			String linkBaseImovel = linkAbsoluto(requisicao, "/imovel?id=");
			imovelServico.publicar(imovel, usuario, linkBaseImovel);
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

	/**
	 * Monta a URL completa (com esquema e host) usada nos e-mails de alerta de
	 * busca salva — um caminho relativo não abriria corretamente fora do navegador.
	 */
	private String linkAbsoluto(HttpServletRequest requisicao, String caminho) {
		return requisicao.getRequestURL().toString().replace(requisicao.getRequestURI(), "")
				+ requisicao.getContextPath() + caminho;
	}
}
