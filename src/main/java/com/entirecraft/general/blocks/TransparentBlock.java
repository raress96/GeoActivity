package com.entirecraft.general.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.entirecraft.lib.Reference;

public class TransparentBlock extends BaseBlock
{
	public TransparentBlock(String name, Material mat, int level)
	{
		super(mat, "pickaxe", level, name);
		this.setStepSound(Block.soundTypeStone);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public EnumWorldBlockLayer getBlockLayer()
	{
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube()
	{
		return false;
	}
}
