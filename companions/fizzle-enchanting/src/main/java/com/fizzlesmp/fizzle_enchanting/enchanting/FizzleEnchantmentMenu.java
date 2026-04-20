package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import com.fizzlesmp.fizzle_enchanting.mixin.EnchantmentMenuAccessor;
import com.fizzlesmp.fizzle_enchanting.net.CluesPayload;
import com.fizzlesmp.fizzle_enchanting.net.EnchantmentClue;
import com.fizzlesmp.fizzle_enchanting.net.StatsPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Stat-driven enchantment table menu. Replaces vanilla {@link EnchantmentMenu} via
 * {@code EnchantmentTableBlockMixin} (T-2.5.2) — this class only replaces the
 * {@link #slotsChanged}/{@link #clickMenuButton} logic, inheriting slot layout, data-slot sync,
 * and the input/lapis container from vanilla.
 *
 * <p>Design references: DESIGN.md §"Table Menu Implementation" and
 * §"Crafting-Result Row". Pure logic lives in {@link FizzleEnchantmentLogic}.
 *
 * <p>The three private fields on {@link EnchantmentMenu} ({@code enchantSlots}, {@code random},
 * {@code enchantmentSeed}) are reached through {@link EnchantmentMenuAccessor} (T-2.5.3); the
 * {@link EnchantmentMenu} instance doubles as the accessor at runtime.
 */
public class FizzleEnchantmentMenu extends EnchantmentMenu {

    private static final int INPUT_SLOT = 0;
    private static final int LAPIS_SLOT = 1;

    private final ContainerLevelAccess access;
    private final Inventory playerInventory;

    /** Last-gathered stats — retained for the client HUD (T-2.5.4) and click-time replay. */
    private StatCollection lastStats = StatCollection.EMPTY;
    /** Slot picks from the most recent {@link #slotsChanged} — consumed on click. */
    private List<List<EnchantmentInstance>> slotPicks = List.of(List.of(), List.of(), List.of());

    public FizzleEnchantmentMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public FizzleEnchantmentMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(containerId, playerInventory, access);
        this.access = access;
        this.playerInventory = playerInventory;
    }

    /**
     * Routes the open-screen packet to our MenuType so the client builds a
     * {@link FizzleEnchantmentMenu} (and therefore the {@code FizzleEnchantmentScreen} wired via
     * {@code MenuScreens.register}) instead of vanilla's {@link EnchantmentMenu}. Vanilla's
     * constructor stashes {@link MenuType#ENCHANTMENT} in the private {@code menuType} field;
     * overriding {@link #getType()} is cheaper than an accessor mixin to re-set it.
     */
    @Override
    public MenuType<?> getType() {
        return FizzleEnchantingRegistry.ENCHANTING_TABLE_MENU;
    }

    /** Alias for vanilla's private {@code enchantSlots} — slot 0 and slot 1 share this container. */
    private Container enchantSlots() {
        return ((EnchantmentMenuAccessor) this).fizzleEnchanting$getEnchantSlots();
    }

    /** Alias for vanilla's private {@link RandomSource} — shared across slot recomputes. */
    private RandomSource random() {
        return ((EnchantmentMenuAccessor) this).fizzleEnchanting$getRandom();
    }

    public StatCollection getLastStats() {
        return lastStats;
    }

    /**
     * Client-side entry point for {@link StatsPayload}. The packet carries the aggregated stat
     * values; the client never re-scans shelves itself, so {@code maxEterna} is stored as the
     * final eterna value (display-only — click-time gating happens server-side).
     */
    public void applyClientStats(StatsPayload payload) {
        this.lastStats = new StatCollection(
                payload.eterna(),
                payload.quanta(),
                payload.arcana(),
                payload.rectification(),
                payload.clues(),
                payload.eterna(),
                new java.util.HashSet<>(payload.blacklist()),
                payload.treasure());
    }

    @Override
    public void slotsChanged(Container container) {
        if (container != enchantSlots()) {
            // Player-inventory changes also fire slotsChanged; vanilla ignores them here too.
            return;
        }
        ItemStack input = container.getItem(INPUT_SLOT);
        if (input.isEmpty() || !input.isEnchantable()) {
            clearSlotState();
            sendEmptyPayloads();
            return;
        }
        access.execute((level, pos) -> recompute(level, pos, input));
    }

    private void recompute(Level level, BlockPos pos, ItemStack input) {
        StatCollection stats = EnchantingStatRegistry.gatherStats(level, pos);
        this.lastStats = stats;

        Registry<Enchantment> registry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Player player = playerInventory.player;
        int seed = player.getEnchantmentSeed();
        boolean hasInf = player.hasInfiniteMaterials();
        int lapisCount = enchantSlots().getItem(LAPIS_SLOT).getCount();

        FizzleEnchantingConfig config = FizzleEnchanting.getConfig();
        boolean allowTreasureOverride = config != null && config.enchantingTable.allowTreasureWithoutShelf;

        FizzleEnchantmentLogic.SlotState[] states = FizzleEnchantmentLogic.recompute(
                stats, input, seed, lapisCount, hasInf, allowTreasureOverride, registry, random());

        List<List<EnchantmentInstance>> newPicks = new ArrayList<>(FizzleEnchantmentLogic.PREVIEW_SLOTS);
        List<List<EnchantmentClue>> cluePayloads = new ArrayList<>(FizzleEnchantmentLogic.PREVIEW_SLOTS);
        List<Boolean> exhaustedFlags = new ArrayList<>(FizzleEnchantmentLogic.PREVIEW_SLOTS);
        for (int slot = 0; slot < FizzleEnchantmentLogic.PREVIEW_SLOTS; slot++) {
            FizzleEnchantmentLogic.SlotState s = states[slot];
            costs[slot] = s.cost();
            newPicks.add(s.picks());
            EnchantmentInstance primary = s.clueBuild().primary();
            if (primary != null) {
                enchantClue[slot] = FizzleEnchantmentLogic.idForHolder(registry, primary.enchantment);
                levelClue[slot] = primary.level;
            } else {
                enchantClue[slot] = -1;
                levelClue[slot] = -1;
            }
            cluePayloads.add(FizzleEnchantmentLogic.toPayloadClues(s.clueBuild()));
            exhaustedFlags.add(s.clueBuild().exhaustedList());
        }
        this.slotPicks = newPicks;

        broadcastStats(stats);
        for (int slot = 0; slot < FizzleEnchantmentLogic.PREVIEW_SLOTS; slot++) {
            broadcastClues(slot, cluePayloads.get(slot), exhaustedFlags.get(slot));
        }
    }

    private void clearSlotState() {
        for (int i = 0; i < FizzleEnchantmentLogic.PREVIEW_SLOTS; i++) {
            costs[i] = 0;
            enchantClue[i] = -1;
            levelClue[i] = -1;
        }
        this.slotPicks = List.of(List.of(), List.of(), List.of());
        this.lastStats = StatCollection.EMPTY;
    }

    private void sendEmptyPayloads() {
        broadcastStats(StatCollection.EMPTY);
        for (int slot = 0; slot < FizzleEnchantmentLogic.PREVIEW_SLOTS; slot++) {
            broadcastClues(slot, List.of(), true);
        }
    }

    private void broadcastStats(StatCollection stats) {
        if (!(playerInventory.player instanceof ServerPlayer sp)) {
            return;
        }
        List<ResourceKey<Enchantment>> blacklist = List.copyOf(stats.blacklist());
        StatsPayload payload = new StatsPayload(
                stats.eterna(), stats.quanta(), stats.arcana(), stats.rectification(),
                stats.clues(), blacklist, stats.treasureAllowed(), Optional.empty());
        ServerPlayNetworking.send(sp, payload);
    }

    private void broadcastClues(int slot, List<EnchantmentClue> clues, boolean exhausted) {
        if (!(playerInventory.player instanceof ServerPlayer sp)) {
            return;
        }
        ServerPlayNetworking.send(sp, new CluesPayload(slot, clues, exhausted));
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        // Throws for id == 3 until Epic 5 wires the crafting-result row.
        FizzleEnchantmentLogic.assertNotCraftingButton(id);

        Container enchantSlots = enchantSlots();
        ItemStack input = enchantSlots.getItem(INPUT_SLOT);
        ItemStack lapis = enchantSlots.getItem(LAPIS_SLOT);
        int cost = (id >= 0 && id < costs.length) ? costs[id] : 0;
        boolean hasInf = player.hasInfiniteMaterials();

        FizzleEnchantmentLogic.ClickAttempt attempt = FizzleEnchantmentLogic.validateClick(
                id, cost, input.isEmpty(), lapis.isEmpty() ? 0 : lapis.getCount(),
                player.experienceLevel, hasInf);
        if (!attempt.success()) {
            return false;
        }

        List<EnchantmentInstance> picks = id < slotPicks.size() ? slotPicks.get(id) : List.of();
        if (picks.isEmpty()) {
            return false;
        }

        access.execute((level, pos) -> applyEnchantment(level, pos, player, id, picks));
        return true;
    }

    private void applyEnchantment(
            Level level, BlockPos pos, Player player, int id, List<EnchantmentInstance> picks) {
        Container enchantSlots = enchantSlots();
        ItemStack input = enchantSlots.getItem(INPUT_SLOT);
        ItemStack lapis = enchantSlots.getItem(LAPIS_SLOT);
        FizzleEnchantmentLogic.EnchantOutcome outcome =
                FizzleEnchantmentLogic.applyPicks(input, id, picks);

        ItemStack result = outcome.resultStack();
        if (input.is(Items.BOOK)) {
            enchantSlots.setItem(INPUT_SLOT, result);
        } else {
            for (EnchantmentInstance inst : picks) {
                input.enchant(inst.enchantment, inst.level);
            }
            result = input;
        }

        player.onEnchantmentPerformed(result, outcome.xpLevelsConsumed());
        lapis.consume(outcome.lapisConsumed(), player);
        if (lapis.isEmpty()) {
            enchantSlots.setItem(LAPIS_SLOT, ItemStack.EMPTY);
        }

        player.awardStat(Stats.ENCHANT_ITEM);
        if (player instanceof ServerPlayer sp) {
            CriteriaTriggers.ENCHANTED_ITEM.trigger(sp, result, outcome.xpLevelsConsumed());
        }
        enchantSlots.setChanged();
        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F,
                level.random.nextFloat() * 0.1F + 0.9F);
    }
}
