
#> fizzle_enchanting:actions/no_gravity
#
# @within		data/enchantplus/enchantment/bow/accuracy_shot.json
# @executed		as & at the arrow shot with accuracy_shot enchantment
# @description	Remove gravity and tag the arrow as accuracy_shot
#

data modify entity @s NoGravity set value 1b
tag @s add fizzle_enchanting.accuracy_shot
execute store result score @s fizzle_enchanting.gametime run time query gametime
schedule function fizzle_enchanting:actions/accuracy_shot/schedule_handler 5s append