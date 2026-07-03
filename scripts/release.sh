#!/usr/bin/env bash
# release.sh — Cut a new FizzleSMP release.
#
# Usage:
#   ./scripts/release.sh patch          # 1.2.3 → 1.2.4
#   ./scripts/release.sh minor          # 1.2.3 → 1.3.0
#   ./scripts/release.sh major          # 1.2.3 → 2.0.0
#   ./scripts/release.sh 1.4.0          # explicit version
#   ./scripts/release.sh patch --no-push # don't push to origin after tagging
#
# What this does:
#   1. Verifies working tree is clean and on master
#   2. Bumps version in modpack/pack.toml
#   3. Runs `packwiz refresh` to keep index.toml in sync
#   4. Promotes changelogs/unreleased.md to changelogs/<version>.md
#   5. Commits + tags vX.Y.Z
#   6. Pushes master and the tag to origin (unless --no-push)
#
# Pushing the tag triggers .github/workflows/release.yml which builds the
# client and server ZIPs and publishes a GitHub Release.

set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
PACK_TOML="$PROJECT_DIR/modpack/pack.toml"
CHANGELOGS_DIR="$PROJECT_DIR/changelogs"

BUMP=""
EXPLICIT_VERSION=""
PUSH=true

usage() {
    sed -n '2,19p' "$0" | sed 's/^# //;s/^#//'
}

for arg in "$@"; do
    case "$arg" in
        patch|minor|major) BUMP="$arg" ;;
        --no-push)         PUSH=false ;;
        --help|-h)         usage; exit 0 ;;
        [0-9]*.[0-9]*.[0-9]*) EXPLICIT_VERSION="$arg" ;;
        *)
            echo "Error: unknown argument '$arg'"
            echo ""
            usage
            exit 1
            ;;
    esac
done

if [[ -z "$BUMP" && -z "$EXPLICIT_VERSION" ]]; then
    echo "Error: must specify a bump type (patch|minor|major) or an explicit version"
    echo ""
    usage
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

if [[ ! -f "$PACK_TOML" ]]; then
    echo "Error: $PACK_TOML not found."
    exit 1
fi

# --- Parse current version ---

current_version=$(awk -F' = ' '/^version = / { gsub(/"/, "", $2); print $2; exit }' "$PACK_TOML")

if [[ -z "$current_version" ]]; then
    echo "Error: could not parse 'version = ' from $PACK_TOML."
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

new_tag="v${new_version}"

# Sanity check tag doesn't already exist
if git rev-parse "$new_tag" >/dev/null 2>&1; then
    echo "Error: tag $new_tag already exists."
    exit 1
fi

# --- Check for changelog content ---

unreleased_file="$CHANGELOGS_DIR/unreleased.md"
versioned_file="$CHANGELOGS_DIR/${new_version}.md"

if [[ -f "$versioned_file" ]]; then
    echo "Error: $versioned_file already exists."
    exit 1
fi

has_content=false
if [[ -f "$unreleased_file" ]]; then
    real_content=$(grep -vE '^(##|[[:space:]]*$)' "$unreleased_file" || true)
    if [[ -n "$real_content" ]]; then
        has_content=true
    fi
fi

if [[ "$has_content" != true ]]; then
    echo "Warning: changelogs/unreleased.md has no entries."
    echo "  The release workflow will generate notes from the commit log."
    read -rp "Continue? [y/N] " confirm
    if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
        echo "Aborted."
        exit 1
    fi
fi

echo "=========================================="
echo " FizzleSMP release"
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

# --- 1. Bump version in pack.toml ---

echo "▸ Bumping version in modpack/pack.toml..."
sed -i.bak -E "s/^version = \"[^\"]+\"/version = \"${new_version}\"/" "$PACK_TOML"
rm -f "${PACK_TOML}.bak"

# --- 2. packwiz refresh ---

echo "▸ Refreshing packwiz index..."
(cd "$PROJECT_DIR/modpack" && packwiz refresh)

# --- 3. Promote changelogs/unreleased.md ---

mkdir -p "$CHANGELOGS_DIR"

if [[ "$has_content" == true ]]; then
    echo "▸ Promoting changelogs/unreleased.md → changelogs/${new_version}.md..."
    cp "$unreleased_file" "$versioned_file"
    # Reset unreleased file to empty template
    : > "$unreleased_file"
else
    echo "▸ No unreleased changelog content; skipping promotion."
fi

# --- 4. Git commit + tag ---

echo "▸ Committing release..."
git add "$PACK_TOML" "$PROJECT_DIR/modpack/index.toml" "$CHANGELOGS_DIR/"
git commit -m "chore(release): ${new_tag}"

echo "▸ Tagging $new_tag..."
tag_message="FizzleSMP ${new_tag}

See changelogs/${new_version}.md for the full release notes."
git tag -a "$new_tag" -m "$tag_message"

# --- 5. Push ---

if [[ "$PUSH" == true ]]; then
    echo "▸ Pushing master and $new_tag to origin..."
    git push origin master
    git push origin "$new_tag"
    echo ""
    echo "✓ Release $new_tag pushed."
    echo "  GitHub Actions will build and publish the release artifacts."
    echo "  Watch: https://github.com/rfizzle/FizzleSMP/actions"
else
    echo ""
    echo "✓ Release $new_tag committed and tagged locally."
    echo "  To publish, run:"
    echo "    git push origin master"
    echo "    git push origin $new_tag"
fi
