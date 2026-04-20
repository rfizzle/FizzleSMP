package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public final class EnchantingStatRegistry implements SimpleSynchronousResourceReloadListener {

    public static final ResourceLocation LISTENER_ID = FizzleEnchanting.id("enchanting_stats");

    /** Vanilla-shelf safety net: applied when a block is in the vanilla power-provider tag but absent from the datapack registry. */
    public static final EnchantingStats VANILLA_FALLBACK = new EnchantingStats(15F, 1F, 0F, 0F, 0F, 0);

    private static final String RESOURCE_DIR = "enchanting_stats";
    private static final String JSON_SUFFIX = ".json";

    private static final EnchantingStatRegistry INSTANCE = new EnchantingStatRegistry();

    private final Map<ResourceLocation, EnchantingStats> byBlock = new HashMap<>();
    private final List<TagBinding> byTag = new ArrayList<>();

    EnchantingStatRegistry() {
    }

    public static EnchantingStatRegistry getInstance() {
        return INSTANCE;
    }

    public static void bootstrap() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(INSTANCE);
    }

    public static EnchantingStats lookup(Level level, BlockState state) {
        return INSTANCE.resolve(state);
    }

    EnchantingStats resolve(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return resolveWith(id, state::is);
    }

    EnchantingStats resolveWith(ResourceLocation blockId, Predicate<TagKey<Block>> inTag) {
        EnchantingStats direct = byBlock.get(blockId);
        if (direct != null) {
            return direct;
        }
        for (TagBinding binding : byTag) {
            if (inTag.test(binding.tag())) {
                return binding.stats();
            }
        }
        if (inTag.test(BlockTags.ENCHANTMENT_POWER_PROVIDER)) {
            return VANILLA_FALLBACK;
        }
        return EnchantingStats.ZERO;
    }

    void registerBlock(ResourceLocation blockId, EnchantingStats stats) {
        byBlock.put(blockId, stats);
    }

    void registerTag(TagKey<Block> tag, EnchantingStats stats) {
        byTag.add(new TagBinding(tag, stats));
    }

    void clear() {
        byBlock.clear();
        byTag.clear();
    }

    int blockEntryCount() {
        return byBlock.size();
    }

    int tagEntryCount() {
        return byTag.size();
    }

    @Override
    public ResourceLocation getFabricId() {
        return LISTENER_ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        clear();
        manager.listResources(RESOURCE_DIR, rl -> rl.getPath().endsWith(JSON_SUFFIX))
                .forEach((rl, resource) -> {
                    try (Reader reader = resource.openAsReader()) {
                        JsonElement json = JsonParser.parseReader(reader);
                        parseAndRegister(rl, json);
                    } catch (Exception e) {
                        FizzleEnchanting.LOGGER.warn(
                                "Failed to load enchanting_stats entry {}: {}", rl, e.getMessage(), e);
                    }
                });
        FizzleEnchanting.LOGGER.info(
                "Loaded enchanting stats: {} block entries, {} tag entries",
                byBlock.size(), byTag.size());
    }

    void parseAndRegister(ResourceLocation source, JsonElement json) {
        DataResult<StatEntry> result = StatEntry.CODEC.parse(JsonOps.INSTANCE, json);
        Optional<DataResult.Error<StatEntry>> error = result.error();
        if (error.isPresent()) {
            throw new JsonParseException(
                    "Invalid enchanting_stats entry " + source + ": " + error.get().message());
        }
        StatEntry entry = result.result().orElseThrow();
        entry.block().ifPresent(id -> registerBlock(id, entry.stats()));
        entry.tag().ifPresent(tag -> registerTag(tag, entry.stats()));
    }

    private record TagBinding(TagKey<Block> tag, EnchantingStats stats) {
    }

    public record StatEntry(
            Optional<ResourceLocation> block,
            Optional<TagKey<Block>> tag,
            EnchantingStats stats
    ) {
        private static final Codec<TagKey<Block>> TAG_CODEC = Codec.STRING.comapFlatMap(
                s -> {
                    String stripped = s.startsWith("#") ? s.substring(1) : s;
                    try {
                        return DataResult.success(
                                TagKey.create(Registries.BLOCK, ResourceLocation.parse(stripped)));
                    } catch (ResourceLocationException e) {
                        return DataResult.error(() -> "Invalid tag id '" + s + "': " + e.getMessage());
                    }
                },
                tag -> "#" + tag.location()
        );

        public static final Codec<StatEntry> CODEC = RecordCodecBuilder.<StatEntry>create(inst -> inst.group(
                        ResourceLocation.CODEC.optionalFieldOf("block").forGetter(StatEntry::block),
                        TAG_CODEC.optionalFieldOf("tag").forGetter(StatEntry::tag),
                        EnchantingStats.MAP_CODEC.forGetter(StatEntry::stats))
                .apply(inst, StatEntry::new))
                .flatXmap(StatEntry::validate, StatEntry::validate);

        private static DataResult<StatEntry> validate(StatEntry entry) {
            if (entry.block().isPresent() && entry.tag().isPresent()) {
                return DataResult.error(() ->
                        "Only one of 'block' or 'tag' may be specified, not both");
            }
            if (entry.block().isEmpty() && entry.tag().isEmpty()) {
                return DataResult.error(() ->
                        "One of 'block' or 'tag' must be specified");
            }
            return DataResult.success(entry);
        }
    }
}
