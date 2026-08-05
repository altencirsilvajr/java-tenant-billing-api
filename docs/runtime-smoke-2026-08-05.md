# Evidência de runtime — 2026-08-05

Ambiente observado: Docker Desktop arm64, PostgreSQL 17, imagem da API Java 17 e imagem Angular 22/Nginx.

## Evidências

- `docker build -t java-tenant-billing-api:local .` — imagem multi-stage construída com sucesso.
- `docker build -t java-tenant-billing-ui:local frontend` — imagem construída e bundle Angular gerado.
- `docker compose up -d --build` — PostgreSQL e API ficaram healthy; a porta padrão `4200` da UI já estava ocupada externamente.
- UI iniciada isoladamente em `4201` — `GET /` retornou o HTML com o título `Tenant Billing Lab`.
- `GET http://localhost:8080/actuator/health` — retornou `{"status":"UP","groups":["liveness","readiness"]}`.
- `GET http://localhost:8080/v3/api-docs` — retornou OpenAPI 3.1 com as rotas do fluxo.

Os containers de smoke foram encerrados após a verificação; o volume local do PostgreSQL foi preservado.
