
#> meridian:hit_block/vein_miner/place_hit_block_marker
#
# @within		data/enchantplus/enchantment/pickaxe/vein_miner.json
# @executed		as the player & at the hit block position
# @description	Summon a vein miner marker
#

summon marker ~ ~ ~ {Tags: ["meridian.libs.hit_block", "meridian.hit_block.veinminer"]}
execute store result score @n[type=minecraft:marker,tag=meridian.hit_block.veinminer] meridian.gametime run time query gametime
schedule function meridian:libs/hit_block/schedule_handler 5s append
function meridian:actions/vein_miner/process_markers