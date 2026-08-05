# ADR-0002 - Append-only transactional audit

## Status

Accepted

## Contexto

Tenant, usuário e assinatura são fatos relevantes para segurança e billing. Uma trilha alterável ou gravada fora da transação pode contradizer o estado confirmado.

## Decisao

Persistir audit records append-only na mesma transação do comando. A aplicação oferece somente inserção e leitura tenant-scoped; a migration bloqueia `UPDATE` e `DELETE` por trigger PostgreSQL.

## Consequencias

- Estado e evidência são confirmados ou revertidos juntos.
- Correções futuras devem adicionar um novo fato compensatório.
- A tabela cresce monotonicamente e exige retenção planejada em um produto real.

## Alternativas rejeitadas

### Log de aplicação

Logs operacionais podem ter retenção e entrega diferentes e não garantem atomicidade com o banco.
