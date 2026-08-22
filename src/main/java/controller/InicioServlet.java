package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import dao.DAOException;
import dao.FavoritoDAO;
import dao.FotoImovelDAO;
import dao.ImovelDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Finalidade;
import model.FiltroImovel;
import model.Imovel;
import model.TipoImovel;
import model.Usuario;
import util.ConversorEnum;
import util.SessaoUsuario;
import util.TokenCsrf;

/**
 * Catálogo de imóveis, aberto tanto a visitantes quanto a clientes logados.
 *
 * O detalhamento completo de um imóvel (rota /imovel) continua exigindo
 * login, barrado pelo FiltroAutenticacao. O resumo da conta (reputação,
 * avaliações, interesses) mora em /perfil, não aqui.
 */
@WebServlet("/inicio")
public class InicioServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA_INICIO = "/WEB-INF/jsp/inicio.jsp";

	private static final int LIMITE_FEED_SEM_FILTRO = 24;

	private final ImovelDAO imovelDAO = new ImovelDAO();
	private final FavoritoDAO favoritoDAO = new FavoritoDAO();
	private final FotoImovelDAO fotoImovelDAO = new FotoImovelDAO();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		carregarCatalogo(requisicao);
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));

		requisicao.getRequestDispatcher(PAGINA_INICIO).forward(requisicao, resposta);
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
			fotoImovelDAO.carregarFotos(imoveis);
			requisicao.setAttribute("imoveis", imoveis);

			Usuario usuarioLogado = SessaoUsuario.obter(requisicao);
			if (usuarioLogado != null) {
				Set<Integer> idsFavoritados = favoritoDAO.listarImoveisFavoritos(usuarioLogado.getId()).stream()
						.map(Imovel::getId)
						.collect(Collectors.toSet());
				requisicao.setAttribute("idsFavoritados", idsFavoritados);
			}
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar o catálogo de imóveis.", e);
			requisicao.setAttribute("erroCatalogo", "Não foi possível carregar os imóveis agora.");
		}
	}

	private FiltroImovel montarFiltro(HttpServletRequest requisicao) {
		FiltroImovel filtro = new FiltroImovel();
		filtro.setCidade(paramTexto(requisicao, "cidade"));
		filtro.setBairro(paramTexto(requisicao, "bairro"));
		filtro.setEstado(paramTexto(requisicao, "estado"));
		filtro.setTipo(paramEnum(TipoImovel.class, requisicao, "tipo"));
		filtro.setFinalidade(paramEnum(Finalidade.class, requisicao, "finalidade"));
		filtro.setPrecoMinimo(paramDecimal(requisicao, "precoMinimo"));
		filtro.setPrecoMaximo(paramDecimal(requisicao, "precoMaximo"));
		filtro.setQuartosMinimo(paramInteiro(requisicao, "quartosMinimo"));
		filtro.setBanheirosMinimo(paramInteiro(requisicao, "banheirosMinimo"));
		filtro.setVagasMinimo(paramInteiro(requisicao, "vagasMinimo"));
		filtro.setAreaMinima(paramDouble(requisicao, "areaMinima"));
		return filtro;
	}

	private boolean filtroVazio(FiltroImovel filtro) {
		return filtro.getCidade() == null && filtro.getBairro() == null && filtro.getEstado() == null && filtro.getTipo() == null
				&& filtro.getFinalidade() == null && filtro.getPrecoMinimo() == null
				&& filtro.getPrecoMaximo() == null && filtro.getQuartosMinimo() == null
				&& filtro.getBanheirosMinimo() == null && filtro.getVagasMinimo() == null
				&& filtro.getAreaMinima() == null;
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

	private Double paramDouble(HttpServletRequest requisicao, String nome) {
		String valor = paramTexto(requisicao, nome);
		if (valor == null) {
			return null;
		}
		try {
			return Double.valueOf(valor);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
