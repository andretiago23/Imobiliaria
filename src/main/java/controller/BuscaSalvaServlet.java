package controller;

import java.io.IOException;
import java.math.BigDecimal;

import dao.DAOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.BuscaSalva;
import model.BuscaSalvaServico;
import model.Finalidade;
import model.RegraNegocioException;
import model.TipoImovel;
import model.Usuario;
import util.ConversorEnum;
import util.SessaoUsuario;

/**
 * Área "Minhas buscas": salvar critérios de pesquisa e ligar/desligar o
 * alerta por e-mail associado a cada uma (o disparo do e-mail em si é uma
 * etapa futura do projeto — aqui só fica o cadastro e o opt-in).
 */
@WebServlet("/minhas-buscas")
public class BuscaSalvaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/minhas-buscas.jsp";

	private final BuscaSalvaServico buscaSalvaServico = new BuscaSalvaServico();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);

		Object erroPendente = requisicao.getSession().getAttribute("erroMinhasBuscas");
		if (erroPendente != null) {
			requisicao.getSession().removeAttribute("erroMinhasBuscas");
			requisicao.setAttribute("erro", erroPendente);
		}

		try {
			requisicao.setAttribute("buscas", buscaSalvaServico.listarDoUsuario(usuarioLogado.getId()));
		} catch (DAOException e) {
			getServletContext().log("Falha ao listar buscas salvas.", e);
			requisicao.setAttribute("erro", "Não foi possível carregar suas buscas agora.");
		}

		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuarioLogado = SessaoUsuario.obter(requisicao);
		String acao = requisicao.getParameter("acao");

		try {
			switch (acao == null ? "" : acao) {
				case "salvar" -> buscaSalvaServico.salvar(montarBusca(requisicao, usuarioLogado.getId()));
				case "pausar" -> buscaSalvaServico.pausarAlerta(idParametro(requisicao), usuarioLogado.getId());
				case "reativar" -> buscaSalvaServico.reativarAlerta(idParametro(requisicao), usuarioLogado.getId());
				case "excluir" -> buscaSalvaServico.excluir(idParametro(requisicao), usuarioLogado.getId());
				default -> throw new RegraNegocioException("Ação inválida.");
			}
		} catch (RegraNegocioException e) {
			requisicao.getSession().setAttribute("erroMinhasBuscas", e.getMessage());
		} catch (DAOException e) {
			getServletContext().log("Falha ao processar ação em busca salva.", e);
			requisicao.getSession().setAttribute("erroMinhasBuscas", "Não foi possível concluir a ação agora.");
		}

		resposta.sendRedirect(requisicao.getContextPath() + "/minhas-buscas");
	}

	private BuscaSalva montarBusca(HttpServletRequest requisicao, int idUsuario) {
		BuscaSalva busca = new BuscaSalva();
		busca.setIdUsuario(idUsuario);
		busca.setNome(requisicao.getParameter("nome"));
		busca.setTipo(enumOuNulo(TipoImovel.class, requisicao.getParameter("tipo")));
		busca.setFinalidade(enumOuNulo(Finalidade.class, requisicao.getParameter("finalidade")));
		busca.setCidade(requisicao.getParameter("cidade"));
		busca.setQuartosMinimo(inteiroOuNulo(requisicao.getParameter("quartosMinimo")));
		busca.setPrecoMaximo(decimalOuNulo(requisicao.getParameter("precoMaximo")));
		busca.setAlertaAtivo("on".equals(requisicao.getParameter("alertaAtivo")));
		return busca;
	}

	private int idParametro(HttpServletRequest requisicao) {
		return Integer.parseInt(requisicao.getParameter("id"));
	}

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
}
