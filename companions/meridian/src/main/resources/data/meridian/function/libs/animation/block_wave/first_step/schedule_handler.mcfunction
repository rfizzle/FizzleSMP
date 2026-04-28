execute store result score #global meridian.gametime run time query gametime
execute as @e[type=item_display,tag=meridian.item_display.teluric,scores={meridian.animation_state=0}] run function meridian:libs/animation/block_wave/first_step/entry
schedule function meridian:libs/animation/block_wave/second_step/schedule_handler 7t append