package com.entirecraft.general.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.entirecraft.general.GeoActivity;
import com.entirecraft.lib.IHasName;

public abstract class BaseBlock extends Block implements IHasName
{
	protected final String[] name;

	public BaseBlock(Material mat, String tool, int tier, String... name)
	{
		super(mat);
		this.name = name;
		this.setUnlocalizedName(name[0]);
		this.setHarvestLevel(tool, tier);
		this.setCreativeTab(GeoActivity.tabMain);
		GameRegistry.registerBlock(this, name[0]);
	}

	public <C extends ItemBlock> BaseBlock(Material mat, String tool, int tier, Class<C> item, String... name)
	{
		super(mat);
		this.name = name;
		this.setUnlocalizedName(name[0]);
		this.setHarvestLevel(tool, tier);
		this.setCreativeTab(GeoActivity.tabMain);
		GameRegistry.registerBlock(this, item, name[0]);
	}

	@Override
	public String getName()
	{
		return name[0];
	}

	@Override
	public String getName(int meta)
	{
		return name[meta];
	}
}
