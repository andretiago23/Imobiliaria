# PROJECT_SPEC.md — Marketplace Imobiliário

## 1. Visão geral do projeto

O projeto será uma plataforma web no formato de **marketplace imobiliário**.

O sistema não representa uma única imobiliária.

A plataforma deverá permitir a participação de diferentes vendedores, porém os vendedores deverão estar vinculados a uma imobiliária para a qual prestam serviço.

O objetivo é permitir que compradores encontrem imóveis anunciados por vendedores vinculados a imobiliárias, mantendo uma estrutura organizada e transparente.

A plataforma deverá seguir uma estrutura **Java Web MVC**.

---

# 2. Arquitetura do projeto

O projeto deve utilizar:

- Java
- Java Web
- Arquitetura MVC (Model-View-Controller)
- Apache Tomcat 11
- Java 21

A aplicação deve manter separação entre:

### Model

Responsável pelos dados e pela comunicação com o banco:

- Beans/Entidades
- DAOs
- Regras relacionadas aos dados

### View

Responsável pela interface:

- JSP
- HTML
- CSS
- JavaScript

### Controller

Responsável pelo fluxo da aplicação:

- Servlets
- Recebimento de requisições
- Processamento das ações
- Controle de navegação
- Comunicação entre View e Model

---

# 3. Perfis de acesso

O sistema terá, inicialmente, dois tipos principais de usuários:

1. Cliente/Comprador
2. Vendedor

O tipo de usuário deverá ser definido durante o cadastro.

---

# 4. Cadastro de usuário

Durante o cadastro, o usuário deverá selecionar qual será seu perfil:

- Cliente/Comprador
- Vendedor

O sistema deverá aplicar regras diferentes de acordo com o perfil escolhido.

---

# 5. Cliente / Comprador

O cliente é o usuário interessado em encontrar e visualizar imóveis.

O cliente poderá:

- Navegar pelo catálogo de imóveis.
- Pesquisar imóveis.
- Utilizar filtros.
- Visualizar cards de imóveis.
- Acessar informações dos imóveis de acordo com as permissões do sistema.
- Demonstrar interesse em um imóvel.
- Utilizar o comparador de imóveis.
- Utilizar o simulador de financiamento.
- Salvar buscas.
- Receber alertas por e-mail quando houver imóveis compatíveis.
- Informar CPF quando necessário.
- Autorizar ou não uma consulta de crédito simulada.

---

# 6. Vendedor

O vendedor será o usuário responsável por anunciar ou administrar imóveis dentro da plataforma.

Um vendedor NÃO será considerado uma imobiliária.

Todo vendedor deverá estar vinculado a uma imobiliária para a qual presta serviço.

Essa informação deverá fazer parte do cadastro/perfil do vendedor.

---

# 7. Vínculo do vendedor com uma imobiliária

Durante o cadastro de um vendedor, o sistema deverá exigir a identificação da imobiliária para a qual ele presta serviço.

O objetivo é:

- Evitar vendedores sem vínculo definido.
- Organizar os anúncios por imobiliária.
- Evitar divergências sobre quem representa determinado imóvel.
- Melhorar a confiabilidade da plataforma.
- Permitir identificação da imobiliária responsável pelo vendedor.
- Facilitar futuras funcionalidades de reputação, controle e administração.

O vendedor deverá possuir uma relação com uma imobiliária.

Conceitualmente:

```text
Imobiliária
    ↓
Vendedores vinculados
    ↓
Imóveis anunciados/administrados
```

Um vendedor poderá estar vinculado a uma imobiliária conforme as regras definidas pelo sistema.

---

# 8. Imobiliária

A imobiliária passa a ser uma entidade da plataforma e não o único proprietário do catálogo.

A plataforma deverá permitir o cadastro/registro das imobiliárias necessárias para vincular vendedores.

A imobiliária deverá poder ser identificada a partir do vendedor e dos imóveis relacionados.

As regras exatas de criação, validação e administração das imobiliárias deverão ser definidas conforme a implementação existente e as necessidades do projeto.

---

# 9. Requisitos para vendedor

O vendedor deverá cumprir os requisitos já existentes no código/projeto para possuir esse perfil.

Esses requisitos devem ser preservados.

Ao adaptar o sistema para o marketplace, deverá ser acrescentada a informação referente à imobiliária à qual o vendedor está vinculado.

Não remover requisitos de vendedor que já existam no projeto sem necessidade.

---

# 10. Catálogo de imóveis

A plataforma terá um catálogo centralizado de imóveis.

Diferentemente de uma imobiliária única, o catálogo poderá possuir imóveis relacionados a diferentes vendedores e imobiliárias.

Cada imóvel deverá possuir informações como:

- Código.
- Título.
- Tipo.
- Operação.
- Cidade.
- Bairro.
- Endereço.
- Valor.
- Área.
- Quartos.
- Banheiros.
- Vagas.
- Ano.
- Descrição.
- Status.
- Fotos.
- Vendedor responsável.
- Imobiliária à qual o vendedor está vinculado.

---

# 11. Relação entre imóvel, vendedor e imobiliária

A estrutura deverá permitir identificar claramente:

```text
Imóvel
   ↓
Vendedor responsável
   ↓
Imobiliária vinculada
```

Essa relação é importante para evitar divergências e permitir rastreabilidade dos anúncios.

O sistema não deve permitir que um imóvel fique associado a um vendedor sem que seja possível identificar sua imobiliária vinculada.

---

# 12. Status dos imóveis

Os imóveis poderão possuir estados como:

- Disponível
- Reservado
- Vendido
- Alugado

### Disponível

O imóvel aparece normalmente no catálogo.

### Reservado

O imóvel continua no catálogo, mas deverá apresentar indicação visual de que está reservado.

### Vendido

O imóvel não deverá aparecer normalmente no catálogo público, mas deverá permanecer armazenado para histórico.

### Alugado

O imóvel não deverá aparecer normalmente no catálogo público, mas deverá permanecer armazenado para histórico.

Imóveis vendidos ou alugados não devem ser apagados automaticamente do banco.

---

# 13. Busca de imóveis

A plataforma deverá permitir que clientes pesquisem imóveis.

Filtros previstos:

- Tipo de imóvel.
- Operação:
  - Venda
  - Aluguel
- Cidade.
- Quartos mínimos.
- Valor máximo.

Os filtros poderão ser combinados.

---

# 14. Cards de imóveis

O catálogo deverá apresentar cards contendo informações básicas, como:

- Foto.
- Título.
- Bairro.
- Cidade.
- Preço.
- Área.
- Quartos.
- Banheiros.
- Vagas.

Também deverá ser possível identificar o vendedor e/ou a imobiliária responsável pelo anúncio conforme as regras de exposição definidas pela plataforma.

---

# 15. Detalhamento do imóvel

O detalhamento poderá apresentar:

- Fotos.
- Código.
- Título.
- Tipo.
- Operação.
- Cidade.
- Bairro.
- Endereço.
- Valor.
- Área.
- Quartos.
- Banheiros.
- Vagas.
- Ano.
- Descrição.
- Status.
- Vendedor responsável.
- Imobiliária vinculada.

O nível de informação disponível para visitantes não autenticados deverá respeitar as regras de acesso já definidas para o projeto.

---

# 16. Comparador

O cliente poderá selecionar até 3 imóveis para comparação.

A comparação deverá apresentar:

- Preço.
- Área.
- Preço por m².
- Quartos.
- Banheiros.
- Vagas.
- Ano.

O preço por m² deverá ser calculado automaticamente:

```text
preço por m² = valor do imóvel / área do imóvel
```

Não permitir mais de 3 imóveis simultaneamente.

---

# 17. Tenho interesse

O cliente poderá demonstrar interesse em um imóvel através de uma ação explícita.

Exemplo:

```text
Tenho interesse
```

O simples acesso ao imóvel, login, pesquisa ou visualização não deverá criar automaticamente um lead.

O lead deverá ser criado somente após uma ação explícita do cliente.

---

# 18. Lead

O lead deverá conter, quando aplicável:

- Cliente.
- Imóvel.
- Vendedor responsável.
- Imobiliária vinculada.
- Data/hora.
- Status.
- Dados da simulação, quando houver.
- Resultado da verificação de crédito, quando houver e quando autorizado.

Status previstos:

- Novo
- Contatado
- Negociando
- Convertido
- Perdido

---

# 19. Simulador de financiamento

O cliente poderá utilizar um simulador de financiamento.

Campos:

- Valor do imóvel.
- Valor de entrada.
- Entrada em percentual ou valor.
- Prazo.
- Sistema de amortização.
- Instituição de referência.

O valor do imóvel deverá ser inicialmente preenchido com o preço do anúncio, mas poderá ser editado.

O prazo poderá utilizar uma faixa como:

```text
5 a 35 anos
```

O sistema poderá utilizar SAC ou Price.

Para simplificação do protótipo, poderá ser utilizado somente SAC.

---

# 20. Resultado da simulação

O sistema deverá apresentar:

- Valor financiado.
- Parcela inicial estimada.
- Total de juros estimado.
- Prazo.
- Instituição de referência.

Deve existir o aviso:

> Simulação ilustrativa. Sujeita à análise de crédito da instituição financeira.

As taxas utilizadas no protótipo serão fictícias.

Não haverá integração real com bancos.

---

# 21. Simulação anexada ao lead

Durante o fluxo de interesse, o cliente poderá escolher:

```text
[ ] Enviar simulação de financiamento junto
```

Quando selecionado, os dados da simulação deverão ser anexados ao lead.

A imobiliária/vendedor responsável deverá conseguir visualizar esses dados conforme suas permissões.

---

# 22. Buscas salvas

O cliente poderá salvar uma busca.

A busca poderá conter:

- Tipo.
- Operação.
- Cidade.
- Quartos mínimos.
- Valor máximo.

O cliente poderá ativar:

```text
Avisar por e-mail quando aparecer imóvel novo com esse perfil
```

---

# 23. Minhas buscas

O cliente deverá possuir uma área para gerenciar suas buscas.

Funcionalidades:

- Visualizar buscas.
- Visualizar critérios.
- Pausar alerta.
- Reativar alerta.
- Excluir busca.

---

# 24. Alertas por e-mail

Os alertas deverão ser opt-in.

O sistema não deverá enviar e-mail automaticamente para toda busca realizada.

O cliente deverá salvar a busca e ativar o alerta.

Quando um novo imóvel compatível for disponibilizado, o sistema deverá identificar clientes com buscas correspondentes.

---

# 25. Conteúdo do alerta

O e-mail poderá conter:

- Foto.
- Título.
- Preço.
- Características principais.
- Link para o imóvel.
- Opção de cancelar aquele alerta específico.

---

# 26. Regra de não reenvio

O sistema não deverá enviar novamente o mesmo imóvel para o mesmo cliente caso o imóvel seja apenas editado posteriormente.

Deve existir algum controle de envio para evitar duplicidade.

---

# 27. Verificação de crédito

A verificação de crédito será opcional e simulada.

Quando o cliente informar CPF, deverá aparecer:

```text
[ ] Autorizo a consulta do meu CPF em birô de crédito para agilizar minha análise
```

O checkbox deverá estar desmarcado por padrão.

Sem autorização:

- Não realizar consulta.
- Não gerar resultado de crédito.

Com autorização:

- Executar somente uma consulta fictícia no protótipo.

---

# 28. Resultado da consulta de crédito

A consulta simulada deverá retornar somente:

```text
Nome regular
```

ou:

```text
Restrição encontrada
```

Não apresentar:

- Valor de dívida.
- Credor.
- Relatório completo.
- Dados financeiros detalhados.

Não haverá integração real com Serasa, SPC ou outro birô.

---

# 29. Revogação do consentimento

O cliente deverá poder revogar a autorização de consulta de crédito nas configurações do perfil.

Após a revogação, novas consultas não deverão ser realizadas sem uma nova autorização explícita.

---

# 30. Regras de negócio fundamentais

## Regra 1 — Marketplace

O sistema não representa uma única imobiliária.

Existirão diferentes imobiliárias e vendedores vinculados a elas.

## Regra 2 — Vendedor

Todo vendedor deverá possuir uma imobiliária vinculada.

## Regra 3 — Cliente

O usuário poderá escolher o perfil de cliente/comprador durante o cadastro.

## Regra 4 — Vendedor

O usuário poderá escolher o perfil de vendedor durante o cadastro, mas deverá cumprir os requisitos definidos pelo projeto.

## Regra 5 — Imóvel

Todo imóvel deverá possuir vendedor responsável.

## Regra 6 — Imobiliária

O vendedor responsável deverá estar vinculado a uma imobiliária.

## Regra 7 — Lead

Lead somente poderá ser criado por ação explícita do cliente.

## Regra 8 — Histórico

Imóveis vendidos ou alugados não devem ser excluídos automaticamente.

## Regra 9 — Alertas

Alertas por e-mail somente serão enviados quando o cliente tiver optado por salvar a busca e ativar o alerta.

## Regra 10 — Crédito

Consulta de crédito simulada somente poderá ocorrer após consentimento explícito.

## Regra 11 — Simulação

A simulação é fictícia e não representa proposta de financiamento.

## Regra 12 — Bancos

Não existe integração real com instituições financeiras.

---

# 31. Pontos que precisam ser definidos na implementação

Os seguintes pontos ainda precisam ser definidos antes ou durante a implementação:

- Como uma imobiliária será cadastrada.
- Quem poderá cadastrar uma imobiliária.
- Como será validado o vínculo entre vendedor e imobiliária.
- Se um vendedor poderá trabalhar para mais de uma imobiliária.
- Se uma imobiliária poderá possuir vários vendedores.
- Se um imóvel poderá possuir mais de um vendedor responsável.
- Quem poderá editar um imóvel.
- Quem poderá remover/desativar um imóvel.
- Se a imobiliária poderá visualizar todos os imóveis de seus vendedores.
- Se o vendedor poderá visualizar somente seus próprios imóveis.
- Se o cliente verá o nome da imobiliária no card.
- Se o cliente verá o nome do vendedor no card.
- Como será feita a validação dos requisitos já existentes para vendedor.
- Como será feita a validação da existência da imobiliária.
- Como será tratado um vendedor que perder o vínculo com uma imobiliária.
- Como serão tratados imóveis cujo vendedor seja desvinculado da imobiliária.

Essas decisões devem ser tomadas antes de criar regras permanentes no banco ou no backend.

---

# 32. Segurança e controle de acesso

Mesmo sendo um protótipo, as permissões deverão ser verificadas no backend.

Não confiar apenas em esconder botões na interface.

Deve existir controle para impedir que:

- Um cliente acesse funções de vendedor.
- Um vendedor altere imóveis de outro vendedor sem permissão.
- Um vendedor altere informações de uma imobiliária à qual não pertence.
- Um usuário acesse dados administrativos diretamente pela URL.
- Um usuário consulte dados de outro cliente sem autorização.

---

# 33. Princípios para implementação

A IA responsável pelo desenvolvimento deverá:

1. Ler este arquivo antes de implementar novas funcionalidades.
2. Respeitar a arquitetura Java MVC.
3. Preservar funcionalidades já existentes no projeto quando forem compatíveis com este documento.
4. Não remover requisitos existentes para vendedor sem solicitação.
5. Adicionar o vínculo entre vendedor e imobiliária.
6. Manter separação entre Model, View e Controller.
7. Validar permissões no backend.
8. Não criar integrações externas que não foram solicitadas.
9. Não criar consulta real de crédito.
10. Não criar integração real com bancos.
11. Não criar leads automaticamente.
12. Não enviar alertas sem opt-in.
13. Não excluir imóveis vendidos ou alugados do histórico.
14. Evitar funcionalidades fora do escopo.

---

# 34. Estrutura conceitual das relações

A estrutura principal deverá seguir a lógica:

```text
USUÁRIO
   │
   ├── CLIENTE
   │
   └── VENDEDOR
          │
          ↓
      IMOBILIÁRIA
          │
          ↓
       IMÓVEIS
          │
          ↓
        LEADS
```

Uma visão mais detalhada:

```text
Cliente
   │
   ├── Buscas salvas
   ├── Alertas
   ├── Simulações
   └── Leads
          │
          ↓
       Imóvel
          │
          ↓
       Vendedor
          │
          ↓
     Imobiliária
```

---

# 35. Objetivo do MVP

O MVP deverá demonstrar um marketplace imobiliário funcional no qual:

```text
Usuário
   ↓
Escolhe perfil
   ↓
Cliente ou Vendedor
```

Para vendedor:

```text
Vendedor
   ↓
Cumpre requisitos existentes
   ↓
Informa/seleciona imobiliária
   ↓
Fica vinculado à imobiliária
   ↓
Pode trabalhar com imóveis conforme suas permissões
```

Para cliente:

```text
Cliente
   ↓
Pesquisa imóveis
   ↓
Filtra
   ↓
Visualiza detalhes
   ↓
Compara
   ↓
Simula financiamento
   ↓
Demonstra interesse
   ↓
Gera lead
```

A plataforma deverá manter a relação:

```text
Imóvel → Vendedor → Imobiliária
```

para garantir organização, rastreabilidade e maior confiabilidade do marketplace.

---

# FIM DA ESPECIFICAÇÃO
