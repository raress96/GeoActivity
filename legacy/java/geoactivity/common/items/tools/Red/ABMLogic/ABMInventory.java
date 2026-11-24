package geoactivity.common.items.tools.Red.ABMLogic;

import geoactivity.common.items.ToolPerks.EnumToolPerks;
import geoactivity.common.items.tools.Red.Logic.RedInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ABMInventory extends RedInventory
{
	public ABMInventory(ItemStack stack, EntityPlayer player)
	{
		super(stack, player, 12);
	}

	@Override
	public String getName()
	{
		return "AdvancedRedstoneBattleMiner";
	}

	@Override
	public void setCharge()
	{
		
	}

	@Override
	public void writeToNBT(NBTTagCompound myCompound)
	{
		super.writeToNBT(myCompound);

		byte speed = 0, dam = 0, fortune = 0, wide = 0;
		boolean xp = false, smelt = false, silk = false, capitator = false, nodrops = false, droptp = false, barmor = false;
		int[] tpcoords = null;

		for(int i = 0;i < inventory.length;++i)
		{
			if(inventory[i] != null && inventory[i].getMetadata() < EnumToolPerks.values().length)
				switch(EnumToolPerks.values()[inventory[i].getItemDamage()])
				{
					case SPEED:
						speed++;
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
						wide++;
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
		if(speed > 4)
			speed = 4;
		if(dam > 4)
			dam = 4;
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
		if(wide > 0 && capitator)
			capitator = false;
		if(wide > 4)
			wide = 4;

		myCompound.setByte("speed", speed);
		myCompound.setByte("damage", dam);
		myCompound.setByte("fortune", fortune);
		myCompound.setByte("widerRadius", wide);
		myCompound.setBoolean("experience", xp);
		myCompound.setBoolean("auto-smelt", smelt);
		myCompound.setBoolean("silk-touch", silk);
		myCompound.setBoolean("capitator", capitator);
		myCompound.setBoolean("nodrops", nodrops);
		myCompound.setBoolean("barmor", barmor);

		myCompound.setBoolean("droptp", droptp);

		if(droptp && tpcoords.length > 0)
			myCompound.setIntArray("droptp-coords", tpcoords);
	}
}