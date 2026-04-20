package com.fizzlesmp.fizzle_enchanting.anvil;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import com.mojang.serialization.Lifecycle;
import net.minecraft.SharedConstants;
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
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-4.1.4 — behavioural contract for {@link PrismaticWebHandler}. The four scenarios directly
 * mirror the task's acceptance tests:
 * <ol>
 *   <li>Cursed left + web right → output strips curses, keeps non-curse enchantments, reports
 *       {@code prismaticWebLevelCost} and consumes one web.</li>
 *   <li>No curses on the left → handler declines so vanilla (or the next dispatcher entry) can
 *       own the pair.</li>
 *   <li>Non-web in right slot → declines.</li>
 *   <li>{@code prismaticWebRemovesCurses=false} → declines even when the pair is otherwise
 *       valid.</li>
 * </ol>
 *
 * <p>Vanilla {@code Bootstrap.bootStrap()} leaves the Enchantment registry empty, so a synthetic
 * registry is rebuilt in {@link #bootstrap}. Only the holders from that registry carry the
 * {@link EnchantmentTags#CURSE} binding — {@code holder.is(EnchantmentTags.CURSE)} returns
 * {@code true} for the two curse fixtures and {@code false} for Sharpness.
 *
 * <p>{@link PrismaticWebItem} is looked up via {@link BuiltInRegistries#ITEM} after
 * {@code FizzleEnchantingRegistry} has registered it — {@link PrismaticWebItemTest} handles the
 * unfreeze/refreeze dance, so this test relies on a shared bootstrap helper.
 */
class PrismaticWebHandlerTest {

    private static final ResourceKey<Enchantment> SHARPNESS =
            mcKey("sharpness");
    private static final ResourceKey<Enchantment> CURSE_OF_VANISHING =
            mcKey("vanishing_curse");
    private static final ResourceKey<Enchantment> CURSE_OF_BINDING =
            mcKey("binding_curse");

    private static Registry<Enchantment> enchantmentRegistry;
    private static PrismaticWebItem prismaticWebItem;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        // PrismaticWebItem requires a MappedRegistry that can create intrusive holders, which
        // Bootstrap#bootStrap freezes. Follow PrismaticWebItemTest's pattern — unfreeze the
        // block/item/menu/block-entity registries, drive FizzleEnchantingRegistry.register(), then
        // refreeze so downstream tests see the normal frozen state.
        unfreezeIntrusive(BuiltInRegistries.BLOCK);
        unfreezeIntrusive(BuiltInRegistries.ITEM);
        unfreezeIntrusive(BuiltInRegistries.BLOCK_ENTITY_TYPE);
        unfreeze(BuiltInRegistries.MENU);

        FizzleEnchantingRegistry.register();

        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
        BuiltInRegistries.MENU.freeze();
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();

        enchantmentRegistry = buildEnchantmentRegistry();
        prismaticWebItem = FizzleEnchantingRegistry.PRISMATIC_WEB;
    }

    @Test
    void handle_strippCurses_keepsNonCurseEnchantments() {
        PrismaticWebHandler handler = new PrismaticWebHandler(PrismaticWebHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(
                SHARPNESS, 3,
                CURSE_OF_VANISHING, 1));
        ItemStack right = web(2);

        Optional<AnvilResult> result = handler.handle(left, right, null);

        assertTrue(result.isPresent(),
                "cursed sword + web must produce a handler claim — otherwise the web is useless");

        AnvilResult r = result.get();
        assertEquals(defaultConfig().anvil.prismaticWebLevelCost, r.xpCost(),
                "xp cost must read from config.anvil.prismaticWebLevelCost");
        assertEquals(1, r.rightConsumed(),
                "one web is consumed per click regardless of input stack size");

        ItemEnchantments output = r.output().getOrDefault(
                DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(1, output.size(),
                "exactly one enchantment survives — sharpness kept, curse dropped");
        assertEquals(3, output.getLevel(holderFor(SHARPNESS)),
                "sharpness level preserved verbatim");
        assertEquals(0, output.getLevel(holderFor(CURSE_OF_VANISHING)),
                "curse of vanishing must be fully removed");

        assertNotSame(left, r.output(),
                "handler must not mutate the left stack in-place — vanilla TAIL hook relies on "
                        + "the returned copy living only in resultSlots");
        ItemEnchantments leftStillCursed = left.getOrDefault(
                DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(1, leftStillCursed.getLevel(holderFor(CURSE_OF_VANISHING)),
                "left stack remains cursed until the player commits the anvil output");
    }

    @Test
    void handle_multipleCurses_allRemoved() {
        PrismaticWebHandler handler = new PrismaticWebHandler(PrismaticWebHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(
                SHARPNESS, 5,
                CURSE_OF_VANISHING, 1,
                CURSE_OF_BINDING, 1));
        ItemStack right = web(1);

        AnvilResult r = handler.handle(left, right, null).orElseThrow();
        ItemEnchantments output = r.output().getOrDefault(
                DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        assertEquals(1, output.size(),
                "both curses removed, leaving only sharpness");
        assertEquals(5, output.getLevel(holderFor(SHARPNESS)));
    }

    @Test
    void handle_noCursesOnLeft_declines() {
        PrismaticWebHandler handler = new PrismaticWebHandler(PrismaticWebHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        ItemStack right = web(1);

        Optional<AnvilResult> result = handler.handle(left, right, null);

        assertTrue(result.isEmpty(),
                "no curses to strip — handler must decline so the web isn't consumed as a no-op");
    }

    @Test
    void handle_unenchantedLeft_declines() {
        PrismaticWebHandler handler = new PrismaticWebHandler(PrismaticWebHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        ItemStack right = web(1);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "completely unenchanted item — nothing to strip, handler must decline");
    }

    @Test
    void handle_nonWebRight_declines() {
        PrismaticWebHandler handler = new PrismaticWebHandler(PrismaticWebHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(
                SHARPNESS, 3,
                CURSE_OF_VANISHING, 1));
        ItemStack right = new ItemStack(Items.COBWEB);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "right slot must be a PrismaticWebItem — plain cobweb does not strip curses");
    }

    @Test
    void handle_emptySlots_declines() {
        PrismaticWebHandler handler = new PrismaticWebHandler(PrismaticWebHandlerTest::defaultConfig);

        assertTrue(handler.handle(ItemStack.EMPTY, web(1), null).isEmpty(),
                "empty left slot — handler must decline");
        ItemStack left = enchantedSword(Map.of(CURSE_OF_VANISHING, 1));
        assertTrue(handler.handle(left, ItemStack.EMPTY, null).isEmpty(),
                "empty right slot — handler must decline");
    }

    @Test
    void handle_configGateOff_declines() {
        FizzleEnchantingConfig config = defaultConfig();
        config.anvil.prismaticWebRemovesCurses = false;
        PrismaticWebHandler handler = new PrismaticWebHandler(() -> config);

        ItemStack left = enchantedSword(Map.of(
                SHARPNESS, 3,
                CURSE_OF_VANISHING, 1));
        ItemStack right = web(1);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "config gate forces decline even on an otherwise-valid pairing — "
                        + "operator toggle must neutralise the handler without unregistration");
    }

    @Test
    void handle_configMissing_declines() {
        // getConfig() returns null before onInitialize has run; the handler must treat that as
        // "feature off" rather than NPE.
        PrismaticWebHandler handler = new PrismaticWebHandler(() -> null);

        ItemStack left = enchantedSword(Map.of(CURSE_OF_VANISHING, 1));
        ItemStack right = web(1);

        assertFalse(handler.handle(left, right, null).isPresent(),
                "null config must not crash — handler must decline until onInitialize populates it");
    }

    @Test
    void handle_customXpCost_reflectsConfig() {
        FizzleEnchantingConfig config = defaultConfig();
        config.anvil.prismaticWebLevelCost = 7;
        PrismaticWebHandler handler = new PrismaticWebHandler(() -> config);

        AnvilResult r = handler.handle(
                enchantedSword(Map.of(CURSE_OF_VANISHING, 1)),
                web(1),
                null).orElseThrow();
        assertEquals(7, r.xpCost(),
                "xp cost must read live config — operators tuning the level cost see it at claim time");
    }

    // ---- Fixtures ----------------------------------------------------------

    private static FizzleEnchantingConfig defaultConfig() {
        return new FizzleEnchantingConfig();
    }

    private static ItemStack web(int count) {
        ItemStack stack = new ItemStack(prismaticWebItem);
        stack.setCount(count);
        return stack;
    }

    private static ItemStack enchantedSword(Map<ResourceKey<Enchantment>, Integer> enchants) {
        ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (Map.Entry<ResourceKey<Enchantment>, Integer> entry : enchants.entrySet()) {
            mutable.set(holderFor(entry.getKey()), entry.getValue());
        }
        stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
        return stack;
    }

    private static Holder<Enchantment> holderFor(ResourceKey<Enchantment> key) {
        return enchantmentRegistry.getHolderOrThrow(key);
    }

    private static ResourceKey<Enchantment> mcKey(String path) {
        return ResourceKey.create(
                Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath("minecraft", path));
    }

    private static Registry<Enchantment> buildEnchantmentRegistry() {
        MappedRegistry<Enchantment> reg = new MappedRegistry<>(Registries.ENCHANTMENT, Lifecycle.stable());
        Holder.Reference<Enchantment> sharpness = reg.register(SHARPNESS, synthetic(), RegistrationInfo.BUILT_IN);
        Holder.Reference<Enchantment> vanishing = reg.register(CURSE_OF_VANISHING, synthetic(), RegistrationInfo.BUILT_IN);
        Holder.Reference<Enchantment> binding = reg.register(CURSE_OF_BINDING, synthetic(), RegistrationInfo.BUILT_IN);

        reg.bindTags(Map.of(
                EnchantmentTags.CURSE, List.of(vanishing, binding)
                // Sharpness intentionally omitted — it must not match the curse tag.
        ));
        return reg.freeze();
    }

    private static Enchantment synthetic() {
        HolderSet<Item> any = HolderSet.direct(List.of(
                BuiltInRegistries.ITEM.wrapAsHolder(Items.DIAMOND_SWORD)));
        Enchantment.EnchantmentDefinition def = Enchantment.definition(
                any, 1, 1,
                Enchantment.dynamicCost(1, 10),
                Enchantment.dynamicCost(51, 10),
                1, EquipmentSlotGroup.ANY);
        return new Enchantment(
                Component.literal("test"),
                def,
                HolderSet.empty(),
                DataComponentMap.EMPTY);
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
