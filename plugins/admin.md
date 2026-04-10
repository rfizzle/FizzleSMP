# Server Admin & Management

<!-- Mods for permissions, world management, backups, monitoring, and server tooling. -->

## Ledger
- **CurseForge ID:** 491137
- **Slug:** ledger
- **Mod Loader:** Fabric
- **Side:** server
- **Summary:** A server-side block/entity/container action logging mod with rollback support.
- **Why:** Essential admin tool for tracking griefing, theft, and other player actions on the SMP with full rollback capability.
- **Dependencies:** Fabric API, Fabric Language Kotlin
- **Conflicts:** None known

## Connectivity
- **CurseForge ID:** 470193
- **Slug:** connectivity
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Fixes various connection issues including timeouts, login failures, and chunk loading problems.
- **Why:** Prevents frustrating disconnects and login issues on the SMP server by increasing timeouts and fixing packet handling bugs.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Chunky
- **CurseForge ID:** 433175
- **Slug:** chunky-pregenerator
- **Mod Loader:** Fabric
- **Side:** server
- **Summary:** Pre-generates chunks quickly and efficiently using server commands.
- **Why:** Allows admins to pre-generate the world around spawn and key areas to eliminate lag from on-the-fly chunk generation on the SMP.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Spark
- **CurseForge ID:** 361579
- **Slug:** spark
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A performance profiler for Minecraft clients, servers, and proxies.
- **Why:** Essential diagnostic tool for identifying TPS drops, lag spikes, and memory issues on the SMP server.
- **Dependencies:** None
- **Conflicts:** None known

## Neruina
- **CurseForge ID:** 851046
- **Slug:** neruina
- **Modrinth Slug:** neruina
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Prevents ticking entity/block entity crashes from bricking worlds by catching and removing the offending entity.
- **Why:** Critical stability mod — catches ticking crashes at runtime and removes the problem entity instead of crashing the server, keeping the world playable.
- **Dependencies:** Fabric API, Configurable
- **Conflicts:** None known

## Configurable
- **CurseForge ID:** 1092048
- **Slug:** configurable
- **Modrinth Slug:** configurable
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Config library that allows decentralised settings in a mod.
- **Why:** Required dependency of Neruina.
- **Dependencies:** None
- **Conflicts:** None known

## Not Enough Crashes
- **CurseForge ID:** 353890
- **Slug:** not-enough-crashes
- **Modrinth Slug:** notenoughcrashes
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Recovers from crashes to the title screen instead of closing the game, with deobfuscated crash reports and mod identification.
- **Why:** Lets players recover from client crashes without restarting, and identifies which mod caused the crash for faster debugging.
- **Dependencies:** None
- **Conflicts:** None known

## MixinTrace
- **CurseForge ID:** 433447
- **Slug:** mixintrace
- **Modrinth Slug:** mixintrace
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds a list of mixins in the stack trace to crash reports for easier mod conflict identification.
- **Why:** Makes crash reports actionable by showing exactly which mod's mixin caused the crash, essential for diagnosing conflicts in a large modpack.
- **Dependencies:** None
- **Conflicts:** None known

## Item Obliterator
- **CurseForge ID:** 835861
- **Slug:** item-obliterator
- **Modrinth Slug:** item-obliterator
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Modpack utility that disables items and their interactions via JSON5 config, removing them from creative tabs, recipes, trades, and EMI/JEI/REI.
- **Why:** Lets the pack maintainer surgically disable conflicting or unwanted items from other mods (e.g., Kibe's Big Torch, Angel Ring, Conveyor Belts, Sleeping Bag) without removing the host mod entirely.
- **Dependencies:** Necronomicon API
- **Conflicts:** None known; designed to integrate with EMI for proper recipe/item hiding.

## Necronomicon API
- **CurseForge ID:** 586157
- **Slug:** necronomicon
- **Modrinth Slug:** necronomicon
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A utility library by ElocinDev providing config helpers and shared code for their mods.
- **Why:** Required dependency of Item Obliterator (provides the JSON5 config loader).
- **Dependencies:** None
- **Conflicts:** None known

