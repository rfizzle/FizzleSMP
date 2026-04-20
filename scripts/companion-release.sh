#!/usr/bin/env bash
# companion-release.sh — Cut a new release for a companion mod.
#
# Usage:
#   ./scripts/companion-release.sh <companion> patch          # 0.1.0 → 0.1.1
#   ./scripts/companion-release.sh <companion> minor          # 0.1.0 → 0.2.0
#   ./scripts/companion-release.sh <companion> major          # 0.1.0 → 1.0.0
#   ./scripts/companion-release.sh <companion> 0.3.0          # explicit version
#   ./scripts/companion-release.sh <companion> patch --no-push
#
# What this does:
#   1. Verifies working tree is clean and on master
#   2. Bumps mod_version in companions/<companion>/gradle.properties
#   3. Commits + tags <companion>-vX.Y.Z
#   4. Pushes master and the tag to origin (unless --no-push)
#
# The tag name is intentionally namespaced as <companion>-vX.Y.Z so that
# multiple companions can coexist with the modpack's own vX.Y.Z tags without
# collisions. build.gradle's git-describe logic matches <companion>-v* for
# its automatic dev-build suffix.

set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

usage() { sed -n '2,20p' "$0" | sed 's/^# //;s/^#//'; }

COMPANION=""
BUMP=""
EXPLICIT_VERSION=""
PUSH=true

for arg in "$@"; do
    case "$arg" in
        patch|minor|major)    BUMP="$arg" ;;
        --no-push)            PUSH=false ;;
        --help|-h)            usage; exit 0 ;;
        [0-9]*.[0-9]*.[0-9]*) EXPLICIT_VERSION="$arg" ;;
        -*)
            echo "Error: unknown flag '$arg'"
            echo ""
            usage
            exit 1
            ;;
        *)
            if [[ -z "$COMPANION" ]]; then
                COMPANION="$arg"
            else
                echo "Error: unexpected argument '$arg'"
                echo ""
                usage
                exit 1
            fi
            ;;
    esac
done

if [[ -z "$COMPANION" ]]; then
    echo "Error: companion name required (e.g., fizzle-difficulty)"
    echo ""
    usage
    exit 1
fi

if [[ -z "$BUMP" && -z "$EXPLICIT_VERSION" ]]; then
    echo "Error: must specify a bump type (patch|minor|major) or an explicit version"
    echo ""
    usage
    exit 1
fi

COMPANION_DIR="$PROJECT_DIR/companions/$COMPANION"
PROPS="$COMPANION_DIR/gradle.properties"

if [[ ! -d "$COMPANION_DIR" ]]; then
    echo "Error: $COMPANION_DIR not found."
    exit 1
fi

if [[ ! -f "$PROPS" ]]; then
    echo "Error: $PROPS not found."
    exit 1
fi

# --- Preflight checks ---

cd "$PROJECT_DIR"

if [[ -n "$(git status --porcelain)" ]]; then
    echo "Error: working tree has uncommitted changes. Commit or stash first."
    git status --short
    exit 1
fi

current_branch=$(git rev-parse --abbrev-ref HEAD)
if [[ "$current_branch" != "master" ]]; then
    echo "Error: releases must be cut from master (currently on '$current_branch')."
    exit 1
fi

# --- Parse current version ---

current_version=$(awk -F= '/^mod_version/ {gsub(/ /,"",$2); print $2; exit}' "$PROPS")

if [[ -z "$current_version" ]]; then
    echo "Error: could not parse 'mod_version' from $PROPS."
    exit 1
fi

IFS='.' read -r cur_major cur_minor cur_patch <<< "$current_version"

# --- Compute new version ---

if [[ -n "$EXPLICIT_VERSION" ]]; then
    new_version="$EXPLICIT_VERSION"
else
    case "$BUMP" in
        patch) new_version="${cur_major}.${cur_minor}.$((cur_patch + 1))" ;;
        minor) new_version="${cur_major}.$((cur_minor + 1)).0" ;;
        major) new_version="$((cur_major + 1)).0.0" ;;
    esac
fi

new_tag="${COMPANION}-v${new_version}"

if git rev-parse "$new_tag" >/dev/null 2>&1; then
    echo "Error: tag $new_tag already exists."
    exit 1
fi

echo "=========================================="
echo " $COMPANION release"
echo "=========================================="
echo "  Current: $current_version"
echo "  New:     $new_version"
echo "  Tag:     $new_tag"
echo "  Push:    $PUSH"
echo ""
read -rp "Proceed? [y/N] " confirm
if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
    echo "Aborted."
    exit 1
fi
echo ""

# --- Bump mod_version in gradle.properties ---

echo "▸ Bumping mod_version in companions/${COMPANION}/gradle.properties..."
sed -i.bak -E "s/^mod_version[[:space:]]*=.*/mod_version=${new_version}/" "$PROPS"
rm -f "${PROPS}.bak"

# --- Commit + tag ---

echo "▸ Committing release..."
git add "$PROPS"
git commit -m "chore(${COMPANION}): release v${new_version}"

echo "▸ Tagging $new_tag..."
git tag -a "$new_tag" -m "${COMPANION} v${new_version}"

# --- Push ---

if [[ "$PUSH" == true ]]; then
    echo "▸ Pushing master and $new_tag to origin..."
    git push origin master
    git push origin "$new_tag"
    echo ""
    echo "✓ Release $new_tag pushed."
else
    echo ""
    echo "✓ Release $new_tag committed and tagged locally."
    echo "  To publish, run:"
    echo "    git push origin master"
    echo "    git push origin $new_tag"
fi
