package geoactivity.common.blocks.Machines.TC;

import geoactivity.common.util.BaseElectricTool;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ToolSlot extends Slot
{
	public ToolSlot(IInventory inv, int index, int xPosition, int yPosition)
	{
		super(inv, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
    {
        return isRFTool(stack);
    }
	
	public static boolean isRFTool(ItemStack stack)
	{
		if(stack.getItem() instanceof BaseElectricTool)
			return true;
		
		return false;
	}
}
