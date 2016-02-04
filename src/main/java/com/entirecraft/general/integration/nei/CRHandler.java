package com.entirecraft.general.integration.nei;

import java.awt.Rectangle;
import java.util.Map.Entry;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.blocks.Machines.CR.CRRecipes;

public class CRHandler extends FurnaceRecipeHandler
{
	static final ItemStack[] fuels = new ItemStack[] {new ItemStack(Items.coal), new ItemStack(GAMod.gemAnthraciteCoal),
			new ItemStack(GAMod.gemBituminousCoal), new ItemStack(GAMod.gemLigniteCoal)};
	public static Class<? extends GuiContainer> guiclass;

	public String getRecipeName()
	{
		return "Coal Refiner";
	}

	@Override
	public boolean hasOverlay(GuiContainer gui, Container container, int recipe)
	{
		return (RecipeInfo.hasDefaultOverlay(gui, getOverlayIdentifier())) || (RecipeInfo.hasOverlayHandler(gui, getOverlayIdentifier()));
	}

	public void loadTransferRects()
	{
		this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(80, 23, 24, 18), "CR.gui", new Object[0]));
	}

	public Class<? extends GuiContainer> getGuiClass()
	{
		return guiclass;
	}

	public String getGuiTexture()
	{
		return "GeoActivity:textures/gui/CR.png";
	}

	public void drawExtras(int recipe)
	{
		drawProgressBar(44, 25, 176, 0, 14, 14, 48, 7);
		drawProgressBar(84, 23, 176, 14, 24, 16, 48, 0);
	}

	public void loadCraftingRecipes(String outputId, Object... results)
	{
		if((outputId.equals("CR.gui")) && (getClass() == CRHandler.class))
		{
			for(Entry<ItemStack, ItemStack> recipe : CRRecipes.getInstance().getSmeltingList().entrySet())
			{
				ItemStack item = recipe.getValue();
				this.arecipes.add(new CachedCRRecipe(new ItemStack(recipe.getKey().getItem(), 1, recipe.getKey().getItemDamage()), item));
			}
		}
		else
		{
			super.loadCraftingRecipes(outputId, results);
		}
	}

	public void loadUsageRecipes(ItemStack ingred)
	{
		for(Entry<ItemStack, ItemStack> recipe : CRRecipes.getInstance().getSmeltingList().entrySet())
		{
			ItemStack item = recipe.getValue();

			if(ingred.getItem() == recipe.getKey().getItem() && ingred.getItemDamage() == recipe.getKey().getItemDamage())
				arecipes.add(new CachedCRRecipe(ingred, item));
		}
	}

	public void loadCraftingRecipes(ItemStack result)
	{
		for(Entry<ItemStack, ItemStack> recipe : CRRecipes.getInstance().getSmeltingList().entrySet())
		{
			ItemStack item = recipe.getValue();
			if(NEIServerUtils.areStacksSameType(item, result))
			{
				arecipes.add(new CachedCRRecipe(new ItemStack(recipe.getKey().getItem(), 1, recipe.getKey().getItemDamage()), item));
			}
		}
	}

	public class CachedCRRecipe extends TemplateRecipeHandler.CachedRecipe
	{
		public CachedCRRecipe(ItemStack ingred, ItemStack result)
		{
			ingred.stackSize = 1;
			this.ingred = new PositionedStack(ingred, 61, 24);
			this.result = new PositionedStack(result, 121, 24);
		}

		public PositionedStack getResult()
		{
			return result;
		}

		public PositionedStack getIngredient()
		{
			return ingred;
		}

		@Override
		public PositionedStack getOtherStack()
		{
			if(fuels.length > 0)
				return new PositionedStack(fuels[(CRHandler.this.cycleticks / 48 % fuels.length)], 25, 24);
			return null;
		}

		PositionedStack ingred;
		PositionedStack result;
	}
}
