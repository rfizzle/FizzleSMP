package com.rfizzle.meridian.command;

import com.rfizzle.meridian.Meridian;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class MeridianCommand {
    public static final String ROOT = "meridian";

    static final String RELOAD_OK_KEY = "command.meridian.reload.ok";
    static final String RELOAD_ERROR_KEY = "command.meridian.reload.error";
    static final String STATS_STUB_KEY = "command.meridian.stats.not_implemented";
    static final String LIBRARY_DUMP_STUB_KEY = "command.meridian.library.dump.not_implemented";
    static final String GIVE_TOME_STUB_KEY = "command.meridian.give_tome.not_implemented";

    public static final String TOME_SCRAP = "scrap";
    public static final String TOME_IMPROVED_SCRAP = "improved_scrap";
    public static final String TOME_EXTRACTION = "extraction";

    private MeridianCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(ROOT)
                        .then(Commands.literal("reload")
                                .requires(src -> src.hasPermission(2))
                                .executes(MeridianCommand::runReload))
                        .then(Commands.literal("stats")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(MeridianCommand::runStats)))
                        .then(Commands.literal("library")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.literal("dump")
                                                .executes(MeridianCommand::runLibraryDump))))
                        .then(Commands.literal("give-tome")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.literal(TOME_SCRAP)
                                                .executes(ctx -> runGiveTome(ctx, TOME_SCRAP)))
                                        .then(Commands.literal(TOME_IMPROVED_SCRAP)
                                                .executes(ctx -> runGiveTome(ctx, TOME_IMPROVED_SCRAP)))
                                        .then(Commands.literal(TOME_EXTRACTION)
                                                .executes(ctx -> runGiveTome(ctx, TOME_EXTRACTION)))))
        );
    }

    private static int runReload(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        return runReload(() -> Meridian.reloadConfig(src.getServer()), (success, message) -> {
            if (success) src.sendSuccess(message, true);
            else src.sendFailure(message.get());
        });
    }

    /**
     * Pure reload core — runs {@code reloader} and routes the translated
     * success/failure message through {@code sink}. Split out so tests can
     * drive it without a live {@link CommandSourceStack}: the command layer
     * only translates the outcome into {@code sendSuccess}/{@code sendFailure}.
     */
    static int runReload(Runnable reloader, MessageSink sink) {
        try {
            reloader.run();
            sink.send(true, () -> Component.translatable(RELOAD_OK_KEY));
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            Meridian.LOGGER.error("Config reload failed via command", e);
            sink.send(false, () -> Component.translatable(RELOAD_ERROR_KEY));
            return 0;
        }
    }

    // ---- Stub subcommands (bodies land in later stories) ----

    private static int runStats(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
        CommandSourceStack src = ctx.getSource();
        return runStatsStub(target.getGameProfile().getName(), msg -> src.sendSuccess(msg, false));
    }

    private static int runLibraryDump(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
        CommandSourceStack src = ctx.getSource();
        return runLibraryDumpStub(target.getGameProfile().getName(), msg -> src.sendSuccess(msg, false));
    }

    private static int runGiveTome(CommandContext<CommandSourceStack> ctx, String type) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
        CommandSourceStack src = ctx.getSource();
        return runGiveTomeStub(target.getGameProfile().getName(), type, msg -> src.sendSuccess(msg, false));
    }

    /**
     * Pure stats stub — logs the "not implemented yet" notice and emits a
     * translated placeholder reply. Story S-2.x replaces the body with the
     * real table-stat dump; the command surface keeps its shape.
     */
    static int runStatsStub(String playerName, Consumer<Supplier<Component>> sink) {
        Meridian.LOGGER.warn("/meridian stats {}: not implemented yet", playerName);
        sink.accept(() -> Component.translatable(STATS_STUB_KEY, playerName));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Pure library-dump stub — same shape as {@link #runStatsStub}. Story S-4.x
     * replaces the body with the real library point dump.
     */
    static int runLibraryDumpStub(String playerName, Consumer<Supplier<Component>> sink) {
        Meridian.LOGGER.warn("/meridian library {} dump: not implemented yet", playerName);
        sink.accept(() -> Component.translatable(LIBRARY_DUMP_STUB_KEY, playerName));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Pure give-tome stub — same shape as {@link #runStatsStub}. Story S-5.x
     * replaces the body with the real tome-item dispatch.
     */
    static int runGiveTomeStub(String playerName, String type, Consumer<Supplier<Component>> sink) {
        Meridian.LOGGER.warn("/meridian give-tome {} {}: not implemented yet", playerName, type);
        sink.accept(() -> Component.translatable(GIVE_TOME_STUB_KEY, playerName, type));
        return Command.SINGLE_SUCCESS;
    }

    @FunctionalInterface
    interface MessageSink {
        void send(boolean success, Supplier<Component> message);
    }
}
