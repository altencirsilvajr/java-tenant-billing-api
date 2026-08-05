# ADR-0001 - Shared PostgreSQL with explicit tenant boundary

## Status

Accepted

## Contexto

O laboratório precisa demonstrar isolamento em uma arquitetura SaaS econômica sem depender de um banco por cliente.

## Decisao

Usar um schema PostgreSQL compartilhado. Toda operação tenant-scoped exige tenant autenticado e tenant alvo, validados no boundary da aplicação, e toda consulta persistente tenant-scoped contém `tenant_id` no predicado.

## Consequencias

- O risco de vazamento lógico fica visível e testável.
- Índices e constraints compostos incluem `tenant_id`.
- Nenhum repository tenant-scoped expõe busca apenas por identificador global.

## Alternativas rejeitadas

### Database per tenant

Melhora o isolamento físico, mas aumenta custo operacional e não exercita a competência central escolhida para o laboratório.
