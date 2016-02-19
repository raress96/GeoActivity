package com.entirecraft.general.integration.jei;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.entirecraft.general.blocks.Machines.CrM.CrMShapedRecipe;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CrMShapedRecipeWrapper implements IRecipeWrapper
{
	private final CrMShapedRecipe recipe;

	public CrMShapedRecipeWrapper(CrMShapedRecipe recipe)
	{
		this.recipe = recipe;

		for (ItemStack itemStack : this.recipe.recipeItems) {
			if (itemStack != null && itemStack.stackSize != 1) {
				itemStack.stackSize = 1;
			}
		}
	}

	@Override
	public List getInputs()
	{
		return Arrays.asList(recipe.recipeItems);
	}

	@Override
	public List getOutputs()
	{
		return Collections.singletonList(recipe.getRecipeOutput());
	}

	@Override
	public List<FluidStack> getFluidInputs()
	{
		return null;
	}

	@Override
	public List<FluidStack> getFluidOutputs()
	{
		return null;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight)
	{}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight)
	{}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY)
	{
		return null;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton)
	{
		return false;
	}

}
