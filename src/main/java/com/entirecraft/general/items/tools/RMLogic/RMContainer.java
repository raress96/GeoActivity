package com.entirecraft.general.items.tools.RMLogic;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.items.tools.Adv.Logic.FuelSlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class RMContainer extends Container
{
	static RMInventory containerInv;
	int invSize;

	public RMContainer(RMInventory inv, EntityPlayer player)
	{
		containerInv = inv;
		invSize = containerInv.getSizeInventory();

		addSlotToContainer(new FuelSlot(inv, 0, 80, 34));

		int var3;
		for(var3 = 0;var3 < 3;++var3)
			for(int var4 = 0;var4 < 9;++var4)
				this.addSlotToContainer(new Slot(player.inventory, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));

		for(var3 = 0;var3 < 9;++var3)
			this.addSlotToContainer(new Slot(player.inventory, var3, 8 + var3 * 18, 142));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		ItemStack itemStack = player.getCurrentEquippedItem();
		super.onContainerClosed(player);
		if(!itemStack.hasTagCompound())
			itemStack.setTagCompound(new NBTTagCompound());
		containerInv.writeToNBT(itemStack.getTagCompound());
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int toSlot)
	{
		ItemStack tempStack = null;
		Slot invSlot = inventorySlots.get(toSlot);

		if(invSlot != null && invSlot.getHasStack())
		{
			ItemStack tempStack2 = invSlot.getStack();
			tempStack = tempStack2.copy();

			Item item = tempStack2.getItem();

			if(toSlot >= invSize && toSlot < 28)
			{
				if(item == GAMod.gemLigniteCoal || item == GAMod.gemBituminousCoal || item == GAMod.gemAnthraciteCoal)
				{
					if(!this.mergeItemStack(tempStack2, 0, 1, false))
					{
						if(!this.mergeItemStack(tempStack2, 28, 37, false))
							return null;
					}
				}
				else if(!this.mergeItemStack(tempStack2, 28, 37, false))
					return null;
			}
			else if(toSlot >= 28 && toSlot < 37)
			{
				if(!this.mergeItemStack(tempStack2, invSize, 28, false))
					return null;
			}
			else if(!this.mergeItemStack(tempStack2, 1, 37, false))
				return null;

			if(tempStack2.stackSize == 0)
				invSlot.putStack((ItemStack) null);
			else
				invSlot.onSlotChanged();

			if(tempStack2.stackSize == tempStack.stackSize)
				return null;
			invSlot.onPickupFromSlot(player, tempStack2);
		}
		return tempStack;
	}

	@Override
	public ItemStack slotClick(int slotID, int buttonPressed, int flag, EntityPlayer player)
	{

		Slot tmpSlot;
		if(slotID >= 0 && slotID < inventorySlots.size())
			tmpSlot = inventorySlots.get(slotID);
		else
			tmpSlot = null;

		if (tmpSlot != null &&
			player.inventory.getStackInSlot(player.inventory.currentItem) == tmpSlot.getStack()) {
			return tmpSlot.getStack();
		}

		return super.slotClick(slotID, buttonPressed, flag, player);
	}
}
