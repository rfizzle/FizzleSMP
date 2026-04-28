# Create scoreboard objectives
scoreboard objectives add meridian.data dummy

# New scoreboards for the Teluric Wave area
scoreboard objectives add meridian.teluric.side dummy
scoreboard objectives add meridian.teluric.long dummy
scoreboard objectives add meridian.animation_state dummy
scoreboard objectives add meridian.gametime dummy
scoreboard objectives add meridian.gametime.temp dummy
scoreboard objectives add meridian.teluric.radius dummy
scoreboard objectives add meridian.striker.countdown dummy
scoreboard objectives add meridian.striker.gametime dummy

scoreboard objectives add meridian.transformation.w dummy
scoreboard objectives add meridian.transformation.x dummy
scoreboard objectives add meridian.transformation.y dummy
scoreboard objectives add meridian.transformation.z dummy

# New scoreboards for the Thrower enchantment
scoreboard objectives add meridian.timer dummy
scoreboard objectives add meridian.rot dummy
scoreboard objectives add meridian.motion dummy
scoreboard objectives add meridian.var dummy

# Default values for thrower
scoreboard players set #default meridian.motion 1

# Default values for the area size (modifiable)
scoreboard players set #default meridian.teluric.side 5
scoreboard players set #default meridian.teluric.long 12
scoreboard players set #default meridian.teluric.radius 5
scoreboard players set #teluric_motion meridian.data 9

# Constants for calculations
scoreboard players set #-1 meridian.data -1
scoreboard players set #0 meridian.data 0
scoreboard players set #1 meridian.data 1
scoreboard players set #2 meridian.data 2
scoreboard players set #3 meridian.data 3
scoreboard players set #4 meridian.data 4
scoreboard players set #5 meridian.data 5
scoreboard players set #6 meridian.data 6
scoreboard players set #7 meridian.data 7
scoreboard players set #8 meridian.data 8
scoreboard players set #9 meridian.data 9
scoreboard players set #10 meridian.data 10
scoreboard players set #11 meridian.data 11
scoreboard players set #12 meridian.data 12
scoreboard players set #13 meridian.data 13
scoreboard players set #14 meridian.data 14
scoreboard players set #15 meridian.data 15
scoreboard players set #16 meridian.data 16
scoreboard players set #17 meridian.data 17
scoreboard players set #18 meridian.data 18
scoreboard players set #19 meridian.data 19
scoreboard players set #20 meridian.data 20
scoreboard players set #21 meridian.data 21
scoreboard players set #22 meridian.data 22
scoreboard players set #23 meridian.data 23
scoreboard players set #24 meridian.data 24
scoreboard players set #25 meridian.data 25
scoreboard players set #26 meridian.data 26
scoreboard players set #27 meridian.data 27
scoreboard players set #28 meridian.data 28
scoreboard players set #29 meridian.data 29
scoreboard players set #30 meridian.data 30
scoreboard players set #36 meridian.data 36
scoreboard players set #40 meridian.data 40 
scoreboard players set #45 meridian.data 45
scoreboard players set #50 meridian.data 50
scoreboard players set #60 meridian.data 60
scoreboard players set #64 meridian.data 64
scoreboard players set #70 meridian.data 70
scoreboard players set #80 meridian.data 80
scoreboard players set #90 meridian.data 90
scoreboard players set #100 meridian.data 100
scoreboard players set #360 meridian.data 360
scoreboard players set #1000 meridian.data 1000

# New scoreboards for the rebound system
scoreboard objectives add meridian.rebound_vx dummy
scoreboard objectives add meridian.rebound_vy dummy
scoreboard objectives add meridian.rebound_vz dummy