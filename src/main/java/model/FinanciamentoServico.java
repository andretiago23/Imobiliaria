package model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import dao.DAOException;
import dao.SimulacaoFinanciamentoDAO;

/**
 * Simulador de financiamento pelo sistema SAC (amortização constante).
 *
 * Todo o cálculo é ilustrativo: a taxa de juros é fictícia e fixa, não há
 * consulta a nenhuma instituição financeira real, e o resultado sempre deve
 * vir acompanhado do aviso de que é só uma estimativa (ver
 * PROJECT_SPEC seção 20).
 */
public class FinanciamentoServico {

	public static final int PRAZO_MINIMO_ANOS = 5;
	public static final int PRAZO_MAXIMO_ANOS = 35;

	/** Taxa de juros anual fictícia usada em todas as simulações do protótipo. */
	private static final BigDecimal TAXA_JUROS_ANUAL_FICTICIA = new BigDecimal("0.105");

	private static final int MESES_NO_ANO = 12;
	private static final int ESCALA_MONETARIA = 2;
	private static final MathContext PRECISAO_INTERMEDIARIA = new MathContext(20);

	private final SimulacaoFinanciamentoDAO simulacaoDAO = new SimulacaoFinanciamentoDAO();

	/**
	 * Calcula a simulação, sem gravar nada no banco. A gravação só acontece se
	 * o cliente decidir anexá-la a um lead (ver InteracaoServico).
	 */
	public SimulacaoFinanciamento simular(BigDecimal valorImovel, BigDecimal valorEntrada, int prazoAnos,
			String instituicaoReferencia) throws RegraNegocioException {

		validarDados(valorImovel, valorEntrada, prazoAnos);

		BigDecimal valorFinanciado = valorImovel.subtract(valorEntrada);
		int totalParcelas = prazoAnos * MESES_NO_ANO;
		BigDecimal amortizacaoMensal = valorFinanciado.divide(BigDecimal.valueOf(totalParcelas), PRECISAO_INTERMEDIARIA);
		BigDecimal taxaMensal = TAXA_JUROS_ANUAL_FICTICIA.divide(BigDecimal.valueOf(MESES_NO_ANO), PRECISAO_INTERMEDIARIA);

		BigDecimal saldoDevedor = valorFinanciado;
		BigDecimal totalJuros = BigDecimal.ZERO;
		BigDecimal parcelaInicial = null;

		for (int parcela = 1; parcela <= totalParcelas; parcela++) {
			BigDecimal jurosDoMes = saldoDevedor.multiply(taxaMensal);
			totalJuros = totalJuros.add(jurosDoMes);

			if (parcela == 1) {
				parcelaInicial = amortizacaoMensal.add(jurosDoMes);
			}

			saldoDevedor = saldoDevedor.subtract(amortizacaoMensal);
		}

		SimulacaoFinanciamento simulacao = new SimulacaoFinanciamento();
		simulacao.setValorImovel(arredondar(valorImovel));
		simulacao.setValorEntrada(arredondar(valorEntrada));
		simulacao.setPrazoAnos(prazoAnos);
		simulacao.setSistemaAmortizacao(SistemaAmortizacao.SAC);
		simulacao.setInstituicaoReferencia(instituicaoReferencia);
		simulacao.setValorFinanciado(arredondar(valorFinanciado));
		simulacao.setParcelaInicial(arredondar(parcelaInicial));
		simulacao.setTotalJuros(arredondar(totalJuros));
		return simulacao;
	}

	/**
	 * Grava a simulação, opcionalmente já vinculada a um lead. Usado quando o
	 * cliente escolhe anexar a simulação ao demonstrar interesse.
	 */
	public void salvar(SimulacaoFinanciamento simulacao) throws DAOException {
		simulacaoDAO.inserir(simulacao);
	}

	private void validarDados(BigDecimal valorImovel, BigDecimal valorEntrada, int prazoAnos)
			throws RegraNegocioException {

		if (valorImovel == null || valorImovel.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RegraNegocioException("Informe o valor do imóvel.");
		}
		if (valorEntrada == null || valorEntrada.compareTo(BigDecimal.ZERO) < 0) {
			throw new RegraNegocioException("Informe o valor de entrada.");
		}
		if (valorEntrada.compareTo(valorImovel) >= 0) {
			throw new RegraNegocioException("O valor de entrada deve ser menor que o valor do imóvel.");
		}
		if (prazoAnos < PRAZO_MINIMO_ANOS || prazoAnos > PRAZO_MAXIMO_ANOS) {
			throw new RegraNegocioException(
					"O prazo deve estar entre " + PRAZO_MINIMO_ANOS + " e " + PRAZO_MAXIMO_ANOS + " anos.");
		}
	}

	private BigDecimal arredondar(BigDecimal valor) {
		return valor.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
	}
}
