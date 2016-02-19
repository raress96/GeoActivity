package com.entirecraft.general.integration.jei;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.entirecraft.general.blocks.Machines.CrM.CrMShapelessRecipe;
import com.entirecraft.util.Translator;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CrMShapelessRecipeWrapper implements IRecipeWrapper
{
	private final CrMShapelessRecipe recipe;
	private final List<String> perkSlotTooltip;
	private final List<String> tempSlotTooltip;

	public CrMShapelessRecipeWrapper(CrMShapelessRecipe recipe)
	{
		this.recipe = recipe;
		
		for (Object input : this.recipe.recipeItems) {
			if (input instanceof ItemStack) {
				ItemStack itemStack = (ItemStack) input;
				if (itemStack.stackSize != 1) {
					itemStack.stackSize = 1;
				}
			}
		}
		
		perkSlotTooltip = Translator.getWrappedTranslation("ga.jei.crafting_machine.perk.slot");
		tempSlotTooltip = Translator.getWrappedTranslation("ga.jei.crafting_machine.temp.slot");
	}

	@Nonnull
	@Override
	public List getInputs()
	{
		return recipe.recipeItems;
	}

	@Nonnull
	@Override
	public List<ItemStack> getOutputs()
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
		if (mouseX >= 85 && mouseX <= 101 && mouseY >= 20 && mouseY <= 36)
		{
			return this.perkSlotTooltip;
		}
		
		if (mouseX >= 38 && mouseX <= 54 && mouseY >= 58 && mouseY <= 74)
		{
			return this.tempSlotTooltip;
		}
		
		return null;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton)
	{
		return false;
	}
}
