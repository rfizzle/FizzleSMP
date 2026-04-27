package com.fizzlesmp.fizzle_enchanting.compat.jade;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

/**
 * Suppresses Jade's default "Collecting items..." tooltip on library blocks. The library exposes
 * a Fabric Transfer API {@code Storage<ItemVariant>} for hopper automation, which Jade's built-in
 * {@code ItemStorageProvider} auto-detects. Registering this no-op provider for the library BE
 * class overrides the default and returns an empty group list so no inventory line appears.
 */
final class LibraryItemStorageProvider implements IServerExtensionProvider<ItemStack> {

    static final LibraryItemStorageProvider INSTANCE = new LibraryItemStorageProvider();

    private static final ResourceLocation UID = FizzleEnchanting.id("library_no_items");

    private LibraryItemStorageProvider() {
    }

    @Override
    public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        return List.of();
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
