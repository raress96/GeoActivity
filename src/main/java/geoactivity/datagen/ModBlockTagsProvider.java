package geoactivity.datagen;

import geoactivity.GeoActivity;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, GeoActivity.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        // Everything in the mod is mined with a pickaxe.
        var pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        ModBlocks.BLOCKS.getEntries().forEach(b -> pickaxe.add(b.getKey()));

        // 1.10.2 harvest level 2 = iron, 3 = diamond
        tag(BlockTags.NEEDS_IRON_TOOL).add(key(ModBlocks.LIGNITE_ORE), key(ModBlocks.BITUMINOUS_ORE), key(ModBlocks.GRAPHITE_BASE));
        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(key(ModBlocks.ANTHRACITE_ORE), key(ModBlocks.HARDENED_BRICK),
                key(ModBlocks.ADVANCED_HARDENED_BRICK), key(ModBlocks.THERMAL_HARDENED_BRICK), key(ModBlocks.HARDENED_GLASS));

        tag(ModTags.Blocks.COAL_ORES).add(key(ModBlocks.LIGNITE_ORE), key(ModBlocks.BITUMINOUS_ORE), key(ModBlocks.ANTHRACITE_ORE));
        tag(Tags.BlockItems.ORES.block()).addTag(ModTags.Blocks.COAL_ORES);
        tag(Tags.BlockItems.ORES_IN_GROUND_STONE.block()).addTag(ModTags.Blocks.COAL_ORES);
        tag(Tags.BlockItems.GLASS_BLOCKS.block()).add(key(ModBlocks.HARDENED_GLASS));
    }

    private static net.minecraft.resources.ResourceKey<Block> key(DeferredBlock<?> block) {
        return block.getKey();
    }
}
