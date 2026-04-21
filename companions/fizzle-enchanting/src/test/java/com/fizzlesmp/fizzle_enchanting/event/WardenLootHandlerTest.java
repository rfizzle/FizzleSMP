package com.fizzlesmp.fizzle_enchanting.event;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Structural contract for {@link WardenLootHandler}. Verifies the MODIFY listener appends two
 * tendril pools, each gated by a {@link WardenPoolCondition} of the expected {@link
 * WardenPoolCondition.Kind}.
 *
 * <p>The handler no longer bakes config values into the pool conditions — per T-5.4.4 the roll
 * reads the live config — so the structural checks here only need to pin the kind marker. The
 * actual chance math lives in {@link WardenPoolConditionTest}.
 *
 * <p>Full loot-roll simulations are out of scope for unit tests — building a {@code LootContext}
 * needs a real {@code ServerLevel}, which can't be synthesised without Minecraft's server
 * bootstrap.
 */
class WardenLootHandlerTest {

    private static HolderLookup.Provider registries;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        // Item registry must carry WARDEN_TENDRIL before LootItem.lootTableItem wraps it into a
        // Holder — the registration path normally runs in onInitialize, but the test bypasses
        // that and drives FizzleEnchantingRegistry.register() directly.
        unfreezeIntrusive(BuiltInRegistries.BLOCK);
        unfreezeIntrusive(BuiltInRegistries.ITEM);
        unfreezeIntrusive(BuiltInRegistries.BLOCK_ENTITY_TYPE);
        unfreeze(BuiltInRegistries.MENU);
        unfreeze(BuiltInRegistries.LOOT_CONDITION_TYPE);

        FizzleEnchantingRegistry.register();

        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
        BuiltInRegistries.MENU.freeze();
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();
        BuiltInRegistries.LOOT_CONDITION_TYPE.freeze();

        registries = HolderLookup.Provider.create(java.util.stream.Stream.empty());
    }

    @Test
    void modify_addsTwoPoolsReferencingWardenTendril() throws Exception {
        LootTable table = runModify();
        List<LootPool> pools = poolsOf(table);
        assertEquals(2, pools.size(),
                "modify must append exactly 2 pools — Pool A guaranteed, Pool B looting-gated");

        for (int i = 0; i < pools.size(); i++) {
            List<LootPoolEntryContainer> entries = entriesOf(pools.get(i));
            assertEquals(1, entries.size(),
                    "pool " + i + " must hold exactly one entry (the tendril) — extras would double-drop");
            LootItem entry = assertInstanceOf(LootItem.class, entries.get(0),
                    "each pool's entry must be a LootItem pointing at WARDEN_TENDRIL");
            assertSame(FizzleEnchantingRegistry.WARDEN_TENDRIL, itemOf(entry).value(),
                    "LootItem must resolve to the registered WARDEN_TENDRIL instance");
        }
    }

    @Test
    void modify_poolAUsesDropChanceKind() throws Exception {
        LootTable table = runModify();
        LootPool poolA = poolsOf(table).get(0);
        List<LootItemCondition> conditions = conditionsOf(poolA);
        assertEquals(1, conditions.size(),
                "Pool A must carry exactly one condition — the drop-chance gate");

        WardenPoolCondition condition = assertInstanceOf(WardenPoolCondition.class, conditions.get(0),
                "Pool A's condition must be the custom WardenPoolCondition — vanilla randomChance "
                        + "bakes values at MODIFY time, which defeats /fizzleenchanting reload");
        assertEquals(WardenPoolCondition.Kind.DROP_CHANCE, condition.kind(),
                "Pool A's kind marker must be DROP_CHANCE so the condition reads "
                        + "config.warden.tendrilDropChance at roll time");
    }

    @Test
    void modify_poolBUsesLootingBonusKind() throws Exception {
        LootTable table = runModify();
        LootPool poolB = poolsOf(table).get(1);
        List<LootItemCondition> conditions = conditionsOf(poolB);
        assertEquals(1, conditions.size(),
                "Pool B must carry exactly one condition — the looting-bonus gate");

        WardenPoolCondition condition = assertInstanceOf(WardenPoolCondition.class, conditions.get(0),
                "Pool B's condition must be the custom WardenPoolCondition so looting-bonus "
                        + "reads config.warden.tendrilLootingBonus live per roll");
        assertEquals(WardenPoolCondition.Kind.LOOTING_BONUS, condition.kind(),
                "Pool B's kind marker must be LOOTING_BONUS so it scales by the attacker's "
                        + "looting level at roll time");
    }

    // ---- Fixtures / reflection helpers -------------------------------------

    private static LootTable runModify() {
        LootTable.Builder builder = LootTable.lootTable();
        WardenLootHandler.modify(builder, registries);
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static List<LootPool> poolsOf(LootTable table) throws Exception {
        Field f = LootTable.class.getDeclaredField("pools");
        f.setAccessible(true);
        return (List<LootPool>) f.get(table);
    }

    @SuppressWarnings("unchecked")
    private static List<LootItemCondition> conditionsOf(LootPool pool) throws Exception {
        Field f = LootPool.class.getDeclaredField("conditions");
        f.setAccessible(true);
        return (List<LootItemCondition>) f.get(pool);
    }

    @SuppressWarnings("unchecked")
    private static List<LootPoolEntryContainer> entriesOf(LootPool pool) throws Exception {
        Field f = LootPool.class.getDeclaredField("entries");
        f.setAccessible(true);
        return (List<LootPoolEntryContainer>) f.get(pool);
    }

    @SuppressWarnings("unchecked")
    private static Holder<Item> itemOf(LootItem entry) throws Exception {
        Field f = LootItem.class.getDeclaredField("item");
        f.setAccessible(true);
        return (Holder<Item>) f.get(entry);
    }

    private static void unfreeze(Registry<?> registry) throws Exception {
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.setBoolean(registry, false);
    }

    private static void unfreezeIntrusive(Registry<?> registry) throws Exception {
        unfreeze(registry);
        Field intrusive = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        intrusive.setAccessible(true);
        if (intrusive.get(registry) == null) {
            intrusive.set(registry, new IdentityHashMap<>());
        }
    }
}
