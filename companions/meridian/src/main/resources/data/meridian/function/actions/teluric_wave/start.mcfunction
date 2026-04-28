# Summon the initial marker
summon marker ~ ~ ~ {Tags:["meridian.marker.teluric_wave","meridian.marker.teluric_wave.origin","meridian.marker.teluric_wave.left","meridian.marker.teluric_wave.right"]}

# Set up the initial marker
execute as @e[type=marker,tag=meridian.marker.teluric_wave.origin,limit=1] at @s run function meridian:actions/teluric_wave/setup_origin_marker
schedule function meridian:actions/teluric_wave/process_markers 1t