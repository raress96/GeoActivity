package com.entirecraft.general.items;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.GeoActivity;
import com.entirecraft.lib.Reference;

public class CustomCoalItems extends BaseItem
{
	public CustomCoalItems(String coalType)
	{
		super(coalType);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack)
	{
		if(name.equals("anthracitecoal"))
			return EnumRarity.UNCOMMON;
		else
			return EnumRarity.COMMON;
	}
}
