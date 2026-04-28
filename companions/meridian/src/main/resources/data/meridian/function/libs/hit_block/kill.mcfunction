
#> meridian:libs/hit_block/kill
#
# @within		meridian:libs/hit_block/main
# @executed		as & at the hit_block marker
# @description	Kill the marker if the entity lived more than 100 ticks
#

execute store result score #global meridian.gametime run time query gametime
scoreboard players operation @s meridian.gametime.temp = @s meridian.gametime
scoreboard players operation @s meridian.gametime.temp += #100 meridian.data
scoreboard players operation @s meridian.gametime.temp -= #global meridian.gametime
execute if entity @s[scores={meridian.gametime.temp=1..}] run return fail
kill @s