# 004 - Deliver UI and operability

## Commit

`feat: deliver Angular lab and operability assets`

## Objetivo

Tornar o laboratório demonstrável de ponta a ponta e verificável em pipelines e plataformas de container.

## Implementacao

- Angular 22 standalone com fluxo real e simulação cross-tenant visível.
- Testes do cliente HTTP nos contratos públicos, build de produção e lockfile.
- Imagens multi-stage, Compose, probes, manifests Kubernetes e Route OpenShift.
- GitHub Actions executável e exemplos equivalentes Jenkins/GitLab.
- README PT-BR com execução, estudo e decisões para entrevista.

## Rastreabilidade ADR

Decisao local sem ADR novo: UI, pipelines e manifests aplicam os critérios operacionais já definidos no SDD sem introduzir uma decisão durável de domínio.

## Verificacao

- `npm --prefix frontend ci` — passou; o npm local alertou que Node 24.12 é anterior ao mínimo do Angular 22.
- testes e build Angular executados com Node 24.15 — passaram.
- `./mvnw verify` — passou com 5 testes unitários/arquiteturais e 2 integrações PostgreSQL.
- `docker compose config --quiet` — passou.
- build das imagens da API e frontend — passou.
- smoke em containers — health `UP`, OpenAPI e HTML Angular responderam; a UI foi mapeada temporariamente em `4201` porque `4200` já estava ocupada por outro processo local.
- `./scripts/verify-traceability.sh` e `git diff --check` — passaram.

## Alternativas e trade-offs

A URL local da API é explícita no laboratório para manter a demonstração simples; um produto usaria configuração de runtime ou proxy por ambiente.

## Proximo passo

Usar a UI e o Swagger no roteiro de entrevista.
