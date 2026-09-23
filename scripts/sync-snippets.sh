#!/usr/bin/env bash
# Refreshes every "<!-- snippet: ... -->" code block in a module's docs from the current source.
#
#   ./scripts/sync-snippets.sh 01-core-container
exec python3 "$(dirname "$0")/lib/coursetool.py" sync-snippets "$@"
