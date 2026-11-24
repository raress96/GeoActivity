package geoactivity.machine.refiner;

import geoactivity.machine.RecipeInput;
import geoactivity.registry.ModItems;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

/** Coal Refiner (and Advanced Coal Refiner) recipes, from 1.10.2 {@code CRRecipes}. */
public final class CoalRefinerRecipes {
    public record Recipe(RecipeInput input, Supplier<ItemStack> result, float experience) {
        public ItemStack output() {
            return result.get();
        }
    }

    private static List<Recipe> recipes;

    private CoalRefinerRecipes() {}

    public static List<Recipe> all() {
        if (recipes == null) {
            recipes = List.of(
                    new Recipe(RecipeInput.of(Items.COAL), () -> new ItemStack(ModItems.GRAPHITE.get()), 0.2F),
                    new Recipe(RecipeInput.of(ModItems.LIGNITE_COAL.get()), () -> new ItemStack(ModItems.GRAPHITE.get(), 8), 0.2F),
                    new Recipe(RecipeInput.of(ModItems.BITUMINOUS_COAL.get()), () -> new ItemStack(ModItems.GRAPHITE.get(), 16), 0.2F),
                    new Recipe(RecipeInput.of(ModItems.ANTHRACITE_COAL.get()), () -> new ItemStack(ModItems.GRAPHITE.get(), 24), 0.2F),
                    // 1.10.2: vanilla coal ore only; now any coal ore (incl. deepslate)
                    new Recipe(RecipeInput.of(Tags.BlockItems.ORES_COAL.item()), () -> new ItemStack(ModItems.GRAPHITE.get(), 4), 0.2F),
                    new Recipe(RecipeInput.of(ModItems.LIGNITE_ORE.get()), () -> new ItemStack(ModItems.LIGNITE_COAL.get(), 2), 0.3F),
                    new Recipe(RecipeInput.of(ModItems.BITUMINOUS_ORE.get()), () -> new ItemStack(ModItems.BITUMINOUS_COAL.get(), 2), 0.4F),
                    new Recipe(RecipeInput.of(ModItems.ANTHRACITE_ORE.get()), () -> new ItemStack(ModItems.ANTHRACITE_COAL.get(), 2), 0.5F));
        }
        return recipes;
    }

    public static Optional<Recipe> find(ItemStack input) {
        return all().stream().filter(r -> r.input().test(input)).findFirst();
    }

    public static boolean isInput(ItemStack stack) {
        return find(stack).isPresent();
    }

    /** XP per output item; 1.10.2 looked it up by the result item. */
    public static float experienceFor(ItemStack output) {
        return all().stream().filter(r -> r.output().is(output.getItem())).map(Recipe::experience).findFirst().orElse(0.0F);
    }

    /** Coal Refiner fuel: burn ticks per item. */
    public static int fuelTime(ItemStack stack) {
        if (stack.is(Items.COAL)) return 400;
        if (stack.is(ModItems.LIGNITE_COAL.get())) return 3200;
        if (stack.is(ModItems.BITUMINOUS_COAL.get())) return 6400;
        if (stack.is(ModItems.ANTHRACITE_COAL.get())) return 9600;
        return 0;
    }

    public static boolean isFuel(ItemStack stack) {
        return fuelTime(stack) > 0;
    }
}
