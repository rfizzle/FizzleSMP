# Visuals & Shaders Guide

FizzleSMP includes shader support, dynamic lighting, enhanced animations, and atmospheric audio. Most of these are client-side and can be toggled to fit your hardware.

---

## Shaders — Iris + Complementary

The pack includes **Iris Shaders** (the shader loader) and two shader packs pre-installed:

### Complementary Shaders — Reimagined
A cinematic, stylized look with vibrant colors and soft lighting.

### Complementary Shaders — Unbound
A more realistic variant with natural lighting and atmospheric effects.

### Euphoria Patches
An addon that enhances both Complementary shader packs with additional visual effects and fixes.

### How to Use
1. Press **O** (default) to open the Iris shader settings.
2. Select a shader pack from the list.
3. Click **Apply** — shaders will load (may take a moment).
4. To disable shaders, select "OFF" or press **K** (default toggle).

### Performance
Shaders have a significant performance cost. If your FPS drops:
- Try Complementary Reimagined first (generally lighter than Unbound).
- Lower shader quality in the shader settings.
- Disable shaders entirely with **K** — the pack looks great without them too.

---

## Dynamic Lighting — LambDynamicLights

Holding a torch, lantern, or other light source **illuminates your surroundings in real-time** without placing the block. Works in your hand and when dropped on the ground.

- Torches glow while held
- Lava buckets illuminate nearby blocks
- Dropped glowstone lights up where it lands
- Configurable intensity via Mod Menu

---

## Visual Enhancements

| Mod | What It Does |
|---|---|
| **Continuity** | Connected textures — glass panes, bookshelves, and other blocks connect seamlessly |
| **Falling Leaves** | Leaf blocks emit falling leaf particles |
| **Visuality** | Extra particle effects — hit sparks, crystal sparkles, water splashes |
| **Not Enough Animations** | Third-person animations for eating, drinking, using maps, spyglasses, etc. |
| **Enhanced Block Entities** | Faster rendering for chests, signs, beds, and other block entities |
| **Colorful Hearts** | Health bar shows colored hearts based on armor and effects |
| **Traveler's Titles** | Shows a title card with the biome/area name when entering new regions |
| **Blur+** | Blurs the background when opening inventories and menus |

---

## Audio Enhancements

| Mod | What It Does |
|---|---|
| **AmbientSounds** | Adds environmental soundscapes — birds, wind, water, cave ambience based on your surroundings |
| **Sound Physics Remastered** | Sound reverberates in caves and large rooms. Muffled through walls. Realistic audio propagation |
| **Presence Footsteps** | Footstep sounds change based on the surface you're walking on (wood, stone, gravel, etc.) |

---

## Performance Mods (Running in Background)

These run automatically and don't need configuration:

| Mod | What It Does |
|---|---|
| **Sodium** | Rendering engine replacement — massive FPS improvement |
| **Lithium** | Optimizes game logic (mob AI, world gen, block ticking) |
| **FerriteCore** | Reduces memory usage |
| **ModernFix** | Various performance and bug fixes |
| **Entity Culling** | Skips rendering entities you can't see |
| **More Culling** | Extends culling to more block types |
| **ImmediatelyFast** | Optimizes immediate-mode rendering |
| **C2ME** | Faster chunk generation and loading |
| **NoisiumForked** | Optimized worldgen noise calculations |
| **Dynamic FPS** | Reduces FPS when the game window is unfocused |
| **Krypton** | Optimized networking |

---

## Tips

- If you're on lower-end hardware, the pack runs well even without shaders — all the performance mods are active regardless.
- **Sodium Extra** (in Mod Menu > Video Settings) gives extra graphics toggles beyond vanilla options.
- Press **K** to quickly toggle shaders on/off for screenshots vs. gameplay.
- **BetterF3** replaces the debug screen with a cleaner, more readable layout. Press **F3** to see it.
- **Controlify** adds full controller/gamepad support if you prefer playing with a controller.
