package util;

import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import model.ContatoInteresse;
import model.Imovel;
import model.Usuario;
import model.VisitaAgendada;

/**
 * Ponto único de envio de e-mail da aplicação (JavaMail/jakarta.mail), usado
 * para notificar o proprietário quando recebe um lead, confirmar o envio
 * para quem demonstrou interesse e avisar clientes com busca salva quando um
 * imóvel compatível é publicado.
 *
 * O envio é síncrono, como combinado para o escopo do projeto — aceitável
 * porque cada chamada acontece depois que a ação principal (gravar o lead,
 * publicar o anúncio) já foi persistida com sucesso. Uma falha de SMTP fica
 * só registrada no log do servidor: nunca propaga para quebrar a resposta ao
 * usuário, e por isso os métodos aqui não lançam exceção checada.
 */
public class EmailService {

	private static final System.Logger LOG = System.getLogger(EmailService.class.getName());

	/**
	 * Monta a sessão SMTP a partir de mail.properties a cada envio.
	 *
	 * A troca de conexão a cada e-mail é o suficiente para o volume de uma
	 * aplicação acadêmica; um pool de sessões SMTP fica fora de escopo.
	 */
	private Session criarSessao() {
		Properties propriedades = new Properties();
		propriedades.put("mail.smtp.host", ConfiguracaoEmail.host());
		propriedades.put("mail.smtp.port", String.valueOf(ConfiguracaoEmail.porta()));
		propriedades.put("mail.smtp.auth", String.valueOf(ConfiguracaoEmail.autenticar()));
		propriedades.put("mail.smtp.starttls.enable", String.valueOf(ConfiguracaoEmail.starttls()));

		return Session.getInstance(propriedades, new jakarta.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(ConfiguracaoEmail.usuario(), ConfiguracaoEmail.senha());
			}
		});
	}

	/**
	 * Envio genérico, em texto simples. Não lança exceção: registra a falha
	 * no log da aplicação e segue em frente, já que e-mail é um reforço de
	 * comunicação, nunca o único registro da ação (o lead/anúncio já está no
	 * banco antes deste método ser chamado).
	 */
	public void enviar(String destinatario, String assunto, String corpo) {
		if (!ConfiguracaoEmail.configurado()) {
			LOG.log(System.Logger.Level.INFO,
					"E-mail não enviado (SMTP não configurado em mail.properties): {0} — {1}", destinatario, assunto);
			return;
		}
		if (destinatario == null || destinatario.isBlank()) {
			return;
		}

		try {
			Session sessao = criarSessao();
			MimeMessage mensagem = new MimeMessage(sessao);
			mensagem.setFrom(new InternetAddress(ConfiguracaoEmail.remetente(), ConfiguracaoEmail.nomeRemetente()));
			mensagem.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
			mensagem.setSubject(assunto, "UTF-8");
			mensagem.setText(corpo, "UTF-8");

			Transport.send(mensagem);
		} catch (MessagingException | java.io.UnsupportedEncodingException e) {
			LOG.log(System.Logger.Level.WARNING, "Falha ao enviar e-mail para " + destinatario, e);
		}
	}

	/**
	 * Notifica o proprietário do imóvel que recebeu um novo lead, e envia uma
	 * confirmação para quem demonstrou interesse. Disparado pelo
	 * InteracaoServico logo após o lead ser gravado no banco.
	 *
	 * @param linkPainel URL completa da página de detalhe/painel, já montada
	 *                   pelo chamador (que tem acesso ao contextPath da requisição)
	 */
	public void notificarNovoLead(ContatoInteresse lead, Imovel imovel, Usuario proprietario, Usuario cliente,
			String linkPainel) {

		String corpoProprietario = """
				Você recebeu um novo interesse no anúncio "%s".

				De: %s (%s)
				Mensagem: %s

				Veja os detalhes e responda pelo painel:
				%s

				— Habittar
				""".formatted(imovel.getTitulo(), cliente.getNome(), cliente.getEmail(), lead.getMensagem(), linkPainel);

		enviar(proprietario.getEmail(), "Novo interesse em \"" + imovel.getTitulo() + "\"", corpoProprietario);

		String corpoCliente = """
				Recebemos sua mensagem de interesse no imóvel "%s".

				O anunciante recebeu seus dados de contato e a mensagem enviada, e vai
				entrar em contato em breve.

				Confira o anúncio novamente quando quiser:
				%s

				— Habittar
				""".formatted(imovel.getTitulo(), linkPainel);

		enviar(cliente.getEmail(), "Recebemos seu interesse em \"" + imovel.getTitulo() + "\"", corpoCliente);
	}

	/**
	 * Avisa um cliente que um imóvel compatível com uma busca salva sua
	 * acabou de ser publicado. Disparado pelo ImovelServico depois de
	 * publicar um anúncio, uma vez por busca_salva compatível — a checagem de
	 * duplicidade (não avisar duas vezes do mesmo imóvel) é feita antes de
	 * chamar este método, via BuscaSalvaDAO.jaNotificado/registrarNotificacao.
	 */
	public void notificarAlertaBuscaSalva(Usuario cliente, model.BuscaSalva busca, Imovel imovel, String linkImovel) {
		String nomeBusca = (busca.getNome() != null && !busca.getNome().isBlank()) ? busca.getNome() : "sua busca salva";

		String corpo = """
				Um novo imóvel compatível com "%s" acabou de ser publicado:

				%s
				%s

				Veja os detalhes:
				%s

				Isso é uma simulação ilustrativa de alerta automático da Habittar.
				Você pode desativar esse alerta a qualquer momento em Minhas Buscas.

				— Habittar
				""".formatted(nomeBusca, imovel.getTitulo(), imovel.getEnderecoCompleto(), linkImovel);

		enviar(cliente.getEmail(), "Novo imóvel compatível com " + nomeBusca, corpo);
	}

	/**
	 * Avisa o anunciante que um cliente marcou uma visita — disparado por
	 * model.VisitaServico logo após gravar o agendamento.
	 */
	public void notificarVisitaAgendada(VisitaAgendada visita, Imovel imovel, Usuario proprietario, Usuario cliente,
			String linkPainel) {

		String dataFormatada = visita.getDataVisita().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

		String corpo = """
				%s agendou uma visita ao imóvel "%s".

				Data: %s
				Horário: %s às %s

				Contato do cliente: %s (%s)

				Veja os detalhes do anúncio:
				%s

				— Habittar
				""".formatted(cliente.getNome(), imovel.getTitulo(), dataFormatada, visita.getHoraInicio(),
				visita.getHoraFim(), cliente.getEmail(), cliente.getTelefone() == null ? "sem telefone" : cliente.getTelefone(),
				linkPainel);

		enviar(proprietario.getEmail(), "Nova visita agendada — \"" + imovel.getTitulo() + "\"", corpo);
	}

	/**
	 * E-mail periódico "ainda está disponível?", disparado pelo job agendado
	 * (util.AgendadorStatusImovel) para imóveis sem mudança de status há 15
	 * dias. O link de confirmação leva ao ConfirmacaoStatusServlet, que
	 * apenas reseta o contador — sem confirmação em 7 dias, o job seguinte
	 * muda o status para PENDENTE_CONFIRMACAO.
	 */
	public void notificarPedidoConfirmacaoStatus(Usuario proprietario, Imovel imovel, String linkConfirmacao) {
		String corpo = """
				O anúncio "%s" está há um tempo sem nenhuma atualização.

				Ele ainda está disponível? Confirme clicando no link abaixo — se não
				confirmar em até 7 dias, o anúncio sai temporariamente do catálogo
				até você confirmar.

				%s

				— Habittar
				""".formatted(imovel.getTitulo(), linkConfirmacao);

		enviar(proprietario.getEmail(), "Seu anúncio \"" + imovel.getTitulo() + "\" ainda está disponível?", corpo);
	}
}
