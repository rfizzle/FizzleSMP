
#> fizzle_enchanting:actions/miningplus/break/north_south
#
# @within		fizzle_enchanting:actions/miningplus/break/starting
# @executed		as the player breaking the block & at the miningplus marker
# @description	Destroy each block in the 3x3 area (except middle)
#

execute positioned ~1 ~1 ~ run function fizzle_enchanting:actions/miningplus/break/execute
execute positioned ~1 ~-1 ~ run function fizzle_enchanting:actions/miningplus/break/execute
execute positioned ~-1 ~1 ~ run function fizzle_enchanting:actions/miningplus/break/execute
execute positioned ~-1 ~-1 ~ run function fizzle_enchanting:actions/miningplus/break/execute
execute positioned ~-1 ~ ~ run function fizzle_enchanting:actions/miningplus/break/execute
execute positioned ~1 ~ ~ run function fizzle_enchanting:actions/miningplus/break/execute
execute positioned ~ ~1 ~ run function fizzle_enchanting:actions/miningplus/break/execute
execute positioned ~ ~-1 ~ run function fizzle_enchanting:actions/miningplus/break/execute

