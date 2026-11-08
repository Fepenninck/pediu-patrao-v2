# Release 1.1.0 — Pediu Patrão

## Funcionalidades

- Dashboard administrativo com indicadores, vendas diárias, distribuição de status e ranking de produtos.
- API integrada ao MongoDB, filtros por período e tratamento de erros.
- Proteções do gerenciamento de usuários, auditoria de exclusão de clientes e revisão de permissões.
- Favicon, documentação, evidências de testes e vídeo de apresentação.

## Fluxo de integração

1. Integração da branch de funcionalidade `dashboard` em `develop`.
2. Criação de `release/1.1.0` a partir de `develop`.
3. Atualização da versão Maven para `1.1.0` e verificação do pacote.
4. Integração da release em `main` e criação da tag anotada `v1.1.0`.
5. Integração da release de volta em `develop`.

A integração direta anterior em `main` permanece no histórico. Esta release organiza o fechamento da versão sem alterar commits já publicados. As branches `dashboard`, `develop` e `release/1.1.0` são mantidas para consulta na entrega.

## Validação

Comando de preparação: `.\mvnw.cmd -B verify`.

Resultado em 08/11/2026: **BUILD SUCCESS**, com 48 testes Java sem falhas e geração de `target/pediupatrao-1.1.0.jar`. Os cinco testes JavaScript também passaram com `node --test src/test/js/dashboard.test.cjs`.

Os testes usam MockMvc e repositórios simulados, conforme documentado em `docs/revisao-seguranca.md`. O vídeo está em `docs/video/VideoDashboard.mp4`.
