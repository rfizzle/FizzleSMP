The user is working on a companion Fabric mod in the FizzleSMP modpack. Apply the following guidance for all development work.

## Target Platform

- **Minecraft:** 1.21.1
- **Mod Loader:** Fabric
- **Java Version:** 21
- **Fabric Loader:** 0.19.2+
- **Fabric API:** 0.116.11+1.21.1
- **Mappings:** Official Mojang mappings (`loom.officialMojangMappings()`)

## Project Location

All companion mods live under `companions/<mod-name>/` as standalone Gradle projects. Build with `./scripts/build-companions.sh <mod-name>`.

## Code Style & Conventions

### Package Structure
```
com.rfizzle.<modid>/
├── <ModName>.java              # ModInitializer entrypoint
├── config/                     # Configuration classes
├── command/                    # Command registration
├── event/                      # Event handlers
├── mixin/                      # Mixin classes (dedicated package)
├── data/                       # Persistent state, data models
└── util/                       # Shared utilities
```

### Naming
- Classes suffixed by type: `ZombieScalingHandler`, `DifficultyConfig`, `ScaleCommand`
- Mod ID: lowercase, underscores only (e.g., `tribulation`)
- Constants: `public static final` in the main mod class for MOD_ID and LOGGER
- Attribute modifier IDs: `Identifier.of(MOD_ID, "descriptive_name")`

### Logging
- Use SLF4J: `LoggerFactory.getLogger(MOD_ID)`
- `LOGGER.info()` for startup/milestones only
- `LOGGER.warn()` for recoverable issues
- `LOGGER.error("message", exception)` — always pass the exception object
- `LOGGER.debug()` for dev diagnostics
- Use parameterized messages (`"{}"`) not string concatenation

### Configuration
- Use bundled GSON (no extra dependency) for server-side mods
- Config file goes in `FabricLoader.getInstance().getConfigDir()`
- Config class has public fields with defaults, a static `load()` and instance `save()`
- Validate values on load (clamp out-of-range, log warnings for corrections)

### Error Handling
- Never silently swallow exceptions — log them
- Null-check `getAttribute()` results — entities may lack attributes
- Wrap config I/O and file operations in try-catch
- Fail gracefully: a broken config should fall back to defaults, not crash the server

## Fabric API Patterns

### Events (prefer over Mixins when available)
- `ServerEntityEvents.ENTITY_LOAD` — entity enters a ServerWorld (including spawns)
- `ServerLivingEntityEvents.AFTER_DEATH` — after entity death
- `ServerTickEvents.END_SERVER_TICK` — periodic tasks via tick counter
- `ServerLifecycleEvents.SERVER_STARTED` / `SERVER_STOPPED` — lifecycle hooks
- `CommandRegistrationCallback.EVENT` — command registration
- `ServerPlayerEvents.AFTER_RESPAWN` — player respawn handling

### Attribute Modifiers (1.21.1 API)
```java
// 1.21.1: Identifier-based, NOT UUID-based
new EntityAttributeModifier(
    Identifier.of(MOD_ID, "modifier_name"),
    amount,
    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
);

// Always remove before re-adding to avoid stacking
attribute.removeModifier(modifierId);
attribute.addPersistentModifier(modifier);  // survives restart
attribute.addTemporaryModifier(modifier);   // lost on unload
```

Operations:
- `ADD_VALUE` — flat addition to base
- `ADD_MULTIPLIED_BASE` — percentage of base value (applied after ADD_VALUE)
- `ADD_MULTIPLIED_TOTAL` — multiplies final total (applied last)

### Persistent State
- Extend `PersistentState` for data that must survive restarts
- Always call `markDirty()` after modifications
- Attach to the Overworld: `server.getWorld(World.OVERWORLD).getPersistentStateManager()`

### Commands
- Use Brigadier via `CommandRegistrationCallback.EVENT`
- Require appropriate permission level: `.requires(source -> source.hasPermissionLevel(2))`
- Return `Command.SINGLE_SUCCESS` on success

### Tick-Based Tasks
```java
private int tickCounter = 0;
ServerTickEvents.END_SERVER_TICK.register(server -> {
    if (++tickCounter >= INTERVAL_TICKS) {
        tickCounter = 0;
        // periodic work
    }
});
```

## Mixin Guidelines
- Prefer Fabric API events over Mixins whenever possible
- Accessor/invoker mixins are the safest type
- Prefix accessor methods with mod ID: `modid$getField()`
- Use `@Inject` for hooks; avoid `@Redirect` (only one mod can redirect a call site)
- Keep mixin classes in a dedicated `mixin` package
- `compatibilityLevel` must be `JAVA_21`

## Testing

### Unit Tests
- Use `fabric-loader-junit` (same version as loader)
- Test config parsing, math/formula logic, utility functions without Minecraft bootstrap
- For registry-dependent tests, call `SharedConstants.tryDetectVersion()` and `Bootstrap.bootStrap()` in `@BeforeAll`

### Test Organization
```
src/test/java/com/rfizzle/<modid>/
├── config/         # Config load/save/validation tests
├── scaling/        # Formula and calculation tests
└── ...
```

### What to Test
- Config defaults are valid and within expected ranges
- Config round-trips (save then load produces same values)
- Scaling formulas produce correct values at key breakpoints (level 0, 50, 100, 150, 200, 250)
- Attribute caps are respected
- Edge cases: level 0, max level, negative values, missing config fields

## Build & Integration

### fabric.mod.json
```json
{
    "schemaVersion": 1,
    "id": "<modid>",
    "version": "${version}",
    "environment": "*",
    "entrypoints": {
        "main": ["com.rfizzle.<modid>.<ModName>"]
    },
    "depends": {
        "fabricloader": ">=0.19.2",
        "minecraft": "~1.21.1",
        "java": ">=21",
        "fabric-api": "*"
    }
}
```

### Gradle (build.gradle)
- Use `net.fabricmc.fabric-loom-remap` plugin
- `loom.splitEnvironmentSourceSets()` for client/server separation
- Source/target compatibility: `JavaVersion.VERSION_21`
- `options.release = 21`

### 1.21.1-Specific Gotchas
- `new Identifier()` is gone — use `Identifier.of("namespace", "path")`
- Enchantments are data-driven — use `EnchantmentHelper` + tags, not class references
- Data pack paths are singular now: `tags/block` not `tags/blocks`
- Register everything during `ModInitializer.onInitialize()`, not lazily — registries freeze early

## Quality Checklist

Before considering a companion mod complete:
- [ ] All config values validated on load with sane defaults
- [ ] Unit tests pass for all formulas and config handling
- [ ] No silent exception swallowing
- [ ] Attribute modifiers use unique, namespaced Identifiers
- [ ] Modifiers removed before re-application (no stacking bugs)
- [ ] Commands require appropriate permission levels
- [ ] fabric.mod.json has correct depends/breaks/environment
- [ ] LOGGER used appropriately (not spamming on every tick)
- [ ] Mixin usage minimized; Fabric API events preferred
- [ ] Plugin entry added to appropriate `plugins/*.md` file
