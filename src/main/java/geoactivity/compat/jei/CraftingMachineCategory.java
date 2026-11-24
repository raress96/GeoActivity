package geoactivity.compat.jei;

import geoactivity.machine.RecipeInput;
import geoactivity.machine.crafting.CraftingMachineRecipes;
import geoactivity.registry.ModBlocks;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** Crafting Machine: 3×3 grid, fuel, the perks that fit the result, output. Layout from 1.10.2. */
final class CraftingMachineCategory extends TexturedCategory<CraftingMachineRecipes.Recipe> {
    private final IDrawableAnimated flame;
    private final IDrawableAnimated arrow;

    CraftingMachineCategory(IGuiHelper gui) {
        super(gui, JeiTypes.CRAFTING_MACHINE, ModBlocks.CRAFTING_MACHINE.get(), "crafting_machine", 4, 4, 169, 78);
        flame = animated(176, 0, 14, 14, 164, IDrawableAnimated.StartDirection.TOP, true);
        arrow = animated(176, 14, 78, 16, 80, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CraftingMachineRecipes.Recipe recipe, IFocusGroup focuses) {
        for (int i = 0; i < 9; i++) {
            var slot = builder.addSlot(RecipeIngredientRole.INPUT, (i % 3) * 18 + 2, (i / 3) * 18 + 2);
            cell(recipe, i).ifPresent(input -> slot.addItemStacks(input.displayStacks()));
        }
        if (recipe instanceof CraftingMachineRecipes.Shapeless) {
            builder.setShapeless();
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 85, 58).addItemStacks(JeiTypes.craftingMachineFuels());
        var perks = recipe.compatiblePerks();
        if (!perks.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 85, 20).addItemStacks(perks)
                    .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("jei.geoactivity.crafting_machine.perk_slot")));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 146, 38).add(recipe.result());
    }

    /** The input shown in grid cell {@code index} (row-major); shaped patterns sit in the top-left corner. */
    static Optional<RecipeInput> cell(CraftingMachineRecipes.Recipe recipe, int index) {
        return switch (recipe) {
            case CraftingMachineRecipes.Shaped shaped -> {
                int col = index % 3;
                int row = index / 3;
                yield col < shaped.width() && row < shaped.height() ? shaped.pattern().get(col + row * shaped.width()) : Optional.empty();
            }
            case CraftingMachineRecipes.Shapeless shapeless -> {
                List<RecipeInput> inputs = shapeless.inputs();
                yield index < inputs.size() ? Optional.of(inputs.get(index)) : Optional.empty();
            }
        };
    }

    @Override
    protected void drawExtras(GuiGraphicsExtractor graphics) {
        flame.draw(graphics, 62, 59);
        arrow.draw(graphics, 57, 37);
    }
}
