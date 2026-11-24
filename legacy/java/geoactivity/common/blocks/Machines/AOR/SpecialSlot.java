package geoactivity.common.blocks.Machines.AOR;

import geoactivity.common.GAMod;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SpecialSlot extends Slot
{
	public SpecialSlot(IInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return stack.getItem() == GAMod.carbonOreCutter;
	}
}
