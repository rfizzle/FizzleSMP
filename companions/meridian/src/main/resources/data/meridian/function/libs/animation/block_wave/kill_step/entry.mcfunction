execute store result score #global meridian.gametime run time query gametime
scoreboard players operation @s meridian.gametime.temp = @s meridian.gametime
scoreboard players operation @s meridian.gametime.temp += #16 meridian.data
scoreboard players operation @s meridian.gametime.temp -= #global meridian.gametime
execute if entity @s[scores={meridian.gametime.temp=1..}] run return fail

kill @s