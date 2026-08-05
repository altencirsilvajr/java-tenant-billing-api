# 003 - Persist and expose tenant billing flow

## Commit

`feat: persist and expose tenant billing flow`

## Objetivo

Entregar o fluxo REST completo com PostgreSQL, tenant boundary em leitura/escrita, snapshot de plano e auditoria append-only.

## Implementacao

- Controllers Spring MVC, validação, OpenAPI e Problem Details.
- Serviço transacional com repositories tenant-scoped e relógio injetado.
- Spring Data JPA/Hibernate, migration Flyway e proteção append-only no banco.
- Testcontainers para fluxo real e ArchUnit para a independência do domínio.

## Rastreabilidade ADR

Novo ADR criado: ADR-0002 - Append-only transactional audit.

## Verificacao

- `./mvnw test` antes da implementação — falhou por aplicação HTTP ausente (red).
- `./mvnw verify` após a implementação — passou com testes de domínio, boundary, arquitetura e integração PostgreSQL (green).
- `./scripts/verify-traceability.sh` — passou.
- `git diff --check` — passou.

## Alternativas e trade-offs

Entidades JPA ficam no adapter de infraestrutura e são mapeadas para records do domínio; a duplicação explícita preserva o domínio sem annotations de persistência.

## Proximo passo

Adicionar UI Angular, Docker Compose, CI e documentação de demonstração.
