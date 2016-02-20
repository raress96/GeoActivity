package geoactivity.common.items.tools.Red.Logic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import geoactivity.common.GAMod;
import geoactivity.common.items.ToolPerks.EnumToolPerks;
import geoactivity.common.util.BaseInventory;

public class RedInventory extends BaseInventory
{
	public RedInventory(ItemStack stack, EntityPlayer player, int size)
	{
		super(stack, player, size);
	}

	@Override
	public String getName()
	{
		return "RedstoneTool";
	}

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
		if(current.getItemDamage() <= 998)
		{
			NBTTagCompound tag = current.getTagCompound();
			tag.setBoolean("destroyed", false);
			current.setTagCompound(tag);
		}
		writeToNBT(current.getTagCompound());
	}

	@Override
	public void writeToNBT(NBTTagCompound myCompound)
	{
		super.writeToNBT(myCompound);

		byte speed = 0, eff = 0, dam = 0, fortune = 0;
		boolean xp = false, smelt = false, silk = false, widerRadius = false, capitator = false, nodrops = false, droptp = false, barmor = false;
		int[] tpcoords = null;

		for(int i = 4;i < inventory.length;++i)
		{
			if(inventory[i] != null && inventory[i].getMetadata() < EnumToolPerks.values().length)
				switch(EnumToolPerks.values()[inventory[i].getItemDamage()])
				{
					case SPEED:
						speed++;
					break;
					case EFFICIENCY:
						eff++;
					break;
					case DAMAGE:
						dam++;
					break;
					case EXPERIENCE:
						xp = true;
					break;
					case SMELTING:
						smelt = true;
					break;
					case FORTUNE:
						fortune++;
					break;
					case SILKTOUCH:
						silk = true;
					break;
					case RADIUS:
						widerRadius = true;
					break;
					case CAPITATOR:
						capitator = true;
					break;
					case NODROPS:
						nodrops = true;
					break;
					case DROPTP:
					{
						if(inventory[i].hasTagCompound()
							&& inventory[i].getTagCompound().getIntArray("coords").length > 0)
						{
							tpcoords = inventory[i].getTagCompound().getIntArray("coords");
							droptp = true;
						}
					}
					break;
					case BYPASSARMOR:
						barmor = true;
					break;
					default:
					break;
				}
		}
		if(speed > 2)
			speed = 2;
		if(eff > 2)
			eff = 2;
		if(dam > 2)
			dam = 2;
		if(fortune > 4)
			fortune = 4;
		if(nodrops)
		{
			if(smelt)
				smelt = false;
			if(silk)
				silk = false;
			if(fortune > 0)
				fortune = 0;
			if(droptp)
				droptp = false;
		}
		if((smelt || fortune > 0) && silk)
			silk = false;
		if(widerRadius && capitator)
			capitator = false;

		myCompound.setByte("speed", speed);
		myCompound.setByte("efficiency", eff);
		myCompound.setByte("damage", dam);
		myCompound.setByte("fortune", fortune);
		myCompound.setBoolean("experience", xp);
		myCompound.setBoolean("auto-smelt", smelt);
		myCompound.setBoolean("silk-touch", silk);
		myCompound.setBoolean("widerRadius", widerRadius);
		myCompound.setBoolean("capitator", capitator);
		myCompound.setBoolean("nodrops", nodrops);
		myCompound.setBoolean("barmor", barmor);

		myCompound.setBoolean("droptp", droptp);

		if(droptp && tpcoords.length > 0)
			myCompound.setIntArray("droptp-coords", tpcoords);
	}
}
