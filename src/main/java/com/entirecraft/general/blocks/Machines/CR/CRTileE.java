package com.entirecraft.general.blocks.Machines.CR;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.blocks.Machines.CoalRefiner;
import com.entirecraft.general.blocks.Machines.ACR.FuelSlot;
import com.entirecraft.util.BaseTileEntity;

public class CRTileE extends BaseTileEntity
{
	public int furnaceBurnTime = 0;
	public int currentItemBurnTime = 0;
	public int furnaceCookTime = 0;

	public CRTileE()
	{
		super(3);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		furnaceBurnTime = tag.getShort("BurnTime");
		furnaceCookTime = tag.getShort("CookTime");
		currentItemBurnTime = tag.getShort("ItemBurnTime");

	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		tag.setShort("BurnTime", (short) furnaceBurnTime);
		tag.setShort("CookTime", (short) furnaceCookTime);
		tag.setShort("ItemBurnTime", (short) currentItemBurnTime);
	}

	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int par1)
	{
		return furnaceCookTime * par1 / 400;
	}

	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int par1)
	{
		if (currentItemBurnTime == 0)
			currentItemBurnTime = 400;

		return furnaceBurnTime * par1 / currentItemBurnTime;
	}

	public boolean isBurning()
	{
		return furnaceBurnTime > 0;
	}

	@Override
	public void update()
	{
		boolean var1 = furnaceBurnTime > 0;
		boolean var2 = false;

		if (furnaceBurnTime > 0)
			--furnaceBurnTime;

		if (!worldObj.isRemote)
		{
			if (furnaceBurnTime == 0 && this.canSmelt())
			{
				currentItemBurnTime = furnaceBurnTime = getItemBurnTime(inventory[1]);

				if (furnaceBurnTime > 0)
				{
					var2 = true;

					if (inventory[1] != null)
					{
						--inventory[1].stackSize;

						if (inventory[1].stackSize == 0)
							inventory[1] = inventory[1].getItem().getContainerItem(inventory[1]);
					}
				}
			}

			if (this.isBurning() && this.canSmelt())
			{
				++furnaceCookTime;

				if (furnaceCookTime == 400)
				{
					furnaceCookTime = 0;
					this.smeltItem();
					var2 = true;
				}
			}
			else
				furnaceCookTime = 0;

			if (var1 != furnaceBurnTime > 0)
			{
				var2 = true;
				CoalRefiner.setState(furnaceBurnTime > 0, worldObj, pos);
			}
		}

		if (var2)
			this.markDirty();
	}

	private boolean canSmelt()
	{
		if (inventory[0] == null)
			return false;
		else
		{
			ItemStack var1 = CRRecipes.getInstance().getSmeltingResult(inventory[0]);
			if (var1 == null)
				return false;
			if (inventory[2] == null)
				return true;
			if (!inventory[2].isItemEqual(var1))
				return false;
			int result = inventory[2].stackSize + var1.stackSize;
			return (result <= getInventoryStackLimit() && result <= var1.getMaxStackSize());
		}
	}

	public void smeltItem()
	{
		if (this.canSmelt())
		{
			ItemStack var1 = CRRecipes.getInstance().getSmeltingResult(inventory[0]);

			if (inventory[2] == null)
				inventory[2] = var1.copy();
			else if (inventory[2].isItemEqual(var1))
				inventory[2].stackSize += var1.stackSize;

			--inventory[0].stackSize;

			if (inventory[0].stackSize <= 0)
				inventory[0] = null;
		}
	}

	public static int getItemBurnTime(ItemStack stack)
	{
		if (stack == null)
			return 0;
		else
		{
			Item var1 = stack.getItem();

			if (var1 == Items.coal)
				return 400; // fuel times
			if (var1 == GAMod.gemLigniteCoal)
				return 3200;
			if (var1 == GAMod.gemBituminousCoal)
				return 6400;
			if (var1 == GAMod.gemAnthraciteCoal)
				return 9600;
			return 0;
		}
	}
	
	@Override
	public String getName()
	{
		return "CoalRefiner";
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return new int[]{0,1,2};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction)
	{
		if (CRRecipes.getInstance().getSmeltingResult(stack) != null && index == 0)
			return true;
		
		if (FuelSlot.isFuel(stack) && index == 1)
			return true;
		
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		if(index == 2)
			return true;
		return false;
	}
}
