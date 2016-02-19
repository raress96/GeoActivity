package geoactivity.common.items.tools.RMLogic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import geoactivity.common.GAMod;
import geoactivity.common.util.BaseInventory;

public class RMInventory extends BaseInventory
{
	public RMInventory(ItemStack stack, EntityPlayer player)
	{
		super(stack, player, 1);
	}

	@Override
	public String getName()
	{
		return "ReinforcedMiner";
	}

	public void setCharge()
	{
		int fuel = 0;
		if(inventory != null)
		{
			Item item = inventory[0].getItem();
			if(item == GAMod.gemLigniteCoal)
				fuel = 100;
			else if(item == GAMod.gemBituminousCoal)
				fuel = 200;
			else if(item == GAMod.gemAnthraciteCoal)
				fuel = 300;
		}
		while(fuel != 0 && inventory != null)
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
		if(current.getItemDamage() <= 498)
		{
			NBTTagCompound tag = current.getTagCompound();
			tag.setBoolean("destroyed", false);
			current.setTagCompound(tag);
		}
		writeToNBT(current.getTagCompound());
	}
}
