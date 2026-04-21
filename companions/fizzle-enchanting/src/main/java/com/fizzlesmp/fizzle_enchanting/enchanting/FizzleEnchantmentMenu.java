package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.EnchantingRecipe;
import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.EnchantingRecipeRegistry;
import com.fizzlesmp.fizzle_enchanting.mixin.EnchantmentMenuAccessor;
import com.fizzlesmp.fizzle_enchanting.net.CluesPayload;
import com.fizzlesmp.fizzle_enchanting.net.CraftingResultEntry;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
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
    /**
     * Stat-gated crafting recipe that matches the current input + shelf totals (T-5.3.1). Consumed
     * server-side by the button-id=3 handler (T-5.3.3) and projected onto the outgoing
     * {@link StatsPayload} (T-5.3.2). Reset to {@link Optional#empty()} whenever the input slot is
     * cleared or the stack becomes unenchantable.
     */
    private Optional<RecipeHolder<? extends Recipe<SingleRecipeInput>>> currentRecipe = Optional.empty();
    /**
     * Client mirror of the server's projected {@link CraftingResultEntry}. Populated from the
     * incoming {@link StatsPayload} in {@link #applyClientStats} so {@code FizzleEnchantmentScreen}
     * can paint the fourth-row preview without plumbing an extra network hop (T-5.3.4). The
     * server-side {@link #currentRecipe} stays authoritative; this field exists solely for the
     * client-side render path.
     */
    private Optional<CraftingResultEntry> lastCraftingResult = Optional.empty();

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
     * Server-resolved recipe holder for the fourth-row crafting preview, if any. Only ever
     * populated during a {@link #recompute} pass on the server — clients read the projected
     * {@code CraftingResultEntry} off {@link StatsPayload} instead (T-5.3.2).
     */
    public Optional<RecipeHolder<? extends Recipe<SingleRecipeInput>>> currentRecipe() {
        return currentRecipe;
    }

    /**
     * Client-side mirror of the outgoing {@link StatsPayload}'s {@code craftingResult}. The menu
     * caches it here on {@link #applyClientStats} so {@code FizzleEnchantmentScreen} can decide
     * whether to paint the fourth row and dispatch a {@code buttonId=3} click without reaching
     * into the raw payload (T-5.3.4).
     */
    public Optional<CraftingResultEntry> lastCraftingResult() {
        return lastCraftingResult;
    }

    /**
     * Testable seam for the recipe lookup run inside {@link #recompute}. Kept package-private so
     * the menu's {@code slotsChanged} integration can be verified without standing up a full
     * {@link Level} — T-5.3.1's acceptance test drives this helper directly. Empty input stacks
     * short-circuit to avoid scanning the recipe manager for a match that will always miss.
     */
    static Optional<RecipeHolder<? extends Recipe<SingleRecipeInput>>> lookupCraftingResult(
            RecipeManager recipes, ItemStack input, StatCollection stats) {
        if (input.isEmpty()) {
            return Optional.empty();
        }
        return EnchantingRecipeRegistry.findMatch(recipes, input, stats);
    }

    /**
     * Projects the cached {@link #currentRecipe} (if any) into the wire-friendly
     * {@link CraftingResultEntry} carried on {@link StatsPayload}. Static + package-private so
     * T-5.3.2's round-trip test can drive the projection without instantiating a menu — the
     * server-only {@code currentRecipe} field would otherwise need a {@link Level} +
     * {@link Player} chain to populate. Holders whose value isn't an {@link EnchantingRecipe}
     * subclass collapse to empty as a safety net for future recipe types that wander into the
     * generic {@link Recipe} bound; today {@link EnchantingRecipeRegistry#findMatch} only ever
     * returns enchanting recipes.
     */
    static Optional<CraftingResultEntry> projectCraftingResult(
            Optional<RecipeHolder<? extends Recipe<SingleRecipeInput>>> recipe) {
        return recipe.flatMap(holder -> {
            if (holder.value() instanceof EnchantingRecipe enchanting) {
                return Optional.of(new CraftingResultEntry(
                        enchanting.getResult().copy(),
                        enchanting.getXpCost(),
                        holder.id()));
            }
            return Optional.empty();
        });
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
        this.lastCraftingResult = payload.craftingResult();
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
        this.currentRecipe = lookupCraftingResult(level.getRecipeManager(), input, stats);

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
        this.currentRecipe = Optional.empty();
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
                stats.clues(), blacklist, stats.treasureAllowed(),
                projectCraftingResult(currentRecipe));
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
        if (id == FizzleEnchantmentLogic.CRAFTING_BUTTON_ID) {
            return handleCraftingClick(player);
        }

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

    /**
     * Handles a click on the stat-gated crafting-result row (button id 3 per DESIGN §
     * "Crafting-Result Row"). Gating runs server-authoritatively against {@link #currentRecipe}
     * and the recipe's {@code xp_cost}; a present client payload with a stale recipe cache
     * rejects cleanly. Delegates recipe application to {@link #applyCraftingRecipe} inside
     * {@link ContainerLevelAccess#execute} so the {@link Level} is only touched on the server
     * tick thread that owns the table.
     */
    private boolean handleCraftingClick(Player player) {
        Optional<RecipeHolder<? extends Recipe<SingleRecipeInput>>> recipeOpt = currentRecipe;
        int xpCost = recipeOpt
                .map(holder -> holder.value() instanceof EnchantingRecipe e ? e.getXpCost() : 0)
                .orElse(0);
        FizzleEnchantmentLogic.CraftingClickAttempt attempt = FizzleEnchantmentLogic.validateCraftingClick(
                recipeOpt.isPresent(), xpCost, player.experienceLevel, player.hasInfiniteMaterials());
        if (!attempt.success()) {
            return false;
        }
        RecipeHolder<? extends Recipe<SingleRecipeInput>> holder = recipeOpt.get();
        access.execute((level, pos) -> applyCraftingRecipe(level, pos, player, holder, xpCost));
        return true;
    }

    /**
     * Server-side application of the cached crafting recipe. Mirrors Zenith's button-id=3 flow:
     * the recipe's {@link Recipe#assemble} runs against the current input (keep-nbt subtypes copy
     * {@code ItemEnchantments} onto the result there), the input stack is decremented by one, the
     * result is inserted into the player's inventory (dropped at the table if full), XP is
     * consumed, and the shelf scan is re-run via {@link Container#setChanged()} so the
     * fourth-row preview refreshes from the new input state.
     */
    private void applyCraftingRecipe(
            Level level, BlockPos pos, Player player,
            RecipeHolder<? extends Recipe<SingleRecipeInput>> holder, int xpCost) {
        Container enchantSlots = enchantSlots();
        ItemStack input = enchantSlots.getItem(INPUT_SLOT);
        if (input.isEmpty()) {
            return;
        }
        ItemStack result = holder.value().assemble(new SingleRecipeInput(input), level.registryAccess());

        input.shrink(1);
        if (input.isEmpty()) {
            enchantSlots.setItem(INPUT_SLOT, ItemStack.EMPTY);
        }

        if (!player.getInventory().add(result)) {
            player.drop(result, false);
        }

        if (!player.hasInfiniteMaterials() && xpCost > 0) {
            player.giveExperienceLevels(-xpCost);
        }

        enchantSlots.setChanged();
        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F,
                level.random.nextFloat() * 0.1F + 0.9F);
    }
}
