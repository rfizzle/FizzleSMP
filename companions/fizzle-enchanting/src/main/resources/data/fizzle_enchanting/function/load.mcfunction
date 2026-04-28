# Create scoreboard objectives
scoreboard objectives add fizzle_enchanting.data dummy

# New scoreboards for the Teluric Wave area
scoreboard objectives add fizzle_enchanting.teluric.side dummy
scoreboard objectives add fizzle_enchanting.teluric.long dummy
scoreboard objectives add fizzle_enchanting.animation_state dummy
scoreboard objectives add fizzle_enchanting.gametime dummy
scoreboard objectives add fizzle_enchanting.gametime.temp dummy
scoreboard objectives add fizzle_enchanting.teluric.radius dummy
scoreboard objectives add fizzle_enchanting.striker.countdown dummy
scoreboard objectives add fizzle_enchanting.striker.gametime dummy

scoreboard objectives add fizzle_enchanting.transformation.w dummy
scoreboard objectives add fizzle_enchanting.transformation.x dummy
scoreboard objectives add fizzle_enchanting.transformation.y dummy
scoreboard objectives add fizzle_enchanting.transformation.z dummy

# New scoreboards for the Thrower enchantment
scoreboard objectives add fizzle_enchanting.timer dummy
scoreboard objectives add fizzle_enchanting.rot dummy
scoreboard objectives add fizzle_enchanting.motion dummy
scoreboard objectives add fizzle_enchanting.var dummy

# Default values for thrower
scoreboard players set #default fizzle_enchanting.motion 1

# Default values for the area size (modifiable)
scoreboard players set #default fizzle_enchanting.teluric.side 5
scoreboard players set #default fizzle_enchanting.teluric.long 12
scoreboard players set #default fizzle_enchanting.teluric.radius 5
scoreboard players set #teluric_motion fizzle_enchanting.data 9

# Constants for calculations
scoreboard players set #-1 fizzle_enchanting.data -1
scoreboard players set #0 fizzle_enchanting.data 0
scoreboard players set #1 fizzle_enchanting.data 1
scoreboard players set #2 fizzle_enchanting.data 2
scoreboard players set #3 fizzle_enchanting.data 3
scoreboard players set #4 fizzle_enchanting.data 4
scoreboard players set #5 fizzle_enchanting.data 5
scoreboard players set #6 fizzle_enchanting.data 6
scoreboard players set #7 fizzle_enchanting.data 7
scoreboard players set #8 fizzle_enchanting.data 8
scoreboard players set #9 fizzle_enchanting.data 9
scoreboard players set #10 fizzle_enchanting.data 10
scoreboard players set #11 fizzle_enchanting.data 11
scoreboard players set #12 fizzle_enchanting.data 12
scoreboard players set #13 fizzle_enchanting.data 13
scoreboard players set #14 fizzle_enchanting.data 14
scoreboard players set #15 fizzle_enchanting.data 15
scoreboard players set #16 fizzle_enchanting.data 16
scoreboard players set #17 fizzle_enchanting.data 17
scoreboard players set #18 fizzle_enchanting.data 18
scoreboard players set #19 fizzle_enchanting.data 19
scoreboard players set #20 fizzle_enchanting.data 20
scoreboard players set #21 fizzle_enchanting.data 21
scoreboard players set #22 fizzle_enchanting.data 22
scoreboard players set #23 fizzle_enchanting.data 23
scoreboard players set #24 fizzle_enchanting.data 24
scoreboard players set #25 fizzle_enchanting.data 25
scoreboard players set #26 fizzle_enchanting.data 26
scoreboard players set #27 fizzle_enchanting.data 27
scoreboard players set #28 fizzle_enchanting.data 28
scoreboard players set #29 fizzle_enchanting.data 29
scoreboard players set #30 fizzle_enchanting.data 30
scoreboard players set #36 fizzle_enchanting.data 36
scoreboard players set #40 fizzle_enchanting.data 40 
scoreboard players set #45 fizzle_enchanting.data 45
scoreboard players set #50 fizzle_enchanting.data 50
scoreboard players set #60 fizzle_enchanting.data 60
scoreboard players set #64 fizzle_enchanting.data 64
scoreboard players set #70 fizzle_enchanting.data 70
scoreboard players set #80 fizzle_enchanting.data 80
scoreboard players set #90 fizzle_enchanting.data 90
scoreboard players set #100 fizzle_enchanting.data 100
scoreboard players set #360 fizzle_enchanting.data 360
scoreboard players set #1000 fizzle_enchanting.data 1000

# New scoreboards for the rebound system
scoreboard objectives add fizzle_enchanting.rebound_vx dummy
scoreboard objectives add fizzle_enchanting.rebound_vy dummy
scoreboard objectives add fizzle_enchanting.rebound_vz dummy