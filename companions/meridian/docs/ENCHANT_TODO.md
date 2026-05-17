# Enchantment Reimplementation TODO

Tracks every task needed to implement the enchantment spec in
`docs/ENCHANT_SPEC.md`. Check items as completed.

---

## Phase 0: Infrastructure

- [x] Wipe all files in `src/main/resources/data/meridian/enchantment/`
- [x] Wipe all generated enchantment tags in `src/main/generated/data/meridian/tags/enchantment/`
- [x] Remove all `enchantment.meridian.*` keys from `src/main/resources/assets/meridian/lang/en_us.json`
- [x] Add `enabled` boolean (default `true`) to `EnchantmentOverride` in `MeridianConfig.java`
- [x] Wire `enabled` into `EnchantmentInfoRegistry.rebuild()` — skip disabled enchantments when populating the active registry
- [x] Wire `enabled` into enchanting table mixin — disabled enchantments never offered
- [x] Wire `enabled` into loot table injection — disabled enchantments never roll
- [x] Add `enabled` field to `EnchantmentInfoPayload` serialization — sync S2C
- [x] Update `ClientPayloadHandlers` to read the `enabled` field
- [x] Update `EnchantingInfoScreen` — hide disabled enchantments from the browser, adjust slot tab counts
- [x] Update `EnchantmentLibraryScreen` — grey out extraction for disabled enchantments, block new deposits, show "disabled by server" tooltip
- [x] Update `OverLeveledTooltipHandler` — suppress tooltip lines for disabled enchantments
- [x] Update `InlineEnchDescTooltipHandler` — skip `.desc` lines for disabled enchantments
- [x] Suppress enchantment glint on items that have only disabled enchantments
- [x] Update `MeridianConfig.validate()` to handle `enabled` field defaults
- [x] Document `enabled` field in `docs/CONFIG.md` with examples

### Exclusive set tags

Create the following tag files:

- [x] Append Meridian members to `minecraft:exclusive_set/damage` tag (Voidbane, Sanctify, Sentinel, Rift Strike, Keen Edge)
- [x] Append Spellguard to `minecraft:exclusive_set/armor` tag
- [x] Append Cinderwalk to `minecraft:exclusive_set/boots` tag
- [x] Append Tempest, Seismic Slam, Updraft to `minecraft:exclusive_set/mace` tag
- [x] Create `meridian:exclusive_set/aspect` tag (Blight, Decay, Shackle, Nightfall, vanilla Fire Aspect)
- [x] Create `meridian:exclusive_set/arrow_impact` tag (Gale Shot, Resonance, Permafrost, Detonation, Stormcall)
- [x] Create `meridian:exclusive_set/size` tag (Diminish, Colossus)
- [x] Create `meridian:exclusive_set/mining` tag (Excavate, Prospect)
- [x] Create `meridian:exclusive_set/glass_cannon` tag (Bloodrage, Reckless)
- [x] Create `meridian:exclusive_set/mending` tag (Vital Mend, vanilla Mending)

### Entity type tags (for mob-type damage enchantments)

- [x] Create `meridian:sensitive_to_voidbane` entity type tag (Ender mobs)
- [x] Create `meridian:sensitive_to_sanctify` entity type tag (Nether mobs)
- [x] Create `meridian:sensitive_to_sentinel` entity type tag (Illagers)

### Damage type tags

- [x] Create `meridian:is_magic` damage type tag (for Spellguard)

---

## Phase 1: Data-driven enchantments (JSON only)

These enchantments use only vanilla `EnchantmentEffectComponents` and need no
custom Java beyond the JSON definition and lang keys.

For each: create `data/meridian/enchantment/<id>.json`, add name + `.desc` lang
keys, and verify via the enchanting table in-game.

### Attribute-based (simplest)

- [x] **Tempo** (`tempo`) — `minecraft:attributes` → `generic.attack_speed`, add_value. Wt 5, Lvl 1–2. Primary: sword, Supported: weapon.
- [x] **Outreach** (`outreach`) — `minecraft:attributes` → `player.entity_interaction_range`, add_value. Wt 5, Lvl 2–3. Primary: sword, Supported: weapon.
- [x] **Bulwark** (`bulwark`) — `minecraft:attributes` → `generic.knockback_resistance`, add_value. Wt 10, Lvl 1–3. Supported: armor.
- [x] **Vitality** (`vitality`) — `minecraft:attributes` → `generic.max_health`, add_value. Wt 5, Lvl 3–5. Supported: armor.
- [x] **Alacrity** (`alacrity`) — `minecraft:attributes` → `generic.movement_speed`, add_value. Wt 10, Lvl 3–5. Primary: boots, Supported: boots + leggings.
- [x] **Vault** (`vault`) — `minecraft:attributes` → `generic.jump_strength`, add_value. Wt 5, Lvl 2–3. Supported: boots.
- [x] **Clamber** (`clamber`) — `minecraft:attributes` → `generic.step_height`, add_value. Wt 10, Lvl 1–3. Supported: boots.
- [x] **Mason's Reach** (`masons_reach`) — `minecraft:attributes` → `player.block_interaction_range`, add_value. Wt 5, Lvl 2–3. Supported: chestplate.

### Damage-conditional (entity type predicates)

- [x] **Voidbane** (`voidbane`) — `minecraft:damage` with `entity_properties` predicate → `#meridian:sensitive_to_voidbane`. Exclusive set: `minecraft:exclusive_set/damage`. Wt 5, Lvl 3–5.
- [x] **Sanctify** (`sanctify`) — `minecraft:damage` with `entity_properties` predicate → `#meridian:sensitive_to_sanctify`. Exclusive set: `minecraft:exclusive_set/damage`. Wt 5, Lvl 3–5.
- [x] **Sentinel** (`sentinel`) — `minecraft:damage` with `entity_properties` predicate → `#meridian:sensitive_to_sentinel`. Exclusive set: `minecraft:exclusive_set/damage`. Wt 5, Lvl 3–5.
- [x] **Rift Strike** (`rift_strike`) — `minecraft:damage` with dimension predicate (not `minecraft:overworld`). Exclusive set: `minecraft:exclusive_set/damage`. Wt 2, Lvl 3–5.

### On-hit status effects (post_attack → apply_mob_effect)

- [x] **Shackle** (`shackle`) — `minecraft:post_attack` → apply Slowness. Exclusive set: `meridian:exclusive_set/aspect`. Wt 5, Lvl 1–3. Primary: sword, Supported: weapon.
- [x] **Blight** (`blight`) — `minecraft:post_attack` → apply Poison. Exclusive set: `meridian:exclusive_set/aspect`. Wt 5, Lvl 2–3. Primary: sword, Supported: weapon.
- [x] **Decay** (`decay`) — `minecraft:post_attack` → apply Wither. Exclusive set: `meridian:exclusive_set/aspect`. Wt 2, Lvl 2–3. Primary: sword, Supported: weapon.
- [x] **Nightfall** (`nightfall`) — `minecraft:post_attack` → apply Darkness. Exclusive set: `meridian:exclusive_set/aspect`. Wt 2, Lvl 1. Primary: sword, Supported: weapon.

### XP multipliers

- [x] **Insight** (`insight`) — `minecraft:mob_experience` multiply. Wt 10, Lvl 2–3. Primary: sword, Supported: weapon.
- [x] **Animus** (`animus`) — `minecraft:mob_experience` + `minecraft:block_experience` multiply. Wt 5, Lvl 1–3. Supported: armor.

### Protection-style

- [x] **Spellguard** (`spellguard`) — `minecraft:damage_protection` with `#meridian:is_magic` damage tag predicate. Exclusive set: `minecraft:exclusive_set/armor`. Wt 5, Lvl 2–4. Supported: armor.
- [x] **Impact Ward** (`impact_ward`) — `minecraft:damage_protection` for `fly_into_wall` damage type (uses `#minecraft:is_fall` tag). Wt 5, Lvl 3–5. Supported: elytra.
- [x] **Ironwing** (`ironwing`) — `minecraft:damage_protection` while entity `is_fall_flying`. Wt 5, Lvl 2–4. Supported: elytra.

### Durability modification

- [x] **Curse of Decay** (`curse_of_decay`) — `minecraft:item_damage` multiply (increase). Treasure, Wt 2. Lvl 3–5. Supported: durability.

### Size-based (attribute bundles)

- [x] **Diminish** (`diminish`) — `minecraft:attributes` → `generic.scale` (decrease), `generic.movement_speed` (decrease), `generic.step_height` (increase). Exclusive set: `meridian:exclusive_set/size`. Wt 1, Lvl 3–5. Supported: leggings.
- [x] **Colossus** (`colossus`) — `minecraft:attributes` → `generic.scale` (increase), `generic.movement_speed` (decrease), `player.entity_interaction_range` (decrease). Exclusive set: `meridian:exclusive_set/size`. Wt 1, Lvl 2–4. Supported: leggings.

### Damage-based (flat/conditional)

- [x] **Keen Edge** (`keen_edge`) — `minecraft:damage` with random chance for bonus damage. Exclusive set: `minecraft:exclusive_set/damage`. Wt 2, Lvl 3–4. Implemented as flat bonus with scaling chance (no armor bypass — deferred to custom Java if needed).

### Reckless (attribute trade-off)

- [x] **Reckless** (`reckless`) — `minecraft:attributes` → `generic.armor` (decrease) + `generic.attack_damage` (increase). Exclusive set: `meridian:exclusive_set/glass_cannon`. Wt 1, Lvl 2–3. Supported: armor.

---

## Phase 2: Custom Java enchantments

These need event handlers, mixins, or custom effect component logic beyond what
vanilla JSON supports. Implement the Java code, then create the JSON definition
and lang keys.

### Sword / Weapon

- [x] **Quell** (`quell`) — Intercept creeper fuse ignition; delay/reset when hit by a weapon with this enchant. Mixin on `CreeperEntity` or `MobEntity` damage handler. Wt 5, Lvl 1.
- [x] **Final Gambit** (`final_gambit`) — On attack, destroy the weapon and deal massive damage (scale with remaining durability?). Custom attack handler. Treasure, Wt 1, Lvl 1. *Design decision: trigger on next hit always, or require activation (sneak+attack)?*
- [x] **Siphon** (`siphon`) — Chance on hit to heal attacker. `minecraft:post_attack` may support `heal` effect; if not, custom handler. Wt 2, Lvl 2–3. Primary: sword, Supported: weapon.
- [x] **Snare** (`snare`) — On kill, chance to drop the mob's spawn egg. Custom loot modifier or death event handler. Need to map entity types to spawn egg items. Treasure, Wt 1, Lvl 1.
- [x] **Soul Tax** (`soul_tax`) — On hit, drain player XP to add bonus damage. Custom attack handler that checks XP levels. Wt 2, Lvl 2–3.

### Axe

- [x] **Cleave** (`cleave`) — On hit, deal damage to nearby entities in an arc. Custom attack handler with AoE detection (exclude the primary target, respect friendly fire settings). Wt 2, Lvl 1–3. *Design decision: arc angle, range scaling with level.*

### Bow / Ranged

All bow enchants need a custom projectile handler that runs on arrow/bolt impact.
Consider a shared `MeridianProjectileImpactHandler` base.

- [x] **True Flight** (`true_flight`) — Set projectile `noGravity` on launch when bow has this enchant. Mixin on `AbstractArrowEntity` or `ProjectileEntity`. Wt 2, Lvl 1. Primary: bow, Supported: bow + crossbow.
- [x] **Gale Shot** (`gale_shot`) — On impact, emit wind-charge-like knockback burst. Spawn a `WindChargeEntity` effect or replicate its AoE. Wt 5, Lvl 1–2. Primary: bow, Supported: bow + crossbow. Exclusive set: `meridian:exclusive_set/arrow_impact`.
- [x] **Resonance** (`resonance`) — On impact, deal AoE damage in a radius (sonic boom visual). Wt 2, Lvl 1–2. Exclusive set: `meridian:exclusive_set/arrow_impact`.
- [x] **Permafrost** (`permafrost`) — On impact, convert water to ice in radius, apply Slowness to nearby entities. Wt 5, Lvl 1. Exclusive set: `meridian:exclusive_set/arrow_impact`.
- [x] **Detonation** (`detonation`) — On impact, create explosion (scaling radius per level, no block damage by default). Wt 1, Lvl 2–4. Exclusive set: `meridian:exclusive_set/arrow_impact`. *Design decision: block damage toggle in config?*
- [x] **Ricochet** (`ricochet`) — On block hit, reflect projectile at a new angle with reduced velocity. Track bounce count to cap recursion. Wt 2, Lvl 2–3. *Bounces scale with level.*
- [x] **Stormcall** (`stormcall`) — On impact, summon `LightningEntity` at hit position. Wt 2, Lvl 1. Exclusive set: `meridian:exclusive_set/arrow_impact`.

### Trident

- [x] **Glacial Lance** (`glacial_lance`) — On trident hit, freeze water in radius and apply Slowness. Custom trident impact handler. Wt 2, Lvl 2–3.

### Mace

All three mace enchants trigger on ground-slam impact. Hook into the mace's
existing ground-slam detection (the `fall_distance > 1.5` smash attack).

- [x] **Tempest** (`tempest`) — On slam during thunderstorm, summon lightning at impact + grant brief lightning immunity. Wt 2, Lvl 1–2. Exclusive set: `minecraft:exclusive_set/mace`.
- [x] **Seismic Slam** (`seismic_slam`) — On crouch+slam, emit shockwave that damages and knocks back nearby entities. Wt 2, Lvl 1. Exclusive set: `minecraft:exclusive_set/mace`. *Design decision: require crouch, or trigger on all slams?*
- [x] **Updraft** (`updraft`) — On slam, launch the wielder upward (wind burst variant). Wt 5, Lvl 2–3. Exclusive set: `minecraft:exclusive_set/mace`.

### Helmet

- [x] **Luminance** (`luminance`) — Tick handler: apply Night Vision while helmet equipped. Remove on unequip. Wt 5, Lvl 1. *Note: Night Vision flickers near expiry — re-apply with duration > 10 seconds on each tick to avoid flicker, or use 300+ tick duration.*
- [x] **Abyss Ward** (`abyss_ward`) — Intercept void damage; on first trigger, apply Levitation for ~3 seconds, then set a cooldown (track via item NBT or player persistent data). One-time save per life/respawn. Treasure, Wt 1, Lvl 1.
- [x] **Premonition** (`premonition`) — Tick handler: scan for hostile mobs within radius (~16 blocks?), apply Glowing effect. Wt 2, Lvl 1. *Performance: use scheduled tick interval, not every tick. Consider entity caching.*

### Chestplate

- [x] **Repulse** (`repulse`) — When wearer takes melee damage, knock the attacker back. Scale knockback strength with level. Custom damage handler. Wt 2, Lvl 1–3. Primary: chestplate, Supported: chestplate + leggings.
- [x] **Frostguard** (`frostguard`) — When wearer takes melee damage, apply Slowness to attacker. Custom damage handler or `minecraft:post_attack` on `victim` side (verify 1.21.1 supports armor-side post_attack). Wt 2, Lvl 2–3. Primary: chestplate, Supported: armor.
- [x] **Rally** (`rally`) — When health drops below 20%, grant Regeneration II for a few seconds. Track cooldown (configurable, suggest 5 minutes) via persistent player data. Treasure, Wt 1, Lvl 1–2. *Level scales regen duration or cooldown reduction.*

### Armor (any slot)

- [x] **Bloodrage** (`bloodrage`) — On taking damage, apply Resistance + Strength + Speed for short duration, but take additional health cost (instant damage or drain). Custom damage handler. Exclusive set: `meridian:exclusive_set/glass_cannon`. Wt 1, Lvl 1–3. Primary: chestplate, Supported: armor.
- [x] **Antidote** (`antidote`) — Reduce duration of negative potion effects applied to wearer. Mixin on `LivingEntity.addStatusEffect()` or tick-based duration reduction. Wt 5, Lvl 1. *Design decision: reduce duration on application, or accelerate expiry?*
- [x] **Gravitas** (`gravitas`) — Increase item pickup radius attribute. Wt 5, Lvl 1–3. *Note: check if `generic.pickup_reach` exists in 1.21.1. If not, custom tick handler that teleports nearby items to player.*

### Leggings / Boots

- [x] **Slipstream** (`slipstream`) — Tick handler: apply Dolphin's Grace while worn and in water. Wt 2, Lvl 1.
- [x] **Cinderwalk** (`cinderwalk`) — Tick handler: solidify lava under the wearer into obsidian/basalt (temporary, reverts after delay). Mirror Frost Walker logic for lava. Treasure, Wt 2, Lvl 2–3. Exclusive set: `minecraft:exclusive_set/boots`.
- [x] **Steadfast** (`steadfast`) — Negate the mining speed penalty while not on ground. Mixin on `PlayerEntity.getBlockBreakingSpeed()` or mining speed event. Wt 2, Lvl 1.

### Tools / Hoe

- [x] **Excavate** (`excavate`) — On block break, also break surrounding blocks in a 3×3 (perpendicular to mined face). Custom block break handler. Respect block hardness, tool tier, and enchantment compatibility (Fortune/Silk Touch propagate). Wt 2, Lvl 1. Primary: pickaxe, Supported: pickaxe + shovel. Exclusive set: `meridian:exclusive_set/mining`. *Performance: batch block break events to avoid recursive triggers.*
- [x] **Prospect** (`prospect`) — On ore break, flood-fill connected same-type ore blocks and break them. Cap max vein size (32? 64?). Same tool/enchantment propagation as Excavate. Wt 2, Lvl 1. Exclusive set: `meridian:exclusive_set/mining`. *Performance: iterative BFS, not recursive. Cap per tick if vein is huge.*
- [x] **Bounty** (`bounty`) — On crop harvest (right-click or break mature crop), replant + harvest crops in radius. Scale radius with level. Wt 5, Lvl 2–3. *Design decision: right-click harvest only, or also break?*
- [x] **Furrow** (`furrow`) — On hoe-till, also till surrounding blocks in radius. Scale with level. Wt 5, Lvl 2–3.
- [x] **Beckon** (`beckon`) — Tick handler: nearby farm animals (cows, pigs, sheep, chickens) in ~8 block radius are attracted to the player while hoe is held in main hand. Use temptation goal logic. Wt 10, Lvl 1.
- [x] **Terrasculpt** (`terrasculpt`) — Tick handler: blocks the player walks over convert to natural terrain (dirt → grass, cobble → stone, etc.). Define conversion table. Wt 2, Lvl 1.

### Mounted

Mounted enchantments go on horse armor. Need to read enchantments from the
horse's armor slot and apply effects to the mount entity.

- [x] **Gallop** (`gallop`) — `minecraft:attributes` on the mount → `generic.movement_speed`, add_value. May need custom handler to apply attributes from horse armor to the horse entity. Wt 5, Lvl 2–4.
- [x] **Trample** (`trample`) — Mount's attacks deal bonus damage. Hook into mount attack events or override `HorseEntity` attack damage attribute. Wt 5, Lvl 2–3.
- [x] **Skybound** (`skybound`) — Increase mount jump strength + reduce mount fall damage. Custom mount jump handler. Wt 2, Lvl 3–7.
- [x] **Saddleguard** (`saddleguard`) — Rider takes reduced damage while mounted. Custom damage handler that checks if the player is riding. Wt 5, Lvl 3–5.

### Shield

- [x] **Retribution** (`retribution`) — When blocking damage, chance to reflect a portion back to the attacker. Custom shield block handler. Wt 1, Lvl 3–5.
- [x] **Pummel** (`pummel`) — Shield attacks (left-click while shield in offhand? or bash keybind) deal bonus damage and consume shield durability. Custom attack handler. Wt 2, Lvl 2–4. *Design decision: how is shield bash triggered in vanilla 1.21.1? If no native bash, define the trigger.*
- [x] **Fortify** (`fortify`) — Blocking absorbs more damage and costs less durability. Mixin on shield damage blocking logic. Wt 5, Lvl 1–3.

### Shears

- [x] **Prismatic** (`prismatic`) — On shear, replace wool drop color with a random `DyeColor`. Hook into shear event or mixin on `SheepEntity.sheared()`. Wt 5, Lvl 1.
- [x] **Renewal** (`renewal`) — On shear, chance for the sheep to immediately regrow wool (`SheepEntity.setSheared(false)`). Wt 2, Lvl 1.

### Durability (any item)

- [x] **Vital Mend** (`vital_mend`) — Intercept healing events; convert heal amount to durability repair on equipped items with this enchant. Mixin on `LivingEntity.heal()`. Treasure, Wt 1, Lvl 1–3. Exclusive set: `meridian:exclusive_set/mending`.
- [x] **Plunder** (`plunder`) — On mob kill, chance to duplicate the loot table drops. Hook into `LivingEntity.dropLoot()` or loot event. Wt 1, Lvl 1–3. Primary: sword, Supported: weapon.
- [x] **Tether** (`tether`) — On player death, items with this enchant stay in inventory instead of dropping. Hook into `PlayerEntity.dropInventory()` or death event. Treasure, Wt 1, Lvl 1.

### Curses / Misc

- [x] **Curse of Sealing** (`curse_of_sealing`) — Prevent the item from being enchanted, combined, or modified at an anvil or enchanting table. Mixin on anvil and enchanting table logic. Treasure, Wt 1, Lvl 1.
- [x] **Aurify** (`aurify`) — Right-click a block to chance-convert it to gold block/gold ore (based on block type). Custom use handler. Define conversion table (stone → gold ore, dirt → gold block, etc.). Treasure, Wt 1, Lvl 1. *Design decision: cooldown? Durability cost?*

---

## Phase 3: Lang keys

- [x] Add `enchantment.meridian.<id>` for all 75 enchantments
- [x] Add `enchantment.meridian.<id>.desc` for all 75 enchantments (short, unique description text — do not reuse wording from any other mod)
- [x] Remove all old `enchantment.meridian.*` keys that reference deleted IDs

---

## Phase 4: Testing

Write a test for each enchantment. Test tiers:

**Unit tests** (fabric-loader-junit, no server):
- [x] Verify each enchantment JSON loads without errors
- [x] Verify exclusive set memberships are correct (enchantments reference the right tags)
- [x] Verify treasure enchantments have weight set correctly and are not table-rollable
- [x] Verify primary vs supported item tags are correct
- [x] Verify lang keys exist for all 75 enchantments (name + desc)

**Integration tests** (Fabric Gametest, in-world):
For each enchantment, verify:
- [x] Effect fires correctly (e.g., Blight applies Poison, Bulwark adds knockback resistance)
- [x] Effect respects level scaling (e.g., Shackle Slowness duration increases with level)
- [x] Exclusive set is enforced (e.g., can't combine Blight + Decay via anvil)
- [x] `enabled: false` config flag disables the effect in-world
- [x] Disabled enchantment on existing item has no effect
- [x] Disabled enchantment is hidden from enchanting table
- [x] Disabled enchantment is hidden from tooltips

**Specific edge case tests:**
- [x] Abyss Ward: triggers only once per life, cooldown resets on respawn
- [x] Rally: cooldown timer persists through dimension changes
- [x] Excavate/Prospect: no recursive trigger (mining broken block doesn't re-trigger the enchant)
- [x] Excavate/Prospect: Fortune/Silk Touch propagates to area-mined blocks
- [x] Ricochet: bounce count capped, no infinite loops
- [x] Detonation: no block damage by default (if configured)
- [x] Tether: item survives death, doesn't duplicate
- [x] Vital Mend + Mending: can't coexist (exclusive set enforced)
- [x] Cinderwalk + Frost Walker: can't coexist (exclusive set enforced)
- [x] Snare: spawn egg drops are vanilla-correct item IDs
- [x] Premonition: performance — doesn't lag with many entities
- [x] Final Gambit: weapon is destroyed after the hit, not before

---

## Phase 5: Documentation

- [x] Rewrite `docs/ENCHANTMENTS.md` — full table of all 75 enchantments with new names, IDs, levels, descriptions. Remove all origin/source columns.
- [x] Update `docs/CONFIG.md` — document `enabled` field with examples, update any enchantment ID references to new IDs
- [x] Update `README.md` — remove attribution to NeoEnchant+/Zenith/Enchantology, state all enchantment data is original to Meridian
- [x] Update `LICENSE` if needed — ensure no claims of third-party enchantment content
- [x] Update `ENCHANT_SPEC.md` — mark as "implemented" or archive

---

## Summary

| Phase | Tasks | Description |
|---|---|---|
| 0 | ~26 | Infrastructure: wipe, config, tags, client UI |
| 1 | ~26 | Data-driven enchantments (JSON only) |
| 2 | ~41 | Custom Java enchantments |
| 3 | ~3 | Lang keys (75 name + 75 desc) |
| 4 | ~20+ | Unit + integration + edge case tests |
| 5 | ~5 | Documentation rewrite |
