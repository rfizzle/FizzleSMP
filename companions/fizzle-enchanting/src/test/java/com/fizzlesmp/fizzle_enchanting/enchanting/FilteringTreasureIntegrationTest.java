package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.shelf.FilteringShelfBlockEntity;
import com.fizzlesmp.fizzle_enchanting.shelf.TreasureShelfBlockEntity;
import com.mojang.serialization.Lifecycle;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-3.5.3 — pins the live-BE wiring promised by Story S-3.5: T-2.2.4's stub-only context lookup
 * now resolves to real {@link FilteringShelfBlockEntity} and {@link TreasureShelfBlockEntity}
 * instances. The previous gather-pipeline tests
 * ({@link com.fizzlesmp.fizzle_enchanting.enchanting.EnchantingStatRegistryGatherTest}) prove
 * the {@link BlacklistSource} / {@link TreasureFlagSource} hooks fire for synthetic stubs;
 * this test repeats the same assertions with the registered subclasses so a regression in either
 * BE — losing the interface, returning the wrong blacklist shape, swallowing books — is caught
 * here even when the abstract gather tests still pass.
 *
 * <p>The test drives the package-private {@code gatherStatsFromOffsets} overload — the same one
 * used by the {@code (Level, BlockPos)} entry point — with a {@link Function} that returns the
 * real BE for each offset. That mirrors how vanilla's {@code level.getBlockEntity(pos)} feeds
 * into the production pipeline without standing up a full {@code Level}.
 */
class FilteringTreasureIntegrationTest {

    private static final ResourceKey<Enchantment> SHARPNESS = key("sharpness");
    private static final ResourceKey<Enchantment> SMITE = key("smite");
    private static final ResourceKey<Enchantment> FORTUNE = key("fortune");

    private static Registry<Enchantment> enchantmentRegistry;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreeze(BuiltInRegistries.MENU, false);
        unfreeze(BuiltInRegistries.BLOCK, true);
        unfreeze(BuiltInRegistries.ITEM, true);
        unfreeze(BuiltInRegistries.BLOCK_ENTITY_TYPE, true);

        FizzleEnchantingRegistry.register();

        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
        BuiltInRegistries.MENU.freeze();
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();

        enchantmentRegistry = buildEnchantmentRegistry();
    }

    @Test
    void gather_realFilteringShelf_blacklistFlowsThroughBeHook() {
        EnchantingStatRegistry reg = new EnchantingStatRegistry();
        FilteringShelfBlockEntity shelf = newFilteringEntity();
        shelf.setItem(0, singleEnchantBook(SHARPNESS, 1));

        Function<BlockPos, Object> contextLookup = pos ->
                pos.getX() == 0 ? shelf : null;

        StatCollection result = reg.gatherStatsFromOffsets(
                offsets(3), pos -> EnchantingStats.ZERO, pos -> true, contextLookup);

        assertEquals(Set.of(SHARPNESS), result.blacklist(),
                "the real FilteringShelfBlockEntity must surface its single-book blacklist through the BlacklistSource hook");
    }

    @Test
    void gather_twoRealFilteringShelves_unionContainsBothUniqueEnchants() {
        EnchantingStatRegistry reg = new EnchantingStatRegistry();
        FilteringShelfBlockEntity shelfA = newFilteringEntity();
        shelfA.setItem(0, singleEnchantBook(SHARPNESS, 1));
        FilteringShelfBlockEntity shelfB = newFilteringEntity();
        shelfB.setItem(0, singleEnchantBook(FORTUNE, 1));

        Function<BlockPos, Object> contextLookup = pos -> switch (pos.getX()) {
            case 0 -> shelfA;
            case 1 -> shelfB;
            default -> null;
        };

        StatCollection result = reg.gatherStatsFromOffsets(
                offsets(3), pos -> EnchantingStats.ZERO, pos -> true, contextLookup);

        assertEquals(Set.of(SHARPNESS, FORTUNE), result.blacklist(),
                "two real filtering shelves each holding one unique book must produce a 2-entry union blacklist");
    }

    @Test
    void gather_overlappingFilteringShelves_unionDeduplicates() {
        EnchantingStatRegistry reg = new EnchantingStatRegistry();
        FilteringShelfBlockEntity shelfA = newFilteringEntity();
        shelfA.setItem(0, singleEnchantBook(SHARPNESS, 1));
        shelfA.setItem(1, singleEnchantBook(SMITE, 1));
        FilteringShelfBlockEntity shelfB = newFilteringEntity();
        shelfB.setItem(0, singleEnchantBook(SMITE, 1));
        shelfB.setItem(1, singleEnchantBook(FORTUNE, 1));

        Function<BlockPos, Object> contextLookup = pos -> switch (pos.getX()) {
            case 0 -> shelfA;
            case 1 -> shelfB;
            default -> null;
        };

        StatCollection result = reg.gatherStatsFromOffsets(
                offsets(2), pos -> EnchantingStats.ZERO, pos -> true, contextLookup);

        assertEquals(Set.of(SHARPNESS, SMITE, FORTUNE), result.blacklist(),
                "two filtering shelves sharing the SMITE entry must produce a 3-entry union, not 4");
    }

    @Test
    void gather_realTreasureShelf_flipsTreasureAllowed() {
        EnchantingStatRegistry reg = new EnchantingStatRegistry();
        TreasureShelfBlockEntity shelf = newTreasureEntity();

        Function<BlockPos, Object> contextLookup = pos ->
                pos.getX() == 1 ? shelf : null;

        StatCollection result = reg.gatherStatsFromOffsets(
                offsets(3), pos -> EnchantingStats.ZERO, pos -> true, contextLookup);

        assertTrue(result.treasureAllowed(),
                "a real TreasureShelfBlockEntity in range must flip treasureAllowed via the TreasureFlagSource marker");
    }

    @Test
    void gather_noShelves_treasureAllowedRemainsFalse() {
        EnchantingStatRegistry reg = new EnchantingStatRegistry();

        StatCollection result = reg.gatherStatsFromOffsets(
                offsets(3), pos -> EnchantingStats.ZERO, pos -> true, pos -> null);

        assertFalse(result.treasureAllowed(),
                "no treasure shelf in range → treasureAllowed must remain false");
        assertTrue(result.blacklist().isEmpty(),
                "no filtering shelf in range → blacklist must remain empty");
    }

    @Test
    void gather_filteringPlusTreasureShelf_bothContributionsLand() {
        EnchantingStatRegistry reg = new EnchantingStatRegistry();
        FilteringShelfBlockEntity filtering = newFilteringEntity();
        filtering.setItem(0, singleEnchantBook(SHARPNESS, 1));
        TreasureShelfBlockEntity treasure = newTreasureEntity();

        Function<BlockPos, Object> contextLookup = pos -> switch (pos.getX()) {
            case 0 -> filtering;
            case 1 -> treasure;
            default -> null;
        };

        StatCollection result = reg.gatherStatsFromOffsets(
                offsets(3), pos -> EnchantingStats.ZERO, pos -> true, contextLookup);

        assertEquals(Set.of(SHARPNESS), result.blacklist(),
                "the filtering shelf still contributes its blacklist when a treasure shelf shares the scan");
        assertTrue(result.treasureAllowed(),
                "the treasure shelf still flips its flag when a filtering shelf shares the scan");
    }

    @Test
    void gather_emptyFilteringShelf_contributesNoBlacklist() {
        EnchantingStatRegistry reg = new EnchantingStatRegistry();
        // An empty filtering shelf is the wood-tier base case from DESIGN — its blacklist must be
        // empty so no enchants are accidentally suppressed. The real BE returns Set.of() here.
        FilteringShelfBlockEntity shelf = newFilteringEntity();

        Function<BlockPos, Object> contextLookup = pos ->
                pos.getX() == 0 ? shelf : null;

        StatCollection result = reg.gatherStatsFromOffsets(
                offsets(1), pos -> EnchantingStats.ZERO, pos -> true, contextLookup);

        assertTrue(result.blacklist().isEmpty(),
                "an empty filtering shelf BE must surface an empty blacklist (wood-tier base behavior)");
    }

    @Test
    void filteringShelfBlockEntity_implementsBlacklistSource() {
        BlockEntity be = FizzleEnchantingRegistry.FILTERING_SHELF.newBlockEntity(
                BlockPos.ZERO, FizzleEnchantingRegistry.FILTERING_SHELF.defaultBlockState());
        assertTrue(be instanceof BlacklistSource,
                "FilteringShelfBlockEntity must implement BlacklistSource so the gather pipeline picks it up");
    }

    @Test
    void treasureShelfBlockEntity_implementsTreasureFlagSource() {
        BlockEntity be = FizzleEnchantingRegistry.TREASURE_SHELF.newBlockEntity(
                BlockPos.ZERO, FizzleEnchantingRegistry.TREASURE_SHELF.defaultBlockState());
        assertTrue(be instanceof TreasureFlagSource,
                "TreasureShelfBlockEntity must implement TreasureFlagSource so the gather pipeline picks it up");
    }

    private static FilteringShelfBlockEntity newFilteringEntity() {
        return new FilteringShelfBlockEntity(
                BlockPos.ZERO,
                FizzleEnchantingRegistry.FILTERING_SHELF.defaultBlockState());
    }

    private static TreasureShelfBlockEntity newTreasureEntity() {
        return new TreasureShelfBlockEntity(
                BlockPos.ZERO,
                FizzleEnchantingRegistry.TREASURE_SHELF.defaultBlockState());
    }

    private static List<BlockPos> offsets(int count) {
        BlockPos[] arr = new BlockPos[count];
        for (int i = 0; i < count; i++) {
            arr[i] = new BlockPos(i, 0, 0);
        }
        return List.of(arr);
    }

    private static ItemStack singleEnchantBook(ResourceKey<Enchantment> enchant, int level) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(holderFor(enchant), level);
        stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
        return stack;
    }

    private static Holder<Enchantment> holderFor(ResourceKey<Enchantment> key) {
        return enchantmentRegistry.getHolderOrThrow(key);
    }

    private static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(
                Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath("minecraft", path));
    }

    private static Registry<Enchantment> buildEnchantmentRegistry() {
        MappedRegistry<Enchantment> reg = new MappedRegistry<>(Registries.ENCHANTMENT, Lifecycle.stable());
        registerSyntheticEnchant(reg, SHARPNESS);
        registerSyntheticEnchant(reg, SMITE);
        registerSyntheticEnchant(reg, FORTUNE);
        return reg.freeze();
    }

    private static void registerSyntheticEnchant(MappedRegistry<Enchantment> registry, ResourceKey<Enchantment> key) {
        registry.register(key, syntheticEnchantment(), RegistrationInfo.BUILT_IN);
    }

    private static Enchantment syntheticEnchantment() {
        HolderSet<Item> any = HolderSet.direct(List.of(BuiltInRegistries.ITEM.wrapAsHolder(Items.BOOK)));
        Enchantment.EnchantmentDefinition def = Enchantment.definition(
                any,
                10,
                5,
                Enchantment.dynamicCost(1, 10),
                Enchantment.dynamicCost(51, 10),
                1,
                EquipmentSlotGroup.ANY);
        return new Enchantment(
                Component.literal("test"),
                def,
                HolderSet.empty(),
                DataComponentMap.EMPTY);
    }

    /** Mirror of the unfreeze pattern shared by every registry-touching test in this package. */
    private static void unfreeze(Registry<?> registry, boolean intrusiveHolders) throws Exception {
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.setBoolean(registry, false);
        if (intrusiveHolders) {
            Field intrusive = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
            intrusive.setAccessible(true);
            if (intrusive.get(registry) == null) {
                intrusive.set(registry, new IdentityHashMap<>());
            }
        }
    }
}
