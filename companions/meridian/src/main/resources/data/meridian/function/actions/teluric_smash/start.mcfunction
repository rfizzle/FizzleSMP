# Summon the initial marker
summon marker ~ ~-1 ~ {Tags:["meridian.marker.teluric_smash","meridian.marker.teluric_smash.origin", "meridian.marker.teluric_smash.forward", "meridian.marker.teluric_smash.left", "meridian.marker.teluric_smash.right", "meridian.marker.teluric_smash.backward"]}
scoreboard players operation @e[type=marker,tag=meridian.marker.teluric_smash.origin,limit=1,sort=nearest] meridian.teluric.radius = #default meridian.teluric.radius
execute as @e[type=marker,tag=meridian.marker.teluric_smash.origin,limit=1,sort=nearest] at @s run function meridian:actions/teluric_smash/teluric_smash_recursive
schedule function meridian:actions/teluric_smash/process_markers 1t