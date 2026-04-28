// Tier: 3 (Fabric Gametest)
package com.fizzlesmp.fizzle_enchanting.gametest;

import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentMenu;
import com.fizzlesmp.fizzle_enchanting.enchanting.StatCollection;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;

public class MenuEndToEndGameTest implements FabricGameTest {

    private static final BlockPos TABLE_POS = new BlockPos(4, 1, 4);

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void menuTypeIsFizzleEnchantmentMenu(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos absTable = helper.absolutePos(TABLE_POS);
        FizzleEnchantmentMenu menu = new FizzleEnchantmentMenu(
                1, player.getInventory(),
                ContainerLevelAccess.create(helper.getLevel(), absTable));

        if (!(menu instanceof FizzleEnchantmentMenu)) {
            helper.fail("Menu is not FizzleEnchantmentMenu");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void clickSlotZeroAppliesEnchant(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            helper.setBlock(TABLE_POS.offset(offset), Blocks.BOOKSHELF.defaultBlockState());
        }

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.experienceLevel = 30;
        BlockPos absTable = helper.absolutePos(TABLE_POS);
        FizzleEnchantmentMenu menu = new FizzleEnchantmentMenu(
                1, player.getInventory(),
                ContainerLevelAccess.create(helper.getLevel(), absTable));

        menu.getSlot(0).set(new ItemStack(Items.DIAMOND_SWORD));
        menu.getSlot(1).set(new ItemStack(Items.LAPIS_LAZULI, 64));

        if (menu.costs[0] <= 0) {
            helper.fail("Slot 0 cost should be > 0 with 15 bookshelves, got " + menu.costs[0]);
            return;
        }

        int xpBefore = player.experienceLevel;
        boolean clicked = menu.clickMenuButton(player, 0);
        if (!clicked) {
            helper.fail("clickMenuButton(0) returned false");
            return;
        }

        ItemStack result = menu.getSlot(0).getItem();
        if (!result.isEnchanted()) {
            helper.fail("Sword should be enchanted after clicking slot 0");
            return;
        }
        if (player.experienceLevel >= xpBefore) {
            helper.fail("XP should have decreased after enchanting");
            return;
        }
        helper.succeed();
    }

    // --- S-5.1b: Menu has item slot (slot 0) and lapis slot (slot 1) ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void menuHasItemAndLapisSlots(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos absTable = helper.absolutePos(TABLE_POS);
        FizzleEnchantmentMenu menu = new FizzleEnchantmentMenu(
                1, player.getInventory(),
                ContainerLevelAccess.create(helper.getLevel(), absTable));

        menu.getSlot(0).set(new ItemStack(Items.DIAMOND_SWORD));
        if (menu.getSlot(0).getItem().isEmpty()) {
            helper.fail("Slot 0 (item slot) should accept a diamond sword");
            return;
        }

        menu.getSlot(1).set(new ItemStack(Items.LAPIS_LAZULI, 3));
        if (menu.getSlot(1).getItem().isEmpty()) {
            helper.fail("Slot 1 (lapis slot) should accept lapis lazuli");
            return;
        }

        helper.succeed();
    }

    // --- S-5.1c: Stats are computed and stored on menu when item is placed ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void statsComputedWhenItemPlaced(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            helper.setBlock(TABLE_POS.offset(offset), Blocks.BOOKSHELF.defaultBlockState());
        }

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos absTable = helper.absolutePos(TABLE_POS);
        FizzleEnchantmentMenu menu = new FizzleEnchantmentMenu(
                1, player.getInventory(),
                ContainerLevelAccess.create(helper.getLevel(), absTable));

        menu.getSlot(0).set(new ItemStack(Items.DIAMOND_SWORD));
        menu.getSlot(1).set(new ItemStack(Items.LAPIS_LAZULI, 64));

        StatCollection stats = menu.getLastStats();
        if (stats.eterna() <= 0) {
            helper.fail("Stats should have eterna > 0 with bookshelves, got " + stats.eterna());
            return;
        }
        if (stats.quanta() <= 0) {
            helper.fail("Stats should have quanta > 0 (baseline +15), got " + stats.quanta());
            return;
        }

        helper.succeed();
    }

    // --- S-5.1d: Costs array has 3 entries corresponding to 3 enchanting slots ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void costsArrayHasThreeEntries(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            helper.setBlock(TABLE_POS.offset(offset), Blocks.BOOKSHELF.defaultBlockState());
        }

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.experienceLevel = 30;
        BlockPos absTable = helper.absolutePos(TABLE_POS);
        FizzleEnchantmentMenu menu = new FizzleEnchantmentMenu(
                1, player.getInventory(),
                ContainerLevelAccess.create(helper.getLevel(), absTable));

        if (menu.costs.length != 3) {
            helper.fail("Costs array should have 3 entries, got " + menu.costs.length);
            return;
        }

        menu.getSlot(0).set(new ItemStack(Items.DIAMOND_SWORD));
        menu.getSlot(1).set(new ItemStack(Items.LAPIS_LAZULI, 64));

        boolean anyCostSet = menu.costs[0] > 0 || menu.costs[1] > 0 || menu.costs[2] > 0;
        if (!anyCostSet) {
            helper.fail("At least one cost slot should be > 0 with bookshelves and a sword");
            return;
        }

        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void clickSlotZeroDecrementsLapis(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            helper.setBlock(TABLE_POS.offset(offset), Blocks.BOOKSHELF.defaultBlockState());
        }

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.experienceLevel = 30;
        BlockPos absTable = helper.absolutePos(TABLE_POS);
        FizzleEnchantmentMenu menu = new FizzleEnchantmentMenu(
                1, player.getInventory(),
                ContainerLevelAccess.create(helper.getLevel(), absTable));

        menu.getSlot(0).set(new ItemStack(Items.DIAMOND_SWORD));
        menu.getSlot(1).set(new ItemStack(Items.LAPIS_LAZULI, 64));

        int lapisBefore = menu.getSlot(1).getItem().getCount();
        menu.clickMenuButton(player, 0);
        int lapisAfter = menu.getSlot(1).getItem().getCount();

        if (lapisAfter >= lapisBefore) {
            helper.fail("Lapis should decrease after enchanting, was " + lapisBefore + " now " + lapisAfter);
            return;
        }
        helper.succeed();
    }
}
