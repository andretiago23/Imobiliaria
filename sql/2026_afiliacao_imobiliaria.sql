-- Migração: afiliação de vendedores a uma imobiliária por código de convite.
--
-- Cria a tabela `imobiliaria` e a coluna `usuario.id_imobiliaria`. Sem
-- parceria real com nenhuma imobiliária, o cadastro delas é feito dentro do
-- próprio sistema (ver ImobiliariaServlet, em /imobiliarias/nova — tela sem
-- link em nenhuma página, só acessível por quem sabe a URL) e cada uma recebe
-- um código único que os vendedores digitam no próprio cadastro para provar
-- o vínculo.
--
-- Execute uma vez, no banco já existente do projeto.

CREATE TABLE imobiliaria (
  id INT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(150) NOT NULL,
  codigo VARCHAR(20) NOT NULL,
  ativa TINYINT(1) NOT NULL DEFAULT 1,
  data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE usuario
  ADD COLUMN id_imobiliaria INT DEFAULT NULL AFTER tipo_usuario,
  ADD KEY id_imobiliaria (id_imobiliaria),
  ADD CONSTRAINT usuario_ibfk_imobiliaria FOREIGN KEY (id_imobiliaria) REFERENCES imobiliaria (id);
