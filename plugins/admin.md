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

## Carpet
- **CurseForge ID:** 349239
- **Slug:** carpet
- **Modrinth Slug:** carpet
- **Mod Loader:** Fabric
- **Side:** server
- **Summary:** Server-side rule engine for fine-tuning vanilla mechanics (spawning, tick speed, TNT, random tick rates) via toggleable rules.
- **Why:** Fulfills the "server mechanic tuning" feature — gives admins granular control over vanilla mechanics without per-mod config files, essential for balancing an SMP with 160+ mods.
- **Dependencies:** None
- **Conflicts:** Previously removed (28dfbf0) due to CustomPacketPayload mixin ClassCastException — traced to a Vivecraft bug, not Carpet (gnembon/fabric-carpet#2021, closed). No current conflicts known.
