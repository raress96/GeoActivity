package geoactivity.compat.jei;

import geoactivity.machine.extractor.AtomExtractorRecipes;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

/** Atom Extractor: input + empty element containers → up to 3 filled containers. Layout from 1.10.2. */
final class AtomExtractorCategory extends TexturedCategory<AtomExtractorRecipes.Recipe> {
    static final int MAX_OUTPUTS = 3;

    private final IDrawableAnimated arrow;

    AtomExtractorCategory(IGuiHelper gui) {
        super(gui, JeiTypes.ATOM_EXTRACTOR, ModBlocks.ATOM_EXTRACTOR.get(), "atom_extractor", 10, 10, 160, 65);
        arrow = animated(176, 14, 36, 16, 400, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AtomExtractorRecipes.Recipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 12, 20).addItemStacks(recipe.input().displayStacks());
        builder.addSlot(RecipeIngredientRole.INPUT, 40, 45)
                .add(new ItemStack(ModItems.EMPTY_ELEMENT_CONTAINER.get(), recipe.containersNeeded()));
        List<AtomExtractorRecipes.Output> outputs = recipe.outputs();
        for (int i = 0; i < Math.min(outputs.size(), MAX_OUTPUTS); i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 76 + 26 * i, 20).add(outputs.get(i).stack());
        }
    }

    @Override
    protected void drawExtras(GuiGraphicsExtractor graphics) {
        arrow.draw(graphics, 31, 19);
    }
}
