# SDD — Tenant Billing Lab

## Objetivo

Demonstrar, em um fluxo vertical pequeno, isolamento lógico forte entre tenants em PostgreSQL compartilhado, billing básico, histórico de assinatura e auditoria append-only com Java 17 e Spring Boot.

## Fluxo observável

1. Criar tenant.
2. Criar plano global.
3. Criar usuário no tenant autenticado pelo header `X-Tenant-Id`.
4. Assinar o plano no tenant autenticado.
5. Consultar a assinatura atual somente dentro do mesmo boundary.
6. Consultar a trilha de auditoria do próprio tenant.

Tentativas de usar um `X-Tenant-Id` diferente do tenant da rota devem retornar HTTP 403 em Problem Details e jamais executar leitura ou escrita cross-tenant.

## Arquitetura

- `domain`: agregados, value objects e invariantes sem dependência de framework.
- `application`: casos de uso e portas reais para persistência/tempo.
- `infrastructure`: Spring Data JPA, Hibernate e PostgreSQL.
- `api`: controllers REST, boundary HTTP, Problem Details e OpenAPI.
- `frontend`: Angular funcional consumindo a API real.

## Critérios de aceite

- Domínio rejeita dados e transições inválidas.
- Testes de integração provam o fluxo feliz e o bloqueio cross-tenant com PostgreSQL real.
- Audit records não possuem operação pública de update/delete e são inseridos na mesma transação do fato auditado.
- Flyway cria schema, constraints e índices compostos por tenant.
- OpenAPI, health, metrics, logs correlacionados, Docker e Compose estão disponíveis.
- Angular executa o fluxo real e torna visível o erro 403 do laboratório.
