package geoactivity.common.blocks.Machines.CrM;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class CraftSlot extends SlotCrafting
{
	public CraftSlot(EntityPlayer player, InventoryCrafting craftingInv, IInventory resultInv, int par4, int par5,
			int par6)
	{
		super(player, craftingInv, resultInv, par4, par5, par6);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer)
	{
		return false;
	}
}
