package geoactivity.datagen;

import geoactivity.GeoActivity;
import geoactivity.registry.ModItems;
import geoactivity.registry.ModTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;

public class ModItemTagsProvider extends BlockTagCopyingItemTagProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags, GeoActivity.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        copy(ModTags.Blocks.COAL_ORES, ModTags.Items.COAL_ORES);
        copy(Tags.BlockItems.ORES);
        copy(Tags.BlockItems.ORES_IN_GROUND_STONE);
        copy(Tags.BlockItems.GLASS_BLOCKS);

        // The three new coals (1.10.2 OreDict gem*Coal). Machines and tools burn these.
        tag(ModTags.Items.COALS).add(ModItems.LIGNITE_COAL.key(), ModItems.BITUMINOUS_COAL.key(),
                ModItems.ANTHRACITE_COAL.key());
        tag(ModTags.Items.REINFORCED_TOOL_MATERIALS);
        tag(net.minecraft.tags.ItemTags.SWORDS).add(ModItems.REINFORCED_SWORD.key());
        tag(net.minecraft.tags.ItemTags.PICKAXES).add(ModItems.REINFORCED_PICKAXE.key());
        tag(net.minecraft.tags.ItemTags.AXES).add(ModItems.REINFORCED_AXE.key());
        tag(net.minecraft.tags.ItemTags.SHOVELS).add(ModItems.REINFORCED_SHOVEL.key());
        tag(net.minecraft.tags.ItemTags.HOES).add(ModItems.REINFORCED_HOE.key());
        tag(net.minecraft.tags.ItemTags.HEAD_ARMOR).add(ModItems.REINFORCED_HELMET.key(), ModItems.ADVANCED_HELMET.key());
        tag(net.minecraft.tags.ItemTags.CHEST_ARMOR).add(ModItems.REINFORCED_CHESTPLATE.key(), ModItems.ADVANCED_CHESTPLATE.key());
        tag(net.minecraft.tags.ItemTags.LEG_ARMOR).add(ModItems.REINFORCED_LEGGINGS.key(), ModItems.ADVANCED_LEGGINGS.key());
        tag(net.minecraft.tags.ItemTags.FOOT_ARMOR).add(ModItems.REINFORCED_BOOTS.key(), ModItems.ADVANCED_BOOTS.key());
        var elements = tag(ModTags.Items.FILLED_ELEMENT_CONTAINERS);
        ModItems.ELEMENT_CONTAINERS.values().forEach(e -> elements.add(e.key()));
    }
}
