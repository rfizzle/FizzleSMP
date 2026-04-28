# Process all remaining markers
execute as @e[type=marker,tag=fizzle_enchanting.hit_block.veinminer] at @s if block ~ ~ ~ minecraft:air run function fizzle_enchanting:actions/vein_miner/destroy

# Schedule the next iteration if there are still markers to process
execute if entity @e[type=marker,tag=fizzle_enchanting.hit_block.veinminer] run schedule function fizzle_enchanting:actions/vein_miner/process_markers 1t