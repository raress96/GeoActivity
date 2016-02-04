package com.entirecraft.general.integration.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;

import com.entirecraft.general.blocks.Machines.ChM.ChMCrafting;

public class ChMHandler extends FurnaceRecipeHandler
{
	public static Class<? extends GuiContainer> guiclass;

	public String getRecipeName()
	{
		return "Chemistry Machine";
	}

	@Override
	public boolean hasOverlay(GuiContainer gui, Container container, int recipe)
	{
		return (RecipeInfo.hasDefaultOverlay(gui, getOverlayIdentifier())) || (RecipeInfo.hasOverlayHandler(gui, getOverlayIdentifier()));
	}

	public void loadTransferRects()
	{
		this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(70, 38, 24, 18), "ChM.gui", new Object[0]));
	}

	public Class<? extends GuiContainer> getGuiClass()
	{
		return guiclass;
	}

	public String getGuiTexture()
	{
		return "GeoActivity:textures/gui/ChM.png";
	}

	public HashMap<ItemStack[], ItemStack[]> getRecipeList()
	{
		return ChMCrafting.getInstance().getMetaSmeltingList();
	}

	public ChMHandler.CachedCMRecipe getRecipe(ItemStack input1, ItemStack input2, ItemStack[] results)
	{
		return new CachedCMRecipe(input1, input2, results);
	}

	public void loadCraftingRecipes(String outputId, Object... results)
	{
		if((outputId.equals("ChM.gui")) && (getClass() == ChMHandler.class))
		{
			for(Entry<ItemStack[], ItemStack[]> recipe : getRecipeList().entrySet())
			{
				this.arecipes.add(getRecipe(new ItemStack(recipe.getKey()[0].getItem(), recipe.getKey()[0].stackSize, recipe.getKey()[0]
					.getItemDamage()), new ItemStack(recipe.getKey()[1].getItem(), recipe.getKey()[1].stackSize, recipe.getKey()[1].getItemDamage()),
					recipe.getValue()));
			}
		}
		else
		{
			super.loadCraftingRecipes(outputId, results);
		}
	}

	public void loadUsageRecipes(ItemStack ingredient)
	{
		for(Entry<ItemStack[], ItemStack[]> recipe : getRecipeList().entrySet())
		{
			CachedCMRecipe recipes =
				getRecipe(new ItemStack(recipe.getKey()[0].getItem(), recipe.getKey()[0].stackSize, recipe.getKey()[0].getItemDamage()),
					new ItemStack(recipe.getKey()[1].getItem(), recipe.getKey()[1].stackSize, recipe.getKey()[1].getItemDamage()), recipe.getValue());

			for(int i = 0;i < recipes.ingred.size();i++)
				if(recipes.ingred.get(i).item.getItem() == ingredient.getItem()
					&& recipes.ingred.get(i).item.getItemDamage() == ingredient.getItemDamage())
					this.arecipes.add(recipes);
		}
	}

	public void loadCraftingRecipes(ItemStack result)
	{
		for(Entry<ItemStack[], ItemStack[]> recipe : getRecipeList().entrySet())
		{
			CachedCMRecipe recipes =
				getRecipe(new ItemStack(recipe.getKey()[0].getItem(), recipe.getKey()[0].stackSize, recipe.getKey()[0].getItemDamage()),
					new ItemStack(recipe.getKey()[1].getItem(), recipe.getKey()[1].stackSize, recipe.getKey()[1].getItemDamage()), recipe.getValue());
			if(recipes.result.item.getItem() == result.getItem() && recipes.result.item.getItemDamage() == result.getItemDamage())
				this.arecipes.add(recipes);
		}
	}

	public class CachedCMRecipe extends TemplateRecipeHandler.CachedRecipe
	{
		public CachedCMRecipe(ItemStack input1, ItemStack input2, ItemStack[] item)
		{
			this.result = new PositionedStack(item[0], 75, 20);

			this.ingred = new ArrayList<PositionedStack>();

			if(input1 != null && input1.getItem() != Item.getItemFromBlock(Blocks.air))
				this.ingred.add(new PositionedStack(input1, 48, 20));
			if(input2 != null && input2.getItem() != Item.getItemFromBlock(Blocks.air))
				this.ingred.add(new PositionedStack(input2, 102, 20));

			if(item[1] != null)
				this.ingred.add(new PositionedStack(item[1], 23, 5));
			if(item[2] != null)
				this.ingred.add(new PositionedStack(item[2], 23, 35));
			if(item[3] != null)
				this.ingred.add(new PositionedStack(item[3], 125, 5));
			if(item[4] != null)
				this.ingred.add(new PositionedStack(item[4], 125, 35));
		}

		public PositionedStack getResult()
		{
			return result;
		}

		public PositionedStack getIngredient()
		{
			return null;
		}

		@Override
		public List<PositionedStack> getOtherStacks()
		{
			return this.ingred;
		}

		PositionedStack result;
		List<PositionedStack> ingred;
	}
}
