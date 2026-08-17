package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import dao.DAOException;
import dao.ImovelDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Finalidade;
import model.FiltroImovel;
import model.Imovel;
import model.InteracaoServico;
import model.TipoImovel;
import model.Usuario;
import util.ConversorEnum;
import util.SessaoUsuario;

/**
 * Catálogo de imóveis, aberto tanto a visitantes quanto a clientes logados.
 *
 * Quem não tem sessão vê o mesmo catálogo e a mesma busca, mas sem o resumo
 * de conta; o detalhamento completo de um imóvel (rota /imovel) continua
 * exigindo login, barrado pelo FiltroAutenticacao.
 */
@WebServlet("/inicio")
public class InicioServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_INICIO = "/WEB-INF/jsp/inicio.jsp";

	private static final int LIMITE_FEED_SEM_FILTRO = 24;

	private final InteracaoServico interacaoServico = new InteracaoServico();
	private final ImovelDAO imovelDAO = new ImovelDAO();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);

		if (usuario != null) {
			carregarResumoConta(requisicao, usuario);
		}

		carregarCatalogo(requisicao);

		requisicao.getRequestDispatcher(PAGINA_INICIO).forward(requisicao, resposta);
	}

	/**
	 * A nota é formatada aqui para que o JSP cuide apenas da apresentação,
	 * sem precisar de biblioteca de tags para arredondar o número.
	 */
	private void carregarResumoConta(HttpServletRequest requisicao, Usuario usuario) {
		try {
			double reputacao = interacaoServico.calcularReputacao(usuario.getId());
			requisicao.setAttribute("reputacao", String.format("%.1f", reputacao));
			requisicao.setAttribute("totalAvaliacoes", interacaoServico.contarAvaliacoes(usuario.getId()));
			requisicao.setAttribute("interessesPendentes",
					interacaoServico.contarInteressesPendentes(usuario.getId()));
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar o resumo da conta.", e);
			requisicao.setAttribute("erro", "Não foi possível carregar seus dados agora.");
		}
	}

	/**
	 * Monta o filtro a partir da query string e busca os imóveis correspondentes.
	 * Sem nenhum filtro preenchido, mostra os anúncios ativos mais recentes.
	 */
	private void carregarCatalogo(HttpServletRequest requisicao) {
		FiltroImovel filtro = montarFiltro(requisicao);
		requisicao.setAttribute("filtro", filtro);

		try {
			List<Imovel> imoveis = filtroVazio(filtro)
					? imovelDAO.listarAtivos(LIMITE_FEED_SEM_FILTRO)
					: imovelDAO.buscarComFiltros(filtro);
			requisicao.setAttribute("imoveis", imoveis);
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar o catálogo de imóveis.", e);
			requisicao.setAttribute("erroCatalogo", "Não foi possível carregar os imóveis agora.");
		}
	}

	private FiltroImovel montarFiltro(HttpServletRequest requisicao) {
		FiltroImovel filtro = new FiltroImovel();
		filtro.setCidade(paramTexto(requisicao, "cidade"));
		filtro.setEstado(paramTexto(requisicao, "estado"));
		filtro.setTipo(paramEnum(TipoImovel.class, requisicao, "tipo"));
		filtro.setFinalidade(paramEnum(Finalidade.class, requisicao, "finalidade"));
		filtro.setPrecoMinimo(paramDecimal(requisicao, "precoMinimo"));
		filtro.setPrecoMaximo(paramDecimal(requisicao, "precoMaximo"));
		filtro.setQuartosMinimo(paramInteiro(requisicao, "quartosMinimo"));
		return filtro;
	}

	private boolean filtroVazio(FiltroImovel filtro) {
		return filtro.getCidade() == null && filtro.getEstado() == null && filtro.getTipo() == null
				&& filtro.getFinalidade() == null && filtro.getPrecoMinimo() == null
				&& filtro.getPrecoMaximo() == null && filtro.getQuartosMinimo() == null;
	}

	private String paramTexto(HttpServletRequest requisicao, String nome) {
		String valor = requisicao.getParameter(nome);
		return (valor == null || valor.isBlank()) ? null : valor.trim();
	}

	private <E extends Enum<E> & model.ValorBanco> E paramEnum(Class<E> tipoEnum, HttpServletRequest requisicao,
			String nome) {
		String valor = paramTexto(requisicao, nome);
		try {
			return ConversorEnum.paraEnum(tipoEnum, valor);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private BigDecimal paramDecimal(HttpServletRequest requisicao, String nome) {
		String valor = paramTexto(requisicao, nome);
		if (valor == null) {
			return null;
		}
		try {
			return new BigDecimal(valor);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Integer paramInteiro(HttpServletRequest requisicao, String nome) {
		String valor = paramTexto(requisicao, nome);
		if (valor == null) {
			return null;
		}
		try {
			return Integer.valueOf(valor);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
