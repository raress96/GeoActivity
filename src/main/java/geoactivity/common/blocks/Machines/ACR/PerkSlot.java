package geoactivity.common.blocks.Machines.ACR;

import geoactivity.common.GAMod;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PerkSlot extends Slot
{
	public PerkSlot(IInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) 
	{
		return itemstack.getItem() == GAMod.machine_perks;
	}
}