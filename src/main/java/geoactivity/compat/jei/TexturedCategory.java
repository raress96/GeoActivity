package geoactivity.compat.jei;

import geoactivity.GeoActivity;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;

/** A category drawn from a region of the machine's 1.10.2 GUI texture, like the 1.10.2 JEI plugin did. */
abstract class TexturedCategory<T> extends AbstractRecipeCategory<T> {
    protected final IGuiHelper gui;
    protected final Identifier texture;
    private final IDrawable background;

    TexturedCategory(IGuiHelper gui, IRecipeType<T> type, ItemLike machine, String texture, int u, int v, int width, int height) {
        super(type, new net.minecraft.world.item.ItemStack(machine).getHoverName(), gui.createDrawableItemLike(machine), width, height);
        this.gui = gui;
        this.texture = GeoActivity.id("textures/gui/container/" + texture + ".png");
        this.background = gui.createDrawable(this.texture, u, v, width, height);
    }

    /** An animated part of the texture (flame, arrow) taken from the sheet's right-hand column. */
    protected IDrawableAnimated animated(int u, int v, int width, int height, int ticks, IDrawableAnimated.StartDirection from, boolean inverted) {
        IDrawableStatic part = gui.createDrawable(texture, u, v, width, height);
        return gui.createAnimatedDrawable(part, ticks, from, inverted);
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);
        drawExtras(graphics);
    }

    protected void drawExtras(GuiGraphicsExtractor graphics) {}
}
