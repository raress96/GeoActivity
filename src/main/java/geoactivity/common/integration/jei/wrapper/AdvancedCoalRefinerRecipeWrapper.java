package geoactivity.common.integration.jei.wrapper;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

public class AdvancedCoalRefinerRecipeWrapper extends CoalRefinerRecipeWrapper
{
	public AdvancedCoalRefinerRecipeWrapper(List<ItemStack> inputs, ItemStack output, float experience)
	{
		super(inputs, output, experience);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		if (this.experienceString != null) {
			FontRenderer fontRendererObj = minecraft.fontRendererObj;
			fontRendererObj.drawString(this.experienceString, 135 - fontRendererObj.getStringWidth(this.experienceString) / 2, 12, Color.gray.getRGB());
		}
	}
}
