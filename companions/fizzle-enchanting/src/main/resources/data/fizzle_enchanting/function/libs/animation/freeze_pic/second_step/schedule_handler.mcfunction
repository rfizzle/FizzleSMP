execute store result score #global fizzle_enchanting.gametime run time query gametime
execute as @e[type=block_display,tag=fizzle_enchanting.block_display.freezing,scores={fizzle_enchanting.animation_state=1}] run function fizzle_enchanting:libs/animation/freeze_pic/second_step/entry
schedule function fizzle_enchanting:libs/animation/freeze_pic/kill_step/schedule_handler 8t append