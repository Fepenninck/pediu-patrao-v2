# Dashboard Administrativo — Pediu Patrão

O painel permite que ADMIN e GERENTE acompanhem vendas e pedidos em `/dashboard`.
Ele utiliza a sessão de login existente e consulta quatro endpoints JSON no mesmo servidor.

## Executar

1. Instale Java 21 e configure a conexão MongoDB em `src/main/resources/application.properties`, seguindo a configuração existente do projeto. Não publique credenciais.
2. Na raiz do projeto, execute `./mvnw spring-boot:run` (PowerShell: `.\mvnw.cmd spring-boot:run`).
3. Faça login com um usuário ADMIN ou GERENTE.
4. Abra `/dashboard` ou use o link Dashboard no menu.
5. Selecione um período com pedidos e clique em Atualizar.

Os últimos sete dias são selecionados inicialmente. Também existem atalhos para hoje e últimos 30 dias. Os atalhos incluem o dia atual.

## Documentos da entrega

- [Requisitos, regras e projeto](requisitos.md)
- [Endpoints e exemplos](api.md)
- [Testes, evidências e verificações manuais](testes.md)
- [Roteiro do vídeo de até três minutos](apresentacao.md)

## Organização do código

```text
HomeController            → entrega o template /dashboard
DashboardController       → recebe filtros e devolve DTOs JSON
DashboardExceptionHandler → converte falhas da API em respostas padronizadas
DashboardService          → valida filtros e calcula os indicadores
PedidoRepository          → consulta a coleção pedidos por data de entrada
dashboard.js              → busca os dados, atualiza gráficos e trata os estados da tela
```

A implementação está na branch `dashboard`, criada a partir de `develop` como branch de funcionalidade. Após revisão, a integração prevista no Gitflow é `dashboard → develop`; uma versão estável pode ser integrada à `main` pelo fluxo de release. O nome simplificado `dashboard` foi escolhido para esta funcionalidade. Não foi feito merge automático.

## Limites conhecidos

- A consulta usa a data de criação do pedido e seu status atual. Não representa o fluxo de caixa nem o histórico de mudanças de status.
- O agrupamento usa a convenção de `LocalDateTime` já existente. A aplicação deve manter a mesma configuração de fuso usada para gravar e ler os pedidos.
- Os cálculos ocorrem em memória após filtrar no MongoDB. Para volumes maiores, avaliar índice em `dataHoraEntrada` e agregações no banco.
- As quatro consultas não são um snapshot transacional. Um pedido alterado durante a atualização pode aparecer de forma diferente entre recursos; uma nova atualização refaz as consultas.
- O Chart.js é servido localmente. Bootstrap e ícones continuam usando os CDNs do layout existente.
- O vídeo final precisa ser gravado com a aplicação em funcionamento; o roteiro está pronto. Não há vídeo gravado incluído nesta entrega de código.
