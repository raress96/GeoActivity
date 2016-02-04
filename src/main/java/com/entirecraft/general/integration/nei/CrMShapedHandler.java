package com.entirecraft.general.integration.nei;

import java.awt.Rectangle;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.ShapedRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;

import com.entirecraft.general.blocks.Machines.CrM.CrMCrafting;

public class CrMShapedHandler extends ShapedRecipeHandler
{
	public static Class<? extends GuiContainer> guiclass;

	@Override
	public String getRecipeName()
	{
		return "Crafting Machine";
	}

	public String getRecipeNameSub()
	{
		return "Shaped";
	}

	@Override
	public boolean hasOverlay(GuiContainer gui, Container container, int recipe)
	{
		return (RecipeInfo.hasDefaultOverlay(gui, getOverlayIdentifier())) || (RecipeInfo.hasOverlayHandler(gui, getOverlayIdentifier()));
	}

	@Override
	public String getGuiTexture()
	{
		return "GeoActivity:textures/gui/CrM.png";
	}

	@Override
	public void loadTransferRects()
	{
		this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(84, 40, 24, 10), "CrM.gui", new Object[0]));
	}

	public Class<? extends GuiContainer> getGuiClass()
	{
		return guiclass;
	}

	public void drawBackground(int recipe)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GuiDraw.changeTexture(getGuiTexture());
		GuiDraw.drawTexturedModalRect(0, 0, 5, 5, 166, 56);
	}

	public void drawExtras(int recipe)
	{
		drawProgressBar(57, 36, 176, 14, 78, 16, 144, 0);
		if((recipe % recipiesPerPage() == 0) && (!getRecipeNameSub().equals("")))
			GuiDraw.drawStringC(getRecipeNameSub(), 83, -2, 4210752, false);
	}

	protected ShapedRecipeHandler.CachedShapedRecipe getCachedRecipe(IRecipe irecipe)
	{
		ShapedRecipeHandler.CachedShapedRecipe recipe = null;
		if((irecipe instanceof ShapedRecipes))
		{
			recipe = new ShapedRecipeHandler.CachedShapedRecipe((ShapedRecipes) irecipe);
		}
		else if((irecipe instanceof ShapedOreRecipe))
			recipe = forgeShapedRecipe((ShapedOreRecipe) irecipe);

		if(recipe == null)
			return null;
		for(PositionedStack stack : recipe.ingredients)
		{
			stack.relx -= 24;
			stack.rely -= 5;
		}
		recipe.result.rely += 13;
		recipe.result.relx += 26;
		return recipe;
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results)
	{
		if(outputId.equals("CrM.gui"))
		{
			for(IRecipe irecipe : CrMCrafting.getInstance().getRecipeList())
			{
				ShapedRecipeHandler.CachedShapedRecipe recipe = getCachedRecipe(irecipe);
				if(recipe != null)
				{
					this.arecipes.add(recipe);
				}
			}
		}
		else
			super.loadCraftingRecipes(outputId, results);
	}

	public void loadUsageRecipes(ItemStack ingredient)
	{
		for(IRecipe irecipe : CrMCrafting.getInstance().getRecipeList())
		{
			ShapedRecipeHandler.CachedShapedRecipe recipe = getCachedRecipe(irecipe);
			if(recipe != null)
			{
				if(recipe.contains(recipe.ingredients, ingredient))
				{
					recipe.setIngredientPermutation(recipe.ingredients, ingredient);
					this.arecipes.add(recipe);
				}
			}
		}
	}

	public void loadCraftingRecipes(ItemStack result)
	{
		for(IRecipe irecipe : CrMCrafting.getInstance().getRecipeList())
		{
			if(NEIClientUtils.areStacksSameTypeCrafting(irecipe.getRecipeOutput(), result))
			{
				ShapedRecipeHandler.CachedShapedRecipe recipe = getCachedRecipe(irecipe);
				if(recipe != null)
				{
					this.arecipes.add(recipe);
				}
			}
		}
	}
}
