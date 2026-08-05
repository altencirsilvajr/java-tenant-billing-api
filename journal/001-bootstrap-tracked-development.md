# 001 - Bootstrap tracked development

## Commit

`chore: bootstrap tracked development`

## Objetivo

Estabelecer a fonte de verdade e o gate auditável antes de qualquer código de produto.

## Implementacao

- Regras de contribuição em `AGENTS.md` e `DEVELOPMENT.md`.
- SDD inicial com seams e critérios de aceite.
- Gate executável que exige exatamente um Journal por commit substantivo.

## Rastreabilidade ADR

Decisao local sem ADR novo: o incremento apenas aplica o processo obrigatório do laboratório.

## Verificacao

- `./scripts/verify-traceability.sh` — passou.
- `git diff --check` — passou.

## Alternativas e trade-offs

O bootstrap foi mantido independente do framework para que a rastreabilidade anteceda decisões técnicas.

## Proximo passo

Criar o esqueleto Spring Boot e o primeiro teste vermelho do domínio.
