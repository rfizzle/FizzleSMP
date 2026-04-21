---
name: fabric-testing
description: Write, modify, or migrate tests for Fabric companion mods under companions/<mod>/src/test/. Encodes the three-tier decision tree (pure JUnit / fabric-loader-junit / Fabric Gametest) and the recipe for replacing the Bootstrap.bootStrap()+unfreeze-reflection pattern. TRIGGER when a *Test.java file under companions/*/src/test/ is being created or edited, or when the user asks about testing a Fabric mod without a full server.
---

The user is writing, modifying, or migrating tests in a companion Fabric mod under `companions/<mod-name>/src/test/`. Apply this guidance whenever test code is being touched — both new tests and conversions of existing ones.

## Why this skill exists

The companion mods shipped with two incompatible test bootstrapping patterns:

- **fizzle-difficulty** — pure JUnit 5 tests, no Minecraft classes referenced. Works fine.
- **fizzle-enchanting** — uses `Bootstrap.bootStrap()` plus reflection to unfreeze `BuiltInRegistries` (`MappedRegistry.frozen` / `unregisteredIntrusiveHolders`), plus `forkEvery = 1` to avoid cross-test contamination. This is brittle and slow.

The target is **`fabric-loader-junit`** for anything that needs a registry, item, component, mixin, or entrypoint, and **Fabric Gametest** for anything that needs a real `Level`. The bootstrap+reflection pattern must not be used in new code.

## Decision tree — pick one tier per test

Ask these in order and stop at the first "yes":

1. **Does the test reference any `net.minecraft.*` or `net.fabricmc.*` class?**
   No → **Tier 1: Pure JUnit**. Write a normal `@Test`, no framework, no bootstrap. Example: `ScalingEngineTest`, `PointMathTest`, `BuildClueListTest`.

2. **Does the test need a real `ServerLevel`, tick loop, entity behavior, block placement, or redstone?**
   Yes → **Tier 3: Gametest**. Use `@GameTest` with a `GameTestHelper`. Runs on `./gradlew runGametest`.

3. **Everything else** (registries, items, components, enchantments, menus, payload codecs, mixin accessors) → **Tier 2: `fabric-loader-junit`**. Normal JUnit syntax, but Knot boots Fabric so registries come up the normal way. No `Bootstrap.bootStrap()`, no unfreeze reflection, no `forkEvery`.

Write the tier into a `// Tier: N` comment at the top of every new test file — readers should not have to infer it.

## Tier 1: pure JUnit (no changes needed)

If a test is already pure JUnit, leave it alone. No migration. Example of what qualifies:

```java
class ScalingEngineTest {
    @Test
    void timeFactor_cappedBeforeMaxLevel() {
        assertEquals(2.5, ScalingEngine.computeTimeFactor(500, 0.01, 2.5), 1e-9);
    }
}
```

No Minecraft imports → Tier 1 → done.

## Tier 2: fabric-loader-junit (the main migration target)

### Gradle setup (per companion mod)

Add to `companions/<mod>/build.gradle` under `dependencies {}`:

```gradle
testImplementation "net.fabricmc:fabric-loader-junit:${project.loader_version}"
```

`loader_version` is already in `gradle.properties` — do not hardcode it. Verify the dependency resolves with `./gradlew :companions:<mod>:dependencies --configuration testRuntimeClasspath | grep fabric-loader-junit`.

Then **delete** from the `test {}` block:

```gradle
// REMOVE — fabric-loader-junit handles classloader isolation. forkEvery = 1 was a
// workaround for Bootstrap.bootStrap() latching BuiltInRegistries for the JVM's lifetime.
forkEvery = 1
```

Leave `useJUnitPlatform()` in place.

### Test template

```java
// Tier: 2 (fabric-loader-junit)
package com.fizzlesmp.<modid>.<area>;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExampleTest {
    @Test
    void vanillaRegistriesAreAvailable() {
        // No @BeforeAll, no Bootstrap.bootStrap(), no unfreeze. Knot already booted
        // Fabric before the test class loaded.
        assertNotNull(Items.DIAMOND_SWORD);
        assertTrue(BuiltInRegistries.ITEM.containsKey(Items.DIAMOND_SWORD.arch$registryName()));
    }
}
```

For tests that need the mod's own registered content (items, blocks, menus): `FabricLoader` will have called `onInitialize` on the mod before tests run — `FizzleEnchantingRegistry.EXTRACTION_TOME` is already in the registry. No manual `register()` call required.

### Migration recipe (for each `fizzle-enchanting` test file)

Work file-by-file, but commit **per mod** not per file (see "scope" rule below).

For each test file with `Bootstrap.bootStrap()`:

1. **Delete the `@BeforeAll bootstrap()` method** entirely.
2. **Delete `unfreeze` / `unfreezeIntrusive` helpers** — private static helpers, only called from the bootstrap method.
3. **Delete any explicit `FizzleEnchantingRegistry.register()` call** — the mod's `onInitialize` runs automatically.
4. **Delete the `Field`, `IdentityHashMap` imports** if no longer used.
5. **Delete any `@BeforeAll` that builds a synthetic enchantment registry via reflection** — the real `BuiltInRegistries.ENCHANTMENT` is available via the loaded data pack. If a test needs a specific enchantment, use `holderLookup.lookup(Registries.ENCHANTMENT).orElseThrow().getOrThrow(Enchantments.SHARPNESS)`.
6. **Leave the `@Test` bodies unchanged.** Assertion semantics stay identical — only the setup is changing.
7. **Run `./gradlew :companions:<mod>:test --tests '<fully.qualified.TestName>'`** after each file. If it passes, move to the next. If it fails, stop and diagnose before converting more files.

Do **not** combine migration with test logic changes. A migration commit should only change setup boilerplate; reviewers must be able to trust that no assertion was softened.

### Before/after (real example)

Before (`ExtractionTomeFuelSlotRepairHandlerTest.java`, lines 59–77):

```java
@BeforeAll
static void bootstrap() throws Exception {
    SharedConstants.tryDetectVersion();
    Bootstrap.bootStrap();
    unfreezeIntrusive(BuiltInRegistries.BLOCK);
    unfreezeIntrusive(BuiltInRegistries.ITEM);
    unfreezeIntrusive(BuiltInRegistries.BLOCK_ENTITY_TYPE);
    unfreeze(BuiltInRegistries.MENU);
    FizzleEnchantingRegistry.register();
    BuiltInRegistries.BLOCK.freeze();
    BuiltInRegistries.ITEM.freeze();
    BuiltInRegistries.MENU.freeze();
    BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();
    enchantmentRegistry = buildEnchantmentRegistry();
}
```

After:

```java
// Knot/Fabric initializes registries and calls onInitialize before tests run.
// No bootstrap method needed.
```

Everything from `@BeforeAll` through the private `unfreeze*` / `buildEnchantmentRegistry` / `synthetic()` helpers is deleted. The enchantment registry is the real one from the loaded data pack.

## Tier 3: Fabric Gametest

Use when a test must drive a real `Level` — anvil menu end-to-end, block entity ticking, library-block neighbor updates, player interaction flows.

### Gradle setup

```gradle
loom {
    runs {
        gametest {
            inherit server
            name "Game Test"
            vmArg "-Dfabric-api.gametest"
            vmArg "-Dfabric-api.gametest.report-file=${project.buildDir}/junit.xml"
            runDir "build/gametest"
        }
    }
}

test {
    useJUnitPlatform()
    dependsOn 'runGametest'  // optional — run gametests as part of `test`
}
```

### Test template

```java
package com.fizzlesmp.<modid>.gametest;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;

public class AnvilFlowGameTest {
    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void placeAndBreakAnvil(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, Blocks.ANVIL);
        helper.assertBlockPresent(Blocks.ANVIL, pos);
        helper.succeed();
    }
}
```

Templates live under `src/main/resources/data/<modid>/gametest/structure/*.snbt`. A single `empty_<N>x<N>` template covers most cases.

Run with `./gradlew :companions:<mod>:runGametest`. Gametest failures show up in `build/junit.xml` and in the game log.

### Do not use gametest for

- Pure math or formula checks — Tier 1.
- Registry lookups, item creation, component wiring — Tier 2 is faster and has no world boot cost.
- Anything that can be expressed as "given this `ItemStack`, when I call `handler.handle(...)`, then the `AnvilResult` looks like X" — that's a Tier 2 handler test, not an integration test.

## Scope — one mod per session, one commit per mod

- **Do not** half-migrate a mod. If `fizzle-enchanting` is the target, every test in that mod is either already Tier 1 (leave alone) or becomes Tier 2. Mixed states (some files using `Bootstrap.bootStrap()`, some using fabric-loader-junit) will fail unpredictably because `forkEvery = 1` would need to stay for the unconverted files while being wrong for the new ones.
- **Do** commit the migration as a single `refactor(test)` commit per mod. Example: `refactor(test): migrate fizzle-enchanting to fabric-loader-junit`. The diff should be almost entirely deletions.
- **Do not** bundle migration with new test additions, bug fixes, or assertion changes. Review trust depends on that separation.

## Verification — before declaring the migration done

Run in order:

```bash
./gradlew :companions:<mod>:test                    # all existing tests must pass
./gradlew :companions:<mod>:test --rerun-tasks      # no flakiness from cached state
./gradlew :companions:<mod>:build                   # full build still green
```

Report the test count before and after. It must not decrease — if a test was deleted as "no longer needed," that requires a separate commit and justification.

## Guardrails — what NOT to do

- **Do not** keep `Bootstrap.bootStrap()` "just in case." If the test is Tier 2, the bootstrap call is harmful: it can double-initialize state that Knot already set up.
- **Do not** leave `forkEvery = 1` in `build.gradle` after migration. It silently makes the whole test suite ~20× slower.
- **Do not** add `@BeforeAll` setup unless it is truly shared across all `@Test` methods in the file. Per-test setup is simpler and easier to reason about.
- **Do not** use reflection on `MappedRegistry` in new code. If a test seems to need it, the test is reaching for something that belongs in Tier 3.
- **Do not** change assertion messages or expected values during migration. If an assertion is wrong, fix it in a separate commit.
- **Do not** assume a test needs Tier 2 just because it imports a Minecraft class. A class that is a pure POJO (e.g. `BlockPos`, `Component.literal`) does not require Fabric boot — try Tier 1 first.

## When asked to convert a single file

Follow the migration recipe exactly. Do not rewrite the test structure, rename methods, or reformat unrelated code. Output a diff that a reviewer can scan in 30 seconds and see "this is pure deletion of bootstrap boilerplate."

## When asked to add a new test

1. Run the decision tree. Commit to a tier before writing any code.
2. Put the `// Tier: N` comment at the top.
3. Use the matching template. If Tier 2, the test file has no `@BeforeAll` — delete the template's placeholder rather than filling it in.
4. Run the single test with `./gradlew :companions:<mod>:test --tests '<fully.qualified.TestName>'` before claiming it passes.
