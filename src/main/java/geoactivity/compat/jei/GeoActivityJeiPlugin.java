package geoactivity.compat.jei;

import geoactivity.GeoActivity;
import geoactivity.client.screen.AdvancedCoalRefinerScreen;
import geoactivity.client.screen.AtomExtractorScreen;
import geoactivity.client.screen.ChemistryMachineScreen;
import geoactivity.client.screen.CoalRefinerScreen;
import geoactivity.client.screen.CraftingMachineScreen;
import geoactivity.machine.chemistry.ChemistryRecipes;
import geoactivity.machine.crafting.CraftingMachineRecipes;
import geoactivity.machine.extractor.AtomExtractorRecipes;
import geoactivity.machine.refiner.CoalRefinerRecipes;
import geoactivity.registry.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;

/**
 * JEI support (1.10.2 {@code GeoActivityJEIPlugin}): categories for the Coal Refiner (+ Advanced Coal Refiner),
 * Atom Extractor, Chemistry Machine and Crafting Machine, read straight from the Java recipe tables; click areas on the
 * machine GUIs; description pages for the machines without recipes of their own.
 */
@JeiPlugin
public final class GeoActivityJeiPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return GeoActivity.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CoalRefinerCategory(gui), new AtomExtractorCategory(gui),
                new ChemistryMachineCategory(gui), new CraftingMachineCategory(gui));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(JeiTypes.COAL_REFINER, CoalRefinerRecipes.all());
        registration.addRecipes(JeiTypes.ATOM_EXTRACTOR, AtomExtractorRecipes.all());
        registration.addRecipes(JeiTypes.CHEMISTRY_MACHINE, ChemistryRecipes.all());
        registration.addRecipes(JeiTypes.CRAFTING_MACHINE, CraftingMachineRecipes.all());
        describe(registration, ModBlocks.ADVANCED_COAL_REFINER.get(), "advanced_coal_refiner");
        describe(registration, ModBlocks.ADVANCED_ORE_REFINER.get(), "advanced_ore_refiner");
        describe(registration, ModBlocks.CHEMISTRY_MACHINE.get(), "chemistry_machine");
        describe(registration, ModBlocks.THERMIC_GENERATOR.get(), "thermic_generator");
        describe(registration, ModBlocks.GEOTHERMAL_GENERATOR.get(), "geothermal_generator");
        describe(registration, ModBlocks.THERMIC_MELTER.get(), "thermic_melter");
    }

    private static void describe(IRecipeRegistration registration, ItemLike block, String id) {
        registration.addIngredientInfo(block, Component.translatable("jei.geoactivity.description." + id));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(JeiTypes.COAL_REFINER, ModBlocks.COAL_REFINER.get(), ModBlocks.ADVANCED_COAL_REFINER.get());
        registration.addCraftingStation(JeiTypes.ATOM_EXTRACTOR, ModBlocks.ATOM_EXTRACTOR.get());
        registration.addCraftingStation(JeiTypes.CHEMISTRY_MACHINE, ModBlocks.CHEMISTRY_MACHINE.get());
        registration.addCraftingStation(JeiTypes.CRAFTING_MACHINE, ModBlocks.CRAFTING_MACHINE.get());
    }

    /** Click areas from 1.10.2 (GUI-relative). */
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CraftingMachineScreen.class, 84, 44, 24, 12, JeiTypes.CRAFTING_MACHINE);
        registration.addRecipeClickArea(CoalRefinerScreen.class, 88, 36, 24, 12, JeiTypes.COAL_REFINER);
        registration.addRecipeClickArea(AdvancedCoalRefinerScreen.class, 84, 34, 24, 12, JeiTypes.COAL_REFINER);
        registration.addRecipeClickArea(AtomExtractorScreen.class, 45, 30, 24, 12, JeiTypes.ATOM_EXTRACTOR);
        registration.addRecipeClickArea(ChemistryMachineScreen.class, 76, 32, 24, 12, JeiTypes.CHEMISTRY_MACHINE);
    }
}
