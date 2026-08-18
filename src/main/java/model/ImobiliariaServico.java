package model;

import java.util.Optional;

import dao.DAOException;
import dao.ImobiliariaDAO;
import util.GeradorCodigo;

/**
 * Regras de negócio das imobiliárias.
 *
 * Como o sistema não tem parceria real com nenhuma imobiliária, o cadastro é
 * feito dentro do próprio sistema, sem aprovação externa: quem cria a
 * imobiliária recebe um código único, que os vendedores dela vão digitar no
 * próprio cadastro para provar o vínculo (ver UsuarioServico.cadastrar).
 */
public class ImobiliariaServico {

	private static final int TENTATIVAS_MAXIMAS_CODIGO = 10;

	private final ImobiliariaDAO imobiliariaDAO = new ImobiliariaDAO();

	/**
	 * Cadastra uma nova imobiliária e gera o código de vínculo dela.
	 *
	 * @return o código gerado, que deve ser mostrado ao usuário para ele
	 *         repassar aos vendedores da imobiliária
	 */
	public String cadastrar(Imobiliaria imobiliaria) throws RegraNegocioException, DAOException {
		if (imobiliaria.getNome() == null || imobiliaria.getNome().isBlank()) {
			throw new RegraNegocioException("Informe o nome da imobiliária.");
		}

		imobiliaria.setAtiva(true);
		imobiliaria.setCodigo(gerarCodigoUnico());
		imobiliariaDAO.inserir(imobiliaria);
		return imobiliaria.getCodigo();
	}

	/**
	 * Usado no cadastro do vendedor, para conferir o código informado.
	 */
	public Optional<Imobiliaria> buscarPorCodigo(String codigo) throws DAOException {
		if (codigo == null || codigo.isBlank()) {
			return Optional.empty();
		}
		return imobiliariaDAO.buscarPorCodigo(codigo.trim().toUpperCase());
	}

	/**
	 * Gera um código aleatório e confere no banco até achar um que ainda não
	 * existe. Na prática a primeira tentativa quase sempre já é única, dado o
	 * tamanho do alfabeto usado em GeradorCodigo.
	 */
	private String gerarCodigoUnico() throws DAOException {
		for (int tentativa = 0; tentativa < TENTATIVAS_MAXIMAS_CODIGO; tentativa++) {
			String codigo = GeradorCodigo.gerarCodigoImobiliaria();
			if (!imobiliariaDAO.existeCodigo(codigo)) {
				return codigo;
			}
		}
		throw new IllegalStateException("Não foi possível gerar um código único para a imobiliária.");
	}
}
