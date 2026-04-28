# Summon the initial marker
summon marker ~ ~-1 ~ {Tags:["fizzle_enchanting.marker.teluric_smash","fizzle_enchanting.marker.teluric_smash.origin", "fizzle_enchanting.marker.teluric_smash.forward", "fizzle_enchanting.marker.teluric_smash.left", "fizzle_enchanting.marker.teluric_smash.right", "fizzle_enchanting.marker.teluric_smash.backward"]}
scoreboard players operation @e[type=marker,tag=fizzle_enchanting.marker.teluric_smash.origin,limit=1,sort=nearest] fizzle_enchanting.teluric.radius = #default fizzle_enchanting.teluric.radius
execute as @e[type=marker,tag=fizzle_enchanting.marker.teluric_smash.origin,limit=1,sort=nearest] at @s run function fizzle_enchanting:actions/teluric_smash/teluric_smash_recursive
schedule function fizzle_enchanting:actions/teluric_smash/process_markers 1t