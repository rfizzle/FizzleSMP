
#> meridian:hit_block/vein_miner/destroy
#
# @within		meridian:hit_block/vein_miner/core
# @executed		as & at the vein miner marker
# @description	Add temporary tag for selector, launch the propagation function as the player holding the vein miner, and kill the marker
#

tag @s add meridian.start_breaking
execute as @p[predicate=meridian:enchantments/vein_miner] run function meridian:actions/vein_miner/propagate
kill @s
