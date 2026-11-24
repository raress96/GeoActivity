package geoactivity.common.items.tools.Adv.Logic;

import geoactivity.common.GAMod;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FuelSlot extends Slot
{
	public FuelSlot(IInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		Item item = stack.getItem();
		if (item == GAMod.gemLigniteCoal || item == GAMod.gemBituminousCoal || item == GAMod.gemAnthraciteCoal)
			return true;
		return false;
	}
}
