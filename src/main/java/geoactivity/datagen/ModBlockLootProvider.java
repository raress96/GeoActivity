package geoactivity.datagen;

import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import java.util.Set;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

public class ModBlockLootProvider extends BlockLootSubProvider {
    public ModBlockLootProvider(LootTableSubProvider.Context context) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), context);
    }

    @Override
    protected void generate() {
        // Ores drop their coal (vanilla ore fortune formula; silk touch gives the ore).
        add(ModBlocks.LIGNITE_ORE.get(), b -> createOreDrop(b, ModItems.LIGNITE_COAL.get()));
        add(ModBlocks.BITUMINOUS_ORE.get(), b -> createOreDrop(b, ModItems.BITUMINOUS_COAL.get()));
        add(ModBlocks.ANTHRACITE_ORE.get(), b -> createOreDrop(b, ModItems.ANTHRACITE_COAL.get()));

        dropSelf(ModBlocks.HARDENED_BRICK.get());
        dropSelf(ModBlocks.ADVANCED_HARDENED_BRICK.get());
        dropSelf(ModBlocks.THERMAL_HARDENED_BRICK.get());
        dropSelf(ModBlocks.HARDENED_GLASS.get());
        dropSelf(ModBlocks.GRAPHITE_BASE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().<Block>map(e -> e.get()).toList();
    }
}
