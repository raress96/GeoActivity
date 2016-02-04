package com.entirecraft.general.items.tools.Red.Logic;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.items.ToolPerks.EnumToolPerks;

public class PerkSlot extends Slot
{
	public PerkSlot(IInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack stack) 
	{
		return stack.getItem() == GAMod.tool_perks && stack.getMetadata() != EnumToolPerks.BLANK.getMetadata();
	}
}
