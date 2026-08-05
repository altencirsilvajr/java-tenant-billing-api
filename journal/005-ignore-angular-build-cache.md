# 005 - Ignore Angular build cache

## Commit

`chore: ignore Angular build cache`

## Objetivo

Manter o repositório reproduzível sem versionar artefatos locais gerados pelo Angular.

## Implementacao

- Remoção de `frontend/.angular/` do índice Git.
- Regra explícita no `.gitignore`.

## Rastreabilidade ADR

Decisao local sem ADR novo: trata-se de higiene de build reversível, sem impacto arquitetural.

## Verificacao

- `git status --short` — apenas cache removido, `.gitignore` e este Journal no incremento.
- `./scripts/verify-traceability.sh` — passou.
- `git diff --check` — passou.

## Alternativas e trade-offs

O cache permanece disponível localmente quando regenerado, mas não integra a história do projeto.

## Proximo passo

Publicar os commits locais quando a credencial GitHub possuir o escopo `workflow`.
