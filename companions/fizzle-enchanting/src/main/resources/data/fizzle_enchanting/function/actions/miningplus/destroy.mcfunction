
#> fizzle_enchanting:hit_block/miningplus/destroy
#
# @within		fizzle_enchanting:hit_block/miningplus/core
# @executed		as & at the miningplus marker
# @description	Launch the break function as the player holding the miningplus pickaxe, and kill the marker
#

execute as @p[predicate=fizzle_enchanting:enchantments/miningplus] run function fizzle_enchanting:actions/miningplus/break/starting
kill @s[type=minecraft:marker]