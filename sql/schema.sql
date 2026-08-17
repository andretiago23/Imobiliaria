-- Script de criação do banco `imobiliaria` (marketplace multi-imobiliária).
-- Reflete o schema atual do projeto. Use este arquivo para recriar o banco do zero.
-- Para atualizar um banco já existente com o schema anterior (imobiliária única),
-- use sql/2025_migracao_marketplace.sql em vez deste.

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

CREATE TABLE usuario (
  id INT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  email_confirmado TINYINT(1) DEFAULT 0,
  senha VARCHAR(255) NOT NULL,
  cpf VARCHAR(11) NOT NULL,
  cpf_valido TINYINT(1) DEFAULT 0,
  telefone VARCHAR(20) DEFAULT NULL,
  telefone_confirmado TINYINT(1) DEFAULT 0,
  creci VARCHAR(20) DEFAULT NULL,
  foto_perfil VARCHAR(255) DEFAULT NULL,
  tipo_usuario ENUM('comprador','vendedor') NOT NULL DEFAULT 'comprador',
  id_imobiliaria INT DEFAULT NULL,
  consentimento_credito TINYINT(1) NOT NULL DEFAULT 0,
  data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY email (email),
  UNIQUE KEY cpf (cpf),
  KEY id_imobiliaria (id_imobiliaria),
  CONSTRAINT usuario_ibfk_1 FOREIGN KEY (id_imobiliaria) REFERENCES imobiliaria (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE imovel (
  id INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  titulo VARCHAR(200) NOT NULL,
  descricao TEXT,
  tipo ENUM('casa','apartamento','terreno','comercial','rural') NOT NULL,
  finalidade ENUM('venda','aluguel') NOT NULL,
  preco DECIMAL(12,2) NOT NULL,
  area_m2 DECIMAL(8,2) DEFAULT NULL,
  quartos INT DEFAULT NULL,
  banheiros INT DEFAULT NULL,
  vagas_garagem INT DEFAULT NULL,
  ano SMALLINT DEFAULT NULL,
  endereco VARCHAR(255) DEFAULT NULL,
  cidade VARCHAR(100) DEFAULT NULL,
  estado VARCHAR(2) DEFAULT NULL,
  cep VARCHAR(9) DEFAULT NULL,
  latitude DECIMAL(10,8) DEFAULT NULL,
  longitude DECIMAL(11,8) DEFAULT NULL,
  status ENUM('ativo','reservado','vendido','alugado','inativo') DEFAULT 'ativo',
  data_publicacao DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY id_usuario (id_usuario),
  CONSTRAINT imovel_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuario (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE foto_imovel (
  id INT NOT NULL AUTO_INCREMENT,
  id_imovel INT NOT NULL,
  url_foto VARCHAR(255) NOT NULL,
  ordem INT DEFAULT 0,
  PRIMARY KEY (id),
  KEY id_imovel (id_imovel),
  CONSTRAINT foto_imovel_ibfk_1 FOREIGN KEY (id_imovel) REFERENCES imovel (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE favorito (
  id INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  id_imovel INT NOT NULL,
  data_adicao DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY id_usuario (id_usuario, id_imovel),
  KEY id_imovel (id_imovel),
  CONSTRAINT favorito_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuario (id),
  CONSTRAINT favorito_ibfk_2 FOREIGN KEY (id_imovel) REFERENCES imovel (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE seguidor (
  id INT NOT NULL AUTO_INCREMENT,
  id_seguidor INT NOT NULL,
  id_seguido INT NOT NULL,
  data_inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY id_seguidor (id_seguidor, id_seguido),
  KEY id_seguido (id_seguido),
  CONSTRAINT seguidor_ibfk_1 FOREIGN KEY (id_seguidor) REFERENCES usuario (id),
  CONSTRAINT seguidor_ibfk_2 FOREIGN KEY (id_seguido) REFERENCES usuario (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE avaliacao (
  id INT NOT NULL AUTO_INCREMENT,
  id_avaliador INT NOT NULL,
  id_avaliado INT NOT NULL,
  id_imovel INT DEFAULT NULL,
  nota INT NOT NULL,
  comentario TEXT,
  data_avaliacao DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY id_avaliador (id_avaliador),
  KEY id_avaliado (id_avaliado),
  KEY id_imovel (id_imovel),
  CONSTRAINT avaliacao_ibfk_1 FOREIGN KEY (id_avaliador) REFERENCES usuario (id),
  CONSTRAINT avaliacao_ibfk_2 FOREIGN KEY (id_avaliado) REFERENCES usuario (id),
  CONSTRAINT avaliacao_ibfk_3 FOREIGN KEY (id_imovel) REFERENCES imovel (id),
  CONSTRAINT avaliacao_chk_1 CHECK (nota BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE contato_interesse (
  id INT NOT NULL AUTO_INCREMENT,
  id_imovel INT NOT NULL,
  id_comprador INT NOT NULL,
  mensagem TEXT,
  status ENUM('novo','contatado','negociando','convertido','perdido') DEFAULT 'novo',
  consulta_credito_autorizada TINYINT(1) NOT NULL DEFAULT 0,
  resultado_credito ENUM('nao_solicitado','nome_regular','restricao') NOT NULL DEFAULT 'nao_solicitado',
  data_contato DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY id_imovel (id_imovel),
  KEY id_comprador (id_comprador),
  CONSTRAINT contato_interesse_ibfk_1 FOREIGN KEY (id_imovel) REFERENCES imovel (id),
  CONSTRAINT contato_interesse_ibfk_2 FOREIGN KEY (id_comprador) REFERENCES usuario (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
