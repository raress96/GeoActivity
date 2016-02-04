package com.entirecraft.general.blocks.Machines.ACR;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.entirecraft.general.GAMod;

public class FuelSlot extends Slot
{

	public FuelSlot(IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return isFuel(stack);
	}
	
	public static boolean isFuel(ItemStack stack)
	{
		Item item = stack.getItem();
		if (item == GAMod.gemLigniteCoal || item == GAMod.gemBituminousCoal || item == GAMod.gemAnthraciteCoal
				|| item == Items.coal)
			return true;
		return false;
	}
}
