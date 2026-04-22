# NeoEnchant+ Port Notes

## Attribution

49 of Fizzle Enchanting's 51 MVP enchantments are ports of the JSON definitions
shipped in [NeoEnchant+](https://www.curseforge.com/minecraft/mc-mods/neoenchant)
v5.14.0 by **Hardel** (CurseForge project id 1135663). Every ported enchant is a
data-only rewrite — enchantment JSONs plus exclusive-set tags plus English lang
strings were copied from `NeoEnchant-5.14.0.jar` into
`src/main/resources/data/fizzle_enchanting/enchantment/`,
`src/main/resources/data/fizzle_enchanting/tags/enchantment/exclusive_set/`, and
`src/main/resources/assets/fizzle_enchanting/lang/en_us.json`. Each `enchantplus:`
namespace reference (and every `enchantment.enchantplus.*` lang key) was mechanically
rewritten to `fizzle_enchanting:` / `enchantment.fizzle_enchanting.*` — no behavior
changes, no custom Java code, no new effect component types.

## Cuts (7 of 56)

The NeoEnchant+ v5.14.0 jar ships 56 enchant JSONs. Seven are intentionally omitted
(see DESIGN.md § "Explicitly cut from NeoEnchant+" for the rationale):

| Cut file                       | Reason                                                       |
|--------------------------------|--------------------------------------------------------------|
| `axe/timber`                   | Whole-tree fellers cause server lag and grief.               |
| `pickaxe/bedrock_breaker`      | Bedrock protection is intentional; bypass enables escapes.   |
| `pickaxe/spawner_touch`        | Trivializes mob-farm and spawner economies.                  |
| `tools/auto_smelt`             | Overlaps Fortune/Silk Touch loops; duplicates cut Masterwork. |
| `helmet/auto_feed`             | Removes hunger management, undercutting food variety.        |
| `chestplate/magnet`            | Ships with an empty `effects` block (marker-enchant pattern). |
| `sword/runic_despair`          | Targets Yggdrasil's Runic dimension (not in FizzleSMP).       |

Lang keys matching those seven enchants — plus any `enchantplus:` lang entries that
never mapped to a ported JSON (`happy_boost`, `curse_of_clumsiness`, elytra
`propulsion`, and NeoEnchant+'s `dimensional_strike` name key that conflicts with
our `dimensional_hit` id) — were dropped during the merge.

## License

NeoEnchant+ is licensed under **Creative Commons BY-NC-SA 4.0**. FizzleSMP is a
private server modpack; re-publishing any of the ported `data/` or `lang/` assets
pulls the SA obligation onto derivative works and raises NC commercial-use
questions for public hosting. See DESIGN.md § "Build & Ship" for the publish-time
dual-license or re-author checklist.
