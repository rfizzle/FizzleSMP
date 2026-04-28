# Process all remaining markers
execute as @e[type=marker,tag=meridian.marker.teluric_wave] at @s run function meridian:actions/teluric_wave/teluric_wave_recursive

execute if entity @e[type=marker,tag=meridian.marker.teluric_wave] run schedule function meridian:actions/teluric_wave/process_markers 1t