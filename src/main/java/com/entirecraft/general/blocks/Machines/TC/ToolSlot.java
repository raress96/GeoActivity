package com.entirecraft.general.blocks.Machines.TC;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.entirecraft.util.BaseElectricTool;

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
