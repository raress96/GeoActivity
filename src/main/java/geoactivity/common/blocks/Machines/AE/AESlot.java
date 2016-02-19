package geoactivity.common.blocks.Machines.AE;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class AESlot extends Slot
{
	public AESlot(IInventory inv, int par3, int par4, int par5)
	{
		super(inv, par3, par4, par5);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false;
	}
}
