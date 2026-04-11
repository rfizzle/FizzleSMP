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
#   4. Moves CHANGELOG.md [Unreleased] content to a new [X.Y.Z] - YYYY-MM-DD section
#   5. Commits + tags vX.Y.Z
#   6. Pushes master and the tag to origin (unless --no-push)
#
# Pushing the tag triggers .github/workflows/release.yml which builds the
# client and server ZIPs and publishes a GitHub Release.

set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
PACK_TOML="$PROJECT_DIR/modpack/pack.toml"
CHANGELOG="$PROJECT_DIR/CHANGELOG.md"

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

if [[ ! -f "$CHANGELOG" ]]; then
    echo "Error: $CHANGELOG not found."
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

# --- Verify CHANGELOG has content under [Unreleased] ---

unreleased_body=$(awk '
    /^## \[Unreleased\]/ { found = 1; next }
    found && /^## \[/    { exit }
    found                { print }
' "$CHANGELOG")

# Strip empty section headers to check for real content
real_content=$(echo "$unreleased_body" | grep -vE '^(###|[[:space:]]*$)' || true)

if [[ -z "$real_content" ]]; then
    echo "Warning: CHANGELOG.md [Unreleased] section has no entries."
    read -rp "Continue with empty release notes? [y/N] " confirm
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

# --- 3. Update CHANGELOG.md ---

echo "▸ Rolling CHANGELOG [Unreleased] into [$new_version]..."
today=$(date +%Y-%m-%d)

tmp_changelog=$(mktemp)
awk -v version="$new_version" -v date="$today" '
    /^## \[Unreleased\]/ {
        # Emit a fresh empty Unreleased section...
        print "## [Unreleased]"
        print ""
        print "### Added"
        print ""
        print "### Changed"
        print ""
        print "### Fixed"
        print ""
        print "### Removed"
        print ""
        # ...followed by the new version header. The original content that
        # was under [Unreleased] will now live under [X.Y.Z] since we skip
        # the old header and let the rest of the file stream through.
        print "## [" version "] - " date
        # Skip the blank line that usually follows the Unreleased header
        # so we do not end up with a doubled blank line.
        getline blank
        if (blank != "") print blank
        next
    }
    { print }
' "$CHANGELOG" > "$tmp_changelog"
mv "$tmp_changelog" "$CHANGELOG"

# --- 4. Git commit + tag ---

echo "▸ Committing release..."
git add "$PACK_TOML" "$PROJECT_DIR/modpack/index.toml" "$CHANGELOG"
git commit -m "chore(release): ${new_tag}"

echo "▸ Tagging $new_tag..."
tag_message="FizzleSMP ${new_tag}

See CHANGELOG.md for the full release notes."
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
