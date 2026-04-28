
#> fizzle_enchanting:libs/hit_block/kill
#
# @within		fizzle_enchanting:libs/hit_block/main
# @executed		as & at the hit_block marker
# @description	Kill the marker if the entity lived more than 100 ticks
#

execute store result score #global fizzle_enchanting.gametime run time query gametime
scoreboard players operation @s fizzle_enchanting.gametime.temp = @s fizzle_enchanting.gametime
scoreboard players operation @s fizzle_enchanting.gametime.temp += #100 fizzle_enchanting.data
scoreboard players operation @s fizzle_enchanting.gametime.temp -= #global fizzle_enchanting.gametime
execute if entity @s[scores={fizzle_enchanting.gametime.temp=1..}] run return fail
kill @s