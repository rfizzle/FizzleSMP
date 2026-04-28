
#> fizzle_enchanting:hit_block/vein_miner/destroy
#
# @within		fizzle_enchanting:hit_block/vein_miner/core
# @executed		as & at the vein miner marker
# @description	Add temporary tag for selector, launch the propagation function as the player holding the vein miner, and kill the marker
#

tag @s add fizzle_enchanting.start_breaking
execute as @p[predicate=fizzle_enchanting:enchantments/vein_miner] run function fizzle_enchanting:actions/vein_miner/propagate
kill @s
