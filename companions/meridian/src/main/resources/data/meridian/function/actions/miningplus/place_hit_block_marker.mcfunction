
#> meridian:hit_block/miningplus/place_hit_block_marker
#
# @within		data/enchantplus/enchantment/axe/miningplus.json
# @executed		as the player & at the hit block position
# @description	Summon a miningplus marker
#

execute align xyz summon marker run function meridian:actions/miningplus/summon_marker
schedule function meridian:libs/hit_block/schedule_handler 5s append
function meridian:actions/miningplus/process_markers