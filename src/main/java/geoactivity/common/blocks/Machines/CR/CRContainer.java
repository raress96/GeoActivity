package geoactivity.common.blocks.Machines.CR;

import geoactivity.common.blocks.Machines.ACR.FuelSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CRContainer extends Container
{
	protected CRTileE Tile_E;
	private int lastCookTime = 0;
	private int lastBurnTime = 0;
	private int lastItemBurnTime = 0;

	public CRContainer(CRTileE tile, InventoryPlayer playerInv)
	{
		Tile_E = tile;
		this.addSlotToContainer(new Slot(tile, 0, 66, 35));
		this.addSlotToContainer(new FuelSlot(tile, 1, 30, 35));
		this.addSlotToContainer(new CRSlot(playerInv.player, tile, 2, 126, 35));
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
		crafting.sendProgressBarUpdate(this, 1, Tile_E.furnaceBurnTime);
		crafting.sendProgressBarUpdate(this, 2, Tile_E.currentItemBurnTime);
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

			if (lastBurnTime != Tile_E.furnaceBurnTime)
				var2.sendProgressBarUpdate(this, 1, Tile_E.furnaceBurnTime);

			if (lastItemBurnTime != Tile_E.currentItemBurnTime)
				var2.sendProgressBarUpdate(this, 2, Tile_E.currentItemBurnTime);
		}

		lastCookTime = Tile_E.furnaceCookTime;
		lastBurnTime = Tile_E.furnaceBurnTime;
		lastItemBurnTime = Tile_E.currentItemBurnTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2)
	{
		if (par1 == 0)
			Tile_E.furnaceCookTime = par2;

		if (par1 == 1)
			Tile_E.furnaceBurnTime = par2;

		if (par1 == 2)
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

			if (par2 == 2)
			{
				if (!this.mergeItemStack(var5, 3, 39, true))
					return null;

				var4.onSlotChange(var5, var3);
			}
			else if (par2 != 1 && par2 != 0)
			{
				if (CRRecipes.getInstance().getSmeltingResult(var5) != null)
				{
					if (!this.mergeItemStack(var5, 0, 1, false))
					 if (FuelSlot.isFuel(var5))
					 {
						if (!this.mergeItemStack(var5, 1, 2, false))
							return null;
					 }
					 else
						 return null;
				}
				else if (FuelSlot.isFuel(var5))
				{
					if (!this.mergeItemStack(var5, 1, 2, false))
						return null;
				}
				else if (par2 >= 3 && par2 < 30)
				{
					if (!this.mergeItemStack(var5, 30, 39, false))
						return null;
				}
				else if (par2 >= 30 && par2 < 39 && !this.mergeItemStack(var5, 3, 30, false))
					return null;
			}
			else if (!this.mergeItemStack(var5, 3, 39, false))
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
