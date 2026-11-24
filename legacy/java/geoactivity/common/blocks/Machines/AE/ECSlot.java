package geoactivity.common.blocks.Machines.AE;

import geoactivity.common.GAMod;
import geoactivity.common.items.ElementContainer.EnumElements;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

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
