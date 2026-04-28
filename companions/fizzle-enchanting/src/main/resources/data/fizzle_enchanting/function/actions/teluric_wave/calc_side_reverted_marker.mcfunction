# Copy from the parent marker
execute positioned ^1 ^ ^ run scoreboard players operation @s fizzle_enchanting.teluric.side = @e[type=marker,tag=fizzle_enchanting.marker.teluric_wave.origin,limit=1,sort=nearest] fizzle_enchanting.teluric.side
execute positioned ^1 ^ ^ run scoreboard players operation @s fizzle_enchanting.teluric.long = @e[type=marker,tag=fizzle_enchanting.marker.teluric_wave.origin,limit=1,sort=nearest] fizzle_enchanting.teluric.long
execute positioned ^1 ^ ^ run data modify entity @s Rotation set from entity @e[type=marker,tag=fizzle_enchanting.marker.teluric_wave.origin,limit=1,sort=nearest] Rotation
data modify entity @s Rotation[1] set value 0.0f

# Remove the "new" tag and add the "left" tag
tag @s add fizzle_enchanting.marker.teluric_wave
tag @s add fizzle_enchanting.marker.teluric_wave.right

# Remove 1 from the side score
scoreboard players remove @s fizzle_enchanting.teluric.side 1