package geoactivity.common.items.armor.AdvLogic;

import geoactivity.common.GAMod;
import geoactivity.common.items.ArmorPerks.EnumArmorPerks;
import geoactivity.common.items.ToolPerks.EnumToolPerks;
import geoactivity.common.items.tools.Adv.Logic.AdvTInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AdvAInventory extends AdvTInventory
{
	public AdvAInventory(ItemStack stack, EntityPlayer player)
	{
		super(stack, player);
	}

	@Override
	public String getName()
	{
		return "Advanced Armor";
	}

	@Override
	public void readFromNBT(NBTTagCompound myCompound)
	{
		super.readFromNBT(myCompound);

		if(myCompound.getByte("efficiency") == 1)
			inventory[1] = new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.EFFICIENCY.getMetadata());
		else if(myCompound.getByte("all") == 1)
			inventory[1] = new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.GENERAL.getMetadata());
		else if(myCompound.getByte("blast") == 1)
			inventory[1] = new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.BLAST.getMetadata());
		else if(myCompound.getByte("fire") == 1)
			inventory[1] = new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.FIRE.getMetadata());
		else if(myCompound.getByte("magic") == 1)
			inventory[1] = new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.MAGIC.getMetadata());
		else if(myCompound.getByte("projectile") == 1)
			inventory[1] = new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.PROJECTILE.getMetadata());
		else if(myCompound.getBoolean("fall"))
			inventory[1] = new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.NOFALL.getMetadata());
		else if(myCompound.getBoolean("respiration"))
			inventory[1] = new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.RESPIRATION.getMetadata());
	}
}
