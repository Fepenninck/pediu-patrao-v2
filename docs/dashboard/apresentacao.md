# Roteiro de apresentação — até três minutos

Objetivo: demonstrar o painel em funcionamento e justificar as decisões de análise e projeto. Duração planejada: 2 minutos e 50 segundos.

## Preparação

- Iniciar a aplicação e entrar como GERENTE ou ADMIN.
- Deixar pedidos conhecidos e finalizados em um período curto de uma base de desenvolvimento. Incluir um cancelado e um dia sem venda.
- Abrir o dashboard, um endpoint e os testes em abas separadas.
- Aumentar o zoom do editor para o texto ficar legível e ocultar credenciais/configurações.
- Gravar a tela e a própria narração. Este documento é um roteiro; não é evidência de vídeo já gravado.

## 0:00–0:20 — Problema e objetivo

Mostrar a página do dashboard.

Sugestão de fala: “Este é o dashboard administrativo do Pediu Patrão. Ele permite que o responsável acompanhe vendas, situação dos pedidos e produtos mais vendidos, usando os dados registrados no sistema.”

## 0:20–0:55 — Indicadores e filtros

Selecionar um período com pedidos e clicar em Atualizar. Mostrar faturamento, finalizados, ticket e cancelamentos. Trocar para um período rápido.

Explicar: “O faturamento soma o valor após descontos dos pedidos finalizados. O ticket divide esse total pela quantidade de finalizados. O filtro utiliza a data de entrada do pedido.”

## 0:55–1:25 — Gráficos e ranking

Mostrar barras diárias, um dia com zero, situação dos pedidos e ranking.

Explicar: “Os dias sem vendas continuam no gráfico. A distribuição mostra todos os status. O ranking soma unidades pelo identificador do produto e desconsidera pedidos cancelados.”

## 1:25–1:55 — API e arquitetura

Abrir `/api/dashboard/resumo?dataInicial=...&dataFinal=...` com datas reais da demonstração. Mostrar brevemente controller e service.

Explicar: “A página consome quatro endpoints JSON. O controller recebe os filtros, o service aplica as regras e o repository consulta o MongoDB. DTOs expõem somente os campos necessários. Reaproveitei a autenticação do sistema.”

## 1:55–2:25 — Validação e acesso

Mostrar um período vazio na página. Abrir uma URL com datas invertidas para demonstrar o erro 400. Se houver tempo, demonstrar o bloqueio com ATENDENTE em outra sessão.

Explicar: “A interface trata carregamento, ausência de pedidos e erros. Apenas ADMIN e GERENTE acessam o painel. Valores monetários são calculados com BigDecimal.”

## 2:25–2:50 — Testes e entrega

Mostrar o resumo dos testes e a pasta `docs/dashboard` no GitHub.

Explicar: “Os testes verificam cálculos, filtros, permissões, erros HTTP e estados da interface. A documentação registra requisitos, endpoints, exemplos e limites. O trabalho está na branch dashboard, com commits separados para API, interface e documentação.”

Salvar a gravação com duração inferior a três minutos e anexar ao GitHub conforme a orientação do professor, por exemplo como arquivo em uma release. Se usar um link externo para o vídeo, confirmar se o professor aceita essa forma de entrega.
