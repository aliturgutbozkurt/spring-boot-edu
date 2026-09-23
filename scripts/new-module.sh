#!/usr/bin/env bash
# Scaffolds a course module (lesson/exercise/solution + TR/EN docs + README) and registers it in pom.xml.
#
#   ./scripts/new-module.sh 06-data-jpa-postgres --title-tr "Spring Data JPA ve PostgreSQL" \
#       --title-en "Spring Data JPA and PostgreSQL" --infra postgres
#
# The id must be listed in the SPEC.md capability map (adding modules needs approval — see CLAUDE.md).
exec python3 "$(dirname "$0")/lib/coursetool.py" new "$@"
