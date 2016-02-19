package geoactivity.common.blocks.Machines;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import geoactivity.client.GuiIDs;
import geoactivity.common.GeoActivity;
import geoactivity.common.blocks.Machines.TC.TCTileE;
import geoactivity.common.itemblocks.MultiItemBlock;
import geoactivity.common.lib.Reference;

public class ToolCharger extends BaseContainerBlock
{		
	public ToolCharger(String name)
	{
		super(Material.iron, name, "pickaxe", 2, MultiItemBlock.class);
		this.setHardness(10.0F);
		this.setResistance(15.0F);
		this.setStepSound(Block.soundTypeStone);
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TCTileE();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if (world.isRemote)
			return true;
		else
		{
			TCTileE tile_entity = (TCTileE) world.getTileEntity(pos);
			
			if (tile_entity == null || player.isSneaking())
				return false;
			
			player.openGui(GeoActivity.instance, GuiIDs.TC, world, pos.getX(), pos.getY(), pos.getZ());
			
			return true;
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
		TCTileE tile = (TCTileE) world.getTileEntity(pos);

		if (tile != null)
		{
			InventoryHelper.dropInventoryItems(world, pos, tile);
            world.updateComparatorOutputLevel(pos, this);
		}
		
		super.breakBlock(world, pos, state);
	}
}
