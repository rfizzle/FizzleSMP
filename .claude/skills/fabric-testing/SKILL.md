---
name: fabric-testing
description: Write, modify, or migrate tests for Fabric companion mods under companions/<mod>/src/test/. Encodes the three-tier decision tree (pure JUnit / fabric-loader-junit / Fabric Gametest) and the recipe for replacing the Bootstrap.bootStrap()+unfreeze-reflection pattern. TRIGGER when a *Test.java file under companions/*/src/test/ is being created or edited, or when the user asks about testing a Fabric mod without a full server.
---

The user is writing, modifying, or migrating tests in a companion Fabric mod under `companions/<mod-name>/src/test/`. Apply this guidance whenever test code is being touched — both new tests and conversions of existing ones.

## Why this skill exists

The companion mods shipped with two incompatible test bootstrapping patterns:

- **fizzle-difficulty** — pure JUnit 5 tests, no Minecraft classes referenced. Works fine.
- **fizzle-enchanting** — uses `Bootstrap.bootStrap()` plus reflection to unfreeze `BuiltInRegistries` (`MappedRegistry.frozen` / `unregisteredIntrusiveHolders`), plus `forkEvery = 1` to avoid cross-test contamination. This is brittle and slow.

The target is **`fabric-loader-junit`** for anything that needs a vanilla registry, mixin, or AW (with an explicit `Bootstrap.bootStrap()` in `@BeforeAll` — see below), and **Fabric Gametest** for anything that needs a real `Level` or the mod's own registered content. The `unfreeze`-reflection pattern must not be used in new code.

> **Correction (from real run, 2026-04):** fabric-loader-junit's `FabricLoaderLauncherSessionListener` only initializes Knot's classloader. It does **not** call `Bootstrap.bootStrap()`, and it does **not** invoke the `main` / `ModInitializer` entrypoint. Tests that touch `BuiltInRegistries` still need `@BeforeAll Bootstrap.bootStrap()`. The mod's `onInitialize` does *not* fire, so any test that depends on the mod's own items / blocks / menus being registered can't be done cleanly at Tier 2 without the prohibited unfreeze dance — push those to Tier 3 instead.

## Decision tree — pick one tier per test

Ask these in order and stop at the first "yes":

1. **Does the test reference any `net.minecraft.*` or `net.fabricmc.*` class?**
   No → **Tier 1: Pure JUnit**. Write a normal `@Test`, no framework, no bootstrap. Example: `ScalingEngineTest`, `PointMathTest`, `BuildClueListTest`.

2. **Does the test need a real `ServerLevel`, tick loop, entity behavior, block placement, or redstone?**
   Yes → **Tier 3: Gametest**. Use `@GameTest` with a `GameTestHelper`. Runs on `./gradlew runGametest`.

3. **Everything else** (vanilla registries, enchantments, payload codecs, mixin accessors, AW-widened members) → **Tier 2: `fabric-loader-junit`** + explicit `@BeforeAll Bootstrap.bootStrap()`. Knot applies mixins/AWs; bootstrap populates the vanilla registries; you don't need the unfreeze reflection or `forkEvery` (the latter was only there because the old pattern latched state for the JVM's lifetime — with Knot + bootstrap, read-only tests are safe to share a JVM).

If the test needs to see the **mod's own registered content** (e.g. `FizzleEnchantingRegistry.EXTRACTION_TOME` in `BuiltInRegistries.ITEM`), there is no clean Tier 2 path — fabric-loader-junit does not run `onInitialize`, `Bootstrap.bootStrap()` freezes the registries, and registering post-freeze is the prohibited pattern. Push that test to Tier 3.

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

`loader_version` is already in `gradle.properties` — do not hardcode it.

**Required exclusion (with `loom.splitEnvironmentSourceSets()`):** Loom remaps the fabric-api artifacts to `*-common` variants for the main runtime classpath but leaves the raw `net.fabricmc.fabric-api:fabric-api:<ver>` sibling on `testRuntimeClasspath`. The unmapped jar carries accesswideners in the `intermediary` namespace and fabric-loader-junit aborts at session open with `AccessWidenerFormatException: line 1: Namespace (intermediary) does not match current runtime namespace (named)`. Drop the unmapped sibling:

```gradle
configurations.testRuntimeClasspath {
    exclude group: 'net.fabricmc.fabric-api', module: 'fabric-api'
}
```

The loom-remapped `*-common` variants remain on the classpath, so fabric-api code compiles and runs normally under test.

Then **delete** from the `test {}` block if present:

```gradle
// REMOVE — Knot gives each session a fresh classloader, and Bootstrap.bootStrap() is a
// no-op on subsequent calls (the `isBootstrapped` latch short-circuits). forkEvery = 1
// was only needed because the old pattern reflectively unfroze registries.
forkEvery = 1
```

Leave `useJUnitPlatform()` in place.

Verify with:

```bash
./gradlew :companions:<mod>:dependencies --configuration testRuntimeClasspath | grep fabric-loader-junit
./gradlew :companions:<mod>:test                       # should still pass before you add a Tier 2 test
```

### Test template

```java
// Tier: 2 (fabric-loader-junit)
package com.fizzlesmp.<modid>.<area>;

import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExampleTest {
    @BeforeAll
    static void bootstrapVanillaRegistries() {
        // Knot (fabric-loader-junit's session listener) sets up mixins/AWs but does not
        // call Bootstrap.bootStrap() or invoke mod entrypoints. Any read of BuiltInRegistries
        // still needs an explicit bootstrap. Safe to call from every Tier 2 test class — the
        // isBootstrapped latch inside bootStrap() short-circuits subsequent calls.
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void vanillaRegistriesAreAvailable() {
        assertNotNull(Items.DIAMOND_SWORD);
        assertTrue(BuiltInRegistries.ITEM.containsKey(BuiltInRegistries.ITEM.getKey(Items.DIAMOND_SWORD)));
    }
}
```

### What Tier 2 actually gives you

✅ **Does** — Knot classloader applies your mixins and AWs; `Bootstrap.bootStrap()` populates `Attributes`, `Items`, `BuiltInRegistries`, etc.; `Zombie.createAttributes().build()` and other entity supplier calls work; you can add `AttributeModifier`s to a real `AttributeMap` and observe the resulting `getValue()`.

❌ **Does not** — run the mod's `onInitialize`. The mod's own items / blocks / menus are **not** in `BuiltInRegistries`. Registering them after `Bootstrap.bootStrap()` requires reflective unfreeze (prohibited). If your test needs mod-registered content, it belongs in Tier 3.

### Real example: math-to-AttributeMap bridge

`ScalingEngineAttributeBridgeTest` (in fizzle-difficulty) proves the pure-math `ScalingEngine.computeAttributeFactor` result actually lands as the expected `getMaxHealth()` when applied to a real vanilla `AttributeMap`. That's the Tier 2 sweet spot: integration between computed values and vanilla attribute math, without booting a world.

### Migration recipe (for each `fizzle-enchanting` test file)

Work file-by-file, but commit **per mod** not per file (see "scope" rule below).

**First classify the test:**
- Does it only *read* vanilla content (items, enchantments, attributes)? → Migrate to Tier 2 + `Bootstrap.bootStrap()`. The unfreeze helpers and `register()` call go away.
- Does it *register* mod content via `FizzleEnchantingRegistry.register()` or similar? → Can't migrate cleanly; leave on the old pattern and revisit as a Tier 3 gametest.

For readable-by-Tier-2 test files:

1. **Keep `@BeforeAll` — but simplify it** to just `SharedConstants.tryDetectVersion(); Bootstrap.bootStrap();`. Everything else goes.
2. **Delete `unfreeze` / `unfreezeIntrusive` helpers** — only needed to register mod content, which Tier 2 can't support.
3. **Delete any explicit `FizzleEnchantingRegistry.register()` call** — if a test needed it, that test belongs in Tier 3, not Tier 2.
4. **Delete the `Field`, `IdentityHashMap` imports** that came with the reflection.
5. **Delete any `@BeforeAll` that builds a synthetic enchantment registry via reflection** — the real `BuiltInRegistries.ENCHANTMENT` is populated by `Bootstrap.bootStrap()` (vanilla enchantments) or the loaded data pack (datapack ones). For a specific enchantment use `BuiltInRegistries.ENCHANTMENT.getHolder(Enchantments.SHARPNESS).orElseThrow()`.
6. **Leave the `@Test` bodies unchanged.** Only setup is changing; assertion semantics must be byte-identical.
7. **Run `./gradlew :companions:<mod>:test --tests '<fully.qualified.TestName>'`** after each file. If it passes, move to the next. If it fails, stop and diagnose.

Do **not** combine migration with test logic changes. A migration commit should only change setup boilerplate; reviewers must trust that no assertion was softened.

### Before/after (real example)

Before (`ExtractionTomeFuelSlotRepairHandlerTest.java`, lines 59–77 — registers mod items, so *cannot* migrate cleanly to Tier 2):

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

Correct target: **Tier 3**. Spin up a gametest that exercises the full anvil-fuel-slot repair flow with a real `ServerLevel` — that's what the original test was simulating.

For a test that only reads vanilla enchantments (the cleanly-migratable case), the after looks like:

```java
@BeforeAll
static void bootstrapVanillaRegistries() {
    SharedConstants.tryDetectVersion();
    Bootstrap.bootStrap();
}
```

Everything after those two lines in the original `@BeforeAll`, plus all the private `unfreeze*` / `buildEnchantmentRegistry` / `synthetic()` helpers, can be deleted.

## Tier 3: Fabric Gametest

Use when a test must drive a real `Level` — anvil menu end-to-end, block entity ticking, library-block neighbor updates, player interaction flows, or any case where the mod's own registered content has to be present.

### Gradle setup — full pattern

Three pieces have to be wired, and **order in `build.gradle` matters** (the `loom { runs { gametest { source sourceSets.gametest } } }` block is evaluated eagerly, so the `sourceSets` definition has to come first):

```gradle
loom {
    splitEnvironmentSourceSets()
}

sourceSets {
    gametest {
        compileClasspath += sourceSets.main.compileClasspath + sourceSets.main.output
        runtimeClasspath += sourceSets.main.runtimeClasspath + sourceSets.main.output
    }
}

configurations {
    gametestImplementation.extendsFrom implementation
    gametestRuntimeOnly.extendsFrom runtimeOnly
}

loom {
    runs {
        gametest {
            server()
            name "Game Test"
            source sourceSets.gametest
            vmArg "-Dfabric-api.gametest"
            vmArg "-Dfabric-api.gametest.report-file=${layout.buildDirectory.file('junit-gametest.xml').get().asFile}"
            runDir "build/gametest"
        }
    }
}
```

A dedicated `gametest` source set keeps test Java out of the production jar. The SNBT template **has to live in `src/main/resources`** (see "Template location" below), but that's fine — templates are small inert data files.

Register the gametest class via a `fabric-gametest` entrypoint in `src/main/resources/fabric.mod.json`:

```json
"entrypoints": {
    "main": ["com.fizzlesmp.<modid>.<Mod>"],
    "fabric-gametest": ["com.fizzlesmp.<modid>.gametest.<SomeGameTest>"]
}
```

The entrypoint only fires under `-Dfabric-api.gametest`, so declaring it in production is harmless — FabricLoader lazy-loads the class and never resolves it outside gametest runs.

### Test template

```java
// Tier: 3 (Fabric Gametest)
package com.fizzlesmp.<modid>.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

public class AnvilFlowGameTest implements FabricGameTest {
    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void placeAndBreakAnvil(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, Blocks.ANVIL);
        helper.assertBlockPresent(Blocks.ANVIL, pos);
        helper.succeed();
    }
}
```

Import `net.minecraft.gametest.framework.GameTest` (vanilla) for the annotation; the Fabric package provides `FabricGameTest` (interface) and support classes, but the `@GameTest` method annotation itself is vanilla-sourced in 1.21.1.

### Template location

Structure templates go under `src/main/resources/data/<modid>/gametest/structure/<name>.snbt` — **two subdirectories, singular "structure"**. This is the path the Fabric gametest API's `StructureTemplateManagerMixin` resolves (via `FabricGameTestHelper.GAMETEST_STRUCTURE_FINDER`). Do not be fooled by the vanilla error message `"Missing test structure: <id>"` — the token `"gameteststructures"` appears in vanilla `StructureUtils` but is only used in the error line, not as the actual resource path.

Templates **must** be in `src/main/resources` (the main mod's data pack), not in `src/gametest/resources`. The gametest source set's resources are not loaded as part of the `<modid>` data pack at runtime, so a template there is invisible to the structure manager.

A minimal 3×3×3 empty-air-over-stone template (a single file covers almost every test):

```snbt
{
  DataVersion: 3955,
  size: [3, 3, 3],
  entities: [],
  blocks: [
    {pos: [0, 0, 0], state: 1}, {pos: [1, 0, 0], state: 1}, {pos: [2, 0, 0], state: 1},
    {pos: [0, 0, 1], state: 1}, {pos: [1, 0, 1], state: 1}, {pos: [2, 0, 1], state: 1},
    {pos: [0, 0, 2], state: 1}, {pos: [1, 0, 2], state: 1}, {pos: [2, 0, 2], state: 1}
  ],
  palette: [
    {Name: "minecraft:air"},
    {Name: "minecraft:stone"}
  ]
}
```

### Running

```bash
./gradlew :companions:<mod>:runGametest
```

On success you'll see `All N required tests passed :)` in the log. The `junit-gametest.xml` report lands in `build/` and can be wired into CI. On failure, Minecraft writes a crash report under `build/gametest/crash-reports/`.

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

- **Do not** skip `Bootstrap.bootStrap()` in a Tier 2 test that touches `BuiltInRegistries`. Knot does not call it for you; skipping produces `IllegalArgumentException: Not bootstrapped` at first registry access.
- **Do not** leave `forkEvery = 1` in `build.gradle` after migration. It silently makes the whole test suite ~20× slower. `Bootstrap.bootStrap()` is idempotent (the `isBootstrapped` latch short-circuits), so forking is unnecessary overhead.
- **Do not** try to register the mod's own items / menus / block entities in a Tier 2 `@BeforeAll`. After `Bootstrap.bootStrap()` the registries are frozen, and reflectively unfreezing them is the prohibited pattern. Route those tests to Tier 3.
- **Do not** use reflection on `MappedRegistry` in new code. If a test seems to need it, the test is reaching for something that belongs in Tier 3.
- **Do not** change assertion messages or expected values during migration. If an assertion is wrong, fix it in a separate commit.
- **Do not** assume a test needs Tier 2 just because it imports a Minecraft class. A class that is a pure POJO (e.g. `BlockPos`, `Component.literal`, `RandomSource` as an interface) does not require Fabric boot — try Tier 1 first.
- **Do not** widen production method access (e.g. flipping a `static void` to `public static void`) to make a gametest reach it. Either move the gametest into the same package, use public surface area, or test the observable behavior instead.

## When asked to convert a single file

Follow the migration recipe exactly. Do not rewrite the test structure, rename methods, or reformat unrelated code. Output a diff that a reviewer can scan in 30 seconds and see "this is pure deletion of bootstrap boilerplate."

## When asked to add a new test

1. Run the decision tree. Commit to a tier before writing any code.
2. Put the `// Tier: N` comment at the top.
3. Use the matching template. If Tier 2, the test file has no `@BeforeAll` — delete the template's placeholder rather than filling it in.
4. Run the single test with `./gradlew :companions:<mod>:test --tests '<fully.qualified.TestName>'` before claiming it passes.
