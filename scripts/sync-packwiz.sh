#!/usr/bin/env bash
# sync-packwiz.sh — Sync mods from plugins/*.md into packwiz
#
# Usage:
#   ./scripts/sync-packwiz.sh           # Install missing mods
#   ./scripts/sync-packwiz.sh --prune   # Also remove mods not in plugins/
#   ./scripts/sync-packwiz.sh --dry-run # Show what would happen without changes
#
# Reads every plugins/*.md file, extracts all listed mods,
# and runs packwiz curseforge/modrinth install for each one not already
# present in the packwiz mods/ directory.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
PLUGINS_DIR="$PROJECT_DIR/plugins"
MODPACK_DIR="$PROJECT_DIR/modpack"
MODS_DIR="$MODPACK_DIR/mods"

DRY_RUN=false
PRUNE=false

for arg in "$@"; do
    case "$arg" in
        --dry-run) DRY_RUN=true ;;
        --prune)   PRUNE=true ;;
        --help|-h)
            sed -n '2,11s/^# //p' "$0"
            exit 0
            ;;
        *)
            echo "Unknown flag: $arg (use --help)" >&2
            exit 1
            ;;
    esac
done

# ── Preflight checks ──────────────────────────────────────────────────

if ! command -v packwiz &>/dev/null; then
    echo "ERROR: packwiz not found in PATH."
    echo "Install: https://packwiz.infra.link/installation/"
    exit 1
fi

if [[ ! -f "$MODPACK_DIR/pack.toml" ]]; then
    echo "ERROR: pack.toml not found in $MODPACK_DIR"
    echo "Run 'packwiz init' first."
    exit 1
fi

# ── Parse plugins/*.md ────────────────────────────────────────────────
# Outputs lines: slug|curseforge_id|name
# curseforge_id is "N/A" for Modrinth-only mods.

parse_plugins() {
    local name="" slug="" cf_id=""

    for file in "$PLUGINS_DIR"/*.md; do
        [[ -f "$file" ]] || continue

        while IFS= read -r line; do
            # New mod heading
            if [[ "$line" =~ ^##\  ]]; then
                # Emit previous mod if it has a slug
                if [[ -n "$slug" ]]; then
                    echo "${slug}|${cf_id}|${name}"
                fi
                name="${line#\#\# }"
                slug="" cf_id=""
            fi

            # Field extraction
            if [[ "$line" == *"**Slug:**"* ]]; then
                slug="$(echo "$line" | sed 's/.*\*\*Slug:\*\* *//')"
            elif [[ "$line" == *"**CurseForge ID:**"* ]]; then
                cf_id="$(echo "$line" | sed 's/.*\*\*CurseForge ID:\*\* *//')"
            fi
        done < "$file"

        # Last mod in file
        if [[ -n "$slug" ]]; then
            echo "${slug}|${cf_id}|${name}"
        fi
        name="" slug="" cf_id=""
    done
}

# ── Build set of already-installed CurseForge project IDs and Modrinth mod IDs ──

declare -A installed_cf_ids   # cf_project_id -> pw.toml filename
declare -A installed_mr_ids   # modrinth_mod_id -> pw.toml filename
declare -A installed_files    # pw.toml basename (no ext) -> 1

index_installed() {
    [[ -d "$MODS_DIR" ]] || return 0

    for pw_file in "$MODS_DIR"/*.pw.toml; do
        [[ -f "$pw_file" ]] || continue
        local base
        base="$(basename "$pw_file" .pw.toml)"
        installed_files["$base"]=1

        # Extract CurseForge project-id
        local cf_pid
        cf_pid="$(grep -Po '(?<=^project-id = )\d+' "$pw_file" 2>/dev/null || true)"
        if [[ -n "$cf_pid" ]]; then
            installed_cf_ids["$cf_pid"]="$base"
        fi

        # Extract Modrinth mod-id
        local mr_mid
        mr_mid="$(grep -Po '(?<=^mod-id = ")[^"]+' "$pw_file" 2>/dev/null || true)"
        if [[ -n "$mr_mid" ]]; then
            installed_mr_ids["$mr_mid"]="$base"
        fi
    done
}

# ── Check if a mod is already installed ───────────────────────────────
# Matches by CurseForge project ID first, then falls back to slug-based
# filename matching.

is_installed() {
    local slug="$1" cf_id="$2"

    # Match by CurseForge project ID
    if [[ "$cf_id" != "N/A"* && -n "${installed_cf_ids[$cf_id]+x}" ]]; then
        return 0
    fi

    # Fallback: match by slug as filename
    if [[ -n "${installed_files[$slug]+x}" ]]; then
        return 0
    fi

    return 1
}

# ── Collect mods no longer in plugins (for --prune) ──────────────────

find_orphans() {
    local -n _wanted_ids=$1
    [[ -d "$MODS_DIR" ]] || return 0

    for pw_file in "$MODS_DIR"/*.pw.toml; do
        [[ -f "$pw_file" ]] || continue
        local base
        base="$(basename "$pw_file" .pw.toml)"

        # Check if this mod's CF project ID is in the wanted set
        local cf_pid
        cf_pid="$(grep -Po '(?<=^project-id = )\d+' "$pw_file" 2>/dev/null || true)"
        if [[ -n "$cf_pid" && -n "${_wanted_ids[$cf_pid]+x}" ]]; then
            continue
        fi

        # Check by slug match
        local found=false
        for wanted_slug in "${!_wanted_ids[@]}"; do
            if [[ "$wanted_slug" == "$base" ]]; then
                found=true
                break
            fi
        done
        $found && continue

        # This mod is an orphan (might be a dependency — packwiz installed it
        # automatically). Only flag it; let the user decide.
        echo "$base"
    done
}

# ── Main ──────────────────────────────────────────────────────────────

main() {
    echo "=== FizzleSMP Packwiz Sync ==="
    echo ""

    # Index already-installed mods
    index_installed
    echo "Packwiz mods installed: ${#installed_files[@]}"

    # Parse plugins
    declare -A wanted_cf_ids   # cf_id -> slug (for orphan detection)
    declare -A wanted_slugs    # slug -> 1

    local total=0 added=0 skipped=0 failed=0 removed=0

    # Collect all mods first
    declare -a mod_lines=()
    while IFS= read -r line; do
        mod_lines+=("$line")
    done < <(parse_plugins)

    total=${#mod_lines[@]}
    echo "Mods in plugins/: $total"
    echo ""

    # Process each mod
    for entry in "${mod_lines[@]}"; do
        IFS='|' read -r slug cf_id name <<< "$entry"

        # Track wanted mods for pruning
        if [[ "$cf_id" != "N/A"* ]]; then
            wanted_cf_ids["$cf_id"]="$slug"
        fi
        wanted_slugs["$slug"]=1

        # Skip if already installed
        if is_installed "$slug" "$cf_id"; then
            ((skipped++))
            continue
        fi

        # Determine source
        local source="curseforge"
        if [[ "$cf_id" == "N/A"* ]]; then
            source="modrinth"
        fi

        if $DRY_RUN; then
            echo "[DRY RUN] Would install: $name ($slug) via $source"
            ((added++))
            continue
        fi

        echo "▸ Installing: $name ($slug) via $source..."

        local install_ok=false
        if [[ "$source" == "modrinth" ]]; then
            if packwiz modrinth install "$slug" -y 2>&1 | sed 's/^/  /'; then
                install_ok=true
            fi
        else
            if packwiz curseforge install --addon-id "$cf_id" -y 2>&1 | sed 's/^/  /'; then
                install_ok=true
            else
                echo "  ↳ CurseForge failed, trying Modrinth fallback..."
                if packwiz modrinth install "$slug" -y 2>&1 | sed 's/^/  /'; then
                    install_ok=true
                fi
            fi
        fi

        if $install_ok; then
            ((added++))
            # Re-index so subsequent checks see the new mod
            index_installed
        else
            echo "  ✗ FAILED: $name ($slug)"
            ((failed++))
        fi
    done

    # ── Prune orphaned mods ───────────────────────────────────────────
    if $PRUNE; then
        echo ""
        echo "── Pruning orphaned mods ──"
        while IFS= read -r orphan; do
            [[ -n "$orphan" ]] || continue
            if $DRY_RUN; then
                echo "[DRY RUN] Would remove: $orphan"
            else
                echo "▸ Removing: $orphan"
                packwiz remove "$orphan" 2>&1 | sed 's/^/  /'
            fi
            ((removed++))
        done < <(find_orphans wanted_cf_ids)
    fi

    echo ""
    echo "=== Sync Complete ==="
    echo "Added: $added | Skipped: $skipped | Failed: $failed | Removed: $removed"
}

main
