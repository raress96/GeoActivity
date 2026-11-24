package geoactivity.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

/** Furnace-recipe lookups (server side: the client doesn't receive the recipe list in 26.x). */
public final class Smelting {
    private Smelting() {}

    public static ItemStack result(ServerLevel level, ItemStack input) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        var recipeInput = new SingleRecipeInput(input.copyWithCount(1));
        return level.recipeAccess().getRecipeFor(RecipeType.SMELTING, recipeInput, level)
                .map(holder -> holder.value().assemble(recipeInput))
                .orElse(ItemStack.EMPTY);
    }
}
