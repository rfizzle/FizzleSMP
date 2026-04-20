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
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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

    /**
     * Scans the 15 {@link EnchantingTableBlock#BOOKSHELF_OFFSETS vanilla shelf offsets} around
     * {@code tablePos} and returns an aggregated {@link StatCollection}.
     *
     * <p>Shelves whose midpoint block (half-offset in X and Z, same Y as the shelf) is not in
     * {@link BlockTags#ENCHANTMENT_POWER_TRANSMITTER} contribute zero, matching vanilla's
     * {@link EnchantingTableBlock#isValidBookShelf} line-of-sight rule.
     *
     * <p>Aggregation rules (T-2.2.3): {@code eterna} is clamped to {@code [0, maxEterna]}
     * where {@code maxEterna} is the max across contributors; {@code clues} is clamped to
     * {@code [0, }{@link StatCollection#MAX_CLUES}{@code ]}; {@code quanta}, {@code arcana},
     * and {@code rectification} are uncapped (negatives allowed).
     *
     * <p>Filtering-shelf blacklists and treasure-shelf flags are picked up via the context
     * lookup: any in-range {@link BlockPos} whose block entity implements {@link BlacklistSource}
     * contributes its set to {@link StatCollection#blacklist()}; any that implements
     * {@link TreasureFlagSource} flips {@link StatCollection#treasureAllowed()} to {@code true}.
     * Shelves whose midpoint fails the transmitter check contribute neither stats nor context.
     */
    public static StatCollection gatherStats(Level level, BlockPos tablePos) {
        return INSTANCE.gatherStatsFromOffsets(
                EnchantingTableBlock.BOOKSHELF_OFFSETS,
                offset -> INSTANCE.resolve(level.getBlockState(tablePos.offset(offset))),
                offset -> level.getBlockState(tablePos.offset(midpoint(offset)))
                        .is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER),
                offset -> level.getBlockEntity(tablePos.offset(offset)));
    }

    /**
     * Relative midpoint offset between the table and a shelf at {@code offset}. Matches vanilla
     * {@link EnchantingTableBlock#isValidBookShelf}: X and Z are halved (integer division),
     * Y is preserved. The absolute midpoint position is {@code tablePos.offset(midpoint(offset))}.
     */
    static BlockPos midpoint(BlockPos offset) {
        return new BlockPos(offset.getX() / 2, offset.getY(), offset.getZ() / 2);
    }

    StatCollection gatherStatsFromOffsets(
            List<BlockPos> offsets, Function<BlockPos, EnchantingStats> lookup) {
        return gatherStatsFromOffsets(offsets, lookup, pos -> true, pos -> null);
    }

    StatCollection gatherStatsFromOffsets(
            List<BlockPos> offsets,
            Function<BlockPos, EnchantingStats> lookup,
            Predicate<BlockPos> transmitterCheck) {
        return gatherStatsFromOffsets(offsets, lookup, transmitterCheck, pos -> null);
    }

    StatCollection gatherStatsFromOffsets(
            List<BlockPos> offsets,
            Function<BlockPos, EnchantingStats> lookup,
            Predicate<BlockPos> transmitterCheck,
            Function<BlockPos, Object> contextLookup) {
        float eterna = 0F;
        float quanta = 0F;
        float arcana = 0F;
        float rectification = 0F;
        int clues = 0;
        float maxEterna = 0F;
        Set<ResourceKey<Enchantment>> blacklist = null;
        boolean treasureAllowed = false;
        for (BlockPos offset : offsets) {
            if (!transmitterCheck.test(offset)) {
                continue;
            }
            EnchantingStats stats = lookup.apply(offset);
            if (stats != null && !stats.equals(EnchantingStats.ZERO)) {
                eterna += stats.eterna();
                quanta += stats.quanta();
                arcana += stats.arcana();
                rectification += stats.rectification();
                clues += stats.clues();
                if (stats.maxEterna() > maxEterna) {
                    maxEterna = stats.maxEterna();
                }
            }
            Object context = contextLookup.apply(offset);
            if (context instanceof BlacklistSource source) {
                Set<ResourceKey<Enchantment>> entries = source.getEnchantmentBlacklist();
                if (entries != null && !entries.isEmpty()) {
                    if (blacklist == null) {
                        blacklist = new HashSet<>();
                    }
                    blacklist.addAll(entries);
                }
            }
            if (context instanceof TreasureFlagSource) {
                treasureAllowed = true;
            }
        }
        float clampedEterna = Math.max(0F, Math.min(eterna, maxEterna));
        int clampedClues = Math.max(0, Math.min(clues, StatCollection.MAX_CLUES));
        Set<ResourceKey<Enchantment>> finalBlacklist = blacklist == null
                ? Set.of()
                : Set.copyOf(blacklist);
        return new StatCollection(
                clampedEterna, quanta, arcana, rectification, clampedClues, maxEterna,
                finalBlacklist, treasureAllowed);
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
