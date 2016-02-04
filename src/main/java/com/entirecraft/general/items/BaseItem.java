package com.entirecraft.general.items;

import com.entirecraft.general.GeoActivity;
import com.entirecraft.lib.IHasName;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BaseItem extends Item implements IHasName
{
	protected final String name[];

	public BaseItem(String... name)
	{
		this.name = name;
		this.setUnlocalizedName(name[0]);
		this.setMaxStackSize(64);
		this.setCreativeTab(GeoActivity.tabMain);
		GameRegistry.registerItem(this, name[0]);
	}

	@Override
	public String getName()
	{
		return getName(0);
	}

	@Override
	public String getName(int meta)
	{
		return name[meta];
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if(stack.getHasSubtypes() == true)
			return getName(stack.getMetadata());
		return getName();
	}
}
