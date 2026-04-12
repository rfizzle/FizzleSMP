# FizzleSMP

*Forge your path. Master your craft. Shape the world.*

**FizzleSMP** is a handcrafted multiplayer survival experience built for friends who want more from their world — without losing what makes Minecraft feel like home.

**A World Worth Exploring** — Nearly a hundred biomes reshape the Overworld with dramatic cliffs, lush valleys, and forgotten ruins. The Nether burns hotter with Incendium's volcanic wastes, the End stretches into alien landscapes, and hidden dimensions like the Bumblezone and the depths of Deeper and Darker reward the bold. Every dungeon, stronghold, and ocean temple has been rebuilt from the ground up — no two adventures are the same.

**Choose Your Path** — Train as a Wizard, Paladin, Rogue, Warrior, or Archer through a full RPG class system with spells, skills, and progression trees. Build industrial empires with Tech Reborn and Oritech. Unravel the mysteries of color-based magic with Spectrum. Hunt legendary weapons from massive bosses. Or simply build a farm, open a shop, and watch the economy grow around you.

**Combat That Feels Alive** — Forget spam-clicking. FizzleSMP overhauls combat with directional swings, combat rolls, unique weapon types — from claymores to chakrams — and enemies that grow smarter and tougher the longer your world survives. Boss encounters are designed for groups, and the loot is worth the fight.

**Built for Community** — Proximity voice chat. Player-run shops and economies. Land claims that protect what you've built. Per-player loot so every chest feels like yours. Waystones to stay connected across a massive world. This is an SMP designed for people who play together.

**Runs Well, Plays Better** — Under the hood, a full performance stack (Sodium, Lithium, Noisium, and more) keeps frame rates high and load times low — so the experience stays smooth even with all of this packed in.

Minecraft 1.21.1 · Fabric · Managed with [packwiz](https://packwiz.infra.link/)

## Quick Start

### Syncing the pack

```bash
# Sync plugin lists to packwiz
./scripts/sync-packwiz.sh

# Preview sync changes without modifying anything
./scripts/sync-packwiz.sh --dry-run

# Sync and remove mods not in plugin lists
./scripts/sync-packwiz.sh --prune
```

### Building distribution artifacts

```bash
# Build versioned release ZIPs locally (reads version from modpack/pack.toml)
./scripts/build-pack.sh client    # FizzleSMP-client-X.Y.Z.zip (CurseForge-compatible)
./scripts/build-pack.sh server    # FizzleSMP-server-X.Y.Z.zip (drop-in Fabric server directory)

# Or call packwiz directly (run from modpack/)
cd modpack
packwiz curseforge export                # CurseForge .zip
packwiz curseforge export --side client  # Client-side-only CurseForge .zip
packwiz modrinth export                  # Modrinth .mrpack
```

The GitHub Actions release pipeline runs `build-pack.sh` for both targets on every tag push — you rarely need to build locally unless debugging the build itself.

### Adding mods

Use the Claude Code `/add-mods` command or manually add entries to the appropriate `plugins/*.md` file. See `CLAUDE.md` for the full plugin file format.

## Releases

FizzleSMP uses versioned releases so players and the live server can pin to a known-good build. The version lives in `modpack/pack.toml` (`version = "X.Y.Z"`) and is embedded in release artifact filenames.

### Versioning

Pragmatic SemVer for modpacks:

- **MAJOR** — world-breaking changes (worldgen or dimension mods added/removed). Existing worlds may need reset or migration.
- **MINOR** — additive changes (new mods, new content). Safe to apply to an existing world.
- **PATCH** — config tweaks, mod version bumps, bug fixes, compatibility fixes.

### Cutting a release

1. **Land your changes on `master`** — commit all the plugin/packwiz/config changes you want in the release.
2. **Update `CHANGELOG.md`** — add your entries under the `## [Unreleased]` section (Added / Changed / Fixed / Removed). The release script will roll these into a dated version heading.
3. **Run the release script:**

   ```bash
   ./scripts/release.sh patch       # 1.0.0 → 1.0.1 (tweaks, fixes)
   ./scripts/release.sh minor       # 1.0.0 → 1.1.0 (new mods, new content)
   ./scripts/release.sh major       # 1.0.0 → 2.0.0 (world-breaking)
   ./scripts/release.sh 1.4.0       # explicit version
   ./scripts/release.sh patch --no-push  # tag locally, do not push
   ```

4. **What the script does:**
   - Verifies the working tree is clean and you are on `master`
   - Bumps `version = "..."` in `modpack/pack.toml`
   - Runs `packwiz refresh` to keep `index.toml` in sync
   - Rolls `CHANGELOG.md` `[Unreleased]` into `[X.Y.Z] - YYYY-MM-DD`
   - Commits as `chore(release): vX.Y.Z`
   - Creates an annotated tag `vX.Y.Z`
   - Pushes `master` and the tag to `origin` (unless `--no-push`)

5. **GitHub Actions takes over** — the tag push triggers `.github/workflows/release.yml`, which:
   - Installs the `packwiz` CLI
   - Builds `FizzleSMP-client-X.Y.Z.zip` and `FizzleSMP-server-X.Y.Z.zip`
   - Extracts the matching `CHANGELOG.md` section for the release body
   - Publishes a GitHub Release with both ZIPs attached

   Watch the build at <https://github.com/rfizzle/FizzleSMP/actions>.

### Shipping the client pack to players

Players import `FizzleSMP-client-X.Y.Z.zip` into the CurseForge launcher. Every release attaches this ZIP to the GitHub Release.

### Updating the live server

The server updates in place via `scripts/server-install.sh`, which wraps [packwiz-installer-bootstrap](https://github.com/packwiz/packwiz-installer-bootstrap). It diffs against the published `pack.toml` and only downloads changed mods/configs. Worlds, player data, `server.properties`, and the Fabric launcher are never touched.

```bash
# On the Fabric server host, from the server directory:
./server-install.sh              # install/update to the latest release
./server-install.sh v1.3.0       # pin to a specific version (rollback path)
./server-install.sh master       # install unreleased master (testing only)
```

**Rollback:** `./server-install.sh v1.2.0` — reinstalls the previous release. Note that rolling back over a release that added worldgen/content mods can leave the world inconsistent; treat worldgen-adding releases as one-way migrations.

Full operator guide (fresh install, troubleshooting, what is / is not managed): [`docs/server-deployment.md`](docs/server-deployment.md).

### Hotfix without a release

For urgent fixes that should not wait for a release, point the server at `master`:

```bash
./server-install.sh master
```

Then cut a patch release as soon as practical so the server is back on a pinned version.

## Project Structure

| Path | Purpose |
|------|---------|
| `plugins/*.md` | Human-readable mod lists (source of truth) |
| `modpack/` | Packwiz metadata (machine-readable) |
| `modpack/config/` | Mod config files shipped with the pack |
| `CHANGELOG.md` | Release history (Keep a Changelog format) |
| `docs/compatibility-matrix.md` | Known conflicts and compatibility notes |
| `docs/features-brainstorm.md` | Feature ideas and mod mapping |
| `docs/testing.md` | Testing checklist for pack validation |
| `docs/server-deployment.md` | Operator guide for the live server |
| `scripts/sync-packwiz.sh` | Sync `plugins/` to packwiz |
| `scripts/build-pack.sh` | Build versioned client/server release ZIPs |
| `scripts/release.sh` | Cut a new release (bump, tag, push) |
| `scripts/server-install.sh` | Install/update the live server via packwiz-installer |
| `.github/workflows/release.yml` | CI pipeline that publishes GitHub Releases on tag push |

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
