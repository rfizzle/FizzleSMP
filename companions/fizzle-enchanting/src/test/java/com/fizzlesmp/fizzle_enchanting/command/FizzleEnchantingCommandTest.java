package com.fizzlesmp.fizzle_enchanting.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FizzleEnchantingCommandTest {

    // ---- Dispatcher wiring ----

    @Test
    void rootLiteral_isStable() {
        assertEquals("fizzleenchanting", FizzleEnchantingCommand.ROOT);
    }

    @Test
    void register_addsRootLiteralToDispatcher() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();

        FizzleEnchantingCommand.register(dispatcher);

        CommandNode<CommandSourceStack> node = dispatcher.getRoot().getChild(FizzleEnchantingCommand.ROOT);
        assertNotNull(node, "dispatcher should expose the fizzleenchanting literal (visible to /help)");
        assertEquals(FizzleEnchantingCommand.ROOT, node.getName());
    }

    @Test
    void register_addsReloadSubcommandUnderRoot() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();

        FizzleEnchantingCommand.register(dispatcher);

        CommandNode<CommandSourceStack> reloadNode = dispatcher.getRoot()
                .getChild(FizzleEnchantingCommand.ROOT)
                .getChild("reload");
        assertNotNull(reloadNode, "reload subcommand should be registered under the root literal");
        assertNotNull(reloadNode.getCommand(), "reload should have an executor");
    }

    // ---- Permission predicate ----

    @Test
    void reload_requiresPerm2_rejectsPerm0() {
        Predicate<CommandSourceStack> requires = getRequirement("reload");

        assertFalse(requires.test(stubSource(0)), "perm 0 must not satisfy requires predicate");
        assertFalse(requires.test(stubSource(1)), "perm 1 must not satisfy requires predicate");
    }

    @Test
    void reload_requiresPerm2_acceptsPerm2AndAbove() {
        Predicate<CommandSourceStack> requires = getRequirement("reload");

        assertTrue(requires.test(stubSource(2)), "perm 2 should satisfy requires predicate");
        assertTrue(requires.test(stubSource(4)), "perm 4 should satisfy requires predicate");
    }

    // ---- Reload core ----

    @Test
    void reload_atPerm2_succeeds() {
        AtomicInteger reloaded = new AtomicInteger();
        AtomicReference<Boolean> sinkSuccess = new AtomicReference<>();
        AtomicReference<Supplier<Component>> sinkMessage = new AtomicReference<>();

        int result = FizzleEnchantingCommand.runReload(
                reloaded::incrementAndGet,
                (ok, msg) -> {
                    sinkSuccess.set(ok);
                    sinkMessage.set(msg);
                });

        assertEquals(Command.SINGLE_SUCCESS, result, "successful reload should return SINGLE_SUCCESS");
        assertEquals(1, reloaded.get(), "reload action should be invoked exactly once");
        assertEquals(Boolean.TRUE, sinkSuccess.get(), "sink should report success");
        assertTranslationKey(sinkMessage.get().get(), FizzleEnchantingCommand.RELOAD_OK_KEY);
    }

    @Test
    void reload_atPerm0_fails() {
        Predicate<CommandSourceStack> requires = getRequirement("reload");

        // At perm 0, the requires predicate filters the node out of the dispatch tree
        // (Brigadier surfaces this as a parse/permission failure at the client).
        assertFalse(requires.test(stubSource(0)));
    }

    @Test
    void reload_failedReload_returnsZeroAndSendsFailure() {
        AtomicReference<Boolean> sinkSuccess = new AtomicReference<>();
        AtomicReference<Supplier<Component>> sinkMessage = new AtomicReference<>();

        int result = FizzleEnchantingCommand.runReload(
                () -> { throw new RuntimeException("boom"); },
                (ok, msg) -> {
                    sinkSuccess.set(ok);
                    sinkMessage.set(msg);
                });

        assertEquals(0, result, "failed reload should return 0");
        assertEquals(Boolean.FALSE, sinkSuccess.get(), "sink should report failure");
        assertTranslationKey(sinkMessage.get().get(), FizzleEnchantingCommand.RELOAD_ERROR_KEY);
    }

    // ---- Stub subcommands: dispatcher surface ----

    @Test
    void register_addsStatsSubcommandWithPlayerArgument() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        FizzleEnchantingCommand.register(dispatcher);

        CommandNode<CommandSourceStack> stats = rootOf(dispatcher).getChild("stats");
        assertNotNull(stats, "stats subcommand should be registered");
        CommandNode<CommandSourceStack> playerArg = stats.getChild("player");
        assertNotNull(playerArg, "stats should take a <player> argument");
        assertNotNull(playerArg.getCommand(), "stats <player> should have an executor");
    }

    @Test
    void stats_isOpenToAllPerms() {
        Predicate<CommandSourceStack> requires = getRequirement("stats");
        assertTrue(requires.test(stubSource(0)), "stats is perm 0 (self-service lookup)");
        assertTrue(requires.test(stubSource(2)), "stats must still accept ops");
    }

    @Test
    void register_addsLibraryDumpSubcommand() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        FizzleEnchantingCommand.register(dispatcher);

        CommandNode<CommandSourceStack> library = rootOf(dispatcher).getChild("library");
        assertNotNull(library, "library subcommand should be registered");
        CommandNode<CommandSourceStack> playerArg = library.getChild("player");
        assertNotNull(playerArg, "library should take a <player> argument");
        CommandNode<CommandSourceStack> dump = playerArg.getChild("dump");
        assertNotNull(dump, "library <player> dump literal should be registered");
        assertNotNull(dump.getCommand(), "library <player> dump should have an executor");
    }

    @Test
    void library_requiresPerm2() {
        Predicate<CommandSourceStack> requires = getRequirement("library");
        assertFalse(requires.test(stubSource(0)));
        assertFalse(requires.test(stubSource(1)));
        assertTrue(requires.test(stubSource(2)));
    }

    @Test
    void register_addsGiveTomeWithThreeLiteralTypes() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        FizzleEnchantingCommand.register(dispatcher);

        CommandNode<CommandSourceStack> giveTome = rootOf(dispatcher).getChild("give-tome");
        assertNotNull(giveTome, "give-tome subcommand should be registered");
        CommandNode<CommandSourceStack> playerArg = giveTome.getChild("player");
        assertNotNull(playerArg, "give-tome should take a <player> argument");

        for (String type : new String[]{
                FizzleEnchantingCommand.TOME_SCRAP,
                FizzleEnchantingCommand.TOME_IMPROVED_SCRAP,
                FizzleEnchantingCommand.TOME_EXTRACTION}) {
            CommandNode<CommandSourceStack> typeNode = playerArg.getChild(type);
            assertNotNull(typeNode, "give-tome <player> " + type + " literal should be registered");
            assertNotNull(typeNode.getCommand(), "give-tome <player> " + type + " should have an executor");
        }
    }

    @Test
    void giveTome_requiresPerm2() {
        Predicate<CommandSourceStack> requires = getRequirement("give-tome");
        assertFalse(requires.test(stubSource(0)));
        assertFalse(requires.test(stubSource(1)));
        assertTrue(requires.test(stubSource(2)));
    }

    // ---- Stub subcommands: pure cores ----

    @Test
    void runStatsStub_returnsSuccessAndEmitsPlaceholder() {
        AtomicReference<Supplier<Component>> message = new AtomicReference<>();

        int result = FizzleEnchantingCommand.runStatsStub("Alice", message::set);

        assertEquals(Command.SINGLE_SUCCESS, result);
        Component reply = message.get().get();
        assertTranslationKey(reply, FizzleEnchantingCommand.STATS_STUB_KEY);
        assertTrue(translationArgs(reply)[0] instanceof String s && s.equals("Alice"),
                "stats placeholder should interpolate the target player's name");
    }

    @Test
    void runLibraryDumpStub_returnsSuccessAndEmitsPlaceholder() {
        AtomicReference<Supplier<Component>> message = new AtomicReference<>();

        int result = FizzleEnchantingCommand.runLibraryDumpStub("Bob", message::set);

        assertEquals(Command.SINGLE_SUCCESS, result);
        Component reply = message.get().get();
        assertTranslationKey(reply, FizzleEnchantingCommand.LIBRARY_DUMP_STUB_KEY);
        assertTrue(translationArgs(reply)[0] instanceof String s && s.equals("Bob"));
    }

    @Test
    void runGiveTomeStub_returnsSuccessAndEmitsPlaceholder() {
        for (String type : new String[]{
                FizzleEnchantingCommand.TOME_SCRAP,
                FizzleEnchantingCommand.TOME_IMPROVED_SCRAP,
                FizzleEnchantingCommand.TOME_EXTRACTION}) {
            AtomicReference<Supplier<Component>> message = new AtomicReference<>();

            int result = FizzleEnchantingCommand.runGiveTomeStub("Carol", type, message::set);

            assertEquals(Command.SINGLE_SUCCESS, result, "give-tome stub for " + type + " should succeed");
            Component reply = message.get().get();
            assertTranslationKey(reply, FizzleEnchantingCommand.GIVE_TOME_STUB_KEY);
            Object[] args = translationArgs(reply);
            assertEquals("Carol", args[0], "first arg should be player name");
            assertEquals(type, args[1], "second arg should be tome type literal");
        }
    }

    // ---- helpers ----

    private static CommandNode<CommandSourceStack> rootOf(CommandDispatcher<CommandSourceStack> dispatcher) {
        CommandNode<CommandSourceStack> root = dispatcher.getRoot().getChild(FizzleEnchantingCommand.ROOT);
        assertNotNull(root);
        return root;
    }

    private static Predicate<CommandSourceStack> getRequirement(String subcommand) {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        FizzleEnchantingCommand.register(dispatcher);
        CommandNode<CommandSourceStack> node = rootOf(dispatcher).getChild(subcommand);
        assertNotNull(node, subcommand + " subcommand should be registered");
        return node.getRequirement();
    }

    /**
     * Minimal permission-checking stand-in. The full {@link CommandSourceStack}
     * requires a live server + level, which {@code fabric-loader-junit} is not
     * wired in for. The reload requires predicate only calls
     * {@code hasPermission(int)}, so a subclass that stubs just that method is
     * enough to exercise the gate.
     */
    private static CommandSourceStack stubSource(int permissionLevel) {
        return new CommandSourceStack(
                null, null, null, null,
                permissionLevel, "stub",
                Component.literal("stub"), null, null) {
            @Override
            public boolean hasPermission(int level) {
                return permissionLevel >= level;
            }
        };
    }

    private static void assertTranslationKey(Component component, String expectedKey) {
        assertTrue(component.getContents() instanceof TranslatableContents,
                "expected translatable component, got " + component.getContents().getClass());
        TranslatableContents contents = (TranslatableContents) component.getContents();
        assertEquals(expectedKey, contents.getKey());
    }

    private static Object[] translationArgs(Component component) {
        assertTrue(component.getContents() instanceof TranslatableContents,
                "expected translatable component, got " + component.getContents().getClass());
        return ((TranslatableContents) component.getContents()).getArgs();
    }
}
