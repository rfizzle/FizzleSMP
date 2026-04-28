# Process all remaining markers
execute as @e[type=marker,tag=meridian.hit_block.miningplus] at @s if block ~ ~ ~ minecraft:air run function meridian:actions/miningplus/destroy

# Schedule the next iteration if there are still markers to process
execute if entity @e[type=marker,tag=meridian.hit_block.miningplus] run schedule function meridian:actions/miningplus/process_markers 1t