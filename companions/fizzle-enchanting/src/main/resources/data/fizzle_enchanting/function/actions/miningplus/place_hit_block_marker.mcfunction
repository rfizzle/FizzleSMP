
#> fizzle_enchanting:hit_block/miningplus/place_hit_block_marker
#
# @within		data/enchantplus/enchantment/axe/miningplus.json
# @executed		as the player & at the hit block position
# @description	Summon a miningplus marker
#

execute align xyz summon marker run function fizzle_enchanting:actions/miningplus/summon_marker
schedule function fizzle_enchanting:libs/hit_block/schedule_handler 5s append
function fizzle_enchanting:actions/miningplus/process_markers