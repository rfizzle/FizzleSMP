package com.fizzlesmp.fizzle_enchanting.sound;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public final class ModSounds {

    public static final Holder<SoundEvent> MUSIC_DISC_ETERNA = register("music_disc.eterna");
    public static final Holder<SoundEvent> MUSIC_DISC_QUANTA = register("music_disc.quanta");
    public static final Holder<SoundEvent> MUSIC_DISC_ARCANA = register("music_disc.arcana");

    private static boolean registered = false;

    private ModSounds() {
    }

    public static void register() {
        if (registered) return;
        registered = true;
        // Fields are registered eagerly in their static initializers via the register() helper.
        // This method exists to force class loading at the right time in the init chain.
    }

    private static Holder<SoundEvent> register(String path) {
        ResourceLocation id = FizzleEnchanting.id(path);
        SoundEvent event = SoundEvent.createVariableRangeEvent(id);
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, id, event);
    }
}
