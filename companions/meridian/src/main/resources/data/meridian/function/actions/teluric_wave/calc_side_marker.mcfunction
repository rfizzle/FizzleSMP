# Copy from the parent marker
execute positioned ^-1 ^ ^ run scoreboard players operation @s meridian.teluric.side = @e[type=marker,tag=meridian.marker.teluric_wave.origin,limit=1,sort=nearest] meridian.teluric.side
execute positioned ^-1 ^ ^ run scoreboard players operation @s meridian.teluric.long = @e[type=marker,tag=meridian.marker.teluric_wave.origin,limit=1,sort=nearest] meridian.teluric.long
execute positioned ^-1 ^ ^ run data modify entity @s Rotation set from entity @e[type=marker,tag=meridian.marker.teluric_wave.origin,limit=1,sort=nearest] Rotation

# Remove the "new" tag and add the "right" tag
tag @s add meridian.marker.teluric_wave
tag @s add meridian.marker.teluric_wave.left

# Remove 1 from the side score
scoreboard players remove @s meridian.teluric.side 1