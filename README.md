<div align="center">
  <img src="./assets/banner.png" alt="Banner Pediu Patrão" width="100%">


  <p>
    Sistema web de gerenciamento de pizzarias com controle de acesso,
    gestão de pedidos e auditoria de operações.
  </p>

  <p>
    <img src="https://img.shields.io/badge/Java%2021-ED8B00?logo=openjdk&logoColor=white" alt="Java">
    <img src="https://img.shields.io/badge/Spring%20Boot%203.4.4-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot">
    <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?logo=springsecurity&logoColor=white" alt="Spring Security">
    <img src="https://img.shields.io/badge/MongoDB-47A248?logo=mongodb&logoColor=white" alt="MongoDB">
    <img src="https://img.shields.io/badge/Thymeleaf-005F0F?logo=thymeleaf&logoColor=white" alt="Thymeleaf">
    <img src="https://img.shields.io/badge/Bootstrap-7952B3?logo=bootstrap&logoColor=white" alt="Bootstrap">
    <img src="https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white" alt="Maven">
  </p>
</div>

<hr>

<h2>Sobre o Projeto</h2>

<p>
  O <strong>Pediu Patrão</strong> é um sistema web desenvolvido para
  facilitar o gerenciamento das operações de uma pizzaria, centralizando
  clientes, produtos, pedidos e usuários em uma única plataforma.
</p>

<p>
  Desenvolvido como <strong>Atividade Prática Individual M1</strong>
  da Universidade de Mogi das Cruzes (UMC), o projeto utiliza
  Java, Spring Boot, Spring Security, Thymeleaf e MongoDB.
</p>

<p>
  A evolução do sistema contempla controle de acesso por perfil,
  gerenciamento do ciclo de vida dos pedidos, aplicação de descontos,
  cancelamentos e auditoria das operações realizadas.
</p>

<h2>Objetivo</h2>

<p>
  Evoluir o sistema de gestão de pizzaria, implementando mecanismos
  de segurança, autorização e regras de negócio que permitam
  controlar as operações realizadas pelos funcionários.
</p>

<p>
  A aplicação busca garantir maior organização operacional,
  integridade das informações e rastreabilidade das alterações.
</p>

<h2>Desenvolvedor</h2>

<table>
  <tr>
    <th>Informação</th>
    <th>Descrição</th>
  </tr>
  <tr>
    <td>Nome</td>
    <td>Luís Felipe Penninck Dos Santos</td>
  </tr>
  <tr>
    <td>RGM</td>
    <td>11231101140</td>
  </tr>
  <tr>
    <td>Instituição</td>
    <td>Universidade de Mogi das Cruzes (UMC)</td>
  </tr>
  <tr>
    <td>Atividade</td>
    <td>Atividade Prática Individual M1</td>
  </tr>
  <tr>
    <td>Professores</td>
    <td>Alessandro Silva e Leonardo Alvino</td>
  </tr>
  <tr>
    <td>Ano</td>
    <td>2026</td>
  </tr>
</table>

<h2>Perfis do Sistema</h2>

<p>
  O sistema possui três perfis de acesso, com responsabilidades
  e permissões específicas.
</p>

<ul>
  <li>
    <strong>ADMIN:</strong> administra usuários, atribui perfis,
    consulta pedidos e visualiza auditorias.
  </li>
  <li>
    <strong>GERENTE:</strong> gerencia pedidos, aplica descontos,
    realiza cancelamentos, consulta clientes e auditorias.
  </li>
  <li>
    <strong>ATENDENTE:</strong> cadastra e atualiza clientes,
    registra pedidos e acompanha seu andamento.
  </li>
</ul>

<p>
  As permissões são aplicadas diretamente no backend
  por meio do Spring Security.
</p>

<h2>Funcionalidades</h2>

<ul>
  <li>Autenticação e controle de acesso por perfil</li>
  <li>Gerenciamento de usuários e permissões</li>
  <li>Cadastro e atualização de clientes</li>
  <li>Consulta e gerenciamento de produtos</li>
  <li>Registro de pedidos com múltiplos itens</li>
  <li>Cálculo automático de subtotal, descontos e total</li>
  <li>Controle de preparação, retirada e entrega</li>
  <li>Registro de entrada e saída dos pedidos</li>
  <li>Aplicação de descontos de até 20%</li>
  <li>Cancelamento com justificativa</li>
  <li>Auditoria persistente no MongoDB</li>
  <li>Histórico de alterações e responsáveis</li>
  <li>Navegação adaptada ao perfil autenticado</li>
</ul>

<h2>Stack Tecnológica</h2>

<table>
  <tr>
    <th>Camada</th>
    <th>Tecnologias</th>
  </tr>
  <tr>
    <td>Linguagem</td>
    <td>Java 21</td>
  </tr>
  <tr>
    <td>Back-end</td>
    <td>Spring Boot 3.4.4</td>
  </tr>
  <tr>
    <td>Segurança</td>
    <td>Spring Security, BCrypt e CSRF</td>
  </tr>
  <tr>
    <td>Front-end</td>
    <td>Thymeleaf, HTML, CSS e JavaScript</td>
  </tr>
  <tr>
    <td>Interface</td>
    <td>Bootstrap</td>
  </tr>
  <tr>
    <td>Banco de Dados</td>
    <td>MongoDB / MongoDB Atlas</td>
  </tr>
  <tr>
    <td>Persistência</td>
    <td>Spring Data MongoDB</td>
  </tr>
  <tr>
    <td>Build</td>
    <td>Maven Wrapper</td>
  </tr>
</table>

<h2>Arquitetura do Sistema</h2>

<p>
  O sistema utiliza arquitetura em camadas, separando
  apresentação, controle, regras de negócio e persistência.
</p>

<table>
  <tr>
    <th>Camada</th>
    <th>Responsabilidade</th>
  </tr>
  <tr>
    <td>Interface Web</td>
    <td>Thymeleaf, Bootstrap, HTML, CSS e JavaScript</td>
  </tr>
  <tr>
    <td>Controllers</td>
    <td>Rotas, páginas e APIs</td>
  </tr>
  <tr>
    <td>Services</td>
    <td>Regras de negócio e validações</td>
  </tr>
  <tr>
    <td>Repositories</td>
    <td>Acesso e persistência de dados</td>
  </tr>
  <tr>
    <td>Banco de Dados</td>
    <td>MongoDB</td>
  </tr>
</table>

<p>
  O Spring Security gerencia a autenticação e autorização.
  A camada de serviços valida as operações, enquanto o
  Spring Data MongoDB realiza a persistência dos dados.
</p>

<h2>Requisitos Funcionais</h2>

<h3>RF01 — Controle de Usuários e Permissões</h3>

<p>
  Implementação de três perfis com permissões específicas,
  protegidas no backend.
</p>

<table>
  <tr>
    <th>Operação</th>
    <th>ADMIN</th>
    <th>GERENTE</th>
    <th>ATENDENTE</th>
  </tr>
  <tr>
    <td>Administrar usuários</td>
    <td>Sim</td><td>Não</td><td>Não</td>
  </tr>
  <tr>
    <td>Consultar clientes</td>
    <td>Não</td><td>Sim</td><td>Sim</td>
  </tr>
  <tr>
    <td>Cadastrar e editar clientes</td>
    <td>Não</td><td>Não</td><td>Sim</td>
  </tr>
  <tr>
    <td>Registrar pedidos</td>
    <td>Não</td><td>Não</td><td>Sim</td>
  </tr>
  <tr>
    <td>Consultar pedidos</td>
    <td>Sim</td><td>Sim</td><td>Sim</td>
  </tr>
  <tr>
    <td>Avançar status</td>
    <td>Não</td><td>Sim</td><td>Não</td>
  </tr>
  <tr>
    <td>Aplicar descontos</td>
    <td>Não</td><td>Sim</td><td>Não</td>
  </tr>
  <tr>
    <td>Cancelar pedidos</td>
    <td>Não</td><td>Sim</td><td>Não</td>
  </tr>
  <tr>
    <td>Consultar auditoria</td>
    <td>Sim</td><td>Sim</td><td>Não</td>
  </tr>
</table>

<p>
  A administração de usuários e a alteração de perfis
  possuem restrições para impedir a atribuição indevida
  de privilégios.
</p>

<p>
  A manutenção de produtos é permitida a ADMIN e GERENTE,
  enquanto todos os perfis podem consultar produtos.
</p>

<h3>RF02 — Evolução do Cadastro de Pedidos</h3>

<p>
  Cada pedido registra cliente, itens, quantidades,
  preços unitários, subtotal, desconto, total,
  responsável e data/hora de entrada.
</p>

<p>
  Os preços são consultados no banco de dados e os valores
  são calculados no servidor, impedindo que requisições
  HTTP definam arbitrariamente o total.
</p>

<p>
  Todo pedido é criado inicialmente com o status
  <code>RECEBIDO</code>.
</p>

<h3>RF03 — Controle de Entrada, Saída e Transições</h3>

<p>O fluxo de pedidos segue as etapas:</p>

<pre>
RECEBIDO
    |
    v
EM_PREPARACAO
    |
    v
  PRONTO
    |
    +---- SAIU_PARA_ENTREGA ----+
    |                           |
    +---- RETIRADO -------------+
                                |
                                v
                           FINALIZADO
</pre>

<p>
  As transições são validadas na camada de serviços.
  O sistema registra responsáveis e horários de entrada
  e saída, bloqueando saltos de etapas e alterações
  após cancelamento ou finalização.
</p>

<p>
  O estado CANCELADO é excepcional e permitido somente
  ao GERENTE antes da saída do pedido.
</p>

<h3>RF04 — Descontos e Cancelamentos</h3>

<p>
  Somente o perfil <strong>GERENTE</strong> pode aplicar
  descontos ou cancelar pedidos, inclusive em comparação
  ao ADMIN.
</p>

<ul>
  <li>Descontos percentuais limitados a 20%</li>
  <li>Recálculo automático do total no servidor</li>
  <li>Operações permitidas antes da saída do pedido</li>
  <li>Cancelamento com justificativa obrigatória</li>
  <li>Preservação dos dados históricos</li>
  <li>Bloqueio de alterações após cancelamento</li>
  <li>Registro das operações na auditoria</li>
</ul>

<h3>RF05 — Auditoria de Pedidos e Clientes</h3>

<p>
  O sistema registra operações relevantes na coleção
  <code>auditorias</code> do MongoDB.
</p>

<p>São auditadas:</p>

<ul>
  <li>Criação e alterações de pedidos</li>
  <li>Mudanças de status</li>
  <li>Saída para entrega ou retirada</li>
  <li>Aplicação de descontos</li>
  <li>Cancelamentos</li>
  <li>Alterações cadastrais de clientes</li>
  <li>Alterações de endereços e telefones</li>
</ul>

<p>Cada registro contém:</p>

<ul>
  <li>Usuário autenticado e perfil</li>
  <li>Data e horário da operação</li>
  <li>Tipo de operação</li>
  <li>Entidade afetada e identificador</li>
  <li>Campos modificados</li>
  <li>Valores anteriores e novos</li>
  <li>Justificativa, quando necessária</li>
</ul>

<p>
  A consulta é exclusiva de ADMIN e GERENTE.
  Não existem endpoints comuns para edição ou exclusão
  dos registros de auditoria.
</p>

<p>
  Senhas, tokens e outras credenciais não são
  incluídos nos registros.
</p>

<h2>Como Executar o Projeto</h2>

<h3>Pré-requisitos</h3>

<ul>
  <li>JDK 21</li>
  <li>MongoDB local ou MongoDB Atlas</li>
  <li>Git</li>
  <li>Internet para baixar as dependências do Maven</li>
</ul>

<h3>1. Clonar o repositório</h3>

<pre>
git clone https://github.com/Fepenninck/pediu-patrao-v2.git
cd pediu-patrao-v2
</pre>

<h3>2. Configurar o MongoDB</h3>

<p>No PowerShell, execute:</p>

<pre>
Copy-Item src/main/resources/application.properties.example src/main/resources/application.properties
</pre>

<p>
  Edite o arquivo
  <code>src/main/resources/application.properties</code>:
</p>

<pre>
spring.data.mongodb.uri=mongodb+srv://SEU_USUARIO:SUA_SENHA@SEU_CLUSTER.mongodb.net/?appName=PediuPatrao
spring.data.mongodb.database=pediupatrao

spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML
spring.thymeleaf.cache=false
</pre>

<p>
  No MongoDB Atlas, configure um usuário de banco com
  permissões de leitura e escrita e autorize o IP
  do computador em Network Access.
</p>

<p>
  O arquivo application.properties está no .gitignore
  para evitar a publicação de credenciais.
</p>

<h3>3. Iniciar a aplicação</h3>

<p><strong>Windows:</strong></p>

<pre>
.\mvnw.cmd spring-boot:run
</pre>

<p><strong>Linux ou macOS:</strong></p>

<pre>
sh mvnw spring-boot:run
</pre>

<p>
  Após a inicialização, acesse:
  <a href="http://localhost:8080">http://localhost:8080</a>
</p>

<p>
  Para encerrar a aplicação, pressione Ctrl + C.
</p>

<h2>Usuários de Demonstração</h2>

<table>
  <tr>
    <th>Usuário</th>
    <th>Senha inicial</th>
    <th>Perfil</th>
  </tr>
  <tr>
    <td>admin</td>
    <td>teste123</td>
    <td>ADMIN</td>
  </tr>
  <tr>
    <td>gerente</td>
    <td>teste123</td>
    <td>GERENTE</td>
  </tr>
  <tr>
    <td>atendente</td>
    <td>teste123</td>
    <td>ATENDENTE</td>
  </tr>
</table>

<p>
  As contas são destinadas exclusivamente à demonstração
  acadêmica. Usuários existentes não têm suas senhas
  redefinidas durante a inicialização.
</p>

<h2>Segurança</h2>

<ul>
  <li>Autenticação e autorização com Spring Security</li>
  <li>Controle de acesso aplicado no backend</li>
  <li>Armazenamento de senhas com BCrypt</li>
  <li>Proteção CSRF em formulários e operações sensíveis</li>
  <li>Validação das regras de pedidos e dos dados de usuários e produtos</li>
  <li>Prevenção contra atribuição indevida de privilégios</li>
  <li>Auditoria persistente das operações</li>
  <li>Senhas omitidas das respostas JSON e credenciais fora dos registros de auditoria</li>
</ul>

<p>
  A edição de usuários sem uma nova senha preserva
  a senha existente. As senhas não são expostas
  nas respostas JSON.
</p>

<h2>Organização do Projeto</h2>

<pre>
src/main/
|
+-- java/com/umc/pediupatrao/
|   |
|   +-- config/       Segurança e inicialização
|   +-- controller/   Páginas, APIs e erros
|   +-- dto/          Transferência de dados
|   +-- entity/       Entidades do sistema
|   +-- repository/   Acesso ao MongoDB
|   +-- service/      Regras de negócio e auditoria
|
+-- resources/
    |
    +-- templates/    Páginas Thymeleaf
    +-- static/       CSS, JavaScript e imagens
    +-- application.properties.example
</pre>

<h2>Principais Alterações Realizadas</h2>

<ul>
  <li>Inclusão dos perfis GERENTE e ATENDENTE</li>
  <li>Revisão das permissões e proteção das rotas</li>
  <li>Evolução da entidade Pedido</li>
  <li>Criação de ItemPedido, StatusPedido e DTOs</li>
  <li>Cálculo automático dos valores no servidor</li>
  <li>Validação das transições dos pedidos</li>
  <li>Registro de responsáveis e horários</li>
  <li>Descontos e cancelamentos exclusivos do gerente</li>
  <li>Criação da auditoria persistente no MongoDB</li>
  <li>Integração da auditoria aos pedidos e clientes</li>
  <li>Atualização das interfaces do sistema</li>
  <li>Tratamento de erros em APIs e formulários</li>
  <li>Adaptação do menu lateral conforme o perfil</li>
</ul>

<h2>Funcionalidades Futuras</h2>

<p>
  As funcionalidades abaixo estão identificadas como
  "Em breve" e não fazem parte do escopo desta atividade:
</p>

<ul>
  <li>Gerenciamento de funcionários</li>
  <li>Gerenciamento de entregadores</li>
  <li>Configuração do horário de funcionamento</li>
</ul>

<h2>Licença e Uso</h2>

<p>
  Projeto desenvolvido para fins acadêmicos como parte
  da Atividade Prática Individual M1 de Engenharia
  de Software.
</p>

<p>
  As contas e credenciais de demonstração não devem
  ser utilizadas em ambientes de produção.
</p>

<hr>

<div align="center">
  <h3>Universidade de Mogi das Cruzes - UMC</h3>

  <p>
    Engenharia de Software | Atividade Prática M1 | 2026
  </p>

  <p>
    Desenvolvido por <strong>Luís Felipe Penninck Dos Santos</strong>
  </p>

  <sub>Java | Spring Boot | MongoDB</sub>
</div>
