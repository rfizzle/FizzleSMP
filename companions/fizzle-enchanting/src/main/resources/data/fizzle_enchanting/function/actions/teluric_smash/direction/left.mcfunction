# Copy from the parent marker
execute positioned ~1 ~ ~ run scoreboard players operation @s fizzle_enchanting.teluric.radius = @e[type=marker,tag=fizzle_enchanting.marker.teluric_smash.origin,limit=1,sort=nearest,distance=..0.1] fizzle_enchanting.teluric.radius
# Decrement the radius
scoreboard players remove @s fizzle_enchanting.teluric.radius 1
# Add necessary tags
tag @s add fizzle_enchanting.marker.teluric_smash
tag @s add fizzle_enchanting.marker.teluric_smash.forward 
tag @s add fizzle_enchanting.marker.teluric_smash.backward
tag @s add fizzle_enchanting.marker.teluric_smash.left