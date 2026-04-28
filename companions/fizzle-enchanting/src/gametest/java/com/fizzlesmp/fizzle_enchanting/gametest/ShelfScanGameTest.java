// Tier: 3 (Fabric Gametest)
package com.fizzlesmp.fizzle_enchanting.gametest;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.enchanting.EnchantingStatRegistry;
import com.fizzlesmp.fizzle_enchanting.enchanting.StatCollection;
import com.fizzlesmp.fizzle_enchanting.shelf.FilteringShelfBlockEntity;
import com.fizzlesmp.fizzle_enchanting.shelf.FizzleShelves;
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

import java.util.List;
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

    // --- S-2.1a: no shelves → zero raw stats ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void noShelvesProducesZeroStats(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        if (Math.abs(stats.eterna()) > 1e-6) {
            helper.fail("Expected eterna=0 with no shelves, got " + stats.eterna());
            return;
        }
        if (Math.abs(stats.quanta()) > 1e-6) {
            helper.fail("Expected quanta=0 with no shelves, got " + stats.quanta());
            return;
        }
        if (Math.abs(stats.arcana()) > 1e-6) {
            helper.fail("Expected arcana=0 with no shelves, got " + stats.arcana());
            return;
        }
        if (stats.clues() != 0) {
            helper.fail("Expected clues=0 with no shelves, got " + stats.clues());
            return;
        }
        helper.succeed();
    }

    // --- S-2.2a: single hellshelf → eterna=1.5, quanta=3 ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void singleHellshelfProducesCorrectStats(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        BlockPos offset = EnchantingTableBlock.BOOKSHELF_OFFSETS.get(0);
        helper.setBlock(TABLE_POS.offset(offset), FizzleShelves.HELLSHELF.defaultBlockState());

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        if (Math.abs(stats.eterna() - 1.5F) > 1e-3) {
            helper.fail("Expected eterna=1.5 for single hellshelf, got " + stats.eterna());
            return;
        }
        if (Math.abs(stats.quanta() - 3F) > 1e-3) {
            helper.fail("Expected quanta=3 for single hellshelf, got " + stats.quanta());
            return;
        }
        if (Math.abs(stats.maxEterna() - 22.5F) > 1e-3) {
            helper.fail("Expected maxEterna=22.5 for hellshelf, got " + stats.maxEterna());
            return;
        }
        helper.succeed();
    }

    // --- S-2.2b: 15 hellshelves → eterna capped at maxEterna=22.5 ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void fifteenHellshelvesCapEternaAtMaxEterna(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        List<BlockPos> offsets = EnchantingTableBlock.BOOKSHELF_OFFSETS;
        for (int i = 0; i < 15 && i < offsets.size(); i++) {
            helper.setBlock(TABLE_POS.offset(offsets.get(i)), FizzleShelves.HELLSHELF.defaultBlockState());
        }

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        if (Math.abs(stats.eterna() - 22.5F) > 1e-3) {
            helper.fail("Expected eterna=22.5 for 15 hellshelves (15×1.5 capped at maxE=22.5), got " + stats.eterna());
            return;
        }
        if (Math.abs(stats.quanta() - 45F) > 1e-3) {
            helper.fail("Expected quanta=45 for 15 hellshelves (15×3), got " + stats.quanta());
            return;
        }
        helper.succeed();
    }

    // --- S-2.2c: 15 vanilla bookshelves + 1 hellshelf → step-ladder eterna ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void vanillaPlusHellshelfUsesStepLadderEterna(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        List<BlockPos> offsets = EnchantingTableBlock.BOOKSHELF_OFFSETS;
        for (int i = 0; i < 15 && i < offsets.size(); i++) {
            helper.setBlock(TABLE_POS.offset(offsets.get(i)), Blocks.BOOKSHELF.defaultBlockState());
        }
        helper.setBlock(TABLE_POS.offset(offsets.get(15)), FizzleShelves.HELLSHELF.defaultBlockState());

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        // Step-ladder: vanilla tier (maxE=15): min(15, 15×1) = 15
        //              hellshelf tier (maxE=22.5): min(22.5, 15 + 1.5) = 16.5
        if (Math.abs(stats.eterna() - 16.5F) > 1e-3) {
            helper.fail("Expected eterna=16.5 (step-ladder: 15 vanilla + 1 hellshelf), got " + stats.eterna());
            return;
        }
        if (Math.abs(stats.quanta() - 3F) > 1e-3) {
            helper.fail("Expected quanta=3 (1 hellshelf contribution), got " + stats.quanta());
            return;
        }
        helper.succeed();
    }

    // --- S-2.2d: draconic endshelf → eterna=10, maxEterna=50 ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void draconicEndshelfProducesHighEternaAndMaxEterna(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        BlockPos offset = EnchantingTableBlock.BOOKSHELF_OFFSETS.get(0);
        helper.setBlock(TABLE_POS.offset(offset), FizzleShelves.DRACONIC_ENDSHELF.defaultBlockState());

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        if (Math.abs(stats.eterna() - 10F) > 1e-3) {
            helper.fail("Expected eterna=10 for draconic endshelf, got " + stats.eterna());
            return;
        }
        if (Math.abs(stats.maxEterna() - 50F) > 1e-3) {
            helper.fail("Expected maxEterna=50 for draconic endshelf, got " + stats.maxEterna());
            return;
        }
        helper.succeed();
    }

    // --- S-2.2e: stoneshelf → negative eterna/arcana clamped to 0 ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void stoneshelfNegativeStatsClamped(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        BlockPos offset = EnchantingTableBlock.BOOKSHELF_OFFSETS.get(0);
        helper.setBlock(TABLE_POS.offset(offset), FizzleShelves.STONESHELF.defaultBlockState());

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        if (Math.abs(stats.eterna()) > 1e-6) {
            helper.fail("Expected eterna=0 (clamped from -1.5) for stoneshelf, got " + stats.eterna());
            return;
        }
        if (Math.abs(stats.arcana()) > 1e-6) {
            helper.fail("Expected arcana=0 (clamped from -7.5) for stoneshelf, got " + stats.arcana());
            return;
        }
        helper.succeed();
    }

    // --- S-2.2f: beeshelf → quanta=100, eterna clamped to 0 ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void beeshelfMaxQuantaAndNegativeEterna(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        BlockPos offset = EnchantingTableBlock.BOOKSHELF_OFFSETS.get(0);
        helper.setBlock(TABLE_POS.offset(offset), FizzleShelves.BEESHELF.defaultBlockState());

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        if (Math.abs(stats.quanta() - 100F) > 1e-3) {
            helper.fail("Expected quanta=100 for beeshelf, got " + stats.quanta());
            return;
        }
        if (Math.abs(stats.eterna()) > 1e-6) {
            helper.fail("Expected eterna=0 (clamped from -15) for beeshelf, got " + stats.eterna());
            return;
        }
        helper.succeed();
    }

    // --- S-2.2g: melonshelf → negative quanta and eterna clamped to 0 ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void melonshelfNegativeQuantaAndEternaClamped(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        BlockPos offset = EnchantingTableBlock.BOOKSHELF_OFFSETS.get(0);
        helper.setBlock(TABLE_POS.offset(offset), FizzleShelves.MELONSHELF.defaultBlockState());

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        if (Math.abs(stats.eterna()) > 1e-6) {
            helper.fail("Expected eterna=0 (clamped from -1) for melonshelf, got " + stats.eterna());
            return;
        }
        if (Math.abs(stats.quanta()) > 1e-6) {
            helper.fail("Expected quanta=0 (clamped from -10) for melonshelf, got " + stats.quanta());
            return;
        }
        helper.succeed();
    }

    // --- S-2.2h: sightshelf → only clues, no eterna/quanta/arcana ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void sightshelfContributesOnlyClues(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        BlockPos offset = EnchantingTableBlock.BOOKSHELF_OFFSETS.get(0);
        helper.setBlock(TABLE_POS.offset(offset), FizzleShelves.SIGHTSHELF.defaultBlockState());

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        if (stats.clues() != 1) {
            helper.fail("Expected clues=1 for sightshelf, got " + stats.clues());
            return;
        }
        if (Math.abs(stats.eterna()) > 1e-6) {
            helper.fail("Expected eterna=0 for sightshelf, got " + stats.eterna());
            return;
        }
        if (Math.abs(stats.quanta()) > 1e-6) {
            helper.fail("Expected quanta=0 for sightshelf, got " + stats.quanta());
            return;
        }
        if (Math.abs(stats.arcana()) > 1e-6) {
            helper.fail("Expected arcana=0 for sightshelf, got " + stats.arcana());
            return;
        }
        helper.succeed();
    }

    // --- S-2.2i: sightshelf_t2 → 2 clues ---

    @GameTest(template = "fizzle_enchanting:shelf_scan_9x4x9")
    public void sightshelfT2ContributesTwoClues(GameTestHelper helper) {
        helper.setBlock(TABLE_POS, Blocks.ENCHANTING_TABLE.defaultBlockState());
        BlockPos offset = EnchantingTableBlock.BOOKSHELF_OFFSETS.get(0);
        helper.setBlock(TABLE_POS.offset(offset), FizzleShelves.SIGHTSHELF_T2.defaultBlockState());

        BlockPos absTablePos = helper.absolutePos(TABLE_POS);
        StatCollection stats = EnchantingStatRegistry.gatherStats(helper.getLevel(), absTablePos);

        if (stats.clues() != 2) {
            helper.fail("Expected clues=2 for sightshelf_t2, got " + stats.clues());
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
