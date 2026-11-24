package geoactivity.datagen;

import geoactivity.GeoActivity;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import java.util.Set;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, GeoActivity.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blocks, ItemModelGenerators items) {
        // Simple cubes
        for (var block : Set.of(ModBlocks.LIGNITE_ORE, ModBlocks.BITUMINOUS_ORE, ModBlocks.ANTHRACITE_ORE,
                ModBlocks.HARDENED_BRICK, ModBlocks.ADVANCED_HARDENED_BRICK, ModBlocks.HARDENED_GLASS, ModBlocks.GRAPHITE_BASE)) {
            blocks.createTrivialCube(block.get());
        }
        cubeTop(blocks, ModBlocks.THERMAL_HARDENED_BRICK.get());

        // Machines: front texture swaps to *_front_on while working (1.10.2 "active" models)
        blocks.createFurnace(ModBlocks.COAL_REFINER.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        blocks.createFurnace(ModBlocks.ATOM_EXTRACTOR.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        blocks.createTrivialBlock(ModBlocks.CRAFTING_MACHINE.get(), TexturedModel.CUBE_BOTTOM_TOP);
        blocks.createTrivialBlock(ModBlocks.CHEMISTRY_MACHINE.get(), TexturedModel.CUBE_TOP);

        // Flat items (everything that isn't a block item or a handheld tool)
        for (var entry : ModItems.ITEMS.getEntries()) {
            Item item = entry.get();
            if (!(item instanceof BlockItem) && !isHandheld(item)) {
                items.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            }
        }
    }

    /** Tools are rendered like vanilla tools. Filled in as tools are ported. */
    static boolean isHandheld(Item item) {
        return false;
    }

    /** Top texture {@code <name>_top}, sides {@code <name>} (1.10.2 used block/cube_top). */
    private static void cubeTop(BlockModelGenerators blocks, Block block) {
        var textures = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"));
        var model = ModelTemplates.CUBE_TOP.create(block, textures, blocks.modelOutput);
        blocks.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
    }
}
