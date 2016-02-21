package geoactivity.common.blocks.Machines.AOR;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class OreSlot extends Slot
{
	private static Random rand = new Random();

	public OreSlot(IInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return isItemStackValidOre(stack);
	}

	public static boolean isItemStackValidOre(ItemStack stack)
	{
		Block block = Block.getBlockFromItem(stack.getItem());
		if(block != null && block.getUnlocalizedName().contains("ore")
			&&	block.getItemDropped(block.getStateFromMeta(stack.getMetadata()), rand, 0) == stack.getItem()
			&& FurnaceRecipes.instance().getSmeltingResult(stack) != null)
			return true;

		return false;
	}
}