package geoactivity.common.blocks.Machines.CrM.recipes;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;

public class CrMShapelessRecipe extends ShapelessRecipes
{
	private List<ItemStack> compatiblePerks;
	
	public CrMShapelessRecipe(ItemStack output, List<ItemStack> inputs, List<ItemStack> perks)
	{
		super(output, inputs);
		
		this.compatiblePerks = perks;
	}
	
	public List<ItemStack> getCompatiblePerks()
	{
		return this.compatiblePerks;
	}
}
