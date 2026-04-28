# Companions

Small, single-purpose Fabric mods built in-repo to patch compatibility gaps
between mods in the FizzleSMP pack. Each subdirectory is a standalone Gradle
project — build with `./gradlew build` from inside the mod's folder.

Built jars belong in the server's `mods/` directory and, when the patched
behavior involves client-synced state, in the client pack as well. Declare
their `side` in the matching plugin file entry if/when they are added to
`plugins/`.

## Mods

- **[fizzle-difficulty](fizzle-difficulty/)** — Unified, formula-driven mob
  scaling (time + distance + height) that replaces HMIOT and RpgDifficulty.
  Side: `both`. See [DESIGN.md](fizzle-difficulty/DESIGN.md).
- **[meridian](meridian/)** — Apotheosis/Zenith-style
  stat-driven enchanting table, shelves, Enchantment Library, and anvil/tome
  tooling for 1.21.1 Fabric. Design-only; intended to eventually replace
  Easy Anvils, Enchanting Infuser, NeoEnchant+, BeyondEnchant, and Grind
  Enchantments. See [DESIGN.md](meridian/DESIGN.md).
