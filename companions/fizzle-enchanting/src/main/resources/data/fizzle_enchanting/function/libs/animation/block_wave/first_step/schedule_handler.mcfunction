execute store result score #global fizzle_enchanting.gametime run time query gametime
execute as @e[type=item_display,tag=fizzle_enchanting.item_display.teluric,scores={fizzle_enchanting.animation_state=0}] run function fizzle_enchanting:libs/animation/block_wave/first_step/entry
schedule function fizzle_enchanting:libs/animation/block_wave/second_step/schedule_handler 7t append