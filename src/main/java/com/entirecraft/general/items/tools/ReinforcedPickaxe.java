package com.entirecraft.general.items.tools;

import net.minecraft.item.ItemPickaxe;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.GeoActivity;
import com.entirecraft.lib.IHasName;

public class ReinforcedPickaxe extends ItemPickaxe implements IHasName
{
	private String name;

	public ReinforcedPickaxe(String name)
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
