package geoactivity.common.blocks.Machines.ChM;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ChMContainer extends Container
{
	protected ChMTileE tile_entity;

	public ChMContainer(ChMTileE tile, InventoryPlayer playerInv)
	{
		tile_entity = tile;
		
		this.addSlotToContainer(new Slot(tile, 0, 53, 31));
		this.addSlotToContainer(new Slot(tile, 1, 107, 31));

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for (int i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return tile_entity.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int par2)
	{
		ItemStack var3 = null;
		Slot var4 = (Slot) inventorySlots.get(par2);

		if (var4 != null && var4.getHasStack())
		{
			ItemStack var5 = var4.getStack();
			var3 = var5.copy();
			
			if(par2 == 0 || par2 == 1)
			{
				if (!this.mergeItemStack(var5, 2, 38, true))
					return null;

				var4.onSlotChange(var5, var3);
			}
			else if (par2 != 1 && par2 != 0)
			{
				if(!this.mergeItemStack(var5, 0, 2, false))
					if (par2 >= 2 && par2 < 29)
					{
						if (!this.mergeItemStack(var5, 29, 38, false))
							return null;
					}
					else if (par2 >= 29 && par2 < 38 && !this.mergeItemStack(var5, 2, 29, false))
						return null;
			}
			else if (!this.mergeItemStack(var5, 2, 38, false))
				return null;
			
			
			if (var5.stackSize == 0)
				var4.putStack((ItemStack) null);
			else
				var4.onSlotChanged();

			if (var5.stackSize == var3.stackSize)
				return null;
			
			var4.onPickupFromSlot(player, var5);
		}
		return var3;
	}
}