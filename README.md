# Pediu Patrão

Sistema de gestão de uma pizzaria, desenvolvido para a Atividade Prática M1 de Engenharia de Software. A evolução do projeto inclui controle de acesso por perfil, gestão do fluxo dos pedidos e auditoria das operações no MongoDB.

## Tecnologias

- Java 21
- Spring Boot 3.4.4
- Spring Security
- Spring Data MongoDB
- Thymeleaf
- Bootstrap, HTML, CSS e JavaScript
- Maven Wrapper

## Como executar

É necessário ter o JDK 21 instalado e um banco MongoDB acessível. Na primeira execução, é necessária conexão com a internet para baixar as dependências do Maven.

### 1. Obter o projeto

```powershell
git clone https://github.com/Fepenninck/pediu-patrao-v2.git
cd pediu-patrao-v2
```

Se o projeto já estiver no computador, abra o terminal na pasta que contém o arquivo `pom.xml`.

### 2. Configurar o MongoDB

Copie o arquivo de exemplo, caso ainda não exista uma configuração local:

```powershell
Copy-Item src/main/resources/application.properties.example src/main/resources/application.properties
```

Edite `src/main/resources/application.properties` e preencha a conexão:

```properties
spring.data.mongodb.uri=mongodb+srv://SEU_USUARIO:SUA_SENHA@SEU_CLUSTER.mongodb.net/?appName=PediuPatrao
spring.data.mongodb.database=pediupatrao

spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML
spring.thymeleaf.cache=false
```

No MongoDB Atlas, use um usuário de banco com acesso de leitura e escrita ao banco da aplicação e permita o IP do computador em Network Access. A senha utilizada na conexão é a do usuário de banco, não a senha de login no site do Atlas. Caracteres especiais na senha devem ser codificados para uso em URI.

O nome do arquivo deve ser exatamente `application.properties`, sem `.java` ou `.txt`. Esse arquivo está no `.gitignore` para evitar publicar credenciais.

### 3. Iniciar

No PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
sh mvnw spring-boot:run
```

Acesse `http://localhost:8080`. Para encerrar, pressione `Ctrl + C` no terminal.

## Usuários de demonstração

Na inicialização, o sistema cria os seguintes usuários se eles ainda não existirem:

| Usuário | Senha inicial | Perfil |
|---|---|---|
| admin | teste123 | ADMIN |
| gerente | teste123 | GERENTE |
| atendente | teste123 | ATENDENTE |

Usuários existentes não têm suas senhas redefinidas na inicialização. Essas contas são destinadas à demonstração acadêmica.

## Requisitos implementados

### RF01 — Controle de usuários e permissões

| Operação | ADMIN | GERENTE | ATENDENTE |
|---|:---:|:---:|:---:|
| Administrar usuários e atribuir perfis | Sim | Não | Não |
| Consultar clientes | Não | Sim | Sim |
| Cadastrar e atualizar clientes | Não | Não | Sim |
| Registrar pedidos | Não | Não | Sim |
| Consultar pedidos | Sim | Sim | Sim |
| Avançar status de pedidos | Não | Sim | Não |
| Aplicar desconto e cancelar pedidos | Não | Sim | Não |
| Consultar auditoria | Sim | Sim | Não |

As restrições são aplicadas no backend pelo Spring Security. Os menus e ações também respeitam o perfil autenticado. A manutenção de produtos foi preservada para ADMIN e GERENTE; os três perfis podem consultar produtos.

As senhas são armazenadas com BCrypt e não são expostas nas respostas JSON. A edição sem nova senha preserva a senha existente. Formulários e operações de alteração utilizam proteção CSRF.

### RF02 — Cadastro e cálculo de pedidos

O pedido registra cliente, itens, quantidades, preços unitários, subtotal, percentual e valor de desconto, total, responsável e data/hora de entrada.

A criação recebe somente o cliente e os identificadores dos produtos com suas quantidades. Os preços são obtidos no banco e os valores são calculados no servidor. O pedido começa em `RECEBIDO`, com usuário e horário definidos pelo backend.

### RF03 — Entrada, saída e transições

O fluxo permitido é:

```text
RECEBIDO → EM_PREPARACAO → PRONTO → SAIU_PARA_ENTREGA → FINALIZADO
                                └→ RETIRADO → FINALIZADO
```

A saída para entrega ou retirada registra data/hora e responsável. O serviço bloqueia saltos de etapas e alterações de status após cancelamento ou finalização.

### RF04 — Descontos e cancelamentos

Somente GERENTE pode aplicar desconto ou cancelar, inclusive em comparação ao ADMIN. O desconto é percentual, limitado a 20%, e recalcula o total com base no subtotal, sem acumular descontos sucessivos.

Desconto e cancelamento só são permitidos antes da saída para entrega ou retirada. O cancelamento exige justificativa, registra responsável e horário, mantém os dados históricos e bloqueia novas alterações. Ambas as operações geram auditoria.

### RF05 — Auditoria

Uma entidade, um repositório e um serviço próprios persistem os registros na coleção `auditorias` do MongoDB.

São registradas criação de pedidos, mudanças de status, saída, descontos, cancelamentos e alterações cadastrais de clientes, incluindo telefone e campos do endereço. Os registros identificam usuário, perfil, data/hora, operação, entidade e seu identificador, campo, valores anterior e novo e justificativa quando necessária.

A consulta pela tela e pela API é exclusiva de ADMIN e GERENTE. Não há endpoints comuns de edição ou exclusão dos registros de auditoria. Credenciais não são incluídas nos eventos.

## Principais alterações realizadas

- Inclusão dos perfis GERENTE e ATENDENTE e revisão das permissões das rotas.
- Evolução de Pedido e inclusão de ItemPedido, StatusPedido e DTOs de entrada.
- Cálculo dos valores e validação das transições na camada de serviços.
- Implementação de desconto e cancelamento exclusivos do GERENTE.
- Criação da auditoria persistente e integração com pedidos e clientes.
- Atualização das telas de pedidos, clientes, produtos e usuários, com consulta de auditoria.
- Tratamento de erros nas APIs e nos formulários.
- Ajuste do menu lateral por perfil, com recolhimento e logout.

## Organização do projeto

```text
src/main/
├── java/com/umc/pediupatrao/
│   ├── config/       Configuração de segurança e inicialização
│   ├── controller/   Rotas de páginas e APIs, tratamento de erros
│   ├── dto/          Dados recebidos nas operações de pedidos
│   ├── entity/       Entidades persistidas e tipos de domínio
│   ├── repository/   Acesso ao MongoDB
│   └── service/      Regras de negócio e auditoria
└── resources/
    ├── templates/    Páginas Thymeleaf
    ├── static/       CSS, JavaScript, imagens e bibliotecas visuais
    └── application.properties.example
```

Os itens Funcionários, Entregadores e Horário de Funcionamento estão identificados como “Em breve”; não fazem parte das funcionalidades implementadas nesta atividade.
