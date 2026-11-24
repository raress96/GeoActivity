package geoactivity.compat.jei;

import geoactivity.machine.chemistry.ChemistryRecipes;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Chemistry Machine: two inputs in the middle, the element groups in the four corners (each group = that many item
 * frames on one Advanced Hardened Brick). Layout from 1.10.2.
 */
final class ChemistryMachineCategory extends TexturedCategory<ChemistryRecipes.Recipe> {
    static final int MAX_GROUPS = 4;
    private static final int[][] GROUP_POS = {{22, 1}, {117, 1}, {22, 41}, {117, 41}};

    ChemistryMachineCategory(IGuiHelper gui) {
        super(gui, JeiTypes.CHEMISTRY_MACHINE, ModBlocks.CHEMISTRY_MACHINE.get(), "chemistry_machine", 10, 10, 160, 65);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ChemistryRecipes.Recipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 43, 21).add(recipe.first().get());
        var second = builder.addSlot(RecipeIngredientRole.INPUT, 97, 21);
        if (recipe.second() != null) {
            second.add(recipe.second().get());
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 21).add(recipe.result().get());
        List<ChemistryRecipes.ElementGroup> groups = recipe.groups();
        for (int i = 0; i < Math.min(groups.size(), MAX_GROUPS); i++) {
            ChemistryRecipes.ElementGroup group = groups.get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, GROUP_POS[i][0], GROUP_POS[i][1])
                    .add(new ItemStack(ModItems.element(group.element()), group.count()))
                    .addRichTooltipCallback((view, tooltip) ->
                            tooltip.add(Component.translatable("jei.geoactivity.chemistry_machine.group", group.count())));
        }
    }
}
