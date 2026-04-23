// Tier: 3 (Fabric Gametest)
package com.fizzlesmp.fizzle_enchanting.gametest;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.enchanting.EnchantingStatRegistry;
import com.fizzlesmp.fizzle_enchanting.enchanting.StatCollection;
import com.fizzlesmp.fizzle_enchanting.shelf.FilteringShelfBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;

import java.util.Set;

public class ShelfScanGameTest implements FabricGameTest {

    private static final BlockPos TABLE_POS = new BlockPos(4, 1, 4);

    // --- TEST-2.2-T3a: vanilla bookshelf scan ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void vanillaBookshelvesSumToFifteenEterna(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            helper.setBlock(TABLE_POS.offset(offset), Blocks.BOOKSHELF.defaultBlockState());
        }

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        float expectedEterna = Math.min(EnchantingTableBlock.BOOKSHELF_OFFSETS.size(), 15F);
        if (Math.abs(stats.eterna() - expectedEterna) > 1e-6) {
            helper.fail("Expected eterna=" + expectedEterna + " with all vanilla bookshelves, got " + stats.eterna());
            return;
        }
        if (Math.abs(stats.maxEterna() - 15F) > 1e-6) {
            helper.fail("Expected maxEterna=15, got " + stats.maxEterna());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void blockingMidpointReducesEternaByOne(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            helper.setBlock(TABLE_POS.offset(offset), Blocks.BOOKSHELF.defaultBlockState());
        }

        BlockPos blockedOffset = EnchantingTableBlock.BOOKSHELF_OFFSETS.get(0);
        BlockPos midpoint = new BlockPos(
                blockedOffset.getX() / 2, blockedOffset.getY(), blockedOffset.getZ() / 2);
        helper.setBlock(TABLE_POS.offset(midpoint), Blocks.STONE.defaultBlockState());

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        int unblocked = EnchantingTableBlock.BOOKSHELF_OFFSETS.size() - 1;
        float expectedEterna = Math.min(unblocked, 15F);
        if (Math.abs(stats.eterna() - expectedEterna) > 1e-6) {
            helper.fail("Expected eterna=" + expectedEterna + " after blocking one midpoint, got " + stats.eterna());
            return;
        }
        helper.succeed();
    }

    // --- TEST-2.2-T3b: filtering/treasure shelf scan via real gatherStats ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void twoFilteringShelvesProduceTwoEntryBlacklist(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());

        BlockPos offsetA = EnchantingTableBlock.BOOKSHELF_OFFSETS.get(0);
        BlockPos offsetB = EnchantingTableBlock.BOOKSHELF_OFFSETS.get(1);
        helper.setBlock(TABLE_POS.offset(offsetA), FizzleEnchantingRegistry.FILTERING_SHELF.defaultBlockState());
        helper.setBlock(TABLE_POS.offset(offsetB), FizzleEnchantingRegistry.FILTERING_SHELF.defaultBlockState());

        FilteringShelfBlockEntity beA = (FilteringShelfBlockEntity) helper.getLevel()
                .getBlockEntity(helper.absolutePos(TABLE_POS.offset(offsetA)));
        FilteringShelfBlockEntity beB = (FilteringShelfBlockEntity) helper.getLevel()
                .getBlockEntity(helper.absolutePos(TABLE_POS.offset(offsetB)));
        if (beA == null || beB == null) {
            helper.fail("FilteringShelfBlockEntity not created at offset positions");
            return;
        }

        beA.setItem(0, enchantedBook(helper, Enchantments.SHARPNESS, 1));
        beB.setItem(0, enchantedBook(helper, Enchantments.UNBREAKING, 1));

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        Set<ResourceKey<Enchantment>> bl = stats.blacklist();
        if (bl.size() != 2 || !bl.contains(Enchantments.SHARPNESS) || !bl.contains(Enchantments.UNBREAKING)) {
            helper.fail("Expected blacklist {SHARPNESS, UNBREAKING}, got " + bl);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void treasureShelfInRangeSetsTreasureAllowed(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());

        BlockPos offset = EnchantingTableBlock.BOOKSHELF_OFFSETS.get(0);
        helper.setBlock(TABLE_POS.offset(offset), FizzleEnchantingRegistry.TREASURE_SHELF.defaultBlockState());

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        if (!stats.treasureAllowed()) {
            helper.fail("Treasure shelf in range should set treasureAllowed=true");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void removingTreasureShelfClearsTreasureAllowed(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());

        BlockPos offset = EnchantingTableBlock.BOOKSHELF_OFFSETS.get(0);
        helper.setBlock(TABLE_POS.offset(offset), FizzleEnchantingRegistry.TREASURE_SHELF.defaultBlockState());

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection withShelf = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);
        if (!withShelf.treasureAllowed()) {
            helper.fail("Treasure shelf should have set treasureAllowed=true");
            return;
        }

        helper.setBlock(TABLE_POS.offset(offset), Blocks.AIR.defaultBlockState());

        StatCollection without = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);
        if (without.treasureAllowed()) {
            helper.fail("After removing treasure shelf, treasureAllowed should be false");
            return;
        }
        helper.succeed();
    }

    // --- Helpers ---

    private static ItemStack enchantedBook(GameTestHelper helper, ResourceKey<Enchantment> key, int level) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> holder = reg.getHolderOrThrow(key);
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(holder, level);
        stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
        return stack;
    }
}
