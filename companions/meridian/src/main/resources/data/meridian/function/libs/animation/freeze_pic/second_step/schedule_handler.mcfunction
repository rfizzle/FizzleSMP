execute store result score #global meridian.gametime run time query gametime
execute as @e[type=block_display,tag=meridian.block_display.freezing,scores={meridian.animation_state=1}] run function meridian:libs/animation/freeze_pic/second_step/entry
schedule function meridian:libs/animation/freeze_pic/kill_step/schedule_handler 8t append