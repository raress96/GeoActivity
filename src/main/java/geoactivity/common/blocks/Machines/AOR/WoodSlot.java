package geoactivity.common.blocks.Machines.AOR;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class WoodSlot extends Slot
{
	public WoodSlot(IInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		int wood = OreDictionary.getOreID("logWood");

		for (int id : OreDictionary.getOreIDs(stack))
		{
			if (id == wood)
				return true;
		}

		return false;
	}
}
