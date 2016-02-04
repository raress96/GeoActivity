package com.entirecraft.general.blocks.Machines.AOR;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class BlockSlot extends Slot
{
	private static Random rand = new Random();
	
	public BlockSlot(IInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		Block block = Block.getBlockFromItem(stack.getItem());
		if(block != null && block.getLocalizedName().contains("Ore") &&
				block.getItemDropped(block.getStateFromMeta(stack.getMetadata()), rand, 0) == stack.getItem())
			return true;
		return false;
	}
}