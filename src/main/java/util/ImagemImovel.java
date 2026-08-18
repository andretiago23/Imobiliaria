package util;

import java.util.List;
import java.util.Map;

import model.TipoImovel;

/**
 * Fotos ilustrativas para os imóveis que ainda não têm foto própria
 * cadastrada (ver FotoImovelDAO — quando isso existir, a foto real do
 * anúncio tem prioridade sobre esta).
 *
 * As imagens vêm do Pexels (pexels.com), sob a Licença Pexels: uso livre,
 * inclusive comercial, sem exigir atribuição. Ficam hospedadas no próprio
 * CDN deles — nenhum arquivo é baixado ou versionado neste projeto.
 */
public final class ImagemImovel {

	private static final Map<TipoImovel, List<String>> FOTOS_POR_TIPO = Map.of(
			TipoImovel.CASA, List.of(
					"https://images.pexels.com/photos/2128329/pexels-photo-2128329.jpeg?auto=compress&cs=tinysrgb&w=800",
					"https://images.pexels.com/photos/18078684/pexels-photo-18078684.jpeg?auto=compress&cs=tinysrgb&w=800",
					"https://images.pexels.com/photos/6342356/pexels-photo-6342356.jpeg?auto=compress&cs=tinysrgb&w=800"),
			TipoImovel.APARTAMENTO, List.of(
					"https://images.pexels.com/photos/16753029/pexels-photo-16753029.jpeg?auto=compress&cs=tinysrgb&w=800",
					"https://images.pexels.com/photos/19239905/pexels-photo-19239905.jpeg?auto=compress&cs=tinysrgb&w=800",
					"https://images.pexels.com/photos/2030037/pexels-photo-2030037.jpeg?auto=compress&cs=tinysrgb&w=800"),
			TipoImovel.TERRENO, List.of(
					"https://images.pexels.com/photos/27062931/pexels-photo-27062931.jpeg?auto=compress&cs=tinysrgb&w=800",
					"https://images.pexels.com/photos/30557705/pexels-photo-30557705.jpeg?auto=compress&cs=tinysrgb&w=800"),
			TipoImovel.COMERCIAL, List.of(
					"https://images.pexels.com/photos/8933650/pexels-photo-8933650.jpeg?auto=compress&cs=tinysrgb&w=800",
					"https://images.pexels.com/photos/37293743/pexels-photo-37293743.jpeg?auto=compress&cs=tinysrgb&w=800"),
			TipoImovel.RURAL, List.of(
					"https://images.pexels.com/photos/9027630/pexels-photo-9027630.jpeg?auto=compress&cs=tinysrgb&w=800",
					"https://images.pexels.com/photos/12464355/pexels-photo-12464355.jpeg?auto=compress&cs=tinysrgb&w=800"));

	private static final String PADRAO = FOTOS_POR_TIPO.get(TipoImovel.CASA).get(0);

	private ImagemImovel() {
		// Classe utilitária: não deve ser instanciada.
	}

	/**
	 * Escolhe uma foto ilustrativa para o imóvel, variando conforme o id para
	 * que anúncios do mesmo tipo não fiquem todos com a foto idêntica.
	 */
	public static String urlIlustrativa(TipoImovel tipo, int idImovel) {
		List<String> opcoes = FOTOS_POR_TIPO.get(tipo);
		if (opcoes == null || opcoes.isEmpty()) {
			return PADRAO;
		}
		int indice = Math.floorMod(idImovel, opcoes.size());
		return opcoes.get(indice);
	}
}
