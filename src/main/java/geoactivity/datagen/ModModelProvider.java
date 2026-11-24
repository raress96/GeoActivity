package geoactivity.datagen;

import geoactivity.GeoActivity;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import java.util.Set;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import geoactivity.machine.orerefiner.AdvancedOreRefinerBlock;
import geoactivity.machine.refiner.AdvancedCoalRefinerBlock;
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
        multiblocks(blocks);
        energyMachines(blocks);

        // Flat items (everything that isn't a block item or a handheld tool)
        for (var entry : ModItems.ITEMS.getEntries()) {
            Item item = entry.get();
            if (!(item instanceof BlockItem)) {
                items.generateFlatItem(item, isHandheld(item) ? ModelTemplates.FLAT_HANDHELD_ITEM : ModelTemplates.FLAT_ITEM);
            }
        }
    }

    /** Tools are rendered like vanilla tools. Filled in as tools are ported. */
    static boolean isHandheld(Item item) {
        return item instanceof geoactivity.tool.PerkTool || item instanceof geoactivity.tool.AutoStoneBuilderItem
                || item == ModItems.REINFORCED_SWORD.get() || item == ModItems.REINFORCED_PICKAXE.get() || item == ModItems.REINFORCED_AXE.get()
                || item == ModItems.REINFORCED_SHOVEL.get() || item == ModItems.REINFORCED_HOE.get();
    }

    private static void energyMachines(BlockModelGenerators blocks) {
        Block tg = ModBlocks.THERMIC_GENERATOR.get();
        simple(blocks, tg, ModelTemplates.CUBE_BOTTOM_TOP, new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(tg, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(tg, "_side"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(tg, "_bottom")));
        Block gg = ModBlocks.GEOTHERMAL_GENERATOR.get();
        simple(blocks, gg, ModelTemplates.CUBE_BOTTOM_TOP, new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(gg, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(gg, "_bottom"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(gg, "_bottom")));
        blocks.createTrivialCube(ModBlocks.THERMIC_MELTER.get());
        blocks.createTrivialBlock(ModBlocks.TOOL_CHARGER.get(), TexturedModel.CUBE_TOP);
    }

    private static void simple(BlockModelGenerators blocks, Block block, net.minecraft.client.data.models.model.ModelTemplate template,
            TextureMapping textures) {
        var model = template.create(block, textures, blocks.modelOutput);
        blocks.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
    }

    /** ACR: plain until formed. AOR: core plain/formed, other members show the "formed" texture. */
    private static void multiblocks(BlockModelGenerators blocks) {
        Block acr = ModBlocks.ADVANCED_COAL_REFINER.get();
        var acrPlain = ModelTemplates.CUBE_ALL.create(acr, TextureMapping.cube(acr), blocks.modelOutput);
        var acrFormed = ModelTemplates.CUBE_TOP.createWithSuffix(acr, "_formed", new TextureMapping()
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(acr))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(acr, "_formed")), blocks.modelOutput);
        blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(acr).with(PropertyDispatch.initial(AdvancedCoalRefinerBlock.FORMED)
                .select(false, BlockModelGenerators.plainVariant(acrPlain))
                .select(true, BlockModelGenerators.plainVariant(acrFormed))));

        Block aor = ModBlocks.ADVANCED_ORE_REFINER.get();
        var aorCore = ModelTemplates.CUBE_ALL.create(aor, TextureMapping.cube(aor), blocks.modelOutput);
        var aorCoreFormed = ModelTemplates.CUBE_TOP.createWithSuffix(aor, "_core_formed", new TextureMapping()
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(aor))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(aor, "_core_formed")), blocks.modelOutput);
        var aorMember = ModelTemplates.CUBE_ALL.createWithSuffix(aor, "_formed",
                TextureMapping.cube(TextureMapping.getBlockTexture(aor, "_formed")), blocks.modelOutput);
        blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(aor).with(
                PropertyDispatch.initial(AdvancedOreRefinerBlock.CORE, AdvancedOreRefinerBlock.FORMED)
                        .select(true, false, BlockModelGenerators.plainVariant(aorCore))
                        .select(true, true, BlockModelGenerators.plainVariant(aorCoreFormed))
                        .select(false, true, BlockModelGenerators.plainVariant(aorMember))
                        .select(false, false, BlockModelGenerators.plainVariant(aorMember))));
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
