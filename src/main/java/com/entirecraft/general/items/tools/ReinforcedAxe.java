package com.entirecraft.general.items.tools;

import net.minecraft.item.ItemAxe;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.GeoActivity;
import com.entirecraft.lib.IHasName;

public class ReinforcedAxe extends ItemAxe implements IHasName
{
	private String name;

	public ReinforcedAxe(String name)
	{
		super(GAMod.ReinforcedMaterial);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setCreativeTab(GeoActivity.tabMain);
		GameRegistry.registerItem(this, name);
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public String getName(int meta)
	{
		return this.getName();
	}
}
