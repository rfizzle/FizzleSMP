
#> fizzle_enchanting:hit_block/vein_miner/place_hit_block_marker
#
# @within		data/enchantplus/enchantment/pickaxe/vein_miner.json
# @executed		as the player & at the hit block position
# @description	Summon a vein miner marker
#

summon marker ~ ~ ~ {Tags: ["fizzle_enchanting.libs.hit_block", "fizzle_enchanting.hit_block.veinminer"]}
execute store result score @n[type=minecraft:marker,tag=fizzle_enchanting.hit_block.veinminer] fizzle_enchanting.gametime run time query gametime
schedule function fizzle_enchanting:libs/hit_block/schedule_handler 5s append
function fizzle_enchanting:actions/vein_miner/process_markers