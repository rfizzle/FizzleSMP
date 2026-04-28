
#> fizzle_enchanting:hit_block/miningplus/break/up_down
#
# @within		fizzle_enchanting:hit_block/miningplus/break/east_west
#				fizzle_enchanting:hit_block/miningplus/break/north_south
#				fizzle_enchanting:hit_block/miningplus/break/up_down
# @executed		as the player breaking the block & at a block in the 3x3 area (except middle)
# @description	Try to mine the block with the player's main hand
#

execute if block ~ ~ ~ #fizzle_enchanting:miningplus run return fail
loot spawn ~ ~ ~ mine ~ ~ ~ mainhand
setblock ~ ~ ~ air
