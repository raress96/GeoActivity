package geoactivity.compat.jei;

import geoactivity.machine.refiner.CoalRefinerRecipes;
import geoactivity.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Coal Refiner recipes; the Advanced Coal Refiner runs the same ones in two lanes. Layout from 1.10.2. */
final class CoalRefinerCategory extends TexturedCategory<CoalRefinerRecipes.Recipe> {
    private final IDrawableAnimated flame;
    private final IDrawableAnimated arrow;

    CoalRefinerCategory(IGuiHelper gui) {
        super(gui, JeiTypes.COAL_REFINER, ModBlocks.COAL_REFINER.get(), "coal_refiner", 10, 10, 160, 50);
        flame = animated(176, 0, 14, 14, 400, IDrawableAnimated.StartDirection.TOP, true);
        arrow = animated(176, 14, 24, 16, 400, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CoalRefinerRecipes.Recipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 25).addItemStacks(recipe.input().displayStacks());
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 25).addItemStacks(JeiTypes.coalRefinerFuels());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 25).add(recipe.output());
    }

    @Override
    protected void drawExtras(GuiGraphicsExtractor graphics) {
        flame.draw(graphics, 38, 26);
        arrow.draw(graphics, 79, 24);
    }
}
