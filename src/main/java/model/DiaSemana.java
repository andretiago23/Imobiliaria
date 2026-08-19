package model;

/**
 * Corresponde à coluna ENUM dia_semana da tabela DISPONIBILIDADE_VISITA.
 */
public enum DiaSemana implements ValorBanco {

	SEG("SEG", "Segunda"),
	TER("TER", "Terça"),
	QUA("QUA", "Quarta"),
	QUI("QUI", "Quinta"),
	SEX("SEX", "Sexta"),
	SAB("SAB", "Sábado"),
	DOM("DOM", "Domingo");

	private final String valorBanco;
	private final String rotulo;

	DiaSemana(String valorBanco, String rotulo) {
		this.valorBanco = valorBanco;
		this.rotulo = rotulo;
	}

	@Override
	public String getValorBanco() {
		return valorBanco;
	}

	@Override
	public String getRotulo() {
		return rotulo;
	}

	/**
	 * @return o dia da semana correspondente ao java.time.DayOfWeek, usado
	 *         para bater a disponibilidade contra uma data real escolhida
	 *         pelo cliente ao agendar a visita
	 */
	public static DiaSemana deDayOfWeek(java.time.DayOfWeek diaDaSemana) {
		return switch (diaDaSemana) {
			case MONDAY -> SEG;
			case TUESDAY -> TER;
			case WEDNESDAY -> QUA;
			case THURSDAY -> QUI;
			case FRIDAY -> SEX;
			case SATURDAY -> SAB;
			case SUNDAY -> DOM;
		};
	}
}
