#!/usr/bin/env bash
set -euo pipefail

usage() {
  echo "Usage: $0 <old-name> <new-name> [--regroup <old-group> <new-group>]"
  echo "Example: $0 old-mod-name new-mod-name --regroup com.oldgroup com.newgroup"
  exit 1
}

if [[ $# -lt 2 ]]; then
  usage
fi

OLD_HYPHEN="$1"
NEW_HYPHEN="$2"
OLD_UNDER="${OLD_HYPHEN//-/_}"
NEW_UNDER="${NEW_HYPHEN//-/_}"

OLD_GROUP=""
NEW_GROUP=""
if [[ $# -ge 4 && "$3" == "--regroup" ]]; then
  OLD_GROUP="$4"
  NEW_GROUP="${5:-}"
  if [[ -z "$NEW_GROUP" ]]; then usage; fi
fi

to_camel() {
  local IFS='-_'
  local parts=($1)
  local result=""
  for part in "${parts[@]}"; do
    result+="$(tr '[:lower:]' '[:upper:]' <<< "${part:0:1}")${part:1}"
  done
  echo "$result"
}

OLD_CAMEL="$(to_camel "$OLD_HYPHEN")"
NEW_CAMEL="$(to_camel "$NEW_HYPHEN")"

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MOD_DIR="$REPO_ROOT/companions/$OLD_HYPHEN"

if [[ ! -d "$MOD_DIR" ]]; then
  echo "Error: $MOD_DIR does not exist"
  exit 1
fi

echo "Renaming companion mod:"
echo "  Hyphenated: $OLD_HYPHEN -> $NEW_HYPHEN"
echo "  Underscored: $OLD_UNDER -> $NEW_UNDER"
echo "  CamelCase:  $OLD_CAMEL -> $NEW_CAMEL"
if [[ -n "$OLD_GROUP" ]]; then
  echo "  Group:      $OLD_GROUP -> $NEW_GROUP"
fi
echo ""

# Step 1 — Rename resource files (before directory moves)
echo "Step 1: Renaming resource files..."

MIXIN_JSON="$MOD_DIR/src/main/resources/${OLD_UNDER}.mixins.json"
if [[ -f "$MIXIN_JSON" ]]; then
  git mv "$MIXIN_JSON" "$MOD_DIR/src/main/resources/${NEW_UNDER}.mixins.json"
  echo "  Renamed mixin config"
fi

AW_FILE="$MOD_DIR/src/main/resources/${OLD_UNDER}.accesswidener"
if [[ -f "$AW_FILE" ]]; then
  git mv "$AW_FILE" "$MOD_DIR/src/main/resources/${NEW_UNDER}.accesswidener"
  echo "  Renamed access widener"
fi

# Step 2 — Rename Java package directories (all sourcesets)
echo "Step 2: Renaming Java package directories..."

OLD_GROUP_DIR="${OLD_GROUP//.//}"
NEW_GROUP_DIR="${NEW_GROUP//.//}"

for srcset in main client test gametest; do
  OLD_PKG="$MOD_DIR/src/$srcset/java/${OLD_GROUP_DIR:-com/rfizzle}/$OLD_UNDER"
  NEW_PKG="$MOD_DIR/src/$srcset/java/${OLD_GROUP_DIR:-com/rfizzle}/$NEW_UNDER"
  if [[ -d "$OLD_PKG" ]]; then
    git mv "$OLD_PKG" "$NEW_PKG"
    echo "  Renamed $srcset package"
  fi
done

# Step 2b — Rename group directory if --regroup
if [[ -n "$OLD_GROUP" ]]; then
  echo "Step 2b: Renaming group package directories..."
  for srcset in main client test gametest; do
    OLD_GDIR="$MOD_DIR/src/$srcset/java/$OLD_GROUP_DIR"
    NEW_GDIR="$MOD_DIR/src/$srcset/java/$NEW_GROUP_DIR"
    if [[ -d "$OLD_GDIR" ]]; then
      mkdir -p "$(dirname "$NEW_GDIR")"
      git mv "$OLD_GDIR" "$NEW_GDIR"
      echo "  Renamed $srcset group: $OLD_GROUP_DIR -> $NEW_GROUP_DIR"
    fi
  done
fi

# Step 3 — Rename resource namespace directories
echo "Step 3: Renaming resource namespace directories..."

for base in src/main/resources src/main/generated; do
  for ns in data assets; do
    OLD_DIR="$MOD_DIR/$base/$ns/$OLD_UNDER"
    NEW_DIR="$MOD_DIR/$base/$ns/$NEW_UNDER"
    if [[ -d "$OLD_DIR" ]]; then
      git mv "$OLD_DIR" "$NEW_DIR"
      echo "  Renamed $base/$ns/$OLD_UNDER"
    fi
  done
done

# Step 4 — Rename Java class files with the old prefix
echo "Step 4: Renaming Java class files..."

find "$MOD_DIR/src" -name "${OLD_CAMEL}*.java" | while read -r f; do
  dir="$(dirname "$f")"
  old_basename="$(basename "$f")"
  new_basename="${old_basename/$OLD_CAMEL/$NEW_CAMEL}"
  git mv "$f" "$dir/$new_basename"
  echo "  Renamed $old_basename -> $new_basename"
done

# Step 5 — Bulk find-and-replace in file contents
echo "Step 5: Bulk find-and-replace in file contents..."

# Fully-qualified package references (longest pattern first)
OLD_FQ="${OLD_GROUP:-com.rfizzle}.${OLD_UNDER}"
NEW_FQ="${NEW_GROUP:-com.rfizzle}.${NEW_UNDER}"
find "$MOD_DIR" -type f \( -name '*.java' -o -name '*.json' -o -name '*.gradle' -o -name '*.properties' -o -name '*.toml' -o -name '*.md' \) \
  -exec sed -i "s/${OLD_FQ//./\\.}/${NEW_FQ}/g" {} +
echo "  Replaced fully-qualified package references"

# Group rename in file contents (dotted and slash forms)
if [[ -n "$OLD_GROUP" ]]; then
  find "$MOD_DIR" -type f \( -name '*.java' -o -name '*.json' -o -name '*.gradle' -o -name '*.properties' -o -name '*.toml' -o -name '*.md' \) \
    -exec sed -i "s/${OLD_GROUP//./\\.}/${NEW_GROUP}/g" {} +
  find "$MOD_DIR" -type f \( -name '*.java' -o -name '*.json' -o -name '*.gradle' -o -name '*.properties' -o -name '*.toml' \) \
    -exec sed -i "s|${OLD_GROUP_DIR}|${NEW_GROUP_DIR}|g" {} +
  echo "  Replaced group references"
fi

# CamelCase class prefix (Java, JSON, and Markdown)
find "$MOD_DIR" -type f \( -name '*.java' -o -name '*.json' -o -name '*.md' \) \
  -exec sed -i "s/${OLD_CAMEL}/${NEW_CAMEL}/g" {} +
echo "  Replaced CamelCase references"

# Underscored mod ID in all file contents
find "$MOD_DIR" -type f \( -name '*.java' -o -name '*.json' -o -name '*.gradle' -o -name '*.properties' -o -name '*.md' \) \
  -exec sed -i "s/${OLD_UNDER}/${NEW_UNDER}/g" {} +
echo "  Replaced underscored references"

# Hyphenated project name
find "$MOD_DIR" -type f \( -name '*.gradle' -o -name '*.properties' -o -name '*.md' -o -name '*.toml' \) \
  -exec sed -i "s/${OLD_HYPHEN}/${NEW_HYPHEN}/g" {} +
echo "  Replaced hyphenated references"

# Step 6 — Rename the top-level directory (last)
echo "Step 6: Renaming top-level directory..."
git mv "$MOD_DIR" "$REPO_ROOT/companions/$NEW_HYPHEN"
echo "  Renamed companions/$OLD_HYPHEN -> companions/$NEW_HYPHEN"

# Step 7 — Update external references
echo "Step 7: Updating external references..."

CI_WORKFLOW="$REPO_ROOT/.github/workflows/companion-tests.yml"
if [[ -f "$CI_WORKFLOW" ]]; then
  sed -i "s/${OLD_HYPHEN}/${NEW_HYPHEN}/g" "$CI_WORKFLOW"
  echo "  Updated CI workflow"
fi

find "$REPO_ROOT/.claude/" -type f -name '*.md' \
  -exec sed -i "s/${OLD_HYPHEN}/${NEW_HYPHEN}/g" {} +
find "$REPO_ROOT/.claude/" -type f -name '*.md' \
  -exec sed -i "s/${OLD_UNDER}/${NEW_UNDER}/g" {} +
find "$REPO_ROOT/.claude/" -type f -name '*.md' \
  -exec sed -i "s/${OLD_CAMEL}/${NEW_CAMEL}/g" {} +
if [[ -n "$OLD_GROUP" ]]; then
  find "$REPO_ROOT/.claude/" -type f -name '*.md' \
    -exec sed -i "s/${OLD_GROUP//./\\.}/${NEW_GROUP}/g" {} +
fi
echo "  Updated .claude/ files"

README="$REPO_ROOT/companions/README.md"
if [[ -f "$README" ]]; then
  sed -i "s/${OLD_HYPHEN}/${NEW_HYPHEN}/g" "$README"
  if [[ -n "$OLD_GROUP" ]]; then
    sed -i "s/${OLD_GROUP//./\\.}/${NEW_GROUP}/g" "$README"
  fi
  echo "  Updated companions/README.md"
fi

echo ""
echo "Done! Post-script manual steps:"
echo "  1. Update display name and description in fabric.mod.json"
echo "  2. Update companions/README.md prose"
echo "  3. Clean build artifacts: rm -rf companions/$NEW_HYPHEN/build"
echo "  4. Re-run datagen if applicable"
echo "  5. Run tests: ./gradlew test && ./gradlew runGametest"
echo "  6. Verify build: ./gradlew build"
