# Processo de desenvolvimento

O laboratório evolui por tracer bullets verticais: teste vermelho, implementação mínima verde, revisão e commit atômico. Cada commit substantivo mantém exatamente um Journal com objetivo, implementação, rastreabilidade ADR e comandos realmente observados.

ADRs documentam apenas decisões duráveis. A especificação ativa vive em `docs/sdd/`; mudanças no comportamento devem manter essa fonte de verdade atualizada.

Antes de cada commit, revisar o diff, executar `git diff --check`, o gate de rastreabilidade e as verificações proporcionais ao incremento.
