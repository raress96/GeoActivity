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
import geoactivity.common.blocks.Machines.ChM.ChMTileE;
import geoactivity.common.lib.Reference;

public class ChemistryMachine extends BaseContainerBlock
{	
	public ChemistryMachine(String name)
	{
		super(Material.iron, name, "pickaxe", 3);
		this.setHardness(10.0F);
		this.setResistance(20.0F);
		this.setStepSound(Block.soundTypeStone);
	}

	@Override
	public TileEntity createNewTileEntity(World var1,int var)
	{
		return new ChMTileE();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if (world.isRemote)
			return true;
		else
		{
			TileEntity tile_entity = world.getTileEntity(pos);
			if (tile_entity == null || player.isSneaking())
				return false;
			player.openGui(GeoActivity.instance, GuiIDs.ChM, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
		ChMTileE tile = (ChMTileE) world.getTileEntity(pos);

		if (tile != null)
		{
			 InventoryHelper.dropInventoryItems(world, pos, tile);
             world.updateComparatorOutputLevel(pos, this);
		}
		
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		ChMTileE tile = (ChMTileE) world.getTileEntity(pos);
		tile.craftRecipe();
	}
}
