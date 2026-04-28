# Process all remaining markers
execute as @e[type=marker,tag=fizzle_enchanting.hit_block.miningplus] at @s if block ~ ~ ~ minecraft:air run function fizzle_enchanting:actions/miningplus/destroy

# Schedule the next iteration if there are still markers to process
execute if entity @e[type=marker,tag=fizzle_enchanting.hit_block.miningplus] run schedule function fizzle_enchanting:actions/miningplus/process_markers 1t