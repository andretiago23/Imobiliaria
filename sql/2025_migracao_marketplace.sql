-- Migração: transforma o banco de imobiliária única em marketplace multi-imobiliária.
-- Aplicado sobre o banco `imobiliaria` já existente (não recria as tabelas).

-- 1. Nova entidade: imobiliária
CREATE TABLE imobiliaria (
  id INT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(150) NOT NULL,
  codigo VARCHAR(20) NOT NULL,
  cnpj VARCHAR(18) DEFAULT NULL,
  telefone VARCHAR(20) DEFAULT NULL,
  email VARCHAR(150) DEFAULT NULL,
  cidade VARCHAR(100) DEFAULT NULL,
  estado VARCHAR(2) DEFAULT NULL,
  ativa TINYINT(1) NOT NULL DEFAULT 1,
  data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. usuario: remove tipo "ambos", acrescenta CRECI, vínculo com imobiliária e consentimento de crédito
ALTER TABLE usuario
  MODIFY COLUMN tipo_usuario ENUM('comprador','vendedor') NOT NULL DEFAULT 'comprador',
  ADD COLUMN creci VARCHAR(20) DEFAULT NULL AFTER telefone_confirmado,
  ADD COLUMN id_imobiliaria INT DEFAULT NULL AFTER tipo_usuario,
  ADD COLUMN consentimento_credito TINYINT(1) NOT NULL DEFAULT 0 AFTER id_imobiliaria,
  ADD KEY id_imobiliaria (id_imobiliaria),
  ADD CONSTRAINT usuario_ibfk_1 FOREIGN KEY (id_imobiliaria) REFERENCES imobiliaria (id);

-- 3. imovel: acrescenta ano de construção e o status "reservado"
ALTER TABLE imovel
  ADD COLUMN ano SMALLINT DEFAULT NULL AFTER vagas_garagem,
  MODIFY COLUMN status ENUM('ativo','reservado','vendido','alugado','inativo') DEFAULT 'ativo';

-- 4. contato_interesse: vira o "lead" de fato, com funil e dados da verificação de crédito
ALTER TABLE contato_interesse
  MODIFY COLUMN status ENUM('novo','contatado','negociando','convertido','perdido') DEFAULT 'novo',
  ADD COLUMN consulta_credito_autorizada TINYINT(1) NOT NULL DEFAULT 0 AFTER status,
  ADD COLUMN resultado_credito ENUM('nao_solicitado','nome_regular','restricao') NOT NULL DEFAULT 'nao_solicitado' AFTER consulta_credito_autorizada;

-- 5. Simulação de financiamento, opcionalmente anexada a um lead
CREATE TABLE simulacao_financiamento (
  id INT NOT NULL AUTO_INCREMENT,
  id_contato INT DEFAULT NULL,
  valor_imovel DECIMAL(12,2) NOT NULL,
  valor_entrada DECIMAL(12,2) NOT NULL,
  prazo_anos INT NOT NULL,
  sistema_amortizacao ENUM('sac') NOT NULL DEFAULT 'sac',
  instituicao_referencia VARCHAR(100) DEFAULT NULL,
  valor_financiado DECIMAL(12,2) NOT NULL,
  parcela_inicial DECIMAL(12,2) NOT NULL,
  total_juros DECIMAL(12,2) NOT NULL,
  data_simulacao DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY id_contato (id_contato),
  CONSTRAINT simulacao_financiamento_ibfk_1 FOREIGN KEY (id_contato) REFERENCES contato_interesse (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6. Buscas salvas do cliente
CREATE TABLE busca_salva (
  id INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  nome VARCHAR(100) DEFAULT NULL,
  tipo ENUM('casa','apartamento','terreno','comercial','rural') DEFAULT NULL,
  finalidade ENUM('venda','aluguel') DEFAULT NULL,
  cidade VARCHAR(100) DEFAULT NULL,
  quartos_minimo INT DEFAULT NULL,
  preco_maximo DECIMAL(12,2) DEFAULT NULL,
  alerta_ativo TINYINT(1) NOT NULL DEFAULT 0,
  data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY id_usuario (id_usuario),
  CONSTRAINT busca_salva_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuario (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 7. Controle de envio de alerta, para não notificar o mesmo imóvel duas vezes
-- (o disparo de e-mail em si fica para depois; a tabela só evita reenvio quando existir)
CREATE TABLE busca_salva_notificacao (
  id INT NOT NULL AUTO_INCREMENT,
  id_busca_salva INT NOT NULL,
  id_imovel INT NOT NULL,
  data_envio DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY id_busca_salva (id_busca_salva, id_imovel),
  KEY id_imovel (id_imovel),
  CONSTRAINT busca_salva_notificacao_ibfk_1 FOREIGN KEY (id_busca_salva) REFERENCES busca_salva (id),
  CONSTRAINT busca_salva_notificacao_ibfk_2 FOREIGN KEY (id_imovel) REFERENCES imovel (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
