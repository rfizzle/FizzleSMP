# Compatibility Matrix — FizzleSMP

Tracks known conflicts between mods in the pack and how to resolve them.

## Legend

| Symbol | Meaning |
|--------|---------|
| :x: | Hard conflict — crashes, data corruption, or fundamentally incompatible |
| :warning: | Soft conflict — overlapping features or config tuning needed |

## Hard Conflicts

_None currently._

## Soft Conflicts

| Mod A | Mod B | Details | Resolution |
|---|---|---|---|
| Geophilic | Terralith | Both modify vanilla biome files; features may override each other | Use Terraphilic compatibility pack (must load after both mods) |
| Crafting Tweaks | Mouse Tweaks | Both modify crafting grid interactions (Crafting Tweaks: rotate/balance/clear buttons and shift-click output, Mouse Tweaks: drag-splitting) | Complementary — different interaction types. Verify no keybind overlap. |
| Illager Invasion | Friends & Foes | Both add illager-adjacent mobs (new illagers vs Iceologer/Illusioner); both modify raid/patrol systems | Test raid mechanics together; disable overlapping mob spawns if duplicates appear in config. |
| SwingThrough | Better Combat | Both modify attack targeting (SwingThrough allows hitting through transparent blocks, Better Combat changes hit detection to weapon arcs) | Should be complementary — verify that swing-through-grass works correctly with Better Combat's directional attack system. |
| Farmer's Delight | RightClickHarvest | Both interact with crop harvesting mechanics; FD adds custom crops that RCH may not automatically support | Test that right-click harvest works on FD crops (tomatoes, cabbages, etc.); may need config or datapack support. |

| Simple Voice Chat | Sound Physics Remastered | Explicit integration — SVC uses SPR for directional voice audio and sound occlusion through blocks | Enable SPR integration in SVC config for immersive proximity voice. |
| Krypton | Connectivity | Both modify the networking stack (Krypton optimizes packet compression/flushing, Connectivity fixes timeouts/login issues) | Generally compatible — different networking layers. Monitor for unexpected disconnects after adding both. |
| C2ME | Lithium | C2ME's multithreaded chunk generation concurrently accesses Lithium's `GameEventDispatcherStorage` hashmap, which could theoretically cause `ArrayIndexOutOfBoundsException` during worldgen | If issues arise, disable Lithium's `mixin.world.game_events` in `modpack/config/lithium.properties`. Minimal performance impact (only affects sculk sensor/warden game event dispatch). Previous crashes attributed to this were caused by failing hardware, not an actual mod conflict. |
| C2ME | NoisiumForked | Both optimize chunk generation (C2ME parallelizes chunk operations, NoisiumForked optimizes worldgen algorithms) | Complementary — C2ME handles threading, NoisiumForked handles algorithm efficiency. Widely used together without issues. |
| Mutant Monsters | Bosses of Mass Destruction | Both add boss-tier mobs (Mutant Monsters: roaming mutant variants, BoMD: dedicated arena bosses) | Complementary — different encounter types (random overworld vs structured arena). No overlap. |
| Mutant Monsters | Creeper Overhaul | Both modify creeper variants (Mutant Monsters adds Mutant Creeper, Creeper Overhaul adds biome-specific creepers) | Verify Mutant Creeper spawns independently of Creeper Overhaul variants. |
| MVS - Moog's Voyager Structures | YUNG's suite | Both add varied overworld structures; MVS uses vanilla blocks while YUNG's overhauls specific vanilla structures | Complementary — YUNG's replaces vanilla structure types, MVS adds entirely new structures. No overlap. |
| Tidal Towns | ChoiceTheorem's Overhauled Village | Both add village-type structures (Tidal Towns: ocean, CTOV: overworld/pillager) | Complementary — different biome domains (ocean vs land). No overlap. |
| MNS - Moog's Nether Structures | Incendium | Both add Nether structures; Incendium overhauls biomes and adds structures, MNS adds additional standalone structures | Complementary — different structure sets. May increase Nether structure density; Sparse Structures can manage this. |
| MNS - Moog's Nether Structures | YUNG's Better Nether Fortresses | MNS adds new Nether structures; YUNG's replaces vanilla fortresses | Complementary — YUNG's replaces fortresses, MNS adds entirely new structures. No overlap. |
| MES - Moog's End Structures | YUNG's Better End Island | MES adds outer End structures; YUNG's overhauls the main End Island | Complementary — different areas of the End (main island vs outer islands). |
| MES - Moog's End Structures | Nullscape | Both affect the End dimension; Nullscape reshapes terrain, MES adds structures | Complementary — Nullscape handles terrain, MES adds structures on top of it. |
| Sparse Structures | All new structure mods (Explorify, MVS, MES, MNS, MSS, Philip's Ruins, Tidal Towns) | Sparse Structures controls generation frequency for all structure mods | Essential with many structure mods — tune Sparse Structures config to prevent oversaturation. |
| Archers | Vanilla enchantments (Power, Quick Draw) | Archers overrides vanilla enchantment data files to rebalance Power (+8%/level instead of +50%) and Quick Draw (-10%/level instead of -20%). Configurable in `config/archers/tweaks.json`; datapacks can restore vanilla behavior. | Intentional rebalancing. |
| Rogues & Warriors | Vanilla enchantments (Sharpness) | Rebalances Sharpness to +8%/level for equal scaling between fast and slow weapons | Intentional rebalancing — complementary with Better Combat. |
| Illager Invasion | Bosses of Mass Destruction | Both add challenging PvE encounters (illager raids vs boss arenas) | Complementary — different encounter domains. |
| Armory | RPG Series class mods | Armory requires all 4 class mods (Archers, Rogues & Warriors, Wizards, Paladins & Priests) for its armor set system | Designed integration — Armory adds end-game armor specifically for each RPG class. |
| Gazebos | ChoiceTheorem's Overhauled Village | Both add village structures; Gazebos adds gazebos with Spell Binding Tables, CTOV overhauls village layouts | Complementary — both inject via Structure Pool API. Verify gazebos appear in CTOV's overhauled villages. |
| Critical Strike | Better Combat | Both modify melee attack mechanics; Critical Strike adds RNG crits, Better Combat changes swing animations and hit detection | Should be complementary — verify crit particles/sounds trigger correctly with Better Combat's attack system. |
| Critical Strike | Vanilla jump criticals | Critical Strike replaces vanilla jump-critical with RNG-based crits | Intentional replacement — vanilla jump crits can be re-enabled in config if desired. |
| Critical Strike | Spell Power Attributes | Both add combat-related entity attributes (crit chance/damage vs spell power) | Complementary — different attribute domains. Both enhance RPG-style character building. |
| Fabric Seasons | Terralith | Fabric Seasons changes foliage colors by season; Terralith adds ~100 custom biomes with unique foliage | Requires **Fabric Seasons: Terralith Compat** mod to apply seasonal colors to Terralith biomes. Without it, Terralith biomes won't change with seasons. |
| Fabric Seasons: Extras | Fabric Seasons | Addon by the same developer — adds greenhouse blocks, Season Detector, and Season Calendar to complement Fabric Seasons' season cycle | Designed integration — no conflict. |
| Fabric Seasons | Farmer's Delight Refabricated | Fabric Seasons modifies crop growth rates by season; Farmer's Delight adds custom crops | Requires **Fabric Seasons: Delight Refabricated Compat** mod so FD crops (tomatoes, cabbages, rice, etc.) respect seasonal growth rates. |
| Fabric Seasons: Delight Refabricated Compat | Farmer's Delight Refabricated | Fork of Delight Compat that correctly targets FD Refabricated's crop class names (fixes rice, tomato, and other crop support) | Designed integration — replaces the broken original Delight Compat. |
| Legendary Tooltips | Shulker Box Tooltip | Both modify tooltips (Legendary Tooltips styles borders, Shulker Box Tooltip adds content preview) | Complementary — Legendary Tooltips frames the tooltip, Shulker Box Tooltip adds content inside it. |
| Item Borders | Mutant Monsters | Item Borders colors slots by rarity; Mutant Monsters adds unique drops | Verify Mutant Monster drops display correct rarity borders. |
| EMI | Large modpacks (100+ mods) | EMI's `EmiInitializer.init()` and `EmiStackList.baked()` block the client render thread during server connect, causing 30s+ "Loading terrain" freezes. Two config defaults are problematic: (1) `index-source: creative` iterates every mod's creative tab, and at least one mod in the pack has extremely expensive tab population; (2) `search-tooltip-by-default: true` calls `getTooltipText()` on every indexed item during search bake ([emilyploszaj/emi#800](https://github.com/emilyploszaj/emi/issues/800)). | **Resolved** — in `modpack/config/emi.css`: set `index-source: registered` (reduced load from ~127s to ~17s) and `search-tooltip-by-default: false` (users can still search tooltips with `#` prefix). |
| EMI | C2ME | C2ME can cause hangs when EMI reloads during server connect ([emilyploszaj/emi#759](https://github.com/emilyploszaj/emi/issues/759)) | Monitor for issues; if EMI freezes return, test without C2ME. |
| Controlify | Sodium, Iris, Simple Voice Chat | Controlify explicitly supports and tests against Sodium, Iris, and SVC. Controller GUI navigation works in Sodium/Iris settings screens; SVC push-to-talk can be bound to controller buttons. | No action needed — designed compatibility. |
| Controlify | Better Combat | Controlify remaps attack inputs to controller; Better Combat changes melee hit detection and swing animations | Should be complementary — verify controller attack input triggers Better Combat directional swings correctly. |
| Enhanced Block Entities | Sodium | EBE v0.10.2+ is fully compatible with Sodium 0.6+. Older EBE versions needed Indium as a bridge. | No action needed — current versions are compatible. |
| More Culling | Entity Culling | Both optimize rendering culling but at different levels (More Culling: block faces, Entity Culling: entities/block entities) | Complementary — no overlap. |
| More Culling | Sodium | MoreCulling v1.0.x resolved earlier Sodium incompatibilities. Current v1.0.7 works with Sodium 0.6+/0.7+. | No action needed — current versions are compatible. |
| Sodium Extra | Sodium | Sodium Extra is a Sodium addon providing additional toggle settings | Designed integration — requires Sodium. |
| Handcrafted | Chipped | Both add decorative blocks (Handcrafted: furniture, Chipped: block variants via workbenches) | Complementary — different domains (furniture vs block retextures). No overlap. |
| Every Compat | Handcrafted, Chipped | Every Compat generates wood-type variants for blocks from these mods | Designed integration — Every Compat bridges modded wood types to furniture/decoration mods. |
| Every Compat | Terralith, Incendium | Every Compat generates variants for wood types added by worldgen mods | Complementary — worldgen mods add wood types, Every Compat ensures furniture mods support them. |
| The Bumblezone | All Overworld/Nether/End worldgen mods (Terralith, Tectonic, Incendium, Nullscape) | Bumblezone is a separate bee dimension with its own portal system | No conflict — completely independent dimension. Does not modify Overworld, Nether, or End generation. |
| Deeper and Darker | All Overworld/Nether/End worldgen mods (Terralith, Tectonic, Incendium, Nullscape) | Deeper and Darker adds "The Otherside" as a separate dimension accessed via Ancient Cities | No conflict — completely independent dimension. Does not modify Overworld, Nether, or End generation. |
| Refined Storage | Tech Reborn | RS adds a digital storage network; TR adds industrial machinery and energy. Both touch item logistics but at different layers (network storage vs machine I/O) | Complementary — use RS importers/exporters to feed TR machines from the network. No recipe or block conflicts. |
| Refined Storage | Oritech | RS adds a digital storage network; Oritech adds drones, conveyors, and machine automation | Complementary — RS handles centralized storage, Oritech handles in-world machine automation. Use RS importers on Oritech machine outputs. |
| Refined Storage | Simple Conveyor Belts | RS uses cables/network nodes; Conveyor Belts use spline-based item transport | Complementary — different logistics paradigms. Conveyor belts can feed RS importers and pull from RS exporters. |
| Kibe | Simple Conveyor Belts | Both add item-transport conveyor blocks (Kibe: Conveyor Belt, Simple Conveyor Belts: spline-based belts) | Disable Kibe conveyor blocks via Item Obliterator to keep Simple Conveyor Belts as the single conveyor system. |
| Kibe | Steve's Realistic Sleep | Kibe Sleeping Bag bypasses bed mechanics; Steve's Realistic Sleep relies on bed sleeping for time-acceleration scaling | Disable Kibe Sleeping Bag via Item Obliterator (`kibe:sleeping_bag`) so all sleep goes through Steve's Realistic Sleep. |
| Kibe | Game balance | Kibe Angel Ring grants permanent creative flight, which trivializes survival exploration | Disable Kibe Angel Ring (`kibe:angel_ring`) via Item Obliterator. Optionally disable other rings (Light/Magma/Water) if desired. |
| Item Obliterator | Kibe | Item Obliterator is being added specifically to disable conflicting/unwanted Kibe items via JSON config | Designed integration — list Kibe item IDs in `config/item_obliterator/disabled_items.json` (or equivalent). Restart server after edits. |
| Item Obliterator | EMI | Item Obliterator integrates with EMI to hide disabled items from the recipe viewer | Designed integration — disabled items automatically disappear from EMI search and recipe lookups. |
| Dark Utilities | Simple Conveyor Belts | Both provide entity/item transport (Dark Utilities: vector plates push entities directionally, Simple Conveyor Belts: spline-based item transport) | Complementary — vector plates push entities in a direction (great for mob farms), conveyor belts transport items along paths (great for logistics). Different use cases; both can coexist. |
| Dark Utilities | Item Obliterator | All Dark Utilities content except vector plates is disabled via Item Obliterator (damage/effect plates, mob filters, anchor plate, charms, runes, redstone blocks, blank plate) | Designed integration — Item Obliterator removes recipes, creative tab entries, and EMI listings for disabled items. |
