# Copy player's rotation to the marker
data modify entity @s Rotation set from entity @p Rotation
data modify entity @s Rotation[1] set value 0.0f

# Initialize scores
scoreboard players operation @s fizzle_enchanting.teluric.side = #default fizzle_enchanting.teluric.side
scoreboard players operation @s fizzle_enchanting.teluric.long = #default fizzle_enchanting.teluric.long

# Start the recursive function
function fizzle_enchanting:actions/teluric_wave/teluric_wave_recursive