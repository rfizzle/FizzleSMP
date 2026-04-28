# Copy from the parent marker
execute positioned ~-1 ~ ~ run scoreboard players operation @s meridian.teluric.radius = @e[type=marker,tag=meridian.marker.teluric_smash.origin,limit=1,sort=nearest,distance=..0.1] meridian.teluric.radius
# Decrement the radius
scoreboard players remove @s meridian.teluric.radius 1
# Add necessary tags
tag @s add meridian.marker.teluric_smash
tag @s add meridian.marker.teluric_smash.forward 
tag @s add meridian.marker.teluric_smash.right
tag @s add meridian.marker.teluric_smash.backward