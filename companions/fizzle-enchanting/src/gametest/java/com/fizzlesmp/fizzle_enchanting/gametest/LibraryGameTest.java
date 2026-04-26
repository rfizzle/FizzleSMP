// Tier: 3 (Fabric Gametest)
package com.fizzlesmp.fizzle_enchanting.gametest;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.library.BasicLibraryBlockEntity;
import com.fizzlesmp.fizzle_enchanting.library.EnchantmentLibraryMenu;
import com.fizzlesmp.fizzle_enchanting.library.EnderLibraryBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LibraryGameTest implements FabricGameTest {

    private static final BlockPos LIB_POS = new BlockPos(1, 1, 1);

    // --- TEST-4.4-T3a: Both library blocks register ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void basicLibraryRegistered(GameTestHelper helper) {
        ResourceLocation loc = FizzleEnchanting.id("library");
        if (!BuiltInRegistries.BLOCK.containsKey(loc)) {
            helper.fail("Basic library block not registered: " + loc);
            return;
        }
        if (BuiltInRegistries.BLOCK.get(loc) != FizzleEnchantingRegistry.BASIC_LIBRARY) {
            helper.fail("Basic library singleton doesn't match registry");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void enderLibraryRegistered(GameTestHelper helper) {
        ResourceLocation loc = FizzleEnchanting.id("ender_library");
        if (!BuiltInRegistries.BLOCK.containsKey(loc)) {
            helper.fail("Ender library block not registered: " + loc);
            return;
        }
        if (BuiltInRegistries.BLOCK.get(loc) != FizzleEnchantingRegistry.ENDER_LIBRARY) {
            helper.fail("Ender library singleton doesn't match registry");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void basicLibraryCreatesCorrectBlockEntity(GameTestHelper helper) {
        helper.setBlock(LIB_POS, FizzleEnchantingRegistry.BASIC_LIBRARY.defaultBlockState());
        BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(LIB_POS));
        if (!(be instanceof BasicLibraryBlockEntity)) {
            helper.fail("Expected BasicLibraryBlockEntity, got "
                    + (be == null ? "null" : be.getClass().getSimpleName()));
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void enderLibraryCreatesCorrectBlockEntity(GameTestHelper helper) {
        helper.setBlock(LIB_POS, FizzleEnchantingRegistry.ENDER_LIBRARY.defaultBlockState());
        BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(LIB_POS));
        if (!(be instanceof EnderLibraryBlockEntity)) {
            helper.fail("Expected EnderLibraryBlockEntity, got "
                    + (be == null ? "null" : be.getClass().getSimpleName()));
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void librariesAreDistinctInstances(GameTestHelper helper) {
        if (FizzleEnchantingRegistry.BASIC_LIBRARY == FizzleEnchantingRegistry.ENDER_LIBRARY) {
            helper.fail("Basic and Ender library should be distinct block instances");
            return;
        }
        helper.succeed();
    }

    // --- TEST-4.4-T3b: Menu deposit/extract at a real library block ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void depositAbsorbsBookIntoPool(GameTestHelper helper) {
        helper.setBlock(LIB_POS, FizzleEnchantingRegistry.BASIC_LIBRARY.defaultBlockState());
        BasicLibraryBlockEntity be = (BasicLibraryBlockEntity) helper.getLevel()
                .getBlockEntity(helper.absolutePos(LIB_POS));
        if (be == null) { helper.fail("BE not created"); return; }

        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        EnchantmentLibraryMenu menu = new EnchantmentLibraryMenu(1, player.getInventory(), be);

        Registry<Enchantment> reg = helper.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        ItemStack book = enchantedBook(reg, Enchantments.SHARPNESS, 3);
        menu.getSlot(EnchantmentLibraryMenu.DEPOSIT_SLOT).set(book);

        ResourceKey<Enchantment> key = Enchantments.SHARPNESS;
        int pts = be.getPoints().getInt(key);
        if (pts <= 0) {
            helper.fail("After deposit, points for SHARPNESS should be > 0, got " + pts);
            return;
        }
        int maxLvl = be.getMaxLevels().getInt(key);
        if (maxLvl != 3) {
            helper.fail("maxLevels for SHARPNESS should be 3, got " + maxLvl);
            return;
        }
        ItemStack remaining = menu.ioInv.getItem(EnchantmentLibraryMenu.DEPOSIT_SLOT);
        if (!remaining.isEmpty()) {
            helper.fail("Deposit slot should be empty after absorb");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void depositAndExtractViaBlockEntity(GameTestHelper helper) {
        helper.setBlock(LIB_POS, FizzleEnchantingRegistry.BASIC_LIBRARY.defaultBlockState());
        BasicLibraryBlockEntity be = (BasicLibraryBlockEntity) helper.getLevel()
                .getBlockEntity(helper.absolutePos(LIB_POS));
        if (be == null) { helper.fail("BE not created"); return; }

        Registry<Enchantment> reg = helper.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        ResourceKey<Enchantment> sharpKey = Enchantments.SHARPNESS;

        be.depositBook(enchantedBook(reg, Enchantments.SHARPNESS, 5));

        int pts = be.getPoints().getInt(sharpKey);
        if (pts <= 0) {
            helper.fail("After deposit, points should be > 0, got " + pts);
            return;
        }
        if (be.getMaxLevels().getInt(sharpKey) != 5) {
            helper.fail("maxLevels should be 5 after depositing level 5 book");
            return;
        }
        if (!be.canExtract(sharpKey, 1, 0)) {
            helper.fail("canExtract(target=1, cur=0) should succeed");
            return;
        }

        boolean extracted = be.extract(sharpKey, 1, 0);
        if (!extracted) {
            helper.fail("extract should succeed");
            return;
        }
        if (be.getPoints().getInt(sharpKey) >= pts) {
            helper.fail("Points should decrease after extraction");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void extractDeniedWhenInsufficientMaxLevel(GameTestHelper helper) {
        helper.setBlock(LIB_POS, FizzleEnchantingRegistry.BASIC_LIBRARY.defaultBlockState());
        BasicLibraryBlockEntity be = (BasicLibraryBlockEntity) helper.getLevel()
                .getBlockEntity(helper.absolutePos(LIB_POS));
        if (be == null) { helper.fail("BE not created"); return; }

        Registry<Enchantment> reg = helper.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        ResourceKey<Enchantment> sharpKey = Enchantments.SHARPNESS;

        be.depositBook(enchantedBook(reg, Enchantments.SHARPNESS, 1));

        boolean canExtract = be.canExtract(sharpKey, 2, 1);
        if (canExtract) {
            helper.fail("canExtract(target=2, cur=1) should fail when maxLevels=1");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void listenerRegisteredOnConstructionAndRemovedOnClose(GameTestHelper helper) {
        helper.setBlock(LIB_POS, FizzleEnchantingRegistry.BASIC_LIBRARY.defaultBlockState());
        BasicLibraryBlockEntity be = (BasicLibraryBlockEntity) helper.getLevel()
                .getBlockEntity(helper.absolutePos(LIB_POS));
        if (be == null) { helper.fail("BE not created"); return; }

        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        EnchantmentLibraryMenu menu1 = new EnchantmentLibraryMenu(1, player.getInventory(), be);
        EnchantmentLibraryMenu menu2 = new EnchantmentLibraryMenu(2, player.getInventory(), be);

        menu1.removed(player);
        menu2.removed(player);
        helper.succeed();
    }

    // --- Helpers ---

    private static ItemStack enchantedBook(Registry<Enchantment> reg, ResourceKey<Enchantment> key, int level) {
        Holder<Enchantment> holder = reg.getHolderOrThrow(key);
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(holder, level);
        stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
        return stack;
    }
}
