#!/usr/bin/env bash
# server-install.sh — Install or update the FizzleSMP Fabric server using packwiz-installer.
#
# Usage:
#   ./server-install.sh                 # install/update to the latest published release
#   ./server-install.sh v1.4.0          # install/update to a specific version tag
#   ./server-install.sh master          # install/update to the current master branch (unreleased)
#   ./server-install.sh --help
#
# What this does:
#   1. Downloads packwiz-installer-bootstrap.jar next to this script (if missing)
#   2. Runs the bootstrap against the FizzleSMP pack.toml for the chosen ref
#   3. Installs/updates only server-side mods and configs
#
# Safe to re-run. The installer diffs against a local manifest and only
# downloads changed files. It removes mods/configs that were dropped from
# the pack. Files it does NOT manage (world/, player data, server.properties,
# eula.txt, Fabric server jar) are never touched.
#
# First-run from an existing non-packwiz server:
#   - Back up world/, server.properties, ops.json, whitelist.json before running
#   - The installer will replace any mods/configs that exist under packwiz management

set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BOOTSTRAP_JAR="$SCRIPT_DIR/packwiz-installer-bootstrap.jar"
BOOTSTRAP_URL="https://github.com/packwiz/packwiz-installer-bootstrap/releases/latest/download/packwiz-installer-bootstrap.jar"

REPO="rfizzle/FizzleSMP"
REF="${1:-}"

usage() {
    sed -n '2,21p' "$0" | sed 's/^# //;s/^#//'
}

case "$REF" in
    --help|-h)
        usage
        exit 0
        ;;
esac

# --- Preflight ---

if ! command -v java >/dev/null 2>&1; then
    echo "Error: java is required but was not found in PATH."
    echo "       Install a JDK (e.g., 'apt install openjdk-21-jre-headless') and re-run."
    exit 1
fi

# --- Resolve ref (default: latest GitHub release) ---

if [[ -z "$REF" ]]; then
    echo "▸ Resolving latest release tag..."
    if command -v curl >/dev/null 2>&1; then
        REF=$(curl -fsSL "https://api.github.com/repos/${REPO}/releases/latest" \
            | grep -oE '"tag_name":[[:space:]]*"[^"]+"' \
            | head -1 \
            | sed -E 's/.*"tag_name":[[:space:]]*"([^"]+)".*/\1/')
    fi
    if [[ -z "$REF" ]]; then
        echo "Error: could not determine latest release tag from GitHub API."
        echo "       Specify a version explicitly: $0 v1.2.3"
        exit 1
    fi
    echo "  Latest release: $REF"
fi

PACK_URL="https://raw.githubusercontent.com/${REPO}/${REF}/modpack/pack.toml"

echo "=========================================="
echo " FizzleSMP server install/update"
echo "=========================================="
echo "  Ref:       $REF"
echo "  Pack URL:  $PACK_URL"
echo "  Target:    $(pwd)"
echo ""

# --- Download bootstrap jar if missing ---

if [[ ! -f "$BOOTSTRAP_JAR" ]]; then
    echo "▸ Downloading packwiz-installer-bootstrap.jar..."
    if ! curl -fsSL -o "$BOOTSTRAP_JAR" "$BOOTSTRAP_URL"; then
        echo "Error: failed to download $BOOTSTRAP_URL"
        exit 1
    fi
    echo "  Saved to $BOOTSTRAP_JAR"
fi

# --- Run installer ---

echo "▸ Running packwiz-installer (server side)..."
echo ""

# -g: no GUI (headless server-safe)
# -s server: install only files marked as server/both side
java -jar "$BOOTSTRAP_JAR" -g -s server "$PACK_URL"

echo ""
echo "=========================================="
echo "✓ FizzleSMP server synced to $REF"
echo "=========================================="
echo ""
echo "Next steps:"
echo "  - Start the server: ./run.sh (or however you launch Fabric)"
echo "  - To update later, re-run this script (optionally with a new version)"
