package geoactivity.common.blocks.Machines;

import geoactivity.common.GeoActivity;
import geoactivity.common.blocks.Machines.GG.GGTileE;
import geoactivity.common.itemblocks.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class GeothermalGenerator extends BaseContainerBlock
{
	public GeothermalGenerator(String name)
	{
		super(Material.iron, name, "pickaxe", 2, MultiItemBlock.class);
		this.setHardness(3.0F);
		this.setResistance(15.0F);
		this.setStepSound(Block.soundTypeStone);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new GGTileE();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side,
			float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
			return true;
		else
		{
			TileEntity tile_entity = world.getTileEntity(pos);
			if(tile_entity == null || player.isSneaking())
				return false;
			player.openGui(GeoActivity.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
	}
}
