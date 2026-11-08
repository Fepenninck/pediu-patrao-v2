# Testes e evidências

## Execução registrada

Em 07/11/2026, foram executados:

```powershell
.\mvnw.cmd -B test
node --check src/main/resources/static/js/dashboard.js
node --test src/test/js/dashboard.test.cjs
git diff --check
```

Resultado Java: **14 testes, zero falhas, zero erros, zero ignorados; BUILD SUCCESS**.
Resultado JavaScript: **5 testes, 5 aprovados, zero falhas**.

O empacotamento também foi validado com `.\mvnw.cmd -B package -DskipTests`: **BUILD SUCCESS**, gerando `target/pediupatrao-1.0-SNAPSHOT.jar`. Os testes já haviam sido executados separadamente; esse comando verificou a geração do artefato.

O resumo exportado dos relatórios Surefire está em [evidencias/java.txt](evidencias/java.txt), e a saída do Node em [evidencias/javascript.txt](evidencias/javascript.txt). Os relatórios completos são regenerados em `target/surefire-reports`, ignorado pelo Git. Não é necessário publicar logs completos da aplicação nem suas configurações locais.

## Cobertura Java

### DashboardServiceTest — 7 testes

| Cenário | Verificação |
| --- | --- |
| Finalizados, cancelado e pronto no período | Apenas dois finalizados compõem faturamento 20,01; ticket arredondado para 10,01; cancelado contado separadamente |
| Período vazio | Ticket zero, sem divisão por zero |
| Venda somente no segundo dia | Três dias retornados em ordem, primeiro com zero, segundo com 30,00; consulta termina no início do dia seguinte à data final |
| Um pedido recebido | Todos os sete status retornados; recebido com quantidade um |
| Mesmo produto em dois pedidos | Quantidades agregadas por ID; pizza com quatro unidades fica antes de suco com três; limite um respeitado |
| Filtros inválidos | Datas nulas/invertidas, mais de 366 dias e limite zero rejeitados sem consultar repositório |
| Finalizado sem valor | Falha explícita, sem converter ausência de valor em faturamento zero |

### DashboardControllerTest — 7 testes

| Cenário | Verificação |
| --- | --- |
| GERENTE consulta resumo | HTTP 200 e campos JSON esperados |
| ADMIN consulta análises | HTTP 200 nos três endpoints e serialização de datas/quantidades |
| Parâmetros inválidos | HTTP 400 para ausência, formato inválido e erro de regra; envelope JSON |
| Falha interna | HTTP 500 sem expor detalhe interno na mensagem pública |
| ATENDENTE | HTTP 403 na página e nos quatro recursos; service não chamado |
| Visitante sem sessão | Redirecionado ao login |
| Página autorizada | Template Thymeleaf renderizado com canvas e referência ao JavaScript |

## Cobertura JavaScript — 5 testes

- Consulta inicial aos quatro recursos e exibição de nome de produto como texto, inclusive quando contém marcação HTML.
- Período sem pedidos com mensagem e ranking vazio.
- Sessão expirada com orientação para novo login e liberação do botão.
- Erro da API com ocultação dos resultados anteriores.
- Datas invertidas bloqueadas antes de uma nova chamada.

Os testes Node executam o script em um DOM simplificado e simulam `fetch` e Chart.js. Verificam o comportamento da integração, sem reproduzir o layout real de um navegador.

## Exemplo de resposta sem movimento

Quando não há pedidos finalizados ou cancelados no período, o resumo retorna:

```json
{"faturamento":0.00,"pedidosFinalizados":0,"ticketMedio":0.00,"pedidosCancelados":0}
```

Os cálculos com valores conhecidos são verificados pelos testes automatizados. A homologação com dados reais segue o roteiro abaixo.

## Roteiro de homologação com MongoDB real

Esta verificação final deve ser executada em ambiente de desenvolvimento com dados próprios. Não foi registrada como aprovada automaticamente: os testes Java usam mocks do repositório ou service, sem gravar no banco existente.

1. Iniciar o sistema com a conexão MongoDB configurada e entrar como GERENTE.
2. Usar pedidos conhecidos, ou cadastrar em uma base de desenvolvimento: dois pedidos finalizados de R$ 40,00 e R$ 60,00; um cancelado de R$ 25,00; um recebido de R$ 30,00. Selecionar a data de entrada desses pedidos.
3. Conferir faturamento R$ 100,00, dois finalizados, ticket R$ 50,00, um cancelamento e quatro pedidos na soma dos status. Isolar o período ou a base para que outros pedidos não alterem o resultado.
4. Distribuir finalizados por dois dias e conferir as barras e a tabela. Um dia sem finalizados deve mostrar zero.
5. Incluir o mesmo produto em dois pedidos finalizados e conferir a soma das unidades no ranking. O cancelado não deve participar.
6. Consultar um único dia e verificar que pedidos desse dia inteiro são incluídos e pedidos do dia seguinte não aparecem.
7. Selecionar um período sem pedidos e conferir mensagem, valores zero e ranking vazio.
8. Testar período invertido, data inválida diretamente na API, limite 0 e limite 51. Esperar 400.
9. Entrar como ATENDENTE e acessar diretamente `/dashboard` e `/api/dashboard/resumo` com parâmetros. Esperar 403.
10. Verificar em tela estreita os filtros, cartões, tabelas e gráficos; navegar por teclado.
11. Simular falha de rede em ambiente de desenvolvimento e verificar mensagem e botão disponível para nova tentativa.

Guardar capturas reais desses resultados para complementar a entrega e usar dados não zerados no vídeo. Não há captura de navegador ou teste visual real anexado nesta implementação.
