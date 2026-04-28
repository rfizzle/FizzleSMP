
#> fizzle_enchanting:actions/miningplus/break/starting
#
# @within		fizzle_enchanting:actions/miningplus/destroy
# @executed		as the player breaking the block & at the miningplus marker
# @description	Get in which direction the player is facing and launch the break function accordingly
#

# What direction
scoreboard players set #direction fizzle_enchanting.data 0
execute if score #direction fizzle_enchanting.data matches 0 if entity @s[x_rotation=-90..-45] run scoreboard players set #direction fizzle_enchanting.data 1
execute if score #direction fizzle_enchanting.data matches 0 if entity @s[x_rotation=65..90] run scoreboard players set #direction fizzle_enchanting.data 1
execute if score #direction fizzle_enchanting.data matches 0 if entity @s[y_rotation=-45..45,x_rotation=-45..65] run scoreboard players set #direction fizzle_enchanting.data 2
execute if score #direction fizzle_enchanting.data matches 0 if entity @s[y_rotation=-180..-135,x_rotation=-45..65] run scoreboard players set #direction fizzle_enchanting.data 2
execute if score #direction fizzle_enchanting.data matches 0 if entity @s[y_rotation=135..180,x_rotation=-45..65] run scoreboard players set #direction fizzle_enchanting.data 2
execute if score #direction fizzle_enchanting.data matches 0 if entity @s[y_rotation=45..135,x_rotation=-45..65] run scoreboard players set #direction fizzle_enchanting.data 3
execute if score #direction fizzle_enchanting.data matches 0 if entity @s[y_rotation=-135..-45,x_rotation=-45..65] run scoreboard players set #direction fizzle_enchanting.data 3

# For each found direction, call the function to break blocks
execute if score #direction fizzle_enchanting.data matches 1 run function fizzle_enchanting:actions/miningplus/break/up_down
execute if score #direction fizzle_enchanting.data matches 2 run function fizzle_enchanting:actions/miningplus/break/north_south
execute if score #direction fizzle_enchanting.data matches 3 run function fizzle_enchanting:actions/miningplus/break/east_west

