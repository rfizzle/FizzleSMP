execute store result score #global fizzle_enchanting.gametime run time query gametime
execute as @e[type=block_display,tag=fizzle_enchanting.block_display.freezing,scores={fizzle_enchanting.animation_state=0}] run function fizzle_enchanting:libs/animation/freeze_pic/first_step/entry
schedule function fizzle_enchanting:libs/animation/freeze_pic/second_step/schedule_handler 30t append 