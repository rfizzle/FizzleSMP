tag @s add meridian.rebound
execute as @s run data modify entity @s SoundEvent set value "minecraft:block.amethyst_block.hit"
particle minecraft:end_rod ~ ~ ~ 0.05 0.05 0.05 0.05 2 force

execute store result score @s meridian.rebound_vx run data get entity @s Motion[0] 100
execute store result score @s meridian.rebound_vy run data get entity @s Motion[1] 100
execute store result score @s meridian.rebound_vz run data get entity @s Motion[2] 100

# log the score
# tellraw @a [{"text":"[Rebound] ","color":"green"},{"text":"VX: ","color":"white"},{"score":{"name":"@s","objective":"meridian.rebound_vx"},"color":"yellow"},{"text":" VY: ","color":"white"},{"score":{"name":"@s","objective":"meridian.rebound_vy"},"color":"yellow"},{"text":" VZ: ","color":"white"},{"score":{"name":"@s","objective":"meridian.rebound_vz"},"color":"yellow"}]

schedule function meridian:actions/rebound/on_hit 1t append 
