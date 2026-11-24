package geoactivity.compat.jei;

import geoactivity.GeoActivity;
import geoactivity.machine.chemistry.ChemistryRecipes;
import geoactivity.machine.crafting.CraftingMachineBlockEntity;
import geoactivity.machine.crafting.CraftingMachineRecipes;
import geoactivity.machine.extractor.AtomExtractorRecipes;
import geoactivity.machine.refiner.CoalRefinerRecipes;
import geoactivity.registry.ModItems;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** JEI recipe types and shared ingredient lists. */
final class JeiTypes {
    static final IRecipeType<CoalRefinerRecipes.Recipe> COAL_REFINER =
            IRecipeType.create(GeoActivity.id("coal_refiner"), CoalRefinerRecipes.Recipe.class);
    static final IRecipeType<AtomExtractorRecipes.Recipe> ATOM_EXTRACTOR =
            IRecipeType.create(GeoActivity.id("atom_extractor"), AtomExtractorRecipes.Recipe.class);
    static final IRecipeType<ChemistryRecipes.Recipe> CHEMISTRY_MACHINE =
            IRecipeType.create(GeoActivity.id("chemistry_machine"), ChemistryRecipes.Recipe.class);
    static final IRecipeType<CraftingMachineRecipes.Recipe> CRAFTING_MACHINE =
            IRecipeType.create(GeoActivity.id("crafting_machine"), CraftingMachineRecipes.Recipe.class);

    private JeiTypes() {}

    static List<ItemStack> coalRefinerFuels() {
        return fuels(CoalRefinerRecipes::isFuel);
    }

    static List<ItemStack> craftingMachineFuels() {
        return fuels(CraftingMachineBlockEntity::isFuel);
    }

    private static List<ItemStack> fuels(Predicate<ItemStack> isFuel) {
        return Stream.of(Items.COAL, Items.COAL_BLOCK, ModItems.LIGNITE_COAL.get(), ModItems.BITUMINOUS_COAL.get(), ModItems.ANTHRACITE_COAL.get())
                .map(ItemStack::new).filter(isFuel).toList();
    }
}
