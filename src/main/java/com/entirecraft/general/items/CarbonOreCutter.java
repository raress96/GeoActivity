package com.entirecraft.general.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.entirecraft.lib.Reference;

public class CarbonOreCutter extends BaseItem
{
	public CarbonOreCutter(String name)
	{
		super(name);
		this.setMaxStackSize(16);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
	{
		list.add("\u00A77Used in the Advanced Ore Refiner to increase yield.");
	}
}
