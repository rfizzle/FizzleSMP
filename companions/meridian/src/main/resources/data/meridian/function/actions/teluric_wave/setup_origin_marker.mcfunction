# Copy player's rotation to the marker
data modify entity @s Rotation set from entity @p Rotation
data modify entity @s Rotation[1] set value 0.0f

# Initialize scores
scoreboard players operation @s meridian.teluric.side = #default meridian.teluric.side
scoreboard players operation @s meridian.teluric.long = #default meridian.teluric.long

# Start the recursive function
function meridian:actions/teluric_wave/teluric_wave_recursive