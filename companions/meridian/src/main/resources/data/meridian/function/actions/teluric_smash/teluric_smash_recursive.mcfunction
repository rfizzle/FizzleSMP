function meridian:libs/animation/block_wave/start
tag @s add meridian.marker.teluric_smash.origin
execute as @e[distance=..2,type=!#meridian:teluric_blacklist] store result entity @s Motion[1] double 0.1 run scoreboard players get #teluric_motion meridian.data

# Propagate forward
execute if entity @s[tag=meridian.marker.teluric_smash.forward] if score @s meridian.teluric.radius matches 1.. positioned ~ ~ ~1 unless entity @e[type=marker,tag=meridian.marker.teluric_smash,limit=1,sort=nearest,distance=..0.1] summon marker run function meridian:actions/teluric_smash/direction/forward

# Propagate to the back
execute if entity @s[tag=meridian.marker.teluric_smash.backward] if score @s meridian.teluric.radius matches 1.. positioned ~ ~ ~-1 unless entity @e[type=marker,tag=meridian.marker.teluric_smash,limit=1,sort=nearest,distance=..0.1] summon marker run function meridian:actions/teluric_smash/direction/backward

# Propagate to the right
execute if entity @s[tag=meridian.marker.teluric_smash.right] if score @s meridian.teluric.radius matches 1.. positioned ~1 ~ ~ unless entity @e[type=marker,tag=meridian.marker.teluric_smash,limit=1,sort=nearest,distance=..0.1] summon marker run function meridian:actions/teluric_smash/direction/right

# Propagate to the left
execute if entity @s[tag=meridian.marker.teluric_smash.left] if score @s meridian.teluric.radius matches 1.. positioned ~-1 ~ ~ unless entity @e[type=marker,tag=meridian.marker.teluric_smash,limit=1,sort=nearest,distance=..0.1] summon marker run function meridian:actions/teluric_smash/direction/left


kill @s