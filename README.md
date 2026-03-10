# FizzleSMP

A curated Minecraft 1.21.1 Fabric modpack for a survival multiplayer server. Managed with [packwiz](https://packwiz.infra.link/) and distributable to CurseForge and Modrinth.

## Quick Start

### Building the modpack

```bash
# Sync plugin lists to packwiz
./scripts/sync-packwiz.sh

# Preview sync changes without modifying anything
./scripts/sync-packwiz.sh --dry-run

# Sync and remove mods not in plugin lists
./scripts/sync-packwiz.sh --prune

# Export for distribution (run from modpack/)
cd modpack
packwiz curseforge export    # CurseForge .zip
packwiz modrinth export      # Modrinth .mrpack
```

### Adding mods

Use the Claude Code `/add-mods` command or manually add entries to the appropriate `plugins/*.md` file. See `CLAUDE.md` for the full plugin file format.

## Project Structure

| Path | Purpose |
|------|---------|
| `plugins/*.md` | Human-readable mod lists (source of truth) |
| `modpack/` | Packwiz metadata (machine-readable) |
| `modpack/config/` | Mod config files shipped with the pack |
| `docs/compatibility-matrix.md` | Known conflicts and compatibility notes |
| `docs/features-brainstorm.md` | Feature ideas and mod mapping |
| `docs/testing.md` | Testing checklist for pack validation |
| `scripts/sync-packwiz.sh` | Sync `plugins/` to packwiz |

## In-Game Debugging

### Identifying structures

Use any of these methods to determine which mod generated a structure:

1. **MiniHUD** — Enable structure bounding box overlay (Renderer Hotkeys > "Structure Bounding Boxes"). Displays namespaced IDs like `explorify:ruin_plains`.
2. **F3 debug screen** (BetterF3) — Stand inside a structure to see its namespaced ID.
3. **Explorer's Compass** — Search for structures by name; shows the mod namespace in results.

### Sparse Structures

Sparse Structures controls structure generation density. The config is at `modpack/config/sparsestructures.json5`.

**Dump all registered structure sets:**

Run this in-game (requires op/cheats):

```
/sparsestructures dump
```

The dump file is saved to the instance directory under `sparsestructures/` with a timestamped filename, e.g.:

```
<instance>/sparsestructures/structure_sets_dump_26_03_10_00_37.txt
```

The dump outputs every registered structure set in a format ready to paste into `customSpreadFactors` in the config. This is useful when adding new structure mods to determine what structure set IDs they register.

**Restart the game after editing the config.**

### Checking mod conflicts

- **Logs** — Check `latest.log` for `ERROR` and `WARN` lines after startup. Look for mixin conflicts, failed injections, or missing dependencies.
- **Mod Menu** — Open in-game to verify all expected mods are loaded with correct versions.

### Useful commands

| Command | Purpose |
|---------|---------|
| `/locate structure <namespace>:` | Tab-complete to browse structures by mod namespace |
| `/sparsestructures dump` | Dump all structure sets (see above) |
| `/trigger` | List available Supplementaries triggers |

## Managing the Pack with Claude Code

| Command | Purpose |
|---------|---------|
| `/add-mods <names>` | Add mods with version/dependency/conflict checks |
| `/review-plugins` | Audit all mod lists for issues |
| `/check-conflicts` | Cross-reference mods for conflicts |
| `/compare-sync` | Compare `plugins/` vs packwiz state |
| `/analyze-log <path>` | Diagnose a Minecraft log |
