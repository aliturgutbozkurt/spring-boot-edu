#!/usr/bin/env bash
# Checks modules against the Module Definition of Done (CLAUDE.md).
#
#   ./scripts/check-module.sh 06-data-jpa-postgres       # structure, TR/EN parity, snippets, fresh PDFs
#   ./scripts/check-module.sh --strict 06-data-jpa-postgres  # + finished content (no placeholders, ≥5 examples, ≥3 exercises)
#   ./scripts/check-module.sh --all
exec python3 "$(dirname "$0")/lib/coursetool.py" check "$@"
