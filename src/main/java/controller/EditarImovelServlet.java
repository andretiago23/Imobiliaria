package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dao.DAOException;
import dao.DisponibilidadeVisitaDAO;
import dao.ImovelDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DiaSemana;
import model.DisponibilidadeVisita;
import model.Finalidade;
import model.Imovel;
import model.ImovelServico;
import model.RegraNegocioException;
import model.TipoImovel;
import model.Usuario;
import util.ConversorEnum;
import util.SessaoUsuario;
import util.TokenCsrf;

/**
 * Edição de um anúncio já publicado, a partir de "Imóveis anunciados" —
 * inclui a disponibilidade de horários de visita. A posse é sempre checada
 * comparando id_usuario com quem está logado (ImovelServico.atualizar),
 * nunca pelo tipo de conta.
 */
@WebServlet("/editar-imovel")
public class EditarImovelServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PAGINA = "/WEB-INF/jsp/editar-imovel.jsp";

	private final ImovelDAO imovelDAO = new ImovelDAO();
	private final ImovelServico imovelServico = new ImovelServico();
	private final DisponibilidadeVisitaDAO disponibilidadeVisitaDAO = new DisponibilidadeVisitaDAO();

	@Override
	protected void doGet(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);
		Integer id = idValido(requisicao.getParameter("id"));
		if (id == null) {
			resposta.sendRedirect(requisicao.getContextPath() + "/imoveis-anunciados");
			return;
		}

		try {
			Optional<Imovel> imovel = imovelDAO.buscarPorId(id);
			if (imovel.isEmpty() || imovel.get().getIdUsuario() != usuario.getId()) {
				resposta.sendRedirect(requisicao.getContextPath() + "/imoveis-anunciados");
				return;
			}
			requisicao.setAttribute("imovel", imovel.get());
			requisicao.setAttribute("disponibilidade", disponibilidadeVisitaDAO.listarPorImovel(id));
		} catch (DAOException e) {
			getServletContext().log("Falha ao carregar o imóvel " + id + " para edição.", e);
			resposta.sendRedirect(requisicao.getContextPath() + "/imoveis-anunciados");
			return;
		}

		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
	}

	@Override
	protected void doPost(HttpServletRequest requisicao, HttpServletResponse resposta)
			throws ServletException, IOException {

		Usuario usuario = SessaoUsuario.obter(requisicao);
		Integer id = idValido(requisicao.getParameter("id"));

		if (id == null || !TokenCsrf.valido(requisicao)) {
			resposta.sendRedirect(requisicao.getContextPath() + "/imoveis-anunciados");
			return;
		}

		try {
			Imovel imovel = montarImovel(requisicao, id);
			// O UPDATE grava status/vagas/latitude/longitude junto com o resto —
			// preserva os valores atuais para não sobrescrevê-los, já que este
			// formulário não os edita.
			Optional<Imovel> atual = imovelDAO.buscarPorId(id);
			atual.ifPresent(existente -> {
				imovel.setStatus(existente.getStatus());
				imovel.setVagasGaragem(existente.getVagasGaragem());
				imovel.setLatitude(existente.getLatitude());
				imovel.setLongitude(existente.getLongitude());
			});
			imovelServico.atualizar(imovel, usuario.getId());

			String[] diasMarcados = requisicao.getParameterValues("diaSemana");
			List<DisponibilidadeVisita> janelas = new ArrayList<>();
			if (diasMarcados != null && diasMarcados.length > 0) {
				LocalTime horaInicio = LocalTime.parse(requisicao.getParameter("horaInicio"));
				LocalTime horaFim = LocalTime.parse(requisicao.getParameter("horaFim"));
				for (String diaTexto : diasMarcados) {
					DiaSemana dia = ConversorEnum.paraEnum(DiaSemana.class, diaTexto);
					if (dia != null) {
						janelas.add(new DisponibilidadeVisita(id, dia, horaInicio, horaFim));
					}
				}
			}
			disponibilidadeVisitaDAO.salvarTodas(id, janelas);

			resposta.sendRedirect(requisicao.getContextPath() + "/imoveis-anunciados");

		} catch (RegraNegocioException e) {
			reexibir(requisicao, resposta, id, e.getMessage());
		} catch (NumberFormatException | java.time.format.DateTimeParseException e) {
			reexibir(requisicao, resposta, id, "Confira os valores informados.");
		} catch (DAOException e) {
			getServletContext().log("Falha ao salvar edição do imóvel " + id + ".", e);
			reexibir(requisicao, resposta, id, "Não foi possível salvar agora. Tente novamente.");
		}
	}

	private Imovel montarImovel(HttpServletRequest requisicao, int id) {
		Imovel imovel = new Imovel();
		imovel.setId(id);
		imovel.setTitulo(requisicao.getParameter("titulo"));
		imovel.setDescricao(requisicao.getParameter("descricao"));
		imovel.setTipo(ConversorEnum.paraEnum(TipoImovel.class, requisicao.getParameter("tipo")));
		imovel.setFinalidade(ConversorEnum.paraEnum(Finalidade.class, requisicao.getParameter("finalidade")));
		imovel.setPreco(parseDecimal(requisicao.getParameter("preco")));
		imovel.setAreaM2(parseDouble(requisicao.getParameter("areaM2")));
		imovel.setQuartos(parseInteiro(requisicao.getParameter("quartos")));
		imovel.setBanheiros(parseInteiro(requisicao.getParameter("banheiros")));
		imovel.setEndereco(requisicao.getParameter("endereco"));
		imovel.setCidade(requisicao.getParameter("cidade"));
		imovel.setEstado(requisicao.getParameter("estado"));
		imovel.setCep(requisicao.getParameter("cep"));
		return imovel;
	}

	private void reexibir(HttpServletRequest requisicao, HttpServletResponse resposta, int id, String mensagemErro)
			throws ServletException, IOException {

		try {
			Optional<Imovel> imovel = imovelDAO.buscarPorId(id);
			requisicao.setAttribute("imovel", imovel.orElse(null));
			requisicao.setAttribute("disponibilidade", disponibilidadeVisitaDAO.listarPorImovel(id));
		} catch (DAOException e) {
			getServletContext().log("Falha ao recarregar o imóvel " + id + ".", e);
		}
		requisicao.setAttribute("erro", mensagemErro);
		requisicao.setAttribute("csrf", TokenCsrf.obter(requisicao));
		requisicao.getRequestDispatcher(PAGINA).forward(requisicao, resposta);
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

	private BigDecimal parseDecimal(String valor) {
		String limpo = valorMonetarioLimpo(valor);
		return limpo == null ? null : new BigDecimal(limpo);
	}

	private double parseDouble(String valor) {
		String limpo = valorMonetarioLimpo(valor);
		return limpo == null ? 0 : Double.parseDouble(limpo);
	}

	private String valorMonetarioLimpo(String valor) {
		if (valor == null || valor.isBlank()) {
			return null;
		}
		String semSeparador = valor.replaceAll("[^0-9,]", "");
		return semSeparador.isBlank() ? null : semSeparador.replace(",", ".");
	}

	private int parseInteiro(String valor) {
		if (valor == null || valor.isBlank()) {
			return 0;
		}
		return Integer.parseInt(valor.trim());
	}
}
