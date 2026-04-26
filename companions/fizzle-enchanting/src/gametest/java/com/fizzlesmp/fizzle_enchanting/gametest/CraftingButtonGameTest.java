// Tier: 3 (Fabric Gametest)
package com.fizzlesmp.fizzle_enchanting.gametest;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentLogic;
import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentMenu;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;

/**
 * Tests the crafting-result click path (button id = {@link FizzleEnchantmentLogic#CRAFTING_SLOT}).
 * Uses endshelf blocks (eterna=2.5, quanta=5, arcana=5, maxEterna=45 each) to reach the
 * infused_seashelf recipe thresholds (E≥22.5, Q≥15, A≥10) after baselines (+15Q, +0A for blocks).
 */
public class CraftingButtonGameTest implements FabricGameTest {

    private static final BlockPos TABLE_POS = new BlockPos(4, 1, 4);

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9", timeoutTicks = 100)
    public void craftingRecipeMatchDetected(GameTestHelper helper) {
        Block endshelf = BuiltInRegistries.BLOCK.get(FizzleEnchanting.id("endshelf"));
        if (endshelf == Blocks.AIR) {
            helper.fail("endshelf block not found in registry");
            return;
        }

        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        int placed = 0;
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            helper.setBlock(TABLE_POS.offset(offset), endshelf.defaultBlockState());
            placed++;
            if (placed >= 10) break;
        }

        ResourceLocation seashelfId = FizzleEnchanting.id("seashelf");
        Block seashelfBlock = BuiltInRegistries.BLOCK.get(seashelfId);
        if (seashelfBlock == Blocks.AIR) {
            helper.fail("seashelf block not found — needed as recipe input item");
            return;
        }
        ItemStack seashelfItem = new ItemStack(seashelfBlock);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.experienceLevel = 30;
        BlockPos absTable = helper.absolutePos(TABLE_POS);
        FizzleEnchantmentMenu menu = new FizzleEnchantmentMenu(
                1, player.getInventory(),
                ContainerLevelAccess.create(helper.getLevel(), absTable));

        menu.getSlot(0).set(seashelfItem);
        menu.getSlot(1).set(new ItemStack(Items.LAPIS_LAZULI, 64));

        if (menu.currentRecipe().isEmpty()) {
            helper.fail("With 10 endshelves + seashelf input, a crafting recipe should match. "
                    + "Stats: costs=" + java.util.Arrays.toString(menu.costs));
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9", timeoutTicks = 100)
    public void craftingClickProducesOutput(GameTestHelper helper) {
        Block endshelf = BuiltInRegistries.BLOCK.get(FizzleEnchanting.id("endshelf"));
        if (endshelf == Blocks.AIR) {
            helper.fail("endshelf block not found");
            return;
        }

        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        int placed = 0;
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            helper.setBlock(TABLE_POS.offset(offset), endshelf.defaultBlockState());
            placed++;
            if (placed >= 10) break;
        }

        ResourceLocation seashelfId = FizzleEnchanting.id("seashelf");
        Block seashelfBlock = BuiltInRegistries.BLOCK.get(seashelfId);
        ItemStack seashelfItem = new ItemStack(seashelfBlock);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.experienceLevel = 30;
        BlockPos absTable = helper.absolutePos(TABLE_POS);
        FizzleEnchantmentMenu menu = new FizzleEnchantmentMenu(
                1, player.getInventory(),
                ContainerLevelAccess.create(helper.getLevel(), absTable));

        menu.getSlot(0).set(seashelfItem);
        menu.getSlot(1).set(new ItemStack(Items.LAPIS_LAZULI, 64));

        if (menu.currentRecipe().isEmpty()) {
            helper.fail("Recipe not matched — cannot test crafting click");
            return;
        }

        int xpBefore = player.experienceLevel;
        boolean clicked = menu.clickMenuButton(player, FizzleEnchantmentLogic.CRAFTING_SLOT);
        if (!clicked) {
            helper.fail("clickMenuButton(CRAFTING_SLOT) returned false");
            return;
        }

        ItemStack result = menu.getSlot(0).getItem();
        ResourceLocation infusedId = FizzleEnchanting.id("infused_seashelf");
        if (!BuiltInRegistries.ITEM.getKey(result.getItem()).equals(infusedId)) {
            helper.fail("Expected infused_seashelf in input slot after craft, got "
                    + BuiltInRegistries.ITEM.getKey(result.getItem()));
            return;
        }
        helper.succeed();
    }
}
