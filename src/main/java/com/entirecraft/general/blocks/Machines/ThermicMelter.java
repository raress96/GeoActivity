package com.entirecraft.general.blocks.Machines;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.entirecraft.general.blocks.Machines.Tiles.TMTileE;
import com.entirecraft.general.itemblocks.MultiItemBlock;

public class ThermicMelter extends BaseContainerBlock
{	
	public ThermicMelter(String name)
	{
		super(Material.iron, name, "pickaxe", 2, MultiItemBlock.class);
		this.setHardness(10.0F);
		this.setResistance(15.0F);
		this.setStepSound(Block.soundTypeStone);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int var)
	{
		return new TMTileE();
	}
}
