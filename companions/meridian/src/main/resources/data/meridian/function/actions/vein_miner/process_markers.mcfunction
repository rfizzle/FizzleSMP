# Process all remaining markers
execute as @e[type=marker,tag=meridian.hit_block.veinminer] at @s if block ~ ~ ~ minecraft:air run function meridian:actions/vein_miner/destroy

# Schedule the next iteration if there are still markers to process
execute if entity @e[type=marker,tag=meridian.hit_block.veinminer] run schedule function meridian:actions/vein_miner/process_markers 1t