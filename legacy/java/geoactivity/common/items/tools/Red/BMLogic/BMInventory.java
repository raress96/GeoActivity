package geoactivity.common.items.tools.Red.BMLogic;

import geoactivity.common.GAMod;
import geoactivity.common.items.tools.Red.Logic.RedInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BMInventory extends RedInventory
{
	public BMInventory(ItemStack stack, EntityPlayer player)
	{
		super(stack, player, 12);
	}

	@Override
	public String getName()
	{
		return "RedstoneBattleMiner";
	}

	@Override
	public void setCharge()
	{
		int fuel = 0;
		if(inventory[0] != null)
		{
			int basefuel = 0;
			if(current.getTagCompound().getByte("efficiency") >= 1)
				basefuel = 10 * current.getTagCompound().getByte("efficiency");

			Item item = inventory[0].getItem();
			if(item == GAMod.gemLigniteCoal)
				fuel = 130 + basefuel * 2;
			else if(item == GAMod.gemBituminousCoal)
				fuel = 260 + basefuel * 3;
			else if(item == GAMod.gemAnthraciteCoal)
				fuel = 400 + basefuel * 4;
		}
		while(fuel != 0 && inventory[0] != null)
		{
			int damage = current.getItemDamage();
			if(damage - fuel > 0)
			{
				current.setItemDamage(damage - fuel);
				decrStackSize(0, 1);
			}
			else
				fuel = 0;
		}
		if(current.getItemDamage() <= 1998)
		{
			NBTTagCompound tag = current.getTagCompound();
			tag.setBoolean("destroyed", false);
			current.setTagCompound(tag);
		}
		writeToNBT(current.getTagCompound());
	}
}