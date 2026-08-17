package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

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

/**
 * Publicação e edição de imóveis pelo vendedor.
 *
 * GET sem id mostra o formulário de novo anúncio; GET com id mostra o
 * formulário preenchido para edição, desde que o imóvel pertença a quem está
 * logado. POST decide entre publicar ou atualizar pela presença do id.
 */
@WebServlet("/anunciar")
public class AnuncioServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/anunciar.jsp";

	private final ImovelServico imovelServico = new ImovelServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);
		if (!usuarioLogado.podeAnunciar()) {
			requisicao.setAttribute("erro", "Sua conta é do tipo comprador. Só vendedores podem anunciar imóveis.");
			requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
			return;
		}

		String idParametro = requisicao.getParameter("id");
		if (idParametro != null) {
			try {
				Optional<Imovel> imovel = imovelServico.buscarParaExibicao(Integer.parseInt(idParametro));
				if (imovel.isEmpty() || imovel.get().getIdUsuario() != usuarioLogado.getId()) {
					resposta.sendRedirect(requisicao.getContextPath() + "/meus-imoveis");
					return;
				}
				requisicao.setAttribute("imovel", imovel.get());
			} catch (NumberFormatException | DAOException e) {
				resposta.sendRedirect(requisicao.getContextPath() + "/meus-imoveis");
				return;
			}
		}

		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);
		String idParametro = requisicao.getParameter("id");

		try {
			Imovel imovel = montarImovel(requisicao);

			if (idParametro == null || idParametro.isBlank()) {
				imovelServico.publicar(imovel, usuarioLogado);
			} else {
				imovel.setId(Integer.parseInt(idParametro));
				imovelServico.atualizar(imovel, usuarioLogado.getId());
			}

			resposta.sendRedirect(requisicao.getContextPath() + "/meus-imoveis");

		} catch (RegraNegocioException | IllegalArgumentException e) {
			requisicao.setAttribute("erro", e.getMessage());
			requisicao.setAttribute("imovel", montarImovelParaReexibicao(requisicao));
			requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);

		} catch (DAOException e) {
			getServletContext().log("Falha ao salvar o anúncio.", e);
			requisicao.setAttribute("erro", "Não foi possível salvar o anúncio agora. Tente novamente.");
			requisicao.setAttribute("imovel", montarImovelParaReexibicao(requisicao));
			requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
		}
	}

	private Imovel montarImovel(HttpServletRequest requisicao) {
		Imovel imovel = new Imovel();
		imovel.setTitulo(requisicao.getParameter("titulo"));
		imovel.setDescricao(requisicao.getParameter("descricao"));
		imovel.setTipo(ConversorEnum.paraEnum(TipoImovel.class, requisicao.getParameter("tipo")));
		imovel.setFinalidade(ConversorEnum.paraEnum(Finalidade.class, requisicao.getParameter("finalidade")));
		imovel.setPreco(decimalOuNulo(requisicao.getParameter("preco")));
		imovel.setAreaM2(doubleOuZero(requisicao.getParameter("areaM2")));
		imovel.setQuartos(intOuZero(requisicao.getParameter("quartos")));
		imovel.setBanheiros(intOuZero(requisicao.getParameter("banheiros")));
		imovel.setVagasGaragem(intOuZero(requisicao.getParameter("vagasGaragem")));
		imovel.setAno(inteiroOuNulo(requisicao.getParameter("ano")));
		imovel.setEndereco(requisicao.getParameter("endereco"));
		imovel.setCidade(requisicao.getParameter("cidade"));
		imovel.setEstado(requisicao.getParameter("estado"));
		imovel.setCep(requisicao.getParameter("cep"));
		return imovel;
	}

	/**
	 * Reconstrói o objeto (com o mesmo id, se houver) só para o formulário
	 * devolver os dados digitados quando a validação falha.
	 */
	private Imovel montarImovelParaReexibicao(HttpServletRequest requisicao) {
		Imovel imovel = montarImovel(requisicao);
		imovel.setTitulo(Html.escapar(imovel.getTitulo()));
		imovel.setDescricao(Html.escapar(imovel.getDescricao()));
		imovel.setEndereco(Html.escapar(imovel.getEndereco()));
		imovel.setCidade(Html.escapar(imovel.getCidade()));
		String idParametro = requisicao.getParameter("id");
		if (idParametro != null && !idParametro.isBlank()) {
			try {
				imovel.setId(Integer.parseInt(idParametro));
			} catch (NumberFormatException e) {
				// Formulário de novo anúncio: sem id mesmo.
			}
		}
		return imovel;
	}

	private BigDecimal decimalOuNulo(String valor) {
		try {
			return valor == null || valor.isBlank() ? null : new BigDecimal(valor.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Integer inteiroOuNulo(String valor) {
		try {
			return valor == null || valor.isBlank() ? null : Integer.valueOf(valor.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private int intOuZero(String valor) {
		try {
			return valor == null || valor.isBlank() ? 0 : Integer.parseInt(valor.trim());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private double doubleOuZero(String valor) {
		try {
			return valor == null || valor.isBlank() ? 0 : Double.parseDouble(valor.trim());
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
