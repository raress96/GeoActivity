package geoactivity.common.blocks.Machines;

import geoactivity.common.blocks.Machines.Tiles.TGTileE;
import geoactivity.common.itemblocks.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThermicGenerator extends BaseContainerBlock
{
	public ThermicGenerator(String name)
	{
		super(Material.iron, name, "pickaxe", 2, MultiItemBlock.class);
		this.setHardness(10.0F);
		this.setResistance(15.0F);
		this.setSoundType(SoundType.STONE);
	}

	@Override
	public TileEntity createNewTileEntity(World par1World, int var)
	{
		return new TGTileE();
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		if(!world.isRemote)
		{
			TGTileE tile = (TGTileE) world.getTileEntity(pos);
			tile.checkBricks();
		}
	}
}
