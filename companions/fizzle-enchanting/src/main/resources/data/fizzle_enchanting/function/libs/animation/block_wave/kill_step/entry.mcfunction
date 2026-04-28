execute store result score #global fizzle_enchanting.gametime run time query gametime
scoreboard players operation @s fizzle_enchanting.gametime.temp = @s fizzle_enchanting.gametime
scoreboard players operation @s fizzle_enchanting.gametime.temp += #16 fizzle_enchanting.data
scoreboard players operation @s fizzle_enchanting.gametime.temp -= #global fizzle_enchanting.gametime
execute if entity @s[scores={fizzle_enchanting.gametime.temp=1..}] run return fail

kill @s