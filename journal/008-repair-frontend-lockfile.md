# 008 - Reparar lockfile do frontend

## Commit

`fix: synchronize frontend lockfiles`

## Objetivo

Restaurar a instalacao reproduzivel do console multitenant.

## Implementacao

- Recupera o lockfile integral e regenera a politica de scripts com npm 11.17.

## Rastreabilidade ADR

Decisao local sem ADR novo: reparo mecanico sem alterar isolamento de tenants.

## Verificacao

- Lockfile JSON valido; `npm ci` sem warnings.
- Audit: 0 vulnerabilidades; nenhum script pendente.
