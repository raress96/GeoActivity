package com.entirecraft.general.items.armor.AdvLogic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.items.ArmorPerks.EnumArmorPerks;
import com.entirecraft.general.items.ToolPerks.EnumToolPerks;
import com.entirecraft.general.items.tools.Adv.Logic.AdvTInventory;

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
