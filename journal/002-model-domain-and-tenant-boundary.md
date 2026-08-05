# 002 - Model domain and tenant boundary

## Commit

`feat: model billing domain and tenant boundary`

## Objetivo

Provar as invariantes de billing e tornar explícita a barreira de acesso entre tenants antes da persistência e do HTTP.

## Implementacao

- Maven Wrapper e build Java 17 com Spring Boot 3.5.
- Plano, snapshot e assinatura como modelo de domínio independente do framework.
- Boundary de aplicação que rejeita qualquer tenant autenticado diferente do tenant alvo.

## Rastreabilidade ADR

Novo ADR criado: ADR-0001 - Shared PostgreSQL with explicit tenant boundary.

## Verificacao

- `./mvnw test` antes da implementação — falhou por símbolos de domínio e boundary ausentes (red).
- `./mvnw test` após a implementação — passou com 4 testes (green).
- `./scripts/verify-traceability.sh` — passou.
- `git diff --check` — passou.

## Alternativas e trade-offs

Database-per-tenant reduziria parte do risco lógico, mas esconderia o desafio que este laboratório precisa tornar observável.

## Proximo passo

Persistir o fluxo completo com migrations e auditoria append-only.
