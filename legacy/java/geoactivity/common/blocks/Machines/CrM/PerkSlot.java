package geoactivity.common.blocks.Machines.CrM;

import geoactivity.common.GAMod;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PerkSlot extends Slot
{

	public PerkSlot(IInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		Item item = stack.getItem();
		if (item == GAMod.tool_perks || item == GAMod.armor_perks)
			return true;
		return false;
	}
}
