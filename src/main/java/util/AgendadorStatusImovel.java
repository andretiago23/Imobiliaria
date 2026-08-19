package util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dao.ConfirmacaoStatusDAO;
import dao.DAOException;
import dao.ImovelDAO;
import dao.UsuarioDAO;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import model.Imovel;
import model.StatusImovel;
import model.Usuario;

/**
 * Confirmação periódica de status: dispara o e-mail "ainda está
 * disponível?" para imóveis sem nenhuma mudança de status há 15 dias, e
 * muda o status para PENDENTE_CONFIRMACAO quando o anunciante não responde
 * dentro de mais 7 dias depois disso.
 *
 * O projeto é Servlets/JSP puro, sem Spring — a forma mais simples de
 * rodar uma tarefa periódica aqui é um ScheduledExecutorService iniciado
 * neste ServletContextListener no startup da aplicação, encerrado de
 * volta no shutdown para não vazar a thread quando o Tomcat recarrega o
 * contexto (reloadable="true").
 */
@WebListener
public class AgendadorStatusImovel implements ServletContextListener {

	private static final System.Logger LOG = System.getLogger(AgendadorStatusImovel.class.getName());

	private static final int DIAS_SEM_ATUALIZACAO_PARA_EMAIL = 15;
	private static final int DIAS_SEM_RESPOSTA_PARA_PENDENTE = 7;

	/**
	 * Intervalo entre execuções. Um cron "de verdade" rodaria uma vez por
	 * dia; para o job dar sinal de vida num ambiente de demonstração sem
	 * esperar 24h, a varredura roda a cada hora — como as janelas de dias são
	 * checadas por data/hora no banco (não por "rodou hoje?"), rodar mais
	 * vezes não duplica e-mail nem muda o resultado, só antecipa quando ele é
	 * detectado.
	 */
	private static final long INTERVALO_HORAS = 1;

	private final ImovelDAO imovelDAO = new ImovelDAO();
	private final ConfirmacaoStatusDAO confirmacaoStatusDAO = new ConfirmacaoStatusDAO();
	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final EmailService emailService = new EmailService();
	private final SecureRandom geradorAleatorio = new SecureRandom();

	private ScheduledExecutorService executor;

	@Override
	public void contextInitialized(ServletContextEvent evento) {
		executor = Executors.newSingleThreadScheduledExecutor(execucao -> {
			Thread thread = new Thread(execucao, "agendador-status-imovel");
			thread.setDaemon(true);
			return thread;
		});
		executor.scheduleAtFixedRate(this::executarVarredura, 1, TimeUnit.HOURS.toMinutes(INTERVALO_HORAS), TimeUnit.MINUTES);
		LOG.log(System.Logger.Level.INFO, "Agendador de confirmação de status iniciado.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent evento) {
		if (executor != null) {
			executor.shutdownNow();
		}
	}

	private void executarVarredura() {
		try {
			dispararEmailsDeConfirmacao();
		} catch (Exception e) {
			LOG.log(System.Logger.Level.WARNING, "Falha ao disparar e-mails de confirmação de status.", e);
		}
		try {
			marcarPendentesDeConfirmacao();
		} catch (Exception e) {
			LOG.log(System.Logger.Level.WARNING, "Falha ao marcar imóveis como pendentes de confirmação.", e);
		}
	}

	/**
	 * Imóveis ativos/reservados sem mudança de status há 15 dias recebem o
	 * e-mail — mas só um por vez: se já existe uma confirmação pendente para
	 * aquele imóvel, não manda outra.
	 */
	private void dispararEmailsDeConfirmacao() throws DAOException {
		List<Imovel> semAtualizacao = imovelDAO.listarSemAtualizacaoHa(DIAS_SEM_ATUALIZACAO_PARA_EMAIL);
		for (Imovel imovel : semAtualizacao) {
			if (confirmacaoStatusDAO.existePendente(imovel.getId())) {
				continue;
			}
			String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenAleatorio());
			confirmacaoStatusDAO.criar(imovel.getId(), token);

			Usuario proprietario = usuarioDAO.buscarPorId(imovel.getIdUsuario()).orElse(null);
			if (proprietario != null) {
				String linkConfirmacao = ConfiguracaoEmail.urlBase() + "/confirmar-status?token=" + token;
				emailService.notificarPedidoConfirmacaoStatus(proprietario, imovel, linkConfirmacao);
			}
		}
	}

	/**
	 * Confirmações enviadas há mais de 7 dias e ainda sem resposta: o imóvel
	 * some do catálogo até o anunciante confirmar manualmente (ver
	 * "Imóveis anunciados", que também permite reativar).
	 */
	private void marcarPendentesDeConfirmacao() throws DAOException {
		List<Integer> idsExpirados = confirmacaoStatusDAO.listarImoveisComConfirmacaoExpirada(DIAS_SEM_RESPOSTA_PARA_PENDENTE);
		for (Integer idImovel : idsExpirados) {
			imovelDAO.atualizarStatus(idImovel, StatusImovel.PENDENTE_CONFIRMACAO);
		}
	}

	private byte[] tokenAleatorio() {
		byte[] bytes = new byte[24];
		geradorAleatorio.nextBytes(bytes);
		return bytes;
	}
}
