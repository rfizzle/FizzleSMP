#!/usr/bin/env bash
# build-companions.sh — Build every Fabric mod under companions/, stage the
# resulting jars into modpack/mods/, and run `packwiz refresh` so they land in
# index.toml with fresh hashes.
#
# Usage:
#   ./scripts/build-companions.sh            # build all companions and refresh
#   ./scripts/build-companions.sh --dry-run  # show what would be built/copied
#   ./scripts/build-companions.sh <name>     # build a single companion by folder name
#
# Each companion directory must contain a settings.gradle / build.gradle and
# produce exactly one primary jar under build/libs/ (the script filters out
# -sources.jar and -dev.jar). Gradle is invoked via the companion's own
# ./gradlew wrapper when present; otherwise the system `gradle` is used.

set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
COMPANIONS_DIR="$PROJECT_DIR/companions"
MODS_DIR="$PROJECT_DIR/modpack/mods"
STAMP_PREFIX="companion-"

DRY_RUN=false
ONLY=""

for arg in "$@"; do
    case "$arg" in
        --dry-run) DRY_RUN=true ;;
        --help|-h)
            sed -n '2,15p' "$0"
            exit 0
            ;;
        -*)
            echo "Unknown option: $arg" >&2
            exit 1
            ;;
        *)
            ONLY="$arg"
            ;;
    esac
done

if [[ ! -d "$COMPANIONS_DIR" ]]; then
    echo "Error: $COMPANIONS_DIR not found" >&2
    exit 1
fi

if [[ ! -d "$MODS_DIR" ]]; then
    echo "Error: $MODS_DIR not found" >&2
    exit 1
fi

# --- locate gradle for a given companion dir ---
gradle_cmd() {
    local dir="$1"
    if [[ -x "$dir/gradlew" ]]; then
        echo "$dir/gradlew"
    elif command -v gradle >/dev/null 2>&1; then
        echo "gradle"
    else
        echo ""
    fi
}

# --- collect companion directories ---
shopt -s nullglob
companions=()
for d in "$COMPANIONS_DIR"/*/; do
    [[ -f "$d/build.gradle" || -f "$d/build.gradle.kts" ]] || continue
    name="$(basename "$d")"
    if [[ -n "$ONLY" && "$name" != "$ONLY" ]]; then
        continue
    fi
    companions+=("$d")
done

if [[ ${#companions[@]} -eq 0 ]]; then
    if [[ -n "$ONLY" ]]; then
        echo "No companion matching '$ONLY' found under $COMPANIONS_DIR" >&2
    else
        echo "No buildable companions found under $COMPANIONS_DIR" >&2
    fi
    exit 1
fi

built_jars=()

for dir in "${companions[@]}"; do
    name="$(basename "$dir")"
    echo ""
    echo "==> Building companion: $name"

    gradle="$(gradle_cmd "$dir")"
    if [[ -z "$gradle" ]]; then
        echo "Error: neither $dir/gradlew nor system 'gradle' is available" >&2
        exit 1
    fi

    if $DRY_RUN; then
        echo "  dry-run: would run '$gradle build' in $dir"
    else
        (cd "$dir" && "$gradle" --quiet build)
    fi

    # Pick the main jar: exclude -sources, -dev, -javadoc variants.
    libs="$dir/build/libs"
    if $DRY_RUN && [[ ! -d "$libs" ]]; then
        echo "  dry-run: would stage jar from $libs (not yet built)"
        continue
    fi

    jar=""
    for candidate in "$libs"/*.jar; do
        [[ -e "$candidate" ]] || continue
        case "$candidate" in
            *-sources.jar|*-dev.jar|*-javadoc.jar) continue ;;
        esac
        if [[ -n "$jar" ]]; then
            echo "Error: multiple candidate jars in $libs — refusing to guess" >&2
            ls -1 "$libs"/*.jar >&2
            exit 1
        fi
        jar="$candidate"
    done

    if [[ -z "$jar" ]]; then
        echo "Error: no primary jar found in $libs" >&2
        exit 1
    fi

    target="$MODS_DIR/${STAMP_PREFIX}$(basename "$jar")"
    if $DRY_RUN; then
        echo "  dry-run: would copy $jar -> $target"
    else
        # Remove prior builds of this companion before copying the new one so
        # old versions don't linger in modpack/mods/.
        find "$MODS_DIR" -maxdepth 1 -name "${STAMP_PREFIX}${name}-*.jar" -delete
        cp "$jar" "$target"
        echo "  staged: $(basename "$target")"
        built_jars+=("$target")
    fi
done

echo ""
if $DRY_RUN; then
    echo "Dry run complete. No files written."
    exit 0
fi

# --- refresh packwiz index so staged jars get hashed into index.toml ---
if ! command -v packwiz >/dev/null 2>&1; then
    echo "Warning: 'packwiz' not on PATH — staged ${#built_jars[@]} jar(s) but" >&2
    echo "         could not run 'packwiz refresh'. Run it manually in modpack/." >&2
    exit 0
fi

echo "==> Refreshing packwiz index"
(cd "$PROJECT_DIR/modpack" && packwiz refresh)

echo ""
echo "Done. Staged ${#built_jars[@]} companion jar(s):"
for j in "${built_jars[@]}"; do
    echo "  - ${j#$PROJECT_DIR/}"
done
