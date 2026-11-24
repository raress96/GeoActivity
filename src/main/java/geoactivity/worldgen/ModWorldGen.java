package geoactivity.worldgen;

import geoactivity.GeoActivity;
import geoactivity.registry.ModBlocks;
import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Coal ore generation, same numbers as 1.10.2: per chunk, {@code count} veins of {@code size} blocks at a uniform
 * height in [minY, maxY). Overworld only, replaces stone (incl. granite/diorite/andesite). Override with a datapack.
 */
public final class ModWorldGen {
    public record Ore(String name, java.util.function.Supplier<? extends Block> block, int count, int minY, int maxY, int size) {
        public ResourceKey<Feature> featureKey() {
            return ResourceKey.create(Registries.FEATURE, GeoActivity.id(name));
        }

        public ResourceKey<PlacedFeature> placedKey() {
            return ResourceKey.create(Registries.PLACED_FEATURE, GeoActivity.id(name));
        }
    }

    public static final List<Ore> ORES = List.of(
            new Ore("ore_lignite", ModBlocks.LIGNITE_ORE, 14, 46, 64, 3),
            new Ore("ore_bituminous", ModBlocks.BITUMINOUS_ORE, 14, 34, 48, 3),
            new Ore("ore_anthracite", ModBlocks.ANTHRACITE_ORE, 14, 18, 38, 3));

    public static final ResourceKey<BiomeModifier> ADD_ORES =
            ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, GeoActivity.id("add_coal_ores"));

    private ModWorldGen() {}

    public static void features(BootstrapContext<Feature> context) {
        var stone = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        for (Ore ore : ORES) {
            context.register(ore.featureKey(), new OreFeature(stone, ore.block().get().defaultBlockState(), ore.size()));
        }
    }

    public static void placedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<Feature> features = context.lookup(Registries.FEATURE);
        for (Ore ore : ORES) {
            context.register(ore.placedKey(), new PlacedFeature(features.getOrThrow(ore.featureKey()), List.of(
                    CountPlacement.of(ore.count()),
                    InSquarePlacement.spread(),
                    // old code: minY + nextInt(maxY - minY) -> [minY, maxY - 1]
                    HeightRangePlacement.uniform(VerticalAnchor.absolute(ore.minY()), VerticalAnchor.absolute(ore.maxY() - 1)),
                    BiomeFilter.biome())));
        }
    }

    public static void biomeModifiers(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
        var features = net.minecraft.core.HolderSet.direct(ORES.stream().map(o -> placed.getOrThrow(o.placedKey())).toList());
        context.register(ADD_ORES, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD), features, GenerationStep.Decoration.UNDERGROUND_ORES));
    }
}
