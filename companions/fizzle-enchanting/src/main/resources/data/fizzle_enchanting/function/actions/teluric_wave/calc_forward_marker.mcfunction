# Copy from the parent marker
execute positioned ^ ^ ^-1 run scoreboard players operation @s fizzle_enchanting.teluric.side = @e[type=marker,tag=fizzle_enchanting.marker.teluric_wave.origin,limit=1,sort=nearest,distance=..0.1] fizzle_enchanting.teluric.side
execute positioned ^ ^ ^-1 run scoreboard players operation @s fizzle_enchanting.teluric.long = @e[type=marker,tag=fizzle_enchanting.marker.teluric_wave.origin,limit=1,sort=nearest,distance=..0.1] fizzle_enchanting.teluric.long
execute positioned ^ ^ ^-1 run data modify entity @s Rotation set from entity @e[type=marker,tag=fizzle_enchanting.marker.teluric_wave.origin,limit=1,sort=nearest,distance=..0.1] Rotation

# Remove the "new" tag
tag @s add fizzle_enchanting.marker.teluric_wave

# Remove 1 from the side score
scoreboard players remove @s fizzle_enchanting.teluric.long 1