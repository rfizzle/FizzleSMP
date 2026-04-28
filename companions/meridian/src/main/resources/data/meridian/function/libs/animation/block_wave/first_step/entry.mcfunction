execute store result score #global meridian.gametime run time query gametime
scoreboard players operation @s meridian.gametime.temp = @s meridian.gametime
scoreboard players operation @s meridian.gametime.temp += #1 meridian.data
scoreboard players operation @s meridian.gametime.temp -= #global meridian.gametime
execute if entity @s[scores={meridian.gametime.temp=1..}] run return fail

data merge entity @s {start_interpolation:0,interpolation_duration:7,transformation:{translation:[0.0f,0.75f,0.0f],scale:[1.0f,1.0f,1.0f]}}
scoreboard players set @s meridian.animation_state 1