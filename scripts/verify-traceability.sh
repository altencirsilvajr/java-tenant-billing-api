#!/usr/bin/env bash
set -euo pipefail

repo_root="$(git rev-parse --show-toplevel)"
cd "$repo_root"

test -f AGENTS.md
test -f DEVELOPMENT.md
test -d journal
test -d docs/adr
test -d docs/sdd

if git rev-parse --verify HEAD >/dev/null 2>&1; then
  staged_count="$(git diff --cached --name-only | wc -l | tr -d ' ')"
  if [[ "$staged_count" -gt 0 ]]; then
    journal_count="$(git diff --cached --name-only --diff-filter=AM | awk '/^journal\/.*\.md$/ {count++} END {print count+0}')"
    context="staged increment"
  else
    journal_count="$(git show --pretty=format: --name-only --diff-filter=AM HEAD | awk '/^journal\/.*\.md$/ {count++} END {print count+0}')"
    context="HEAD"
  fi
  if [[ "$journal_count" -ne 1 ]]; then
    echo "A substantive commit must contain exactly one Journal; found $journal_count in $context." >&2
    exit 1
  fi
fi

echo "Traceability gate passed."
