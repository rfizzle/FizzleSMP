#!/usr/bin/env bash
# install-server.sh — Download server-side mods from packwiz metadata into Minecraft/
#
# Usage:
#   ./scripts/install-server.sh           # Install/update server mods
#   ./scripts/install-server.sh --clean   # Wipe Minecraft/ (except bootstrap jar) then install
#   ./scripts/install-server.sh --dry-run # Preview what would be downloaded/removed
#
# Reads all .pw.toml files in modpack/mods/ and modpack/config/paxi/datapacks/,
# filters out client-only mods, downloads missing files, and removes stale ones.

set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
MODPACK_DIR="$PROJECT_DIR/modpack"
MINECRAFT_DIR="$PROJECT_DIR/Minecraft"
MODS_DEST="$MINECRAFT_DIR/mods"
DATAPACKS_DEST="$MINECRAFT_DIR/config/paxi/datapacks"
BOOTSTRAP_JAR="packwiz-installer-bootstrap.jar"

CLEAN=false
DRY_RUN=false
SIDE_FILTER="server"  # install mods with side = "server" or "both"

for arg in "$@"; do
    case "$arg" in
        --clean)   CLEAN=true ;;
        --dry-run) DRY_RUN=true ;;
        --help|-h)
            echo "Usage: $0 [--clean] [--dry-run]"
            echo ""
            echo "  --clean    Wipe Minecraft/mods/ and datapacks before installing"
            echo "  --dry-run  Preview changes without downloading or removing anything"
            exit 0
            ;;
        *)
            echo "Unknown option: $arg"
            exit 1
            ;;
    esac
done

# --- Preflight checks ---

if [[ ! -d "$MODPACK_DIR" ]]; then
    echo "Error: modpack/ directory not found at $MODPACK_DIR"
    exit 1
fi

if [[ ! -d "$MINECRAFT_DIR" ]]; then
    echo "Error: Minecraft/ directory not found at $MINECRAFT_DIR"
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
        # Track TOML sections
        if [[ "$line" =~ ^\[(.+)\]$ ]]; then
            section="${BASH_REMATCH[1]}"
            continue
        fi
        # Skip empty lines and comments
        [[ -z "$line" || "$line" =~ ^# ]] && continue
        # Parse key = value (strip quotes)
        if [[ "$line" =~ ^([a-zA-Z_-]+)[[:space:]]*=[[:space:]]*(.+)$ ]]; then
            local key="${BASH_REMATCH[1]}"
            local val="${BASH_REMATCH[2]}"
            # Strip surrounding quotes
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

# Build a CurseForge edge CDN URL from file-id and filename
# Format: https://edge.forgecdn.net/files/{first 4 digits}/{remaining digits}/{filename}
cf_cdn_url() {
    local file_id="$1"
    local filename="$2"
    local prefix="${file_id:0:4}"
    local suffix="${file_id:4}"
    # URL-encode the filename (mainly + signs)
    local encoded_filename
    encoded_filename=$(printf '%s' "$filename" | sed 's/+/%2B/g; s/ /%20/g')
    echo "https://edge.forgecdn.net/files/${prefix}/${suffix}/${encoded_filename}"
}

# Verify file hash
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

# Download a file with curl, following redirects
download_file() {
    local url="$1"
    local dest="$2"
    curl -sL -o "$dest" "$url"
}

# --- Clean (optional) ---

if [[ "$CLEAN" == true ]]; then
    if [[ "$DRY_RUN" == true ]]; then
        echo "[DRY RUN] Would clean Minecraft/mods/ and datapacks/"
    else
        echo "Cleaning Minecraft/mods/ and datapacks/..."
        rm -rf "$MODS_DEST" "$DATAPACKS_DEST"
        echo "Clean complete."
    fi
fi

# --- Collect expected files from pw.toml metadata ---

declare -A EXPECTED_MODS      # filename -> pw.toml path
declare -A EXPECTED_DATAPACKS  # filename -> pw.toml path

# Process mods
for pw_file in "$MODPACK_DIR"/mods/*.pw.toml; do
    [[ -f "$pw_file" ]] || continue
    parse_pw_toml "$pw_file"

    # Skip client-only mods for server install
    if [[ "$PW_SIDE" == "client" ]]; then
        continue
    fi

    EXPECTED_MODS["$PW_FILENAME"]="$pw_file"
done

# Process datapacks
for pw_file in "$MODPACK_DIR"/config/paxi/datapacks/*.pw.toml; do
    [[ -f "$pw_file" ]] || continue
    parse_pw_toml "$pw_file"

    if [[ "$PW_SIDE" == "client" ]]; then
        continue
    fi

    EXPECTED_DATAPACKS["$PW_FILENAME"]="$pw_file"
done

echo "Found ${#EXPECTED_MODS[@]} mods and ${#EXPECTED_DATAPACKS[@]} datapacks to install."
echo ""

# --- Ensure destination dirs exist ---

if [[ "$DRY_RUN" != true ]]; then
    mkdir -p "$MODS_DEST" "$DATAPACKS_DEST"
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

        # Check if file already exists with correct hash
        if [[ -f "$dest_path" ]] && verify_hash "$dest_path" "$PW_HASH_FORMAT" "$PW_HASH"; then
            ((skip_count++)) || true
            continue
        elif [[ -f "$dest_path" ]]; then
            echo "  Hash mismatch: $filename (re-downloading)"
        fi

        # Determine download URL
        local url=""
        if [[ -n "$PW_URL" ]]; then
            # Direct URL (Modrinth mods, datapacks)
            url="$PW_URL"
        elif [[ "$PW_MODE" == "metadata:curseforge" && -n "$PW_CF_FILE_ID" ]]; then
            # Build CurseForge edge CDN URL
            url=$(cf_cdn_url "$PW_CF_FILE_ID" "$PW_FILENAME")
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

        # Try up to 3 times
        local success=false
        for try in 1 2 3; do
            if download_file "$url" "$dest_path"; then
                if verify_hash "$dest_path" "$PW_HASH_FORMAT" "$PW_HASH"; then
                    success=true
                    break
                else
                    echo -n " (hash mismatch, retry $try)"
                    rm -f "$dest_path"
                fi
            else
                echo -n " (download failed, retry $try)"
                rm -f "$dest_path"
            fi
            sleep 1
        done

        if [[ "$success" == true ]]; then
            echo " OK"
            ((download_count++)) || true
        else
            echo " FAILED"
            FAILED_MODS+=("$PW_NAME ($filename) — $url")
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

# --- Remove stale files ---

remove_count=0

remove_stale() {
    local dest_dir="$1"
    local -n file_map=$2
    local label="$3"

    [[ -d "$dest_dir" ]] || return 0

    for file in "$dest_dir"/*; do
        [[ -f "$file" ]] || continue
        local basename
        basename=$(basename "$file")

        # Skip the bootstrap jar
        [[ "$basename" == "$BOOTSTRAP_JAR" ]] && continue
        # Skip packwiz-installer.jar
        [[ "$basename" == "packwiz-installer.jar" ]] && continue

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
remove_stale "$MODS_DEST" EXPECTED_MODS "mod"
remove_stale "$DATAPACKS_DEST" EXPECTED_DATAPACKS "datapack"
if [[ $remove_count -eq 0 ]]; then
    echo "  No stale files found."
fi
echo ""

# --- Summary ---

echo "=========================================="
echo " Summary"
echo "=========================================="
echo "  Downloaded: $download_count"
echo "  Skipped (already up-to-date): $skip_count"
echo "  Removed (stale): $remove_count"
echo "  Failed: $fail_count"

if [[ ${#FAILED_MODS[@]} -gt 0 ]]; then
    echo ""
    echo "  The following mods failed to download:"
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
