execute store result score #global fizzle_enchanting.gametime run time query gametime
scoreboard players operation @s fizzle_enchanting.gametime.temp = @s fizzle_enchanting.gametime
scoreboard players operation @s fizzle_enchanting.gametime.temp += #1 fizzle_enchanting.data
scoreboard players operation @s fizzle_enchanting.gametime.temp -= #global fizzle_enchanting.gametime
execute if entity @s[scores={fizzle_enchanting.gametime.temp=1..}] run return fail

data merge entity @s {start_interpolation:0,interpolation_duration:7,transformation:{translation:[0.0f,0.75f,0.0f],scale:[1.0f,1.0f,1.0f]}}
scoreboard players set @s fizzle_enchanting.animation_state 1