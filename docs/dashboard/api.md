# API do Dashboard

Base: `/api/dashboard`. Método: GET. Sucesso: HTTP 200 com `application/json`.

## Autenticação e parâmetros comuns

É necessário estar autenticado pela sessão do sistema com perfil ADMIN ou GERENTE. No navegador, faça login e abra a URL na mesma sessão. Não há token adicional nem API pública.

| Parâmetro | Tipo | Obrigatório | Regra |
| --- | --- | --- | --- |
| `dataInicial` | Data ISO `yyyy-MM-dd` | Sim | Data de entrada inicial, inclusiva |
| `dataFinal` | Data ISO `yyyy-MM-dd` | Sim | Data de entrada final, inclusiva; maior ou igual à inicial |

Máximo de 366 dias inclusivos. Não há filtro por status no contrato: resumo e ranking aplicam suas regras de status, enquanto a distribuição apresenta todos.

## GET /resumo

```http
GET /api/dashboard/resumo?dataInicial=2026-11-01&dataFinal=2026-11-07
Accept: application/json
```

Exemplo ilustrativo:

```json
{
  "faturamento": 1500.00,
  "pedidosFinalizados": 30,
  "ticketMedio": 50.00,
  "pedidosCancelados": 2
}
```

Faturamento considera `valorTotal` após descontos dos finalizados. Ticket médio é faturamento dividido por finalizados. Sem finalizados, ambos são zero. A quantidade de casas apresentada pelo cliente pode variar porque JSON representa números, não sua formatação visual.

## GET /vendas-diarias

```http
GET /api/dashboard/vendas-diarias?dataInicial=2026-11-01&dataFinal=2026-11-02
```

```json
[
  { "data": "2026-11-01", "faturamento": 150.00 },
  { "data": "2026-11-02", "faturamento": 0.00 }
]
```

Um objeto para cada dia, em ordem crescente. A soma diária segue a mesma regra do resumo.

## GET /status

```http
GET /api/dashboard/status?dataInicial=2026-11-01&dataFinal=2026-11-07
```

```json
[
  { "status": "RECEBIDO", "quantidade": 2 },
  { "status": "EM_PREPARACAO", "quantidade": 1 },
  { "status": "PRONTO", "quantidade": 0 },
  { "status": "SAIU_PARA_ENTREGA", "quantidade": 1 },
  { "status": "RETIRADO", "quantidade": 0 },
  { "status": "FINALIZADO", "quantidade": 30 },
  { "status": "CANCELADO", "quantidade": 2 }
]
```

Retorna todos os valores de `StatusPedido`, incluindo os de quantidade zero.

## GET /produtos-mais-vendidos

Parâmetro adicional: `limite`, inteiro opcional, padrão 5, mínimo 1 e máximo 50.

```http
GET /api/dashboard/produtos-mais-vendidos?dataInicial=2026-11-01&dataFinal=2026-11-07&limite=5
```

```json
[
  { "produtoId": "produto-pizza", "nomeProduto": "Pizza de calabresa", "quantidade": 20 },
  { "produtoId": "produto-suco", "nomeProduto": "Suco de laranja", "quantidade": 12 }
]
```

Quantidade soma unidades, não número de pedidos. Somente finalizados participam. Sem vendas retorna `[]`.

## Erros

| HTTP | Condição | Resposta |
| --- | --- | --- |
| 400 | Datas ausentes, formato inválido ou limite não inteiro | JSON com `status` e `mensagem` orientando os parâmetros |
| 400 | Período invertido, superior a 366 dias ou limite fora de 1–50 | JSON com a mensagem específica da validação |
| 302 | Usuário sem sessão | Redirecionamento para `/login`, seguindo o form login existente |
| 403 | Usuário autenticado sem ADMIN/GERENTE | Resposta do Spring Security, antes do controller; não é garantido JSON |
| 500 | Banco indisponível, dados inconsistentes ou outra falha interna | JSON genérico; exceção completa somente no log do servidor |

Exemplo de validação:

```json
{ "status": 400, "mensagem": "A data inicial não pode ser posterior à data final." }
```

Exemplo de falha interna:

```json
{ "status": 500, "mensagem": "Não foi possível consultar os dados. Tente novamente mais tarde." }
```

As respostas de exemplo são ilustrativas. As evidências efetivamente executadas estão em [testes.md](testes.md).
