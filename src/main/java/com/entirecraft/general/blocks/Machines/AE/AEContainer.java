package com.entirecraft.general.blocks.Machines.AE;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.items.ElementContainer.EnumElements;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AEContainer extends Container
{
	protected AETileE Tile_E;
	private int lastCookTime = 0;
	private int lastBurnTime = 0;

	public AEContainer(AETileE tile, InventoryPlayer playerInv)
	{
		Tile_E = tile;

		this.addSlotToContainer(new Slot(tile, 0, 22, 30));
		this.addSlotToContainer(new ECSlot(tile, 1, 50, 55));
		this.addSlotToContainer(new AESlot( tile, 2, 86, 30));
		this.addSlotToContainer(new AESlot(tile, 3, 112, 30));
		this.addSlotToContainer(new AESlot(tile, 4, 138, 30));

		int var3;

		for (var3 = 0; var3 < 3; ++var3)
			for (int var4 = 0; var4 < 9; ++var4)
				this.addSlotToContainer(new Slot(playerInv, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));

		for (var3 = 0; var3 < 9; ++var3)
			this.addSlotToContainer(new Slot(playerInv, var3, 8 + var3 * 18, 142));
	}

	@Override
	public void onCraftGuiOpened(ICrafting crafting)
	{
		super.onCraftGuiOpened(crafting);

		crafting.sendProgressBarUpdate(this, 0, Tile_E.furnaceCookTime);
		crafting.sendProgressBarUpdate(this, 1, Tile_E.currentItemBurnTime);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int var1 = 0; var1 < crafters.size(); ++var1)
		{
			ICrafting var2 = crafters.get(var1);

			if (lastCookTime != Tile_E.furnaceCookTime)
				var2.sendProgressBarUpdate(this, 0, Tile_E.furnaceCookTime);

			if (lastBurnTime != Tile_E.currentItemBurnTime)
				var2.sendProgressBarUpdate(this, 1, Tile_E.currentItemBurnTime);
		}

		lastCookTime = Tile_E.furnaceCookTime;
		lastBurnTime = Tile_E.currentItemBurnTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2)
	{
		if (par1 == 0)
			Tile_E.furnaceCookTime = par2;
		else if(par1 == 1)
			Tile_E.currentItemBurnTime = par2;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return Tile_E.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int par2)
	{
		ItemStack var3 = null;
		Slot var4 = inventorySlots.get(par2);

		if (var4 != null && var4.getHasStack())
		{
			ItemStack var5 = var4.getStack();
			var3 = var5.copy();

			if (par2 == 2 || par2 == 3 || par2 == 4)
			{
				if (!this.mergeItemStack(var5, 5, 41, true))
					return null;

				var4.onSlotChange(var5, var3);
			}
			else if (par2 != 1 && par2 != 0)
			{
				if (AERecipes.getInstance().getSmeltingResult(var5) != null)
				{
					if (!this.mergeItemStack(var5, 0, 1, false))
						return null;
				}
				else if (var5.getItem() == GAMod.elementContainer && var5.getItemDamage() == EnumElements.EMPTY.getMetadata())
				{
					if (!this.mergeItemStack(var5, 1, 2, false))
						return null;
				}
				else if (par2 >= 5 && par2 < 32)
				{
					if (!this.mergeItemStack(var5, 32, 41, false))
						return null;
				}
				else if (par2 >= 32 && par2 < 41 && !this.mergeItemStack(var5, 5, 32, false))
					return null;
			}
			else if (!this.mergeItemStack(var5, 5, 41, false))
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
