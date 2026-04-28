# Summon the initial marker
summon marker ~ ~ ~ {Tags:["fizzle_enchanting.marker.teluric_wave","fizzle_enchanting.marker.teluric_wave.origin","fizzle_enchanting.marker.teluric_wave.left","fizzle_enchanting.marker.teluric_wave.right"]}

# Set up the initial marker
execute as @e[type=marker,tag=fizzle_enchanting.marker.teluric_wave.origin,limit=1] at @s run function fizzle_enchanting:actions/teluric_wave/setup_origin_marker
schedule function fizzle_enchanting:actions/teluric_wave/process_markers 1t