# Java Tenant Billing API

Laboratório vertical Senior em Java 17, Spring Boot 3.5 e Angular 22. O escopo é deliberadamente pequeno: demonstrar multi-tenancy em banco compartilhado sem esconder o tenant boundary, billing básico com snapshot histórico do plano e auditoria realmente append-only.

## O que este projeto prova

- Spring MVC, validação, OpenAPI e erros RFC 9457/Problem Details;
- DDD pragmático: domínio sem framework, aplicação transacional e adapter JPA explícito;
- Spring Data JPA/Hibernate sem Open Session in View;
- PostgreSQL 17, Flyway, constraints e índices compostos por tenant;
- tenant boundary aplicado antes de leituras e escritas, e repositories sem busca tenant-scoped apenas por ID;
- assinatura preservando preço/moeda/nome do plano no momento da contratação;
- audit records na mesma transação do fato e trigger bloqueando `UPDATE`/`DELETE`;
- testes JUnit 5, AssertJ, ArchUnit e Testcontainers com PostgreSQL real;
- Angular 22 standalone chamando os contratos reais;
- Docker Compose, Kubernetes/OpenShift, GitHub Actions, Jenkins e GitLab CI.

## Fluxo vertical

```text
POST /api/tenants
POST /api/plans
POST /api/tenants/{tenantId}/users             X-Tenant-Id
POST /api/tenants/{tenantId}/subscriptions     X-Tenant-Id
GET  /api/tenants/{tenantId}/subscriptions/current
GET  /api/tenants/{tenantId}/audit-records
```

Em rotas tenant-scoped, o `X-Tenant-Id` representa o tenant autenticado. Se ele não coincidir com o `tenantId` da rota, a API retorna `403 Tenant Boundary Violation` antes de consultar ou alterar dados.

## Executar a demonstração

Pré-requisitos: Docker/Compose. O caminho mais curto sobe PostgreSQL, API e UI:

```bash
docker compose up --build
```

Abra:

- UI: `http://localhost:4200`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/actuator/health`

Na UI, rode primeiro **Rodar fluxo completo** e depois **Testar violação cross-tenant**. O segundo botão cria um tenant atacante e mostra o Problem Details 403 retornado pela API real.

Para executar o backend fora do container:

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

Para o frontend, use Node 24.15+:

```bash
npm --prefix frontend ci
npm --prefix frontend start
```

## Verificação

```bash
./mvnw verify
./scripts/verify-traceability.sh
npm --prefix frontend ci
npm --prefix frontend test -- --watch=false
npm --prefix frontend run build
docker compose config --quiet
```

`mvn verify` inicia PostgreSQL 17 via Testcontainers e cobre domínio, boundary, arquitetura, migration, fluxo REST, persistência e ataque cross-tenant.

## Decisões defendíveis em entrevista

- **Banco compartilhado, boundary explícito:** o modelo reduz custo operacional e torna o risco lógico testável. A defesa existe na aplicação e os predicados de leitura incluem `tenant_id`; não se depende de um header “mágico” dentro do repository.
- **Snapshot na assinatura:** alterações futuras do catálogo não reescrevem o valor contratado. `billing_plan_id` preserva a origem e as colunas de snapshot preservam a história.
- **Auditoria append-only:** o registro entra na mesma transação e o PostgreSQL rejeita mutações. Um log de aplicação isolado não ofereceria a mesma atomicidade.
- **Domínio sem JPA:** annotations e detalhes Hibernate ficam no adapter. O custo de mapping é aceito para manter invariantes executáveis sem container.
- **Teste com PostgreSQL real:** partial index, trigger PL/pgSQL e tipos não seriam provados com um banco em memória.

Os detalhes duráveis estão em `docs/adr/`; a especificação ativa está em `docs/sdd/tenant-billing-lab.md`; cada incremento possui evidência em `journal/`.

## Operação

- Configuração por ambiente: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PORT`.
- Correlation ID: aceite/devolução do header `X-Correlation-Id`, também inserido no MDC.
- Actuator expõe `health`, `info`, `metrics` e `prometheus`.
- A imagem da API executa como usuário não-root.
- `deploy/kubernetes/app.yaml` contém ConfigMap, Deployment, Service, probes e limites; `deploy/openshift/route.yaml` adiciona Route TLS.
- `deploy/kubernetes/secret.example.yaml` é somente um molde; credenciais reais não são versionadas.

## Estrutura

```text
src/main/java/.../domain          invariantes e records
src/main/java/.../application     casos de uso, boundary e porta
src/main/java/.../infrastructure  JPA/Hibernate e mappings
src/main/java/.../api             HTTP, Problem Details e correlation
frontend/                         Angular standalone
deploy/                           Kubernetes e OpenShift
docs/adr, docs/sdd, journal       decisões e rastreabilidade
```
