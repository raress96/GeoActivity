package com.entirecraft.general.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import com.entirecraft.general.itemblocks.BrickItemBlock;
import com.entirecraft.util.GeneralHelper;

public class HardenedBrick extends BaseBlock
{
	public static final PropertyEnum TYPE = PropertyEnum.create("type", EnumHardenedBrick.class);

	public HardenedBrick(String name)
	{
		super(Material.rock, "pickaxe", 3, BrickItemBlock.class, GeneralHelper.appendStringToArray(
				GeneralHelper.getEnumStrings(EnumHardenedBrick.class), name + "_"));
		this.setHardness(20.0F);
		this.setResistance(150.0F);
		this.setStepSound(Block.soundTypeStone);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumHardenedBrick.NORMAL));
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		EnumHardenedBrick type = (EnumHardenedBrick) state.getValue(TYPE);
		return type.getMetadata();
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List subItems)
	{
		for(int i = 0;i < EnumHardenedBrick.values().length;i++)
		{
			subItems.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumHardenedBrick[] values = EnumHardenedBrick.values();
		if(meta >= 0 && meta < values.length)
			return this.getDefaultState().withProperty(TYPE, values[meta]);
		return this.getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		EnumHardenedBrick type = (EnumHardenedBrick) state.getValue(TYPE);

		return type.getMetadata();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {TYPE});
	}

	public static enum EnumHardenedBrick implements IStringSerializable
	{
		NORMAL(0, "normal"), ADVANCED(1, "advanced"), THERMAL(2, "thermal");

		int meta;
		String name;

		private EnumHardenedBrick(int meta, String name)
		{
			this.meta = meta;
			this.name = name;
		}

		public int getMetadata()
		{
			return this.meta;
		}

		@Override
		public String toString()
		{
			return this.name;
		}

		@Override
		public String getName()
		{
			return this.name;
		}
	}
}
