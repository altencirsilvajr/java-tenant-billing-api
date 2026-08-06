# 007 - Endurecer toolchain de CI

## Commit

`ci: eliminate toolchain warnings`

## Objetivo

Remover alertas npm e avisos de runtime do pipeline.

## Implementacao

- Fixa `@hono/node-server` corrigido em 2.1.0.
- Aprova scripts de instalacao por pacote e versao.
- Atualiza Actions para Node 24 e adiciona audit ao frontend.

## Rastreabilidade ADR

Decisao local sem ADR novo: manutencao reversivel sem alterar multitenancy ou faturamento.

## Verificacao

- `npm audit`: 0 vulnerabilidades e nenhum script pendente.
- Testes frontend: 2 aprovados; build Angular aprovado.
- Workflow validado como YAML e sem Actions antigas.
