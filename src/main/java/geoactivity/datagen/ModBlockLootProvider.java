package geoactivity.datagen;

import geoactivity.machine.orerefiner.AdvancedOreRefinerBlock;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import java.util.Set;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

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

        dropSelf(ModBlocks.COAL_REFINER.get());
        dropSelf(ModBlocks.CRAFTING_MACHINE.get());
        dropSelf(ModBlocks.ATOM_EXTRACTOR.get());
        dropSelf(ModBlocks.CHEMISTRY_MACHINE.get());
        dropSelf(ModBlocks.ADVANCED_COAL_REFINER.get());
        dropSelf(ModBlocks.THERMIC_GENERATOR.get());
        dropSelf(ModBlocks.TOOL_CHARGER.get());
        dropSelf(ModBlocks.GEOTHERMAL_GENERATOR.get());
        dropSelf(ModBlocks.THERMIC_MELTER.get());
        // only the core drops the refiner; other members give back their brick/ACR from the block entity
        Block aor = ModBlocks.ADVANCED_ORE_REFINER.get();
        add(aor, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
                .add(LootItem.lootTableItem(aor).when(MatchBlock.blockMatches(blocks, aor,
                        StatePropertiesPredicate.Builder.properties().hasProperty(AdvancedOreRefinerBlock.CORE, true))))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().<Block>map(e -> e.get()).toList();
    }
}
