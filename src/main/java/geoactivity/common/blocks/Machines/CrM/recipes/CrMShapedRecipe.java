package geoactivity.common.blocks.Machines.CrM.recipes;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

public class CrMShapedRecipe extends ShapedRecipes
{
	private List<ItemStack> compatiblePerks;
	
	public CrMShapedRecipe(int width, int height, ItemStack[] stack, ItemStack output, List<ItemStack> perks)
	{
		super(width, height, stack, output);
		
		this.compatiblePerks = perks;
	}
	
	public List<ItemStack> getCompatiblePerks()
	{
		return this.compatiblePerks;
	}
}
