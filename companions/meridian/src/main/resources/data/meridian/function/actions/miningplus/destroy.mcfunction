
#> meridian:hit_block/miningplus/destroy
#
# @within		meridian:hit_block/miningplus/core
# @executed		as & at the miningplus marker
# @description	Launch the break function as the player holding the miningplus pickaxe, and kill the marker
#

execute as @p[predicate=meridian:enchantments/miningplus] run function meridian:actions/miningplus/break/starting
kill @s[type=minecraft:marker]