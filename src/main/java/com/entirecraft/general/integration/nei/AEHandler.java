package com.entirecraft.general.integration.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.blocks.Machines.AE.AERecipes;

public class AEHandler extends FurnaceRecipeHandler
{
	public static Class<? extends GuiContainer> guiclass;
	
	public String getRecipeName()
	{
		return "Atom Extractor";
	}
	
	@Override
	public boolean hasOverlay(GuiContainer gui, Container container, int recipe)
	{
		return (RecipeInfo.hasDefaultOverlay(gui, getOverlayIdentifier())) || (RecipeInfo.hasOverlayHandler(gui, getOverlayIdentifier()));
	}
	
	public void loadTransferRects()
	{
		this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(42, 23, 24, 18), "AE.gui", new Object[0]));
	}
	
	public Class<? extends GuiContainer> getGuiClass()
	{
		return guiclass;
	}
	
	
	public String getGuiTexture()
	{
		return "GeoActivity:textures/gui/AE.png";
	}
	
	public void drawExtras(int recipe)
	{
		drawProgressBar(38, 19, 176, 14, 36, 16, 72, 0);
		ItemStack item = this.arecipes.get(recipe).getIngredient().item;
		GuiDraw.drawStringC("Time: " + (float)AERecipes.getInstance().getTime(item)/20 + " seconds", 112, 49, -16777216, false);
	}
	
	public HashMap<ItemStack, ItemStack[]> getRecipeList()
	{
		return AERecipes.getInstance().getSmeltingList();
	}
	
	public AEHandler.CachedAERecipe getRecipe(ItemStack input, ItemStack[] results)
	{
		return new CachedAERecipe(input, results);
	}
	
	public void loadCraftingRecipes(String outputId, Object... results)
	{
		if ((outputId.equals("AE.gui")) && (getClass() == AEHandler.class))
		{
			for (Entry<ItemStack, ItemStack[]> recipe : getRecipeList().entrySet())
			{
				ItemStack[] item = recipe.getValue();
				this.arecipes.add(getRecipe(new ItemStack(recipe.getKey().getItem(), 1, recipe.getKey().getItemDamage()), item));
			}
	    }
	    else
	      super.loadCraftingRecipes(outputId, results);
	}
	
	public void loadUsageRecipes(ItemStack ingredient)
	{
		for (Entry<ItemStack, ItemStack[]> recipe : getRecipeList().entrySet())
		{
			CachedAERecipe recipes = getRecipe(new ItemStack(recipe.getKey().getItem(), 1, recipe.getKey().getItemDamage()), recipe.getValue());
			if((recipes.ingred.item.getItem() == ingredient.getItem() && recipes.ingred.item.getItemDamage() == ingredient.getItemDamage()) 
					|| (ingredient.getItem() == GAMod.elementContainer && ingredient.getItemDamage() == 0))
				this.arecipes.add(recipes);
	    }
	}
	
	public void loadCraftingRecipes(ItemStack result)
	{
		for (Entry<ItemStack, ItemStack[]> recipe : getRecipeList().entrySet())
	    {
			CachedAERecipe recipes = getRecipe(new ItemStack(recipe.getKey().getItem(), 1, recipe.getKey().getItemDamage()), recipe.getValue());
			if(recipes.contains(recipes.results, result))
				this.arecipes.add(recipes);
	    }
	}
	
	public class CachedAERecipe extends TemplateRecipeHandler.CachedRecipe
	{
	    public CachedAERecipe(ItemStack ingred, ItemStack[] item)
	    {
	    	 ingred.stackSize = 1;
	    	 this.ingred = new PositionedStack(ingred, 17, 19);
	    	 this.results = new ArrayList<PositionedStack>();
	    	 int number=0;
	    	 
	    	 if(item[0]!=null)
	    	 {
	    		 number+=item[0].stackSize;
	    		 this.results.add(new PositionedStack(item[0], 81, 19));
	    	 }
	    	 if(item[1]!=null)
	    	 {
	    		 number+=item[1].stackSize;
	    		 this.results.add(new PositionedStack(item[1], 107, 19));
	    	 }
	    	 if(item[2]!=null)
	    	 {
	    		 number+=item[2].stackSize;
	    		 this.results.add(new PositionedStack(item[2], 133, 19));
	    	 }
	    	 this.results.add(new PositionedStack(new ItemStack(GAMod.elementContainer, number, 0), 45, 44));
	    }

	    public PositionedStack getResult()
	    {
	    	return null;
	    }

        public PositionedStack getIngredient()
        {
            return ingred;
        }
        
        @Override
	    public List<PositionedStack> getOtherStacks()
	    {
        	return this.results;
	    }
	    
        PositionedStack ingred;
        List<PositionedStack> results;
	  }
}
