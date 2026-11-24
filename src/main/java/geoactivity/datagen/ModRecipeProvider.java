package geoactivity.datagen;

import geoactivity.GeoActivity;
import geoactivity.registry.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

/** Vanilla crafting/smelting recipes, transcribed from 1.10.2 {@code GAInit.craftingRecipes()}. */
public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        super(recipes, advancements, GeoActivity.MOD_ID);
    }

    @Override
    protected void buildRecipes() {
        blocks();
        machines();
        misc();
        smelting();
    }

    private void blocks() {
        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.HARDENED_BRICK.get(), 8)
                .pattern("XYX").pattern("YZY").pattern("XYX")
                .define('Z', ModItems.CARBON_STICK.get()).define('Y', Blocks.STONE_BRICKS).define('X', ModItems.GRAPHITE.get())
                .unlockedBy("has_graphite", has(ModItems.GRAPHITE.get())).save(output);
        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.HARDENED_GLASS.get(), 8)
                .pattern("XYX").pattern("XZX").pattern("XYX")
                .define('X', Blocks.GLASS).define('Z', ModItems.CARBON_FIBER.get()).define('Y', ModItems.CARBON_STICK.get())
                .unlockedBy("has_carbon_fiber", has(ModItems.CARBON_FIBER.get())).save(output);
        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.GRAPHITE_BASE.get())
                .pattern("XYX").pattern("ZXZ").pattern("XYX")
                .define('X', ModItems.GRAPHITE.get()).define('Z', ModItems.CARBON_FIBER.get()).define('Y', ModItems.CARBON_STICK.get())
                .unlockedBy("has_carbon_fiber", has(ModItems.CARBON_FIBER.get())).save(output);
        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.ADVANCED_HARDENED_BRICK.get())
                .pattern("XYX").pattern("ZZZ").pattern("XYX")
                .define('Y', ModItems.CARBON_STICK.get()).define('Z', ModItems.HARDENED_BRICK.get()).define('X', Items.GOLD_INGOT)
                .unlockedBy("has_hardened_brick", has(ModItems.HARDENED_BRICK.get())).save(output);
        shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.THERMAL_HARDENED_BRICK.get())
                .pattern(" W ").pattern("ZYZ").pattern("XXX")
                .define('Y', ModItems.CARBON_STICK.get()).define('Z', ModItems.HARDENED_BRICK.get())
                .define('X', ModItems.CARBON_FIBER.get()).define('W', Blocks.IRON_BLOCK)
                .unlockedBy("has_hardened_brick", has(ModItems.HARDENED_BRICK.get())).save(output);
    }

    private void machines() {
        shaped(RecipeCategory.DECORATIONS, ModItems.COAL_REFINER.get())
                .pattern("XYX").pattern("WZW").pattern("XYX")
                .define('X', Items.GOLD_INGOT).define('W', Items.IRON_INGOT).define('Y', Blocks.FURNACE).define('Z', Items.COAL)
                .unlockedBy("has_furnace", has(Blocks.FURNACE)).save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.CRAFTING_MACHINE.get())
                .pattern("XYX").pattern("WZW").pattern("XUX")
                .define('X', Items.GLOWSTONE_DUST).define('Z', ModItems.GRAPHITE_BASE.get()).define('Y', ModItems.COAL_REFINER.get())
                .define('W', Items.DIAMOND).define('U', Items.QUARTZ)
                .unlockedBy("has_coal_refiner", has(ModItems.COAL_REFINER.get())).save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.ATOM_EXTRACTOR.get())
                .pattern("XYX").pattern("YWY").pattern("XZX")
                .define('Y', ModItems.EMPTY_ELEMENT_CONTAINER.get()).define('W', ModItems.GRAPHITE_BASE.get())
                .define('X', ModItems.SMALL_PRECIOUS_ALLOY_INGOT.get()).define('Z', Items.EMERALD)
                .unlockedBy("has_graphite_base", has(ModItems.GRAPHITE_BASE.get())).save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.CHEMISTRY_MACHINE.get())
                .pattern("XXX").pattern("WZW").pattern("XXX")
                .define('W', ModItems.GRAPHITE_BASE.get()).define('X', ModItems.EMPTY_ELEMENT_CONTAINER.get())
                .define('Z', ModItems.PRECIOUS_ALLOY_INGOT.get())
                .unlockedBy("has_graphite_base", has(ModItems.GRAPHITE_BASE.get())).save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.ADVANCED_COAL_REFINER.get(), 2)
                .pattern("XYX").pattern("WZW").pattern("XYX")
                .define('X', Items.IRON_INGOT).define('Z', ModItems.COAL_REFINER.get())
                .define('Y', ModItems.SMALL_PRECIOUS_ALLOY_INGOT.get()).define('W', Items.QUARTZ)
                .unlockedBy("has_coal_refiner", has(ModItems.COAL_REFINER.get())).save(output);
        shaped(RecipeCategory.DECORATIONS, ModItems.ADVANCED_ORE_REFINER.get())
                .pattern("XWX").pattern("WZW").pattern("XWX")
                .define('W', Items.DIAMOND).define('X', Items.EMERALD).define('Z', ModItems.GRAPHITE_BASE.get())
                .unlockedBy("has_graphite_base", has(ModItems.GRAPHITE_BASE.get())).save(output);
    }

    private void misc() {
        shaped(RecipeCategory.MISC, Items.DIAMOND)
                .pattern("YXY").pattern("XZX").pattern("YXY")
                .define('X', ModItems.CARBON_FIBER.get()).define('Y', ModItems.CARBON_STICK.get()).define('Z', ModItems.GRAPHITE.get())
                .unlockedBy("has_carbon_stick", has(ModItems.CARBON_STICK.get())).save(output, GeoActivity.MOD_ID + ":diamond_from_carbon");
        shaped(RecipeCategory.MISC, Items.COAL)
                .pattern("XX").pattern("XX")
                .define('X', ModItems.GRAPHITE.get())
                .unlockedBy("has_graphite", has(ModItems.GRAPHITE.get())).save(output, GeoActivity.MOD_ID + ":coal_from_graphite");
        shaped(RecipeCategory.MISC, ModItems.CARBON_FIBER.get())
                .pattern("XXX").pattern("XXX")
                .define('X', ModItems.GRAPHITE.get())
                .unlockedBy("has_graphite", has(ModItems.GRAPHITE.get())).save(output);
        shaped(RecipeCategory.MISC, ModItems.CARBON_STICK.get())
                .pattern("X").pattern("X")
                .define('X', ModItems.CARBON_FIBER.get())
                .unlockedBy("has_carbon_fiber", has(ModItems.CARBON_FIBER.get())).save(output);
        shaped(RecipeCategory.MISC, ModItems.SMALL_PRECIOUS_ALLOY_INGOT.get())
                .pattern("X").pattern("Y").pattern("X")
                .define('X', Items.GOLD_INGOT).define('Y', Items.DIAMOND)
                .unlockedBy("has_diamond", has(Items.DIAMOND)).save(output);
        shaped(RecipeCategory.MISC, ModItems.PRECIOUS_ALLOY_INGOT.get())
                .pattern("XYX").pattern("YXY").pattern("XYX")
                .define('Y', ModItems.SMALL_PRECIOUS_ALLOY_INGOT.get()).define('X', Items.EMERALD)
                .unlockedBy("has_small_precious_alloy", has(ModItems.SMALL_PRECIOUS_ALLOY_INGOT.get())).save(output);
        shaped(RecipeCategory.MISC, ModItems.LARGE_PRECIOUS_ALLOY_INGOT.get())
                .pattern("XYX").pattern("ZYZ").pattern("XYX")
                .define('Y', ModItems.PRECIOUS_ALLOY_INGOT.get()).define('X', ModItems.SMALL_PRECIOUS_ALLOY_INGOT.get())
                .define('Z', Blocks.EMERALD_BLOCK)
                .unlockedBy("has_precious_alloy", has(ModItems.PRECIOUS_ALLOY_INGOT.get())).save(output);
        shaped(RecipeCategory.MISC, ModItems.EMPTY_ELEMENT_CONTAINER.get(), 8)
                .pattern(" Y ").pattern("XZX").pattern(" Y ")
                .define('Y', ModItems.CARBON_FIBER.get()).define('X', ModItems.CARBON_STICK.get()).define('Z', ModItems.GRAPHITE.get())
                .unlockedBy("has_graphite", has(ModItems.GRAPHITE.get())).save(output);
    }

    private void smelting() {
        smelt(ModItems.LIGNITE_ORE.get(), ModItems.LIGNITE_COAL.get(), 2.0F);
        smelt(ModItems.BITUMINOUS_ORE.get(), ModItems.BITUMINOUS_COAL.get(), 3.0F);
        smelt(ModItems.ANTHRACITE_ORE.get(), ModItems.ANTHRACITE_COAL.get(), 4.0F);
        // Any filled element container smelts back into an empty one (1.10.2 used a wildcard-meta smelting recipe).
        ModItems.ELEMENT_CONTAINERS.forEach((element, item) -> SimpleCookingRecipeBuilder
                .smelting(Ingredient.of(item.get()), RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.EMPTY_ELEMENT_CONTAINER.get(), 0.5F, 200)
                .unlockedBy("has_" + element.itemId(), has(item.get()))
                .save(output, GeoActivity.MOD_ID + ":empty_element_container_from_" + element.key()));
    }

    private void smelt(ItemLike input, ItemLike result, float xp) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, CookingBookCategory.MISC, result, xp, 200)
                .unlockedBy("has_" + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(input.asItem()).getPath(), has(input))
                .save(output, GeoActivity.MOD_ID + ":" + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(result.asItem()).getPath() + "_from_smelting");
    }
}
