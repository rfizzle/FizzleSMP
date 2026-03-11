#!/usr/bin/env bash
# build-pack.sh — Download mods and package a client or server pack as a ZIP
#
# Usage:
#   ./scripts/build-pack.sh server              # Build FizzleSMP.server.zip
#   ./scripts/build-pack.sh client              # Build FizzleSMP.client.zip
#   ./scripts/build-pack.sh server --dry-run    # Preview without downloading
#   ./scripts/build-pack.sh client --clean      # Wipe build dir, rebuild, and zip
#
# Reads .pw.toml metadata, filters by side, downloads into a temp build dir,
# and packages the result as a ZIP.

set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
MODPACK_DIR="$PROJECT_DIR/modpack"
BUILD_DIR="$PROJECT_DIR/build"
OUTPUT_DIR="$PROJECT_DIR"

CLEAN=false
DRY_RUN=false
TARGET=""

# --- Argument parsing ---

usage() {
    echo "Usage: $0 <server|client> [--clean] [--dry-run]"
    echo ""
    echo "  server     Build server pack (server + both mods)"
    echo "  client     Build client pack (client + both mods)"
    echo "  --clean    Wipe build directory before downloading"
    echo "  --dry-run  Preview changes without downloading or packaging"
}

for arg in "$@"; do
    case "$arg" in
        server|client) TARGET="$arg" ;;
        --clean)       CLEAN=true ;;
        --dry-run)     DRY_RUN=true ;;
        --help|-h)     usage; exit 0 ;;
        *)
            echo "Unknown option: $arg"
            usage
            exit 1
            ;;
    esac
done

if [[ -z "$TARGET" ]]; then
    echo "Error: must specify 'server' or 'client'"
    echo ""
    usage
    exit 1
fi

# Set the opposite side to exclude
if [[ "$TARGET" == "server" ]]; then
    EXCLUDE_SIDE="client"
else
    EXCLUDE_SIDE="server"
fi

PACK_BUILD_DIR="$BUILD_DIR/$TARGET"
MODS_DEST="$PACK_BUILD_DIR/mods"
DATAPACKS_DEST="$PACK_BUILD_DIR/config/paxi/datapacks"
SHADERPACKS_DEST="$PACK_BUILD_DIR/shaderpacks"
ZIP_NAME="FizzleSMP.${TARGET}.zip"
ZIP_PATH="$OUTPUT_DIR/$ZIP_NAME"

echo "=== FizzleSMP $TARGET pack builder ==="
echo ""

# --- Preflight checks ---

if [[ ! -d "$MODPACK_DIR" ]]; then
    echo "Error: modpack/ directory not found at $MODPACK_DIR"
    exit 1
fi

# --- Helpers ---

# Parse a pw.toml file and extract key fields
# Sets: PW_NAME, PW_FILENAME, PW_SIDE, PW_HASH_FORMAT, PW_HASH, PW_MODE, PW_URL
#       PW_CF_FILE_ID, PW_CF_PROJECT_ID, PW_MR_MOD_ID, PW_MR_VERSION
parse_pw_toml() {
    local file="$1"
    PW_NAME="" PW_FILENAME="" PW_SIDE="" PW_HASH_FORMAT="" PW_HASH="" PW_MODE="" PW_URL=""
    PW_CF_FILE_ID="" PW_CF_PROJECT_ID="" PW_MR_MOD_ID="" PW_MR_VERSION=""

    local section=""
    while IFS= read -r line; do
        if [[ "$line" =~ ^\[(.+)\]$ ]]; then
            section="${BASH_REMATCH[1]}"
            continue
        fi
        [[ -z "$line" || "$line" =~ ^# ]] && continue
        if [[ "$line" =~ ^([a-zA-Z_-]+)[[:space:]]*=[[:space:]]*(.+)$ ]]; then
            local key="${BASH_REMATCH[1]}"
            local val="${BASH_REMATCH[2]}"
            val="${val#\"}"
            val="${val%\"}"
            case "$section" in
                "")
                    case "$key" in
                        name) PW_NAME="$val" ;;
                        filename) PW_FILENAME="$val" ;;
                        side) PW_SIDE="$val" ;;
                    esac
                    ;;
                download)
                    case "$key" in
                        hash-format) PW_HASH_FORMAT="$val" ;;
                        hash) PW_HASH="$val" ;;
                        mode) PW_MODE="$val" ;;
                        url) PW_URL="$val" ;;
                    esac
                    ;;
                update.curseforge)
                    case "$key" in
                        file-id) PW_CF_FILE_ID="$val" ;;
                        project-id) PW_CF_PROJECT_ID="$val" ;;
                    esac
                    ;;
                update.modrinth)
                    case "$key" in
                        mod-id) PW_MR_MOD_ID="$val" ;;
                        version) PW_MR_VERSION="$val" ;;
                    esac
                    ;;
            esac
        fi
    done < "$file"
}

# Build CurseForge CDN URLs (multiple fallbacks)
# Returns newline-separated URLs to try in order
cf_cdn_urls() {
    local file_id="$1"
    local filename="$2"
    local project_id="$3"
    local prefix="${file_id:0:4}"
    local suffix="${file_id:4}"
    local encoded_filename
    encoded_filename=$(printf '%s' "$filename" | sed 's/+/%2B/g; s/ /%20/g')

    # Primary: edge CDN (302 → mediafilez)
    echo "https://edge.forgecdn.net/files/${prefix}/${suffix}/${encoded_filename}"
    # Fallback 1: direct mediafilez (no redirect)
    echo "https://mediafilez.forgecdn.net/files/${prefix}/${suffix}/${encoded_filename}"
    # Fallback 2: curseforge.com download API (307 → edge → mediafilez)
    if [[ -n "$project_id" ]]; then
        echo "https://www.curseforge.com/api/v1/mods/${project_id}/files/${file_id}/download"
    fi
}

verify_hash() {
    local file="$1"
    local format="$2"
    local expected="$3"

    local actual
    case "$format" in
        sha1)   actual=$(sha1sum "$file" | awk '{print $1}') ;;
        sha512) actual=$(sha512sum "$file" | awk '{print $1}') ;;
        *)      echo "  Warning: unknown hash format '$format', skipping verification"; return 0 ;;
    esac

    [[ "$actual" == "$expected" ]]
}

download_file() {
    local url="$1"
    local dest="$2"
    curl -sL -o "$dest" "$url"
}

# --- Clean (optional) ---

if [[ "$CLEAN" == true ]]; then
    if [[ "$DRY_RUN" == true ]]; then
        echo "[DRY RUN] Would clean $PACK_BUILD_DIR"
    else
        echo "Cleaning $PACK_BUILD_DIR..."
        rm -rf "$PACK_BUILD_DIR"
        echo "Clean complete."
    fi
fi

# --- Collect expected files from pw.toml metadata ---

declare -A EXPECTED_MODS
declare -A EXPECTED_DATAPACKS
declare -A EXPECTED_SHADERPACKS

skipped_side_count=0

for pw_file in "$MODPACK_DIR"/mods/*.pw.toml; do
    [[ -f "$pw_file" ]] || continue
    parse_pw_toml "$pw_file"

    if [[ "$PW_SIDE" == "$EXCLUDE_SIDE" ]]; then
        ((skipped_side_count++)) || true
        continue
    fi

    EXPECTED_MODS["$PW_FILENAME"]="$pw_file"
done

for pw_file in "$MODPACK_DIR"/config/paxi/datapacks/*.pw.toml; do
    [[ -f "$pw_file" ]] || continue
    parse_pw_toml "$pw_file"

    if [[ "$PW_SIDE" == "$EXCLUDE_SIDE" ]]; then
        ((skipped_side_count++)) || true
        continue
    fi

    EXPECTED_DATAPACKS["$PW_FILENAME"]="$pw_file"
done

# Shaderpacks
for pw_file in "$MODPACK_DIR"/shaderpacks/*.pw.toml; do
    [[ -f "$pw_file" ]] || continue
    parse_pw_toml "$pw_file"

    if [[ "$PW_SIDE" == "$EXCLUDE_SIDE" ]]; then
        ((skipped_side_count++)) || true
        continue
    fi

    EXPECTED_SHADERPACKS["$PW_FILENAME"]="$pw_file"
done

echo "Target:      $TARGET (excluding $EXCLUDE_SIDE-only)"
echo "Mods:        ${#EXPECTED_MODS[@]}"
echo "Datapacks:   ${#EXPECTED_DATAPACKS[@]}"
echo "Shaderpacks: ${#EXPECTED_SHADERPACKS[@]}"
echo "Skipped:     $skipped_side_count ($EXCLUDE_SIDE-only)"
echo ""

# --- Ensure destination dirs exist ---

if [[ "$DRY_RUN" != true ]]; then
    mkdir -p "$MODS_DEST" "$DATAPACKS_DEST" "$SHADERPACKS_DEST"
fi

# --- Copy config files ---

CONFIG_SRC="$MODPACK_DIR/config"
CONFIG_DEST="$PACK_BUILD_DIR/config"

config_count=0

if [[ -d "$CONFIG_SRC" ]]; then
    echo "=== Copying config files ==="

    # Find all non-pw.toml files in modpack/config/
    while IFS= read -r src_file; do
        # Get relative path from config source
        rel_path="${src_file#$CONFIG_SRC/}"
        dest_file="$CONFIG_DEST/$rel_path"
        dest_dir="$(dirname "$dest_file")"

        if [[ "$DRY_RUN" == true ]]; then
            echo "  [DRY RUN] Would copy: config/$rel_path"
        else
            mkdir -p "$dest_dir"
            cp -f "$src_file" "$dest_file"
        fi
        ((config_count++)) || true
    done < <(find "$CONFIG_SRC" -type f ! -name "*.pw.toml")

    echo "  Copied $config_count config file(s)."
    echo ""
fi

# --- Copy options.txt (client only) ---

OPTIONS_SRC="$MODPACK_DIR/options.txt"

if [[ "$TARGET" == "client" && -f "$OPTIONS_SRC" ]]; then
    if [[ "$DRY_RUN" == true ]]; then
        echo "=== Copying options.txt ==="
        echo "  [DRY RUN] Would copy: options.txt"
        echo ""
    else
        echo "=== Copying options.txt ==="
        cp -f "$OPTIONS_SRC" "$PACK_BUILD_DIR/options.txt"
        echo "  Copied options.txt."
        echo ""
    fi
fi

# --- Download missing/changed files ---

download_count=0
skip_count=0
fail_count=0
FAILED_MODS=()

process_files() {
    local -n file_map=$1
    local dest_dir="$2"
    local label="$3"

    for filename in "${!file_map[@]}"; do
        local pw_file="${file_map[$filename]}"
        local dest_path="$dest_dir/$filename"

        parse_pw_toml "$pw_file"

        if [[ -f "$dest_path" ]] && verify_hash "$dest_path" "$PW_HASH_FORMAT" "$PW_HASH"; then
            ((skip_count++)) || true
            continue
        elif [[ -f "$dest_path" ]]; then
            echo "  Hash mismatch: $filename (re-downloading)"
        fi

        # Determine download URLs
        local -a urls=()
        if [[ -n "$PW_URL" ]]; then
            urls+=("$PW_URL")
        elif [[ "$PW_MODE" == "metadata:curseforge" && -n "$PW_CF_FILE_ID" ]]; then
            while IFS= read -r u; do
                urls+=("$u")
            done < <(cf_cdn_urls "$PW_CF_FILE_ID" "$PW_FILENAME" "$PW_CF_PROJECT_ID")
        else
            echo "  ERROR: No download source for $PW_NAME ($filename)"
            FAILED_MODS+=("$PW_NAME ($filename) — no download URL found")
            ((fail_count++)) || true
            continue
        fi

        if [[ "$DRY_RUN" == true ]]; then
            echo "  [DRY RUN] Would download: $filename"
            ((download_count++)) || true
            continue
        fi

        printf "  Downloading: %-60s" "$filename"

        local success=false
        for url in "${urls[@]}"; do
            if download_file "$url" "$dest_path"; then
                if verify_hash "$dest_path" "$PW_HASH_FORMAT" "$PW_HASH"; then
                    success=true
                    break
                else
                    rm -f "$dest_path"
                fi
            else
                rm -f "$dest_path"
            fi
            # Brief pause before trying next CDN
            sleep 0.5
        done

        if [[ "$success" == true ]]; then
            echo " OK"
            ((download_count++)) || true
        else
            echo " FAILED"
            FAILED_MODS+=("$PW_NAME ($filename) — tried ${#urls[@]} CDN(s)")
            ((fail_count++)) || true
            rm -f "$dest_path"
        fi
    done
}

echo "=== Downloading mods ==="
process_files EXPECTED_MODS "$MODS_DEST" "mod"
echo ""
echo "=== Downloading datapacks ==="
process_files EXPECTED_DATAPACKS "$DATAPACKS_DEST" "datapack"
echo ""
echo "=== Downloading shaderpacks ==="
process_files EXPECTED_SHADERPACKS "$SHADERPACKS_DEST" "shaderpack"
echo ""

# --- Remove stale files ---

remove_count=0

remove_stale() {
    local dest_dir="$1"
    local -n file_map=$2

    [[ -d "$dest_dir" ]] || return 0

    for file in "$dest_dir"/*; do
        [[ -f "$file" ]] || continue
        local basename
        basename=$(basename "$file")

        if [[ -z "${file_map[$basename]+_}" ]]; then
            if [[ "$DRY_RUN" == true ]]; then
                echo "  [DRY RUN] Would remove: $basename"
            else
                echo "  Removing: $basename"
                rm -f "$file"
            fi
            ((remove_count++)) || true
        fi
    done
}

echo "=== Cleaning stale files ==="
remove_stale "$MODS_DEST" EXPECTED_MODS
remove_stale "$DATAPACKS_DEST" EXPECTED_DATAPACKS
remove_stale "$SHADERPACKS_DEST" EXPECTED_SHADERPACKS
if [[ $remove_count -eq 0 ]]; then
    echo "  No stale files found."
fi
echo ""

# --- Package as ZIP ---

if [[ "$DRY_RUN" != true && $fail_count -eq 0 ]]; then
    echo "=== Packaging $ZIP_NAME ==="

    # Remove old zip if it exists
    rm -f "$ZIP_PATH"

    # Create zip from the build directory
    (cd "$PACK_BUILD_DIR" && zip -qr "$ZIP_PATH" .)

    zip_size=$(du -h "$ZIP_PATH" | awk '{print $1}')
    echo "  Created: $ZIP_NAME ($zip_size)"
    echo ""
elif [[ "$DRY_RUN" == true ]]; then
    echo "[DRY RUN] Would package as $ZIP_NAME"
    echo ""
else
    echo "Skipping ZIP — there were download failures."
    echo ""
fi

# --- Summary ---

echo "=========================================="
echo " $TARGET pack summary"
echo "=========================================="
echo "  Downloaded: $download_count"
echo "  Skipped (cached): $skip_count"
echo "  Removed (stale): $remove_count"
echo "  Failed: $fail_count"

if [[ ${#FAILED_MODS[@]} -gt 0 ]]; then
    echo ""
    echo "  Failed downloads:"
    for entry in "${FAILED_MODS[@]}"; do
        echo "    - $entry"
    done
fi

echo ""
if [[ $fail_count -gt 0 ]]; then
    echo "Finished with $fail_count error(s)."
    exit 1
else
    echo "Done."
fi
