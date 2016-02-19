package geoactivity.common.blocks.Machines;

import geoactivity.common.blocks.Machines.Tiles.TMTileE;
import geoactivity.common.itemblocks.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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