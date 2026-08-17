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
import model.FiltroImovel;
import model.ImovelServico;
import model.TipoImovel;
import util.ConversorEnum;
import util.Html;

/**
 * Catálogo público de imóveis: cards com busca e filtros, acessível sem
 * login (ver FiltroAutenticacao). O detalhamento completo continua exigindo
 * autenticação — essa página só mostra o resumo de cada card.
 *
 * Responde tanto em /imoveis quanto em /buscar, para reaproveitar o
 * formulário de busca já existente na landing page (que usa "negocio" para
 * a finalidade e "q" para texto livre de cidade).
 */
@WebServlet({ "/imoveis", "/buscar" })
public class CatalogoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/catalogo.jsp";

	private final ImovelServico imovelServico = new ImovelServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		FiltroImovel filtro = montarFiltro(requisicao);

		try {
			requisicao.setAttribute("imoveis", imovelServico.buscar(filtro));
		} catch (DAOException e) {
			getServletContext().log("Falha ao buscar imóveis no catálogo.", e);
			requisicao.setAttribute("erro", "Não foi possível carregar o catálogo agora. Tente novamente.");
		}

		requisicao.setAttribute("filtro", filtro);
		requisicao.setAttribute("cidadeDigitada", Html.escapar(requisicao.getParameter("q")));
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	private FiltroImovel montarFiltro(HttpServletRequest requisicao) {
		FiltroImovel filtro = new FiltroImovel();

		String cidade = valorNaoBranco(requisicao.getParameter("cidade"));
		if (cidade == null) {
			// A landing page usa "q" para o texto livre de bairro/cidade/região.
			cidade = valorNaoBranco(requisicao.getParameter("q"));
		}
		filtro.setCidade(cidade);

		filtro.setTipo(enumOuNulo(TipoImovel.class, requisicao.getParameter("tipo")));
		filtro.setFinalidade(resolverFinalidade(requisicao));
		filtro.setQuartosMinimo(inteiroOuNulo(requisicao.getParameter("quartosMinimo")));
		filtro.setPrecoMaximo(decimalOuNulo(requisicao.getParameter("precoMaximo")));

		return filtro;
	}

	/**
	 * A landing page usa "negocio=alugar/comprar"; o catálogo aceita também
	 * "finalidade=aluguel/venda" diretamente, para os links internos do sistema.
	 */
	private Finalidade resolverFinalidade(HttpServletRequest requisicao) {
		String negocio = requisicao.getParameter("negocio");
		if ("alugar".equalsIgnoreCase(negocio)) {
			return Finalidade.ALUGUEL;
		}
		if ("comprar".equalsIgnoreCase(negocio)) {
			return Finalidade.VENDA;
		}
		return enumOuNulo(Finalidade.class, requisicao.getParameter("finalidade"));
	}

	/**
	 * A landing page tem tipos fictícios (kitnet, cobertura) que não existem no
	 * enum real; nesse caso o filtro é simplesmente ignorado, em vez de quebrar
	 * a página com IllegalArgumentException.
	 */
	private <E extends Enum<E> & model.ValorBanco> E enumOuNulo(Class<E> tipoEnum, String valor) {
		try {
			return ConversorEnum.paraEnum(tipoEnum, valor);
		} catch (IllegalArgumentException e) {
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

	private BigDecimal decimalOuNulo(String valor) {
		try {
			return valor == null || valor.isBlank() ? null : new BigDecimal(valor.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String valorNaoBranco(String valor) {
		return valor == null || valor.isBlank() ? null : valor.trim();
	}
}
