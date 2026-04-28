# Companion Mod Rename Migration

## Overview

Rename both companion mods to standalone-branded names that reflect their
spiritual predecessors without the `fizzle-` prefix.

| Current | New | Mod ID | Display Name | Lineage |
|---------|-----|--------|--------------|---------|
| `fizzle-enchanting` | `meridian` | `meridian` | Meridian | Apotheosis + Zenith |
| `fizzle-difficulty` | `tribulation` | `tribulation` | Tribulation | HMIOT + RpgDifficulty |

**Maven group:** `com.fizzlesmp` -> `com.rfizzle` (changed during migration).

---

## Phase 1 — Deterministic Rename Script

A bash script (`scripts/rename-companion.sh`) should handle all mechanical
renaming. It takes two arguments: the old name and the new name.

```
./scripts/rename-companion.sh fizzle-enchanting meridian
./scripts/rename-companion.sh fizzle-difficulty tribulation
```

### What the script must do

The script derives all name forms from the two arguments:

```bash
OLD_HYPHEN="fizzle-enchanting"    # $1
NEW_HYPHEN="meridian"             # $2
OLD_UNDER="fizzle_enchanting"     # ${OLD_HYPHEN//-/_}
NEW_UNDER="meridian"              # ${NEW_HYPHEN//-/_}
OLD_CAMEL="FizzleEnchanting"      # derived: split on [-_], Title-case each word, join
NEW_CAMEL="Meridian"              # derived: same transform on new name
```

**Step 1 — Rename resource files (before directory moves):**

```bash
# Mixin config
git mv "companions/$OLD_HYPHEN/src/main/resources/${OLD_UNDER}.mixins.json" \
       "companions/$OLD_HYPHEN/src/main/resources/${NEW_UNDER}.mixins.json"

# Access widener (if exists)
if [ -f "companions/$OLD_HYPHEN/src/main/resources/${OLD_UNDER}.accesswidener" ]; then
  git mv "companions/$OLD_HYPHEN/src/main/resources/${OLD_UNDER}.accesswidener" \
         "companions/$OLD_HYPHEN/src/main/resources/${NEW_UNDER}.accesswidener"
fi
```

**Step 2 — Rename Java package directories (all sourcesets):**

For each sourceset (`main`, `client`, `test`, `gametest`):

```bash
for srcset in main client test gametest; do
  OLD_PKG="companions/$OLD_HYPHEN/src/$srcset/java/com/fizzlesmp/$OLD_UNDER"
  NEW_PKG="companions/$OLD_HYPHEN/src/$srcset/java/com/fizzlesmp/$NEW_UNDER"
  if [ -d "$OLD_PKG" ]; then
    git mv "$OLD_PKG" "$NEW_PKG"
  fi
done
```

**Step 3 — Rename resource namespace directories:**

```bash
for base in src/main/resources src/main/generated; do
  for ns in data assets; do
    OLD_DIR="companions/$OLD_HYPHEN/$base/$ns/$OLD_UNDER"
    NEW_DIR="companions/$OLD_HYPHEN/$base/$ns/$NEW_UNDER"
    if [ -d "$OLD_DIR" ]; then
      git mv "$OLD_DIR" "$NEW_DIR"
    fi
  done
done
```

**Step 4 — Rename Java class files with the old prefix:**

```bash
find "companions/$OLD_HYPHEN/src" -name "${OLD_CAMEL}*.java" | while read f; do
  dir=$(dirname "$f")
  old_basename=$(basename "$f")
  new_basename="${old_basename/$OLD_CAMEL/$NEW_CAMEL}"
  git mv "$f" "$dir/$new_basename"
done
```

**Step 5 — Bulk find-and-replace in file contents:**

Order matters — do the longest/most-specific patterns first to avoid
partial matches.

```bash
# Fully-qualified package references (Java imports, fabric.mod.json entrypoints)
find "companions/$OLD_HYPHEN" -type f \( -name '*.java' -o -name '*.json' -o -name '*.gradle' -o -name '*.properties' -o -name '*.toml' -o -name '*.md' \) \
  -exec sed -i "s/com\.fizzlesmp\.${OLD_UNDER}/com.fizzlesmp.${NEW_UNDER}/g" {} +

# CamelCase class prefix (Java class names, references)
find "companions/$OLD_HYPHEN" -type f -name '*.java' \
  -exec sed -i "s/${OLD_CAMEL}/${NEW_CAMEL}/g" {} +

# Underscored mod ID in all file contents (JSON namespaces, mixin refs, etc.)
find "companions/$OLD_HYPHEN" -type f \( -name '*.java' -o -name '*.json' -o -name '*.gradle' -o -name '*.properties' \) \
  -exec sed -i "s/${OLD_UNDER}/${NEW_UNDER}/g" {} +

# Hyphenated project name (gradle, settings, docs)
find "companions/$OLD_HYPHEN" -type f \( -name '*.gradle' -o -name '*.properties' -o -name '*.md' -o -name '*.toml' \) \
  -exec sed -i "s/${OLD_HYPHEN}/${NEW_HYPHEN}/g" {} +
```

**Step 6 — Rename the top-level directory (last):**

```bash
git mv "companions/$OLD_HYPHEN" "companions/$NEW_HYPHEN"
```

**Step 7 — Update external references (outside the mod directory):**

These files reference companion mods by path and must be updated separately:

```bash
# CI workflow
sed -i "s/${OLD_HYPHEN}/${NEW_HYPHEN}/g" .github/workflows/companion-tests.yml

# Claude commands and skills
find .claude/ -type f -name '*.md' \
  -exec sed -i "s/${OLD_HYPHEN}/${NEW_HYPHEN}/g" {} +
find .claude/ -type f -name '*.md' \
  -exec sed -i "s/${OLD_UNDER}/${NEW_UNDER}/g" {} +
find .claude/ -type f -name '*.md' \
  -exec sed -i "s/${OLD_CAMEL}/${NEW_CAMEL}/g" {} +

# companions/README.md
sed -i "s/${OLD_HYPHEN}/${NEW_HYPHEN}/g" companions/README.md
```

### Post-script manual steps

After running the script for both mods:

1. **Update display names** in each `fabric.mod.json`:
   - `"name": "Meridian"` with updated description
   - `"name": "Tribulation"` with updated description
2. **Update `companions/README.md`** descriptions (script handles links, not prose)
3. **Clean build artifacts:** `rm -rf companions/meridian/build companions/tribulation/build`
4. **Re-run datagen:** `cd companions/meridian && ./gradlew runDatagen`
5. **Run tests:** `./gradlew test` and `./gradlew runGametest` in each mod
6. **Verify build:** `./gradlew build` in each mod

### Edge cases to watch

- **`sed` ordering:** The underscored replace (`fizzle_enchanting` -> `meridian`)
  must NOT run on files already processed by the package replace, or it could
  double-replace. Since `meridian` doesn't contain `fizzle_enchanting`, this is
  safe for these specific names. For names where the new name contains the old
  pattern, run replaces in a single pass with a tool like `sd` instead.
- **CamelCase derivation:** `fizzle-enchanting` -> `FizzleEnchanting` ->
  `Meridian`. The script must handle the word-boundary split correctly.
  `fizzle-difficulty` -> `FizzleDifficulty` -> `Tribulation`.
- **Refmap file:** `build/classes/java/main/fizzle-enchanting-refmap.json` is
  generated at build time from `archives_base_name`. After renaming
  `archives_base_name`, a clean build produces the correct refmap. No manual
  rename needed.

---

## Phase 2 — Icon Design

Both mods need a 128x128 pixel icon used in:
- `fabric.mod.json` `"icon"` field (mod list / ModMenu)
- EMI/JEI/REI recipe category icon (16x16 scaled)
- Creative tab icon (in-game)

Icons should be pixel-art style, consistent with Minecraft's aesthetic, and
readable at 16x16.

### Meridian (enchanting mod)

**Theme:** Peak of arcane mastery. The name means "highest point" / "celestial
meridian line."

**Concept:** An enchanting table book with a glowing golden arc (meridian line)
rising above it, suggesting the zenith of enchanting power.

**Elements:**
- Open enchanting book or tome as the base
- Golden/amber arc or crescent sweeping over the top (the "meridian")
- Subtle purple/blue enchantment glint particles
- Color palette: deep purple base, gold arc, cyan/teal particle accents

**Fallback (simpler):** A golden compass rose or astrolabe — the astronomical
instrument used to find the meridian — with enchantment sparkles.

### Tribulation (mob difficulty mod)

**Theme:** Escalating hardship and trial. The world gets harder as you play.

**Concept:** A cracked or battle-scarred mob skull with an upward-pointing
red arrow or flame, suggesting escalating danger.

**Elements:**
- Stylized skeleton or wither skull (recognizable Minecraft mob)
- Red/orange upward arrow or flame motif behind/above the skull
- Cracks or scars on the skull to show wear and difficulty
- Color palette: bone white skull, crimson/dark red arrow, ember orange accents

**Fallback (simpler):** A red upward-pointing sword or crossed swords with a
difficulty-style "rising threat" arrow.

### Icon file placement

```
companions/meridian/src/main/resources/assets/meridian/icon.png
companions/tribulation/src/main/resources/assets/tribulation/icon.png
```

Reference in `fabric.mod.json`:
```json
"icon": "assets/meridian/icon.png"
```

### Generation Prompts

Use these prompts with image AI services. Recraft (pixel art mode) is
recommended; DALL-E 3 or Midjourney are fallbacks that need manual cleanup.

#### Meridian — Recraft / DALL-E 3

```
Pixel art icon for a Minecraft mod, 128x128 pixels, transparent background.
An open enchanting book viewed from a slight angle, with a glowing golden arc
rising above it like a celestial meridian line. The book has a deep purple
cover with arcane symbols. Small cyan and teal enchantment particles float
around the arc. The golden arc is bright and luminous, forming a smooth
crescent from one side of the book to the other. Style: 16-bit pixel art
with clean edges, limited color palette, no anti-aliasing. Colors: deep
purple, rich gold, cyan accents, dark background shading. The icon should
read clearly when scaled down to 16x16 pixels.
```

#### Meridian — Midjourney

```
/imagine pixel art minecraft mod icon, open enchanting book with glowing
golden meridian arc above it, deep purple cover, cyan enchantment particles,
16-bit style, clean pixel edges, transparent background, 128x128, limited
palette, no anti-aliasing --style raw --s 50 --no blur, gradient,
anti-aliasing, smooth shading
```

#### Meridian — Fallback (simpler concept)

```
Pixel art icon for a Minecraft mod, 128x128 pixels, transparent background.
A golden astrolabe or compass rose with enchantment sparkles. The instrument
has concentric golden rings with a central pointer aimed upward. Purple and
cyan magical particles emanate from the edges. Style: 16-bit pixel art,
clean hard edges, limited color palette. Colors: gold, deep purple, teal
accents. Must be readable at 16x16 pixels.
```

#### Tribulation — Recraft / DALL-E 3

```
Pixel art icon for a Minecraft mod, 128x128 pixels, transparent background.
A stylized Minecraft skeleton skull, cracked and battle-scarred, with a
bold red upward-pointing arrow or flame rising behind it. The skull is
bone-white with visible cracks and dark eye sockets. The red motif behind
it suggests escalating danger and increasing difficulty. Small ember-orange
particles float upward. Style: 16-bit pixel art with clean edges, limited
color palette, no anti-aliasing. Colors: bone white, crimson red, dark red
shadows, ember orange accents. The icon should read clearly when scaled
down to 16x16 pixels.
```

#### Tribulation — Midjourney

```
/imagine pixel art minecraft mod icon, cracked skeleton skull with red
upward arrow behind it, battle-scarred bone white skull, crimson flame
motif, ember particles, 16-bit style, clean pixel edges, transparent
background, 128x128, limited palette, no anti-aliasing --style raw --s 50
--no blur, gradient, anti-aliasing, smooth shading
```

#### Tribulation — Fallback (simpler concept)

```
Pixel art icon for a Minecraft mod, 128x128 pixels, transparent background.
Two crossed iron swords with a bold red upward-pointing chevron arrow behind
them, suggesting rising threat and escalating difficulty. Small ember-orange
sparks at the tips of the swords. Style: 16-bit pixel art, clean hard edges,
limited color palette. Colors: iron gray swords, crimson red arrow, dark
background shading, orange spark accents. Must be readable at 16x16 pixels.
```

### Creative tab icons

Creative tab icons are `Item` references in code, not image files. Each mod
should register a representative item to use:
- **Meridian:** the enchanting table variant or the Enchantment Library block
- **Tribulation:** the mob trophy item or a custom "difficulty token" item

---

## Phase 3 — World Compatibility (if deployed)

> **Skip this phase if the mods have not been deployed to a live world yet.**

Changing a mod ID breaks all registry entries saved under the old namespace.
Items, blocks, enchantments, and entity data keyed as `fizzle_enchanting:*`
will vanish from the world.

**Options:**
1. **World reset** — acceptable if the mods are new and no player data matters.
2. **Data fixer** — register a `DataFixerUpper` schema that remaps
   `fizzle_enchanting:*` -> `meridian:*` and `fizzle_difficulty:*` ->
   `tribulation:*` in all chunk, entity, and player NBT.
3. **Alias registry** — some mods support ID aliasing at the Fabric level.
   This is fragile and not recommended for long-term use.

**Recommendation:** If deployed, go with option 2. Write a DFU schema as part
of the rename commit. If not yet deployed, skip entirely.

---

## Appendix — Sentry Block Textures (Tribulation)

A mechanical/arcane sentry block for the Tribulation mod. Needs three face
textures: top (with cyan eye), side (fortified/mechanical), and bottom (dark
base). All textures must be **16x16 pixels** — Minecraft's native resolution.

Generate at 1024x1024 in Recraft (pixel art mode), then downscale to 16x16
with nearest-neighbor: `magick input.png -filter point -resize 16x16 output.png`

**Design references:** Lodestone, sculk shrieker, respawn anchor — dark
mechanical blocks with subtle color and mid-tone contrast.

**General rules for all faces:**
- Flat, head-on view — no perspective, no 3D angle, no isometric
- Dark gray stone/metal base, NOT pure black (use #2a2a2e to #3d3d42 range)
- Subtle warm or cool undertones in the grays (not pure grayscale)
- Edges should tile seamlessly when blocks are placed adjacent
- Minimal detail — think 3-5 distinct shapes max per face
- Mid-tone contrast range (no pure black, minimal pure white)
- Square canvas, filling the entire frame edge-to-edge

### Sentry Top — Recraft

```
Pixel art Minecraft block texture, top-down flat view, 16x16 pixel grid,
square filling entire canvas. Dark gray stone slab with a single glowing
cyan eye in the center. The eye is a simple 4x4 pixel circle with a bright
cyan core and a darker teal ring around it. The surrounding stone is dark
charcoal gray with subtle chisel marks. Two thin carved lines extend from
the eye toward each edge, forming a cross or plus shape. Style: Minecraft
block texture, flat top-down view, no perspective, no 3D shading. Colors:
dark charcoal gray (#3a3a3e) base, cyan (#00ddff) eye, teal (#007788)
ring, subtle dark gray (#2e2e32) carved lines. Must tile when placed next
to identical blocks.

neutral cool gray, NOT purple or mauve — match Minecraft deepslate coloring (#44444a base, #38383e shadows)
```

### Sentry Side — Recraft

```
Pixel art Minecraft block texture, flat front view, 16x16 pixel grid,
square filling entire canvas. Dark gray fortified stone block face. A
vertical slit or narrow window in the center, 2 pixels wide and 6 pixels
tall, with a faint warm glow inside (amber/orange). Simple stone brick
pattern around it — two or three horizontal mortar lines dividing the face
into sections. Small angular reinforcement details in the corners, like
iron brackets. Style: Minecraft block texture, flat front view, no
perspective. Colors: dark charcoal gray (#3a3a3e) stone, darker gray
(#2e2e32) mortar lines, warm amber (#cc8833) glow in the slit, iron gray
(#555560) corner brackets. Must tile vertically and horizontally when
blocks are stacked.

neutral cool gray, NOT purple or mauve — match Minecraft deepslate coloring (#44444a base, #38383e shadows)
```

### Sentry Bottom — Recraft

```
Pixel art Minecraft block texture, bottom-up flat view, 16x16 pixel grid,
square filling entire canvas. Dark gray stone base plate, slightly darker
than the side texture. A simple concentric square pattern carved into the
surface — an outer border 1 pixel from the edge and an inner square in
the center. Subtle texture variation in the stone, like deepslate. No
glowing elements — this is the plain underside. Style: Minecraft block
texture, flat bottom-up view, no perspective. Colors: dark gray (#333338)
base, slightly lighter gray (#3e3e43) border lines, very subtle noise
variation in the fill. Must tile when viewed from below on adjacent blocks.

neutral cool gray, NOT purple or mauve — match Minecraft deepslate coloring (#44444a base, #38383e shadows)
```

### Post-generation cleanup

After generating and downscaling to 16x16:
1. **Check tiling** — place 3x3 in a grid in your image editor and look for
   seam artifacts at edges
2. **Check in-game palette** — compare against lodestone/deepslate textures
   to ensure the block doesn't stick out
3. **Adjust brightness** — if any area is pure black (#000000), bump it to
   at least #1a1a1e
4. **File placement:**
   ```
   companions/tribulation/src/main/resources/assets/tribulation/textures/block/sentry_top.png
   companions/tribulation/src/main/resources/assets/tribulation/textures/block/sentry_side.png
   companions/tribulation/src/main/resources/assets/tribulation/textures/block/sentry_bottom.png
   ```

---

## Execution Checklist

- [x] Write `scripts/rename-companion.sh`
- [x] Run for `fizzle-enchanting` -> `meridian`
- [x] Run for `fizzle-difficulty` -> `tribulation`
- [x] Update display names and descriptions in both `fabric.mod.json`
- [x] Update `companions/README.md` prose
- [x] Clean and rebuild both mods
- [x] Re-run datagen for meridian
- [x] Run unit tests for both mods
- [x] Run gametests for both mods
- [x] Create icons for both mods
- [x] Set `"icon"` field in both `fabric.mod.json`
- [ ] Register creative tab icons in code
- [x] Update `CLAUDE.md` if it references old names
- [x] Update any memory files in `.claude/projects/` referencing old names
- [x] ~~Single commit~~ Split across 3 commits (meridian rename, tribulation rename, script cleanup)
- [x] Regroup `com.fizzlesmp` -> `com.rfizzle` (added during migration)
- [x] Phase 3 skipped — mods not yet deployed to a live world
