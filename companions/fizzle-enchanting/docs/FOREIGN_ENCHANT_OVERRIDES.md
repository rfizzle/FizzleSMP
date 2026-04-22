# Foreign Enchantment Overrides

## Why it exists

The stat-driven enchanting table only surfaces enchantments that can roll — if an
enchant ships with `weight: 0` or a narrow `supported_items` tag, the table will
never produce it, the library pool never fills with it, and the whole discovery
loop stalls on that entry.

Two enchantments on FizzleSMP trip this:

| Enchantment | Source | Upstream problem |
|---|---|---|
| `minecraft:mending` | vanilla | Treasure-flagged with weight 2 — rare enough that the library pool starves. |
| `yigd:soulbound` | [You're in Grave Danger](https://modrinth.com/mod/yigd) | Ships with `weight: 0` and a narrow tag — never rolls. |

Fizzle Enchanting's override files raise Mending's weight and bind Soulbound to a
non-zero weight on `#yigd:soulbindable`, making both enchantments feed the same
discovery loop as any authored enchant.

## How the gating works

The overrides ship inside a built-in Fabric resource pack at
`src/main/resources/resourcepacks/foreign_overrides/`, registered at mod init via
`ResourceManagerHelper.registerBuiltinResourcePack`. The config flag picks the
pack's activation type:

| `config.foreignEnchantments.applyBundledOverrides` | Activation type | Behavior |
|---|---|---|
| `true` (default) | `ALWAYS_ENABLED` | Pack is force-enabled; overrides apply. Higher-priority world datapacks still win. |
| `false` | `NORMAL` | Pack is off by default; upstream Mending / Soulbound weights are restored. Operators can still flip it on via `/datapack enable`. |

This keeps a single source of truth per enchantment — when you want a different
value, edit the override file in the resource pack rather than maintaining a
parallel "restoration" pack.

## Why a built-in pack, not just jar resources

Shipping the overrides as plain `data/minecraft/enchantment/...` resources at the
jar root would bake them in unconditionally — flipping the config flag would then
require registering a second, higher-priority pack with upstream values, which
doubles the maintenance surface per enchantment.

The built-in-pack approach lets the single override file drive both states: the
flag just decides whether the pack activates.

## Expanding the list

To override another enchantment, drop a JSON into the pack:

```
src/main/resources/resourcepacks/foreign_overrides/data/<namespace>/enchantment/<id>.json
```

No Java changes needed — the pack loader picks up new files automatically.
Document the rationale in this file.

## Operator escape valve

Any world-level or Paxi datapack wins over both the built-in pack and the bundled
overrides — if an operator ships their own curated weights, no jar edit is
required.
