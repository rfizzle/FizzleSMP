package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.net.EnchantmentClue;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Pure functions backing {@link FizzleEnchantmentMenu}. Separating the math from the Menu
 * plumbing keeps it testable without bootstrapping a full Minecraft {@code Player}/{@code Level}.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>{@link #recompute} — drives the per-slot cost/picks/clue computation from a
 *       {@link StatCollection} + input stack, matching Zenith's sequencing (three costs first,
 *       then three pick+clue builds) against a shared {@link RandomSource}.</li>
 *   <li>{@link #validateClick} — runs the click-time XP/lapis gate in a form the Menu can reuse
 *       and tests can drive directly.</li>
 *   <li>{@link #validateCraftingClick} — XP gate for the Epic-5 crafting-result row (button id 3).
 *       The Menu routes id==3 through this before running recipe assembly.</li>
 * </ul>
 */
public final class FizzleEnchantmentLogic {

    /** Vanilla enchanting table has 3 preview slots. */
    public static final int PREVIEW_SLOTS = 3;

    /** Button id reserved for the Epic-5 crafting-result row. */
    public static final int CRAFTING_BUTTON_ID = 3;

    private FizzleEnchantmentLogic() {
    }

    /**
     * Per-slot output of {@link #recompute}. Packs the XP-level cost, the full candidate
     * {@code picks} that click-time application will consume, and the {@link RealEnchantmentHelper.ClueBuild}
     * summarising the hover preview.
     */
    public record SlotState(int cost, List<EnchantmentInstance> picks, RealEnchantmentHelper.ClueBuild clueBuild) {
        public static final SlotState EMPTY = new SlotState(
                0, List.of(), new RealEnchantmentHelper.ClueBuild(null, List.of(), true));
    }

    /**
     * Computes the three preview-slot costs and picks for an input stack + aggregated stats.
     *
     * <p>Sequencing is Zenith's: the shared {@code random} is seeded once with
     * {@code enchantmentSeed}, then (1) each slot's cost is drawn in ascending slot order and
     * (2) each slot with a non-zero cost draws its enchantment pool and clue build from the same
     * random. Lapis count gates cost visibility per slot to match vanilla's UI (cost is zeroed
     * when the player can't afford the slot and isn't in creative) — the click-time gate still
     * runs in {@link #validateClick}.
     *
     * @param stats                    gathered shelf stats (clamped Eterna, Quanta, Arcana, …)
     * @param input                    item in the table's input slot (never {@code null})
     * @param enchantmentSeed          seed synced to the client; matches the value the client
     *                                 will use to recompute the same preview independently
     * @param lapisCount               count in the lapis slot; zero when empty
     * @param hasInfiniteMaterials     {@code true} for creative / spectator; bypasses the lapis
     *                                 gate
     * @param allowTreasureWithoutShelf bundled config override that lets treasure enchantments
     *                                 roll even without a treasure-flag shelf in range
     * @param registry                 enchantment registry from the current level's registry access
     * @param random                   shared random; seeded by this method before use
     * @return an array of length {@value #PREVIEW_SLOTS}; slots with no cost carry
     *         {@link SlotState#EMPTY}
     */
    public static SlotState[] recompute(
            StatCollection stats,
            ItemStack input,
            int enchantmentSeed,
            int lapisCount,
            boolean hasInfiniteMaterials,
            boolean allowTreasureWithoutShelf,
            Registry<Enchantment> registry,
            RandomSource random
    ) {
        SlotState[] states = new SlotState[PREVIEW_SLOTS];
        boolean treasureAllowed = stats.treasureAllowed() || allowTreasureWithoutShelf;

        random.setSeed(enchantmentSeed);
        float eterna = stats.eterna();
        if (eterna < 1.5F) eterna = 1.5F;
        int[] costs = new int[PREVIEW_SLOTS];
        for (int slot = 0; slot < PREVIEW_SLOTS; slot++) {
            int cost = RealEnchantmentHelper.getEnchantmentCost(random, slot, eterna, input);
            if (cost < slot + 1) {
                cost = 0;
            }
            if (!hasInfiniteMaterials && lapisCount < slot + 1) {
                cost = 0;
            }
            costs[slot] = cost;
        }

        for (int slot = 0; slot < PREVIEW_SLOTS; slot++) {
            int cost = costs[slot];
            if (cost <= 0) {
                states[slot] = SlotState.EMPTY;
                continue;
            }
            List<EnchantmentInstance> picks = RealEnchantmentHelper.selectEnchantment(
                    random, input, cost,
                    stats.quanta(), stats.arcana(), stats.rectification(),
                    treasureAllowed, stats.blacklist(), registry);
            RealEnchantmentHelper.ClueBuild clueBuild =
                    RealEnchantmentHelper.buildClueList(random, picks, stats.clues());
            states[slot] = new SlotState(cost, picks, clueBuild);
        }
        return states;
    }

    /**
     * Click-time XP/lapis/input gate. Mirrors vanilla {@code clickMenuButton}: both the XP
     * check against {@code requiredLapis} and against {@code costs[id]} must pass (creative
     * bypasses both). Invalid ids and empty inputs reject without consuming resources.
     *
     * @param buttonId           {@code [0, PREVIEW_SLOTS)}; anything else rejects
     * @param cost               XP-level cost for the clicked slot; must be {@code > 0}
     * @param inputEmpty         {@code true} when the item slot is empty
     * @param lapisCount         count in the lapis slot; zero when empty
     * @param playerXpLevel      player's current {@code experienceLevel}
     * @param hasInfiniteMaterials {@code true} for creative / spectator
     * @return success or a reject reason intended for logging / debugging, not player chat
     */
    public static ClickAttempt validateClick(
            int buttonId,
            int cost,
            boolean inputEmpty,
            int lapisCount,
            int playerXpLevel,
            boolean hasInfiniteMaterials
    ) {
        if (buttonId < 0 || buttonId >= PREVIEW_SLOTS) {
            return ClickAttempt.reject("invalid button id: " + buttonId);
        }
        if (inputEmpty) {
            return ClickAttempt.reject("input slot empty");
        }
        if (cost <= 0) {
            return ClickAttempt.reject("slot has no cost");
        }
        int requiredLapis = buttonId + 1;
        if (!hasInfiniteMaterials && lapisCount < requiredLapis) {
            return ClickAttempt.reject("insufficient lapis");
        }
        if (!hasInfiniteMaterials && (playerXpLevel < requiredLapis || playerXpLevel < cost)) {
            return ClickAttempt.reject("insufficient experience");
        }
        return ClickAttempt.ok();
    }

    /** Outcome of {@link #validateClick}. {@link #rejection()} is only populated on failure. */
    public record ClickAttempt(boolean success, String rejection) {
        public static ClickAttempt ok() {
            return new ClickAttempt(true, null);
        }

        public static ClickAttempt reject(String reason) {
            return new ClickAttempt(false, reason);
        }
    }

    /**
     * Click-time gate for the crafting-result row (button id 3). The row is populated when
     * {@link EnchantingStatRegistry}-gathered stats satisfy a recipe's requirements window; the
     * server must still confirm at click time that (a) the cached recipe holder is present and
     * (b) the player can pay the recipe's XP cost. Lapis is not consumed by the crafting row —
     * the XP charge is the whole cost model Zenith uses here.
     *
     * @param recipePresent        {@code true} when {@code FizzleEnchantmentMenu#currentRecipe} is
     *                             populated — a stale payload click with no server-side match
     *                             rejects with {@code "no recipe"}
     * @param xpCost               the recipe's {@code xp_cost} (already clamped non-negative by
     *                             the codec); {@code 0} means free
     * @param playerXpLevel        player's current {@code experienceLevel}
     * @param hasInfiniteMaterials {@code true} for creative / spectator; bypasses the XP gate
     * @return success or a reject reason intended for logging / debugging
     */
    public static CraftingClickAttempt validateCraftingClick(
            boolean recipePresent,
            int xpCost,
            int playerXpLevel,
            boolean hasInfiniteMaterials
    ) {
        if (!recipePresent) {
            return CraftingClickAttempt.reject("no recipe matched");
        }
        if (!hasInfiniteMaterials && playerXpLevel < xpCost) {
            return CraftingClickAttempt.reject("insufficient experience");
        }
        return CraftingClickAttempt.ok();
    }

    /** Outcome of {@link #validateCraftingClick}. {@link #rejection()} is only populated on failure. */
    public record CraftingClickAttempt(boolean success, String rejection) {
        public static CraftingClickAttempt ok() {
            return new CraftingClickAttempt(true, null);
        }

        public static CraftingClickAttempt reject(String reason) {
            return new CraftingClickAttempt(false, reason);
        }
    }

    /**
     * Applies the slot's {@code picks} to the input stack, returning the result stack that
     * should replace the input slot. Vanilla's book-to-enchanted-book transmute is preserved.
     * Lapis consumption and XP deduction are reported as amounts for the Menu to execute
     * against the container; this method never mutates the lapis stack or the player.
     */
    public static EnchantOutcome applyPicks(ItemStack input, int buttonId, List<EnchantmentInstance> picks) {
        int requiredLapis = buttonId + 1;
        ItemStack result = input.is(Items.BOOK) ? input.transmuteCopy(Items.ENCHANTED_BOOK) : input.copy();
        for (EnchantmentInstance inst : picks) {
            result.enchant(inst.enchantment, inst.level);
        }
        return new EnchantOutcome(result, requiredLapis, requiredLapis);
    }

    /** The new input-slot stack, the lapis-slot deduction, and the XP-level deduction. */
    public record EnchantOutcome(ItemStack resultStack, int lapisConsumed, int xpLevelsConsumed) {
    }

    /**
     * Projects a slot's {@link RealEnchantmentHelper.ClueBuild clueBuild} onto the payload shape
     * {@link EnchantmentClue}, stripping clues whose enchantments aren't registered (which would
     * fail the {@link ResourceKey} codec on the wire).
     */
    public static List<EnchantmentClue> toPayloadClues(RealEnchantmentHelper.ClueBuild clueBuild) {
        List<EnchantmentInstance> clues = clueBuild.clues();
        List<EnchantmentClue> out = new ArrayList<>(clues.size());
        for (EnchantmentInstance inst : clues) {
            Optional<ResourceKey<Enchantment>> key = inst.enchantment.unwrapKey();
            if (key.isPresent()) {
                out.add(new EnchantmentClue(key.get(), inst.level));
            }
        }
        return out;
    }

    /** Vanilla stores enchantClue as a numeric holder id (see {@link Registry#asHolderIdMap}). */
    public static int idForHolder(Registry<Enchantment> registry, Holder<Enchantment> holder) {
        return registry.asHolderIdMap().getId(holder);
    }
}
