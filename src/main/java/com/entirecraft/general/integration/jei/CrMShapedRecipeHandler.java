package com.entirecraft.general.integration.jei;

import com.entirecraft.general.blocks.Machines.CrM.CrMShapedRecipe;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public class CrMShapedRecipeHandler implements IRecipeHandler<CrMShapedRecipe>
{
	@Override
	public Class<CrMShapedRecipe> getRecipeClass()
	{
		return CrMShapedRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid()
	{
		return GeoActivityRecipeCategoryUid.CRAFTING_MACHINE;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(CrMShapedRecipe recipe)
	{
		return new CrMShapedRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(CrMShapedRecipe recipe)
	{
		if (recipe.getRecipeOutput() == null) {
			return false;
		}

		int inputCount = 0;
		for (ItemStack input : recipe.recipeItems) {
			if (input != null) {
				inputCount++;
			}
		}

		return inputCount > 0;
	}
}
