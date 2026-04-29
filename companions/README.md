# Companions

Small, single-purpose Fabric mods built in-repo to patch compatibility gaps
between mods in the FizzleSMP pack. Each subdirectory is a standalone Gradle
project — build with `./gradlew build` from inside the mod's folder.

Built jars belong in the server's `mods/` directory and, when the patched
behavior involves client-synced state, in the client pack as well. Declare
their `side` in the matching plugin file entry if/when they are added to
`plugins/`.

## Versioning

Each companion mod versions independently using a git-driven scheme.

### Base version

The base version lives in each mod's `gradle.properties`:

```properties
mod_version=0.1.0
```

### Computed version

`computeModVersion()` in each `build.gradle` runs `git describe` against
mod-prefixed tags (e.g., `tribulation-v*`) and produces a SemVer string:

| Git state | Example output |
|-----------|---------------|
| Exact tag match | `0.1.0` |
| Exact tag + dirty tree | `0.1.0+dirty` |
| N commits past tag | `0.1.0+3.g8f7d42a` |
| No tag yet (dev build) | `0.1.0+g8f7d42a` |
| Git unavailable | `0.1.0` |

The computed version is injected into `fabric.mod.json` via Gradle's
`processResources` (the source file contains `"version": "${version}"`).

### Cutting a release

Use the release script from the repo root:

```bash
./scripts/companion-release.sh tribulation patch   # 0.1.0 → 0.1.1
./scripts/companion-release.sh meridian minor      # 0.1.0 → 0.2.0
./scripts/companion-release.sh tribulation 1.0.0   # explicit version
./scripts/companion-release.sh meridian patch --no-push  # tag locally only
```

The script bumps `gradle.properties`, commits as
`chore(<companion>): release v<version>`, creates an annotated tag
(`<companion>-v<version>`), and pushes both the commit and tag to origin.

## Mods

- **[tribulation](tribulation/)** — Unified, formula-driven mob
  scaling (time + distance + height) that replaces HMIOT and RpgDifficulty.
  Side: `both`. See [DESIGN.md](tribulation/DESIGN.md).
- **[meridian](meridian/)** — Apotheosis/Zenith-style
  stat-driven enchanting table, shelves, Enchantment Library, and anvil/tome
  tooling for 1.21.1 Fabric. Design-only; intended to eventually replace
  Easy Anvils, Enchanting Infuser, NeoEnchant+, BeyondEnchant, and Grind
  Enchantments. See [DESIGN.md](meridian/DESIGN.md).
