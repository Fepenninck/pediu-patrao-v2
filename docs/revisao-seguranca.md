# Revisão a partir do feedback da avaliação

## Gerenciamento de usuários

As regras ficam no `UsuarioService`, usado tanto pelos formulários quanto pela API:

- Um administrador não pode alterar o próprio perfil nem excluir a própria conta.
- A exclusão e o rebaixamento de um ADMIN são bloqueados se a contagem de administradores for menor ou igual a um.
- É possível remover ou rebaixar outro administrador quando houver pelo menos dois.
- O administrador pode trocar a própria senha; login próprio deve ser alterado por outro administrador. Isso evita que a sessão continue com um login antigo e perca sua identificação nas proteções de autoalteração.
- O perfil do autor é relido do banco antes das mutações. Uma sessão antiga com autoridade ADMIN não pode continuar alterando usuários depois que a conta é rebaixada ou excluída.
- As operações de criação, alteração e exclusão são sincronizadas no service para serializar essas mutações dentro da mesma instância da aplicação.
- O formulário do próprio usuário bloqueia visualmente perfil e login; os mesmos bloqueios continuam no backend contra requisições manuais.
- Conflitos de negócio retornam HTTP 409 na API; nos formulários, são apresentados como mensagem após redirecionamento.

O controle sincronizado protege a execução em uma única instância, que é o modo atual do projeto. Não é uma trava distribuída: antes de usar várias instâncias escrevendo na mesma coleção, será necessário adotar uma estratégia transacional/serializada no MongoDB para preservar o último administrador entre instâncias. Alterações diretas no banco não passam por essas regras.

## Exclusão de clientes e auditoria

O service carrega o cliente antes da exclusão e, após excluir, registra `EXCLUSAO` na coleção `auditorias` por meio do `AuditoriaService` existente. O registro contém entidade CLIENTE, ID, nome anterior, usuário responsável, perfil e data/hora. Não registra sucesso quando o cliente não existe ou a operação de exclusão falha.

Como nas outras operações auditadas deste projeto, exclusão e gravação do registro são duas escritas separadas, sem transação entre coleções. Os testes cobrem o fluxo de sucesso e falha de exclusão; não afirmam atomicidade se houver interrupção entre as duas escritas. Para garantia transacional em produção, configurar MongoDB com suporte a transações e envolver as duas escritas na mesma transação.

## Permissões revisadas

O feedback não especificou quais permissões eram restritivas. A revisão libera o atendimento operacional para GERENTE e ATENDENTE, preservando separação das operações financeiras.

| Operação | ADMIN | GERENTE | ATENDENTE |
| --- | --- | --- | --- |
| Administrar usuários | Sim, respeitando as proteções | Não | Não |
| Consultar clientes | Sim | Sim | Sim |
| Cadastrar, editar e excluir clientes | Não | Sim | Sim |
| Consultar pedidos/produtos | Sim | Sim | Sim |
| Registrar pedidos | Não | Sim | Sim |
| Avançar status conforme transições válidas | Não | Sim | Sim |
| Desconto até 20% e cancelamento justificado | Não | Sim | Não |
| Manutenção de produtos | Sim | Sim | Não |
| Auditoria e dashboard | Sim | Sim | Não |

Botões e menu foram atualizados conforme a mesma matriz. CSRF continua obrigatório nas operações de escrita. A liberação do avanço de status não permite pular etapas nem cancelar pelo endpoint de status: as transições são validadas no service.

## Testes automatizados

Executados em 07/11/2026:

```powershell
.\mvnw.cmd -B test
node --test src/test/js/dashboard.test.cjs
```

**46 testes Java aprovados, zero falhas, zero erros, zero ignorados.** Os cinco testes JavaScript do dashboard também passaram.

| Classe | Quantidade | Cobertura |
| --- | --- | --- |
| UsuarioServiceTest | 11 | Próprio perfil/login, própria senha, autoexclusão, último ADMIN, remoções permitidas, sessão antiga, hash e login duplicado |
| ClienteServiceTest | 3 | Exclusão auditada, inexistência e falha de exclusão sem registro indevido |
| AuditoriaServiceTest | 1 | Chamada de persistência com autor, perfil, horário, entidade e valores |
| PedidoServiceTest | 7 | Múltiplos itens, valores, desconto máximo/fora do limite, transições e cancelamento justificado |
| PermissoesControllerTest | 10 | Matriz de perfis em HTTP, CSRF, conflitos em API/formulário e renderização do próprio perfil |
| DashboardControllerTest | 7 | Recursos, autorização, erros e template |
| DashboardServiceTest | 7 | Indicadores e regras do dashboard |

Evidência exportada dos relatórios: [evidencias/revisao-java.txt](evidencias/revisao-java.txt).

Os testes de services usam repositórios simulados; os testes HTTP usam MockMvc e a configuração real de segurança, sem iniciar MongoDB. Eles demonstram regras e chamadas de persistência, não uma homologação de banco real. Para conferir a auditoria no ambiente local, excluir um cliente de teste como gerente/atendente e abrir Auditoria com ADMIN/GERENTE; procurar EXCLUSAO / CLIENTE com o ID excluído.
