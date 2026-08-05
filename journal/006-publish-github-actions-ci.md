# 006 - Publicação do CI no GitHub Actions

## Commit

`ci: publish GitHub Actions workflow`

## Objetivo

Publicar o workflow preservado fora dos commits locais reconstruídos, mantendo `main` linear e o histórico remoto intacto.

## Implementacao

- Ativa gates de backend Java 17, rastreabilidade, frontend Angular e imagens de contêiner.
- Publica workflow e Journal no mesmo commit remoto atômico.
- Mantém a safety branch com os SHAs locais anteriores à reconstrução.

## Rastreabilidade ADR

Decisao local sem ADR novo: a mudança ativa automação já validada e não altera a arquitetura.

## Verificacao

- Os dois commits de produto foram publicados por fast-forward normal sem o workflow.
- A árvore reconstruída difere da safety branch somente pela ausência temporária do workflow.
- Atualização remota feita sem force push.
- Execução do GitHub Actions verificada após a publicação.

## Alternativas e trade-offs

A GitHub App foi usada exclusivamente para o commit que altera `.github/workflows`, pois o token Git local não possui o escopo `workflow`.

## Proximo passo

Manter os três jobs verdes como requisito de integração.
