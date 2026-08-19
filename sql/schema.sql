-- ============================================================================
-- Habittar — schema completo (MySQL 8)
-- ============================================================================
-- Gerado a partir do banco em produção (schema real usado pela aplicação),
-- reordenado por dependência de chave estrangeira e comentado por seção.
--
-- Convenções:
--   - Toda tabela usa AUTO_INCREMENT em id como chave primária.
--   - ENUMs em vez de tabelas de domínio — aceitável na escala deste projeto;
--     os valores espelham os enums Java correspondentes (ver util.ConversorEnum).
--   - Nenhuma FK usa ON DELETE CASCADE: exclusões em cascata são feitas na
--     camada de serviço (ver model.ImovelServico.excluir), na ordem certa,
--     para manter o controle explícito sobre o que é removido.
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------------------------
-- imobiliaria — imobiliárias profissionais (cadastro à parte; um usuário do
-- tipo "vendedor" pode opcionalmente estar vinculado a uma, ver usuario.id_imobiliaria)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `imobiliaria` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) NOT NULL,
  `codigo` varchar(20) NOT NULL,
  `cnpj` varchar(18) DEFAULT NULL,
  `telefone` varchar(20) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `cidade` varchar(100) DEFAULT NULL,
  `estado` varchar(2) DEFAULT NULL,
  `ativa` tinyint(1) NOT NULL DEFAULT '1',
  `data_cadastro` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------------------------------------------------------
-- usuario — conta única para comprador e vendedor/imobiliária.
--
-- tipo_usuario é o "perfil principal" de cadastro; NÃO restringe quem pode
-- anunciar. Qualquer conta autenticada pode publicar um imóvel e virar
-- proprietário dele (imovel.id_usuario) sem precisar mudar de tipo — ver
-- model.Usuario.podeAnunciar() e a checagem de posse em
-- model.ImovelServico.garantirPosse(), que compara id_usuario, nunca o
-- campo tipo_usuario.
--
-- consentimento_credito é o opt-in explícito (desmarcado por padrão) para a
-- consulta de crédito simplificada anexada a um lead — sem ele, o CPF fica
-- só como dado de contato e o resultado nunca é detalhado (ver
-- contato_interesse.resultado_credito, que só assume "nome_regular" ou
-- "restricao", nunca um relatório completo).
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `apelido` varchar(60) DEFAULT NULL,
  `email` varchar(150) NOT NULL,
  `email_confirmado` tinyint(1) DEFAULT '0',
  `senha` varchar(255) NOT NULL,
  `cpf` varchar(11) NOT NULL,
  `cpf_valido` tinyint(1) DEFAULT '0',
  `telefone` varchar(20) DEFAULT NULL,
  `telefone_confirmado` tinyint(1) DEFAULT '0',
  `creci` varchar(20) DEFAULT NULL,
  `foto_perfil` varchar(255) DEFAULT NULL,
  `tipo_usuario` enum('comprador','vendedor') NOT NULL DEFAULT 'comprador',
  `id_imobiliaria` int DEFAULT NULL,
  `consentimento_credito` tinyint(1) NOT NULL DEFAULT '0',
  `termos_aceitos_em` datetime DEFAULT NULL,
  `data_cadastro` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `cpf` (`cpf`),
  KEY `id_imobiliaria` (`id_imobiliaria`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`id_imobiliaria`) REFERENCES `imobiliaria` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------------------------------------------------------
-- imovel — id_usuario é o proprietário/anunciante, seja ele um cliente
-- comum ou uma imobiliária. status controla a visibilidade no catálogo
-- público: 'ativo' e 'reservado' aparecem na busca; 'vendido', 'alugado' e
-- 'inativo' somem do catálogo mas continuam no histórico (ImovelDAO.listarPorUsuario).
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `imovel` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `titulo` varchar(200) NOT NULL,
  `descricao` text,
  `tipo` enum('casa','apartamento','terreno','comercial','rural') NOT NULL,
  `finalidade` enum('venda','aluguel') NOT NULL,
  `preco` decimal(12,2) NOT NULL,
  `area_m2` decimal(8,2) DEFAULT NULL,
  `quartos` int DEFAULT NULL,
  `banheiros` int DEFAULT NULL,
  `vagas_garagem` int DEFAULT NULL,
  `ano` smallint DEFAULT NULL,
  `endereco` varchar(255) DEFAULT NULL,
  `cidade` varchar(100) DEFAULT NULL,
  `estado` varchar(2) DEFAULT NULL,
  `cep` varchar(9) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `status` enum('ativo','reservado','vendido','alugado','inativo') DEFAULT 'ativo',
  `data_publicacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `imovel_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------------------------------------------------------
-- foto_imovel — fotos do carrossel de um anúncio, por URL (sem upload de
-- arquivo). ordem define a posição no carrossel; a de menor valor é a capa.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `foto_imovel` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_imovel` int NOT NULL,
  `url_foto` varchar(255) NOT NULL,
  `ordem` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `id_imovel` (`id_imovel`),
  CONSTRAINT `foto_imovel_ibfk_1` FOREIGN KEY (`id_imovel`) REFERENCES `imovel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------------------------------------------------------
-- contato_interesse — o "lead": criado só pela ação explícita "Tenho
-- interesse" (nunca por login ou visualização passiva). Ao ser gravado,
-- dispara dois e-mails via util.EmailService.notificarNovoLead: um para o
-- proprietário do imóvel, outro de confirmação para quem demonstrou interesse.
--
-- consulta_credito_autorizada / resultado_credito implementam o opt-in de
-- CPF: sem autorização, resultado_credito fica 'nao_solicitado' e o CPF
-- nunca é usado para nada além de contato.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `contato_interesse` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_imovel` int NOT NULL,
  `id_comprador` int NOT NULL,
  `mensagem` text,
  `status` enum('novo','contatado','negociando','convertido','perdido') DEFAULT 'novo',
  `consulta_credito_autorizada` tinyint(1) NOT NULL DEFAULT '0',
  `resultado_credito` enum('nao_solicitado','nome_regular','restricao') NOT NULL DEFAULT 'nao_solicitado',
  `data_contato` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `id_imovel` (`id_imovel`),
  KEY `id_comprador` (`id_comprador`),
  CONSTRAINT `contato_interesse_ibfk_1` FOREIGN KEY (`id_imovel`) REFERENCES `imovel` (`id`),
  CONSTRAINT `contato_interesse_ibfk_2` FOREIGN KEY (`id_comprador`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------------------------------------------------------
-- simulacao_financiamento — simulação de financiamento anexada a um lead
-- (opcional: id_contato aceita NULL para simulações feitas sem enviar
-- interesse ainda). Taxas e instituições são fictícias, com aviso de
-- "simulação ilustrativa" sempre exibido na interface.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `simulacao_financiamento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_contato` int DEFAULT NULL,
  `valor_imovel` decimal(12,2) NOT NULL,
  `valor_entrada` decimal(12,2) NOT NULL,
  `prazo_anos` int NOT NULL,
  `sistema_amortizacao` enum('sac') NOT NULL DEFAULT 'sac',
  `instituicao_referencia` varchar(100) DEFAULT NULL,
  `valor_financiado` decimal(12,2) NOT NULL,
  `parcela_inicial` decimal(12,2) NOT NULL,
  `total_juros` decimal(12,2) NOT NULL,
  `data_simulacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_contato` (`id_contato`),
  CONSTRAINT `simulacao_financiamento_ibfk_1` FOREIGN KEY (`id_contato`) REFERENCES `contato_interesse` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------------------------------------------------------
-- busca_salva — filtro de catálogo salvo pelo cliente, com alerta por
-- e-mail opcional (alerta_ativo, desligado por padrão). Verificada pelo
-- model.ImovelServico logo após um imóvel ser publicado
-- (dispararAlertasDeBuscaSalva), usando model.BuscaSalva.combinaCom(Imovel).
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `busca_salva` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `nome` varchar(100) DEFAULT NULL,
  `tipo` enum('casa','apartamento','terreno','comercial','rural') DEFAULT NULL,
  `finalidade` enum('venda','aluguel') DEFAULT NULL,
  `cidade` varchar(100) DEFAULT NULL,
  `quartos_minimo` int DEFAULT NULL,
  `preco_maximo` decimal(12,2) DEFAULT NULL,
  `alerta_ativo` tinyint(1) NOT NULL DEFAULT '0',
  `data_criacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `busca_salva_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------------------------------------------------------
-- busca_salva_notificacao — registro de "esse alerta já foi enviado", para
-- não avisar o mesmo cliente duas vezes sobre o mesmo imóvel. A UNIQUE KEY
-- é a garantia de banco; dao.BuscaSalvaDAO.jaNotificado() faz a mesma
-- checagem antes de tentar o INSERT, para não depender só da exceção de
-- chave duplicada.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `busca_salva_notificacao` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_busca_salva` int NOT NULL,
  `id_imovel` int NOT NULL,
  `data_envio` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_busca_salva` (`id_busca_salva`,`id_imovel`),
  KEY `id_imovel` (`id_imovel`),
  CONSTRAINT `busca_salva_notificacao_ibfk_1` FOREIGN KEY (`id_busca_salva`) REFERENCES `busca_salva` (`id`),
  CONSTRAINT `busca_salva_notificacao_ibfk_2` FOREIGN KEY (`id_imovel`) REFERENCES `imovel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------------------------------------------------------
-- favorito — imóveis marcados pelo cliente para acompanhar depois.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `favorito` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `id_imovel` int NOT NULL,
  `data_adicao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_usuario` (`id_usuario`,`id_imovel`),
  KEY `id_imovel` (`id_imovel`),
  CONSTRAINT `favorito_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`),
  CONSTRAINT `favorito_ibfk_2` FOREIGN KEY (`id_imovel`) REFERENCES `imovel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------------------------------------------------------
-- seguidor — um usuário acompanhando outro (ex.: cliente seguindo uma
-- imobiliária para ver novos anúncios).
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `seguidor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_seguidor` int NOT NULL,
  `id_seguido` int NOT NULL,
  `data_inicio` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_seguidor` (`id_seguidor`,`id_seguido`),
  KEY `id_seguido` (`id_seguido`),
  CONSTRAINT `seguidor_ibfk_1` FOREIGN KEY (`id_seguidor`) REFERENCES `usuario` (`id`),
  CONSTRAINT `seguidor_ibfk_2` FOREIGN KEY (`id_seguido`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------------------------------------------------------
-- avaliacao — nota (1 a 5) e comentário de um usuário sobre outro, ligada
-- opcionalmente ao imóvel da negociação.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `avaliacao` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_avaliador` int NOT NULL,
  `id_avaliado` int NOT NULL,
  `id_imovel` int DEFAULT NULL,
  `nota` int NOT NULL,
  `comentario` text,
  `data_avaliacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `id_avaliador` (`id_avaliador`),
  KEY `id_avaliado` (`id_avaliado`),
  KEY `id_imovel` (`id_imovel`),
  CONSTRAINT `avaliacao_ibfk_1` FOREIGN KEY (`id_avaliador`) REFERENCES `usuario` (`id`),
  CONSTRAINT `avaliacao_ibfk_2` FOREIGN KEY (`id_avaliado`) REFERENCES `usuario` (`id`),
  CONSTRAINT `avaliacao_ibfk_3` FOREIGN KEY (`id_imovel`) REFERENCES `imovel` (`id`),
  CONSTRAINT `avaliacao_chk_1` CHECK ((`nota` between 1 and 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
