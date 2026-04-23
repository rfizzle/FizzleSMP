// Tier: 2 (fabric-loader-junit)
package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.EnchantingRecipe;
import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.KeepNbtEnchantingRecipe;
import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.StatRequirements;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-5.3.3 — covers the crafting-result-row click path wired through
 * {@link FizzleEnchantmentMenu#clickMenuButton} (button id 3). The actual menu click requires a
 * live {@code ServerPlayer} + {@code Level} pair, so the click-time gate is exercised through its
 * extracted helper ({@link FizzleEnchantmentLogic#validateCraftingClick}) and the keep-nbt path
 * is verified against the same {@link net.minecraft.world.item.crafting.Recipe#assemble} call the
 * server handler performs.
 */
class CraftingResultFlowTest {

    private static HolderLookup.Provider lookup;

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        // Enchantments are data-driven in 1.21.1; VanillaRegistries populates the dynamic lookup
        // so Sharpness resolves below without a server boot.
        lookup = VanillaRegistries.createLookup();
    }

    private static Holder<Enchantment> sharpness() {
        return lookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS);
    }

    // ---- validateCraftingClick (server-side gate) --------------------------

    @Test
    void validateCraftingClick_noRecipe_rejects() {
        // Stale client payload: the client still shows the fourth row but the server's cached
        // recipe has since cleared (input removed between the send and the click).
        FizzleEnchantmentLogic.CraftingClickAttempt attempt = FizzleEnchantmentLogic
                .validateCraftingClick(false, 20, 30, false);
        assertFalse(attempt.success(),
                "id==3 with no cached recipe must reject — there's nothing to craft");
        assertTrue(attempt.rejection().contains("recipe"),
                "rejection reason should mention the missing recipe for debuggability");
    }

    @Test
    void validateCraftingClick_insufficientXp_rejects() {
        // Recipe present but player has 12 levels vs. a 15-level cost — reject, no side effects.
        FizzleEnchantmentLogic.CraftingClickAttempt attempt = FizzleEnchantmentLogic
                .validateCraftingClick(true, 15, 12, false);
        assertFalse(attempt.success(),
                "player xp below the recipe's xp_cost must reject the click");
        assertTrue(attempt.rejection().contains("experience"),
                "rejection reason should mention experience for log grepping");
    }

    @Test
    void validateCraftingClick_enoughXp_passes() {
        FizzleEnchantmentLogic.CraftingClickAttempt attempt = FizzleEnchantmentLogic
                .validateCraftingClick(true, 10, 10, false);
        assertTrue(attempt.success(),
                "xp exactly equal to cost must pass — the ledger balances");
    }

    @Test
    void validateCraftingClick_creative_bypassesXpGate() {
        // Creative bypasses the xp gate just like vanilla enchanting. The recipe-presence check
        // still runs so a click with no server-side match no-ops regardless.
        FizzleEnchantmentLogic.CraftingClickAttempt attempt = FizzleEnchantmentLogic
                .validateCraftingClick(true, 30, 0, true);
        assertTrue(attempt.success(), "creative bypasses the xp gate");

        FizzleEnchantmentLogic.CraftingClickAttempt missing = FizzleEnchantmentLogic
                .validateCraftingClick(false, 30, 0, true);
        assertFalse(missing.success(),
                "creative does not bypass the recipe-presence check — no recipe, no op");
    }

    @Test
    void validateCraftingClick_zeroCost_passesWithNoXp() {
        // Free recipes (xp_cost omitted → default 0) must still apply without any xp.
        FizzleEnchantmentLogic.CraftingClickAttempt attempt = FizzleEnchantmentLogic
                .validateCraftingClick(true, 0, 0, false);
        assertTrue(attempt.success(),
                "xp_cost=0 recipes apply regardless of the player's xp bar");
    }

    // ---- assemble path (keep-nbt preserves stored books) --------------------

    @Test
    void keepNbtAssemble_preservesStoredEnchantmentsOnLibraryUpgrade() {
        // Library → Ender Library upgrade: the menu's button-id=3 handler calls
        // recipe.assemble(new SingleRecipeInput(input), registryAccess). Stand in for the library
        // block-item with Items.BOOK marked with ItemEnchantments — the same DataComponent the
        // keep-nbt subtype copies.
        ItemStack libraryIn = new ItemStack(Items.BOOK);
        ItemEnchantments.Mutable stored = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        stored.set(sharpness(), 5);
        libraryIn.set(DataComponents.ENCHANTMENTS, stored.toImmutable());

        KeepNbtEnchantingRecipe upgrade = new KeepNbtEnchantingRecipe(
                Ingredient.of(Items.BOOK),
                new StatRequirements(50F, 45F, 100F),
                new StatRequirements(50F, 50F, 100F),
                new ItemStack(Items.ENCHANTED_BOOK),
                OptionalInt.empty(),
                30);

        ItemStack crafted = upgrade.assemble(new SingleRecipeInput(libraryIn), null);

        ItemEnchantments transferred = crafted.get(DataComponents.ENCHANTMENTS);
        assertNotNull(transferred, "keep-nbt assemble must set the enchantments component on the output");
        assertEquals(1, transferred.size(),
                "the output must carry exactly the stored enchantment from the input");
        assertEquals(5, transferred.getLevel(sharpness()),
                "sharpness level 5 survives the upgrade — the library's stored books are preserved");
        assertEquals(Items.ENCHANTED_BOOK, crafted.getItem(),
                "the output item type comes from the recipe's static result, not the input");
    }

    @Test
    void baseAssemble_doesNotPropagateInputEnchantments() {
        // The non-keep-nbt subtype (the default for most table recipes — hellshelf → infused, tome
        // upgrades, etc.) must produce a vanilla result with no carryover, so the server handler's
        // "enchanting recipe" branch doesn't accidentally copy NBT from the source stack.
        ItemStack swordIn = new ItemStack(Items.DIAMOND_SWORD);
        ItemEnchantments.Mutable stored = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        stored.set(sharpness(), 5);
        swordIn.set(DataComponents.ENCHANTMENTS, stored.toImmutable());

        EnchantingRecipe plain = new EnchantingRecipe(
                Ingredient.of(Items.DIAMOND_SWORD),
                new StatRequirements(40F, 0F, 0F),
                StatRequirements.NO_MAX,
                new ItemStack(Items.DIAMOND),
                OptionalInt.empty(),
                12);

        ItemStack crafted = plain.assemble(new SingleRecipeInput(swordIn), null);

        ItemEnchantments carried = crafted.get(DataComponents.ENCHANTMENTS);
        // ItemStack components default to ItemEnchantments.EMPTY when never set; verify the
        // output carries the default (empty) map rather than the input's sharpness 5.
        assertTrue(carried == null || carried.isEmpty(),
                "non-keep-nbt recipes must not leak input enchantments onto the output");
        assertEquals(Items.DIAMOND, crafted.getItem(),
                "base enchanting recipes return their static result verbatim");
    }
}
