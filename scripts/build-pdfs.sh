#!/usr/bin/env bash
# Builds PDFs next to the course Markdown files, using Pandoc + XeLaTeX in Docker (nothing to install locally).
#
#   ./scripts/build-pdfs.sh                     # every module, capstone and docs/*.md
#   ./scripts/build-pdfs.sh 06-data-jpa-postgres  # one module (or: capstone)
#   ./scripts/build-pdfs.sh path/to/dir-or-file.md
#   ./scripts/build-pdfs.sh --force ...         # rebuild even if the Markdown did not change
#
# Each docs directory gets a .pdf-manifest with the SHA-256 of every Markdown source. Unchanged files
# are skipped, and scripts/check-module.sh uses the manifest to detect stale PDFs (mtime is useless
# after a git clone). PDFs are reproducible: their timestamp is the Markdown's last commit time.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

force=false
targets=()
for arg in "$@"; do
  case "$arg" in
    --force) force=true ;;
    -h|--help) sed -n '2,11p' "$0"; exit 0 ;;
    *) targets+=("$arg") ;;
  esac
done

# ---- collect Markdown files ------------------------------------------------------------------
sources=()
add_dir() { while IFS= read -r f; do sources+=("$f"); done < <(find "$1" -name '*.md' -not -name 'README.md' | sort); }

if [ ${#targets[@]} -eq 0 ]; then
  for d in modules/*/docs capstone/docs; do [ -d "$d" ] && add_dir "$d"; done
  for f in docs/*.md; do [ -f "$f" ] && sources+=("$f"); done
else
  for t in "${targets[@]}"; do
    if   [ -f "$t" ];                 then sources+=("$t")
    elif [ -d "$t" ];                 then add_dir "$t"
    elif [ -d "modules/$t/docs" ];    then add_dir "modules/$t/docs"
    elif [ "$t" = capstone ] && [ -d capstone/docs ]; then add_dir capstone/docs
    else echo "✗ Unknown target: $t" >&2; exit 2
    fi
  done
fi
[ ${#sources[@]} -eq 0 ] && { echo "Nothing to build."; exit 0; }

sha() { shasum -a 256 "$1" | cut -d' ' -f1; }
manifest_of() { echo "$(dirname "$1" | sed -E 's#/(tr|en)$##')/.pdf-manifest"; }
manifest_key() { local m; m="$(dirname "$(manifest_of "$1")")"; echo "${1#"$m"/}"; }

# ---- decide what needs building ---------------------------------------------------------------
jobs=()
for f in "${sources[@]}"; do
  m="$(manifest_of "$f")"; key="$(manifest_key "$f")"
  if ! $force && [ -f "${f%.md}.pdf" ] && [ -f "$m" ] && grep -qx "$(sha "$f")  $key" "$m"; then
    echo "· up to date  $f"; continue
  fi
  epoch="$(git log -1 --format=%ct -- "$f" 2>/dev/null || true)"
  jobs+=("${epoch:-$(date +%s)}|$f")
done
[ ${#jobs[@]} -eq 0 ] && { echo "All PDFs are up to date."; exit 0; }

docker info >/dev/null 2>&1 || { echo "✗ Docker is not running." >&2; exit 1; }

# Local image = pandoc/extra + Turkish support. The tag changes whenever the Dockerfile changes.
DOCKERFILE="docs/templates/pandoc/Dockerfile"
PANDOC_IMAGE="spring-boot-edu/pandoc:$(sha "$DOCKERFILE" | cut -c1-12)"
if ! docker image inspect "$PANDOC_IMAGE" >/dev/null 2>&1; then
  echo "… building PDF toolchain image $PANDOC_IMAGE (first run only)"
  docker build -q -t "$PANDOC_IMAGE" -f "$DOCKERFILE" docs/templates/pandoc >/dev/null
fi

# ---- build inside one container -----------------------------------------------------------------
stamp="$(mktemp)"; trap 'rm -f "$stamp"' EXIT
sleep 1   # make sure freshly written PDFs are strictly newer than the stamp
build_status=0
docker run --rm -i \
  -v "$ROOT":/data -w /data -u "$(id -u):$(id -g)" -e HOME=/tmp \
  --entrypoint bash "$PANDOC_IMAGE" -s -- "${jobs[@]}" <<'IN_CONTAINER' || build_status=$?
set -euo pipefail
rsvg-convert -f pdf -o /tmp/logo.pdf docs/assets/logo.svg
status=0
for job in "$@"; do
  epoch="${job%%|*}"; src="${job#*|}"; out="${src%.md}.pdf"
  if SOURCE_DATE_EPOCH="$epoch" pandoc "$src" --defaults docs/templates/pandoc/defaults.yaml \
       -V titlepage-logo=/tmp/logo.pdf -o "$out" 2>/tmp/err.log; then
    echo "✓ built       $out"
  else
    echo "✗ FAILED      $src"; sed 's/^/    /' /tmp/err.log; status=1
  fi
done
exit $status
IN_CONTAINER

# ---- update manifests for successfully built files ---------------------------------------------
for job in "${jobs[@]}"; do
  f="${job#*|}"; [ "${f%.md}.pdf" -nt "$stamp" ] || continue
  m="$(manifest_of "$f")"; key="$(manifest_key "$f")"
  touch "$m"
  { grep -v "  $key\$" "$m" || true; echo "$(sha "$f")  $key"; } | sort -k2 > "$m.tmp" && mv "$m.tmp" "$m"
done

exit $build_status
