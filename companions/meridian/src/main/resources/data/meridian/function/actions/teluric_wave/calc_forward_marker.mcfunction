# Copy from the parent marker
execute positioned ^ ^ ^-1 run scoreboard players operation @s meridian.teluric.side = @e[type=marker,tag=meridian.marker.teluric_wave.origin,limit=1,sort=nearest,distance=..0.1] meridian.teluric.side
execute positioned ^ ^ ^-1 run scoreboard players operation @s meridian.teluric.long = @e[type=marker,tag=meridian.marker.teluric_wave.origin,limit=1,sort=nearest,distance=..0.1] meridian.teluric.long
execute positioned ^ ^ ^-1 run data modify entity @s Rotation set from entity @e[type=marker,tag=meridian.marker.teluric_wave.origin,limit=1,sort=nearest,distance=..0.1] Rotation

# Remove the "new" tag
tag @s add meridian.marker.teluric_wave

# Remove 1 from the side score
scoreboard players remove @s meridian.teluric.long 1