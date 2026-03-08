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

set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
PLUGINS_DIR="$PROJECT_DIR/plugins"
MODPACK_DIR="$PROJECT_DIR/modpack"
MODS_DIR="$MODPACK_DIR/mods"
DATAPACKS_DIR="$MODPACK_DIR/config/paxi/datapacks"
SHADERPACKS_DIR="$MODPACK_DIR/shaderpacks"

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
# Outputs lines: slug|curseforge_id|modrinth_slug|name|pin_cf_file_id|mod_loader
# curseforge_id is "N/A" for Modrinth-only mods.
# modrinth_slug is "N/A" or empty if not on Modrinth.
# pin_cf_file_id is empty if no pin is set.
# mod_loader is "Fabric", "Iris", "Datapack", etc.

parse_plugins() {
    local name="" slug="" cf_id="" mr_slug="" pin_cf_file_id="" mod_loader=""

    for file in "$PLUGINS_DIR"/*.md; do
        [[ -f "$file" ]] || continue

        while IFS= read -r line; do
            # New mod heading
            if [[ "$line" =~ ^##\  ]]; then
                # Emit previous mod if it has a slug
                if [[ -n "$slug" ]]; then
                    echo "${slug}|${cf_id}|${mr_slug}|${name}|${pin_cf_file_id}|${mod_loader}"
                fi
                name="${line#\#\# }"
                slug="" cf_id="" mr_slug="" pin_cf_file_id="" mod_loader=""
            fi

            # Field extraction
            if [[ "$line" == *"**Slug:**"* ]]; then
                slug="$(echo "$line" | sed 's/.*\*\*Slug:\*\* *//')"
            elif [[ "$line" == *"**CurseForge ID:**"* ]]; then
                cf_id="$(echo "$line" | sed 's/.*\*\*CurseForge ID:\*\* *//')"
            elif [[ "$line" == *"**Modrinth Slug:**"* ]]; then
                mr_slug="$(echo "$line" | sed 's/.*\*\*Modrinth Slug:\*\* *//')"
            elif [[ "$line" == *"**Pin CurseForge File ID:**"* ]]; then
                pin_cf_file_id="$(echo "$line" | sed 's/.*\*\*Pin CurseForge File ID:\*\* *//')"
            elif [[ "$line" == *"**Mod Loader:**"* ]]; then
                mod_loader="$(echo "$line" | sed 's/.*\*\*Mod Loader:\*\* *//')"
            fi
        done < "$file"

        # Last mod in file
        if [[ -n "$slug" ]]; then
            echo "${slug}|${cf_id}|${mr_slug}|${name}|${pin_cf_file_id}|${mod_loader}"
        fi
        name="" slug="" cf_id="" mr_slug="" pin_cf_file_id="" mod_loader=""
    done
}

# ── Build set of already-installed CurseForge project IDs and Modrinth mod IDs ──

declare -A installed_cf_ids   # cf_project_id -> pw.toml filename
declare -A installed_mr_ids   # modrinth_mod_id -> pw.toml filename
declare -A installed_files    # pw.toml basename (no ext) -> 1

index_installed() {
    local dir
    for dir in "$MODS_DIR" "$DATAPACKS_DIR" "$SHADERPACKS_DIR"; do
        [[ -d "$dir" ]] || continue

        for pw_file in "$dir"/*.pw.toml; do
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
    done
}

# ── Check if a mod is already installed ───────────────────────────────
# Matches by CurseForge project ID, Modrinth slug, or CF slug filename.

is_installed() {
    local slug="$1" cf_id="$2" mr_slug="${3:-}"

    # Match by CurseForge project ID
    if [[ "$cf_id" != "N/A"* && -n "${installed_cf_ids[$cf_id]+x}" ]]; then
        return 0
    fi

    # Match by CurseForge slug as filename
    if [[ -n "${installed_files[$slug]+x}" ]]; then
        return 0
    fi

    # Match by Modrinth slug as filename (pw.toml may use Modrinth slug)
    if [[ -n "$mr_slug" && "$mr_slug" != "N/A" && -n "${installed_files[$mr_slug]+x}" ]]; then
        return 0
    fi

    return 1
}

# ── Collect mods no longer in plugins (for --prune) ──────────────────

find_orphans() {
    local -n _wanted_cf_ids=$1
    local -n _wanted_slugs=$2
    local -n _wanted_mr_slugs=$3

    local dir
    for dir in "$MODS_DIR" "$SHADERPACKS_DIR"; do
        [[ -d "$dir" ]] || continue

        for pw_file in "$dir"/*.pw.toml; do
            [[ -f "$pw_file" ]] || continue
            local base
            base="$(basename "$pw_file" .pw.toml)"

            # 1. Match by CurseForge project ID
            local cf_pid
            cf_pid="$(grep -Po '(?<=^project-id = )\d+' "$pw_file" 2>/dev/null || true)"
            if [[ -n "$cf_pid" && -n "${_wanted_cf_ids[$cf_pid]+x}" ]]; then
                continue
            fi

            # 2. Match by pw.toml filename against CF slugs
            if [[ -n "${_wanted_slugs[$base]+x}" ]]; then
                continue
            fi

            # 3. Match by pw.toml filename against Modrinth slugs
            if [[ -n "${_wanted_mr_slugs[$base]+x}" ]]; then
                continue
            fi

            # This mod is an orphan (might be a dependency — packwiz installed it
            # automatically). Only flag it; let the user decide.
            echo "$base"
        done
    done
}

# ── Run packwiz from the modpack directory ────────────────────────────
# packwiz expects to be run from the directory containing pack.toml.

run_packwiz() {
    (cd "$MODPACK_DIR" && packwiz "$@")
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
    declare -a added_names=() failed_names=() removed_names=() prune_failed_names=()
    declare -a failed_errors=()
    declare -A wanted_mr_slugs  # modrinth_slug -> 1

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
        IFS='|' read -r slug cf_id mr_slug name pin_cf_file_id mod_loader <<< "$entry"

        # Track wanted mods for pruning
        if [[ "$cf_id" != "N/A"* ]]; then
            wanted_cf_ids["$cf_id"]="$slug"
        fi
        wanted_slugs["$slug"]=1
        if [[ -n "$mr_slug" && "$mr_slug" != "N/A" ]]; then
            wanted_mr_slugs["$mr_slug"]=1
        fi

        # Skip if already installed
        if is_installed "$slug" "$cf_id" "$mr_slug"; then
            (( skipped++ )) || true
            continue
        fi

        # Determine source
        local source="curseforge"
        if [[ "$cf_id" == "N/A"* ]]; then
            source="modrinth"
        elif [[ "$mod_loader" == "Iris" ]]; then
            # Shader packs must be installed via Modrinth (project_type: shader)
            source="modrinth"
        fi

        if $DRY_RUN; then
            echo "[DRY RUN] Would install: $name ($slug) via $source"
            added_names+=("$name ($slug)")
            (( added++ )) || true
            continue
        fi

        echo "▸ Installing: $name ($slug) via $source..."

        local install_ok=false
        local err_output=""
        if [[ "$source" == "modrinth" ]]; then
            # Modrinth-only mod: prefer Modrinth slug field, fall back to CF slug
            local install_slug="${mr_slug:-$slug}"
            [[ "$install_slug" == "N/A" ]] && install_slug="$slug"
            err_output="$(run_packwiz modrinth install "$install_slug" -y 2>&1)" && install_ok=true
            echo "$err_output" | sed 's/^/  /'
        else
            local cf_install_args=(curseforge install --addon-id "$cf_id")
            if [[ -n "$pin_cf_file_id" ]]; then
                cf_install_args+=(--file-id "$pin_cf_file_id")
                echo "  ⊳ Pinned to CurseForge file ID: $pin_cf_file_id"
            fi
            cf_install_args+=(-y)
            err_output="$(run_packwiz "${cf_install_args[@]}" 2>&1)" && install_ok=true
            echo "$err_output" | sed 's/^/  /'
            if ! $install_ok; then
                # CurseForge failed — try Modrinth fallback
                local fallback_slug=""
                # Use the Modrinth Slug field from the plugin file if available
                if [[ -n "$mr_slug" && "$mr_slug" != "N/A" ]]; then
                    fallback_slug="$mr_slug"
                else
                    # No Modrinth slug in plugin file — look it up via API
                    fallback_slug="$(curl -s "https://api.modrinth.com/v2/search?query=$(printf '%s' "$name" | jq -sRr @uri)&facets=%5B%5B%22categories:fabric%22%5D,%5B%22versions:1.21.1%22%5D,%5B%22project_type:mod%22%5D%5D" \
                        | jq -r '.hits[0].slug // empty' 2>/dev/null || true)"
                fi
                if [[ -n "$fallback_slug" ]]; then
                    echo "  ↳ CurseForge failed, trying Modrinth (slug: $fallback_slug)..."
                    err_output="$(run_packwiz modrinth install "$fallback_slug" -y 2>&1)" && install_ok=true
                    echo "$err_output" | sed 's/^/  /'
                else
                    echo "  ↳ CurseForge failed, no matching mod found on Modrinth."
                fi
            fi
        fi

        if $install_ok; then
            (( added++ )) || true
            added_names+=("$name ($slug)")
            # Re-index so subsequent checks see the new mod
            index_installed
        else
            echo "  ✗ FAILED: $name ($slug)"
            (( failed++ )) || true
            failed_names+=("$name ($slug)")
            # Capture last line of error output as reason
            local reason
            reason="$(echo "$err_output" | tail -1)"
            failed_errors+=("${reason:-unknown error}")
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
                removed_names+=("$orphan")
            else
                echo "▸ Removing: $orphan"
                if run_packwiz remove "$orphan" 2>&1 | sed 's/^/  /'; then
                    removed_names+=("$orphan")
                else
                    echo "  ✗ Failed to remove: $orphan"
                    prune_failed_names+=("$orphan")
                fi
            fi
            (( removed++ )) || true
        done < <(find_orphans wanted_cf_ids wanted_slugs wanted_mr_slugs)
    fi

    # ── Summary Report ─────────────────────────────────────────────────
    echo ""
    echo "╔══════════════════════════════════════════════════════════════╗"
    echo "║                    Sync Summary Report                     ║"
    echo "╠══════════════════════════════════════════════════════════════╣"
    printf "║  %-20s %5d                                 ║\n" "Total in plugins/:" "$total"
    printf "║  %-20s %5d                                 ║\n" "Already installed:" "$skipped"
    printf "║  %-20s %5d                                 ║\n" "Newly added:" "$added"
    printf "║  %-20s %5d                                 ║\n" "Failed:" "$failed"
    printf "║  %-20s %5d                                 ║\n" "Removed:" "$removed"
    echo "╚══════════════════════════════════════════════════════════════╝"

    if (( ${#added_names[@]} > 0 )); then
        echo ""
        echo "── Added ──────────────────────────────────────────────────────"
        for mod in "${added_names[@]}"; do
            echo "  ✓ $mod"
        done
    fi

    if (( ${#failed_names[@]} > 0 )); then
        echo ""
        echo "── Failed ─────────────────────────────────────────────────────"
        for i in "${!failed_names[@]}"; do
            echo "  ✗ ${failed_names[$i]}"
            echo "    └─ ${failed_errors[$i]}"
        done
    fi

    if (( ${#removed_names[@]} > 0 )); then
        echo ""
        echo "── Removed ────────────────────────────────────────────────────"
        for mod in "${removed_names[@]}"; do
            echo "  - $mod"
        done
    fi

    if (( ${#prune_failed_names[@]} > 0 )); then
        echo ""
        echo "── Failed to Remove ───────────────────────────────────────────"
        for mod in "${prune_failed_names[@]}"; do
            echo "  ✗ $mod"
        done
    fi

    echo ""
    if (( failed > 0 || ${#prune_failed_names[@]} > 0 )); then
        echo "Sync completed with errors. Review failures above."
        return 1
    else
        echo "Sync completed successfully."
    fi
}

main
