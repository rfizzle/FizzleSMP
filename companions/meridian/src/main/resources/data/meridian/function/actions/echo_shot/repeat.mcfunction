execute as @e[type=minecraft:arrow,tag=meridian.echo_shot,predicate=meridian:echo_shot] at @s run function meridian:actions/echo_shot/effect

# Two time per second, call the main function for every arrow with the echo_shoot tag
execute if entity @e[type=minecraft:arrow,tag=meridian.echo_shot,predicate=meridian:echo_shot] run schedule function meridian:actions/echo_shot/repeat 1t append
execute if entity @e[type=minecraft:arrow,tag=meridian.echo_shot,predicate=meridian:echo_shot] run schedule function meridian:actions/echo_shot/repeat 2t append