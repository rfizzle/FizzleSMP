# Enchantment Reimplementation — Agent Prompt

Copy everything below the line and pass it as the prompt to a new Claude Code
session or agent. It is self-contained.

---

## Task

Reimplement the enchantment system for the **Meridian** Fabric mod (Minecraft
1.21.1). Wipe all existing enchantment data and rebuild from a clean spec. The
goal is a fully original enchantment roster — new IDs, names, descriptions,
weights, and costs.

## Key files — read these first

1. **`companions/meridian/docs/ENCHANT_SPEC.md`** — The complete design spec.
   This is the ONLY source of truth for enchantment names, IDs, concepts, rarity
   weights, treasure status, item applicability, and exclusive sets. Every
   implementation decision comes from this file.

2. **`companions/meridian/docs/ENCHANT_TODO.md`** — The full implementation
   checklist broken into phases (infrastructure → data-driven → custom Java →
   lang → testing → docs). Work through it in order. Check items as you
   complete them.

3. **`companions/meridian/docs/CONFIG.md`** — Current config documentation.
   Needs to be updated with the new `enabled` field.

## Key source files

- `companions/meridian/src/main/java/com/rfizzle/meridian/config/MeridianConfig.java` — Config system. `EnchantmentOverride` class needs an `enabled` boolean.
- `companions/meridian/src/main/java/com/rfizzle/meridian/enchanting/EnchantmentInfoRegistry.java` — Rebuilds enchantment info from config. Must respect `enabled`.
- `companions/meridian/src/main/java/com/rfizzle/meridian/enchanting/EnchantmentInfo.java` — Per-enchantment config record.
- `companions/meridian/src/main/java/com/rfizzle/meridian/net/EnchantmentInfoPayload.java` — S2C sync. Must include `enabled` state.
- `companions/meridian/src/main/java/com/rfizzle/meridian/mixin/EnchantmentMixin.java` — Enforces configured max levels.
- `companions/meridian/src/client/java/com/rfizzle/meridian/client/screen/EnchantingInfoScreen.java` — Enchantment browser. Hide disabled enchantments.
- `companions/meridian/src/client/java/com/rfizzle/meridian/client/screen/EnchantmentLibraryScreen.java` — Library UI. Grey out disabled.
- `companions/meridian/src/client/java/com/rfizzle/meridian/client/tooltip/InlineEnchDescTooltipHandler.java` — Reads `.desc` lang keys.
- `companions/meridian/src/client/java/com/rfizzle/meridian/client/tooltip/OverLeveledTooltipHandler.java` — Tooltip coloring.
- `companions/meridian/src/client/java/com/rfizzle/meridian/compat/modmenu/ModMenuIntegration.java` — ModMenu config screen (no changes needed).
- `companions/meridian/src/main/resources/data/meridian/enchantment/` — Enchantment JSON definitions (wipe and rebuild).
- `companions/meridian/src/main/generated/data/meridian/tags/enchantment/` — Generated enchantment tags (wipe and rebuild).
- `companions/meridian/src/main/resources/assets/meridian/lang/en_us.json` — Lang file.

## Rules

1. **Work from the spec only.** Do not read, reference, or open any third-party
   mod data files. The spec contains everything needed.
2. **All data is original.** Every weight, cost, level range, description string,
   and registry ID must be authored fresh from the spec's concept descriptions.
   Do not copy values from the old enchantment JSONs.
3. **Phase order matters.** Complete Phase 0 (infrastructure) before creating any
   enchantment files. The `enabled` config plumbing must be in place first.
4. **Data-driven first.** Implement Phase 1 (JSON-only enchantments) before
   Phase 2 (custom Java). This gets ~26 enchantments working quickly and
   validates the infrastructure.
5. **Two lang keys per enchantment.** Every enchantment needs both
   `enchantment.meridian.<id>` (name) and `enchantment.meridian.<id>.desc`
   (short description for inline tooltips). Descriptions must be original text.
6. **Test as you go.** After completing each sub-group in the TODO, run
   `./gradlew :companions:meridian:test` and verify in-game via the enchanting
   table before moving to the next group.
7. **Use the dev-companion skill** (`/dev-companion`) for Fabric mod development
   guidance and the **gradle-builds skill** (`/gradle-builds`) for build/test
   commands.
8. **Use the fabric-testing skill** (`/fabric-testing`) when writing tests —
   it knows the three-tier test decision tree for this project.
9. **Commit per phase.** Make one commit per completed phase using conventional
   commits (e.g., `refactor(companions): wipe old meridian enchantment data`,
   `feat(companions): implement data-driven meridian enchantments`).

## Build and test

```bash
# Build the mod
./gradlew build

# Run tests
./gradlewtest

# Run gametests (in-world integration tests)
./gradlew runGametest

# Run the dev client for manual testing
./gradlew runClient
```

## What success looks like

- All 75 enchantments from `ENCHANT_SPEC.md` are implemented and functional.
- Every exclusive set is enforced (can't combine conflicting enchants).
- The `enabled: false` config flag fully disables an enchantment across all
  systems (table, loot, tooltips, effects, library, info screen).
- Every enchantment has a test.
- `ENCHANTMENTS.md`, `CONFIG.md`, `README.md`, and `LICENSE` are updated.
- All items in `ENCHANT_TODO.md` are checked off.
