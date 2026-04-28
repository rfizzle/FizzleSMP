function fizzle_enchanting:libs/animation/block_wave/start
tag @s add fizzle_enchanting.marker.teluric_smash.origin
execute as @e[distance=..2,type=!#fizzle_enchanting:teluric_blacklist] store result entity @s Motion[1] double 0.1 run scoreboard players get #teluric_motion fizzle_enchanting.data

# Propagate forward
execute if entity @s[tag=fizzle_enchanting.marker.teluric_smash.forward] if score @s fizzle_enchanting.teluric.radius matches 1.. positioned ~ ~ ~1 unless entity @e[type=marker,tag=fizzle_enchanting.marker.teluric_smash,limit=1,sort=nearest,distance=..0.1] summon marker run function fizzle_enchanting:actions/teluric_smash/direction/forward

# Propagate to the back
execute if entity @s[tag=fizzle_enchanting.marker.teluric_smash.backward] if score @s fizzle_enchanting.teluric.radius matches 1.. positioned ~ ~ ~-1 unless entity @e[type=marker,tag=fizzle_enchanting.marker.teluric_smash,limit=1,sort=nearest,distance=..0.1] summon marker run function fizzle_enchanting:actions/teluric_smash/direction/backward

# Propagate to the right
execute if entity @s[tag=fizzle_enchanting.marker.teluric_smash.right] if score @s fizzle_enchanting.teluric.radius matches 1.. positioned ~1 ~ ~ unless entity @e[type=marker,tag=fizzle_enchanting.marker.teluric_smash,limit=1,sort=nearest,distance=..0.1] summon marker run function fizzle_enchanting:actions/teluric_smash/direction/right

# Propagate to the left
execute if entity @s[tag=fizzle_enchanting.marker.teluric_smash.left] if score @s fizzle_enchanting.teluric.radius matches 1.. positioned ~-1 ~ ~ unless entity @e[type=marker,tag=fizzle_enchanting.marker.teluric_smash,limit=1,sort=nearest,distance=..0.1] summon marker run function fizzle_enchanting:actions/teluric_smash/direction/left


kill @s