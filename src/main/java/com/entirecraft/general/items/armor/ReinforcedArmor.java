package com.entirecraft.general.items.armor;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.GeoActivity;
import com.entirecraft.lib.IHasName;

public class ReinforcedArmor extends ItemArmor implements IHasName
{	
	private String name;
	
	public ReinforcedArmor(String name, int renderIndex, int type)
	{
		super(GAMod.ReinforcedArmorMaterial, renderIndex, type);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setCreativeTab(GeoActivity.tabMain);
		GameRegistry.registerItem(this, name);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getName(int meta)
	{
		return this.getName();
	}
}
