# AGENTS.md

Este repositório usa desenvolvimento rastreável.

## Regras obrigatórias

- Trabalhe em incrementos verticais e test-first.
- Cada commit substantivo não merge deve adicionar ou atualizar exatamente um arquivo em `journal/`.
- Todo Journal deve declarar se criou, aplicou ou dispensou um ADR.
- ADRs são reservados a decisões duráveis e difíceis de reverter.
- Código, identificadores e commits são escritos em inglês; documentação pode ser PT-BR.

## Gate antes de commit

```bash
./scripts/verify-traceability.sh
./mvnw verify
npm --prefix frontend ci
npm --prefix frontend test -- --watch=false
npm --prefix frontend run build
```

Enquanto um subsistema ainda não existir, execute os comandos aplicáveis e registre a limitação no Journal.
