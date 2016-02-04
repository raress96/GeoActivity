package com.entirecraft.general.blocks.Machines.AE;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.items.ElementContainer.EnumElements;

public class ECSlot extends Slot
{
	public ECSlot(IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) 
	{
		return itemstack.getItem() == GAMod.elementContainer && itemstack.getMetadata() == EnumElements.EMPTY.getMetadata();
	}
}
