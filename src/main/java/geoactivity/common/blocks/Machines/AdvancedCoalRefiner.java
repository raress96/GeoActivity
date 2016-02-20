package geoactivity.common.blocks.Machines;

import java.util.Random;

import geoactivity.common.GAMod;
import geoactivity.common.GeoActivity;
import geoactivity.common.blocks.Machines.ACR.ACRTileE;
import geoactivity.common.itemblocks.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class AdvancedCoalRefiner extends BaseContainerBlock
{
	public static final PropertyBool FORMED = PropertyBool.create("formed");
	public static final PropertyBool KEEPINV = PropertyBool.create("keepinv");

	public AdvancedCoalRefiner(String name)
	{
		super(Material.iron, name, "pickaxe", 2, MultiItemBlock.class);
		this.setHardness(3.5F);
		this.setResistance(15.0F);
		this.setStepSound(Block.soundTypeStone);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FORMED, false).withProperty(KEEPINV, false));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int var)
	{
		return new ACRTileE();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(GAMod.advancedcoalrefiner);
	}

	@Override
	public Item getItem(World worldIn, BlockPos pos)
	{
		return Item.getItemFromBlock(GAMod.advancedcoalrefiner);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side,
			float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
			return true;
		else
		{
			ACRTileE tile_entity = (ACRTileE) world.getTileEntity(pos);

			if(tile_entity == null || player.isSneaking())
				return false;

			ACRTileE core = (ACRTileE) tile_entity.getCore();

			if(core != null)
			{
				player.openGui(GeoActivity.instance, 0, world, core.getPos().getX(), core.getPos().getY(),
						core.getPos().getZ());
			}
			else
				return false;

			return true;
		}
	}

	public static void setState(boolean formed, World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		TileEntity tile = world.getTileEntity(pos);

		if(state.getBlock() == GAMod.advancedcoalrefiner)
		{
			state = state.withProperty(KEEPINV, true);
			
			if(formed)
			{
				world.setBlockState(pos, state.withProperty(FORMED, true), 2);
			}
			else
			{
				world.setBlockState(pos, state.withProperty(FORMED, false), 2);
			}

			world.setBlockState(pos, world.getBlockState(pos).withProperty(KEEPINV, false), 2);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		if(!world.isRemote)
		{
			ACRTileE tile_entity = (ACRTileE) world.getTileEntity(pos);

			if(tile_entity != null)
				tile_entity.makeMultiblock();
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		if(!((Boolean)state.getValue(KEEPINV)))
		{
			ACRTileE tile_entity = (ACRTileE) world.getTileEntity(pos);

			if(tile_entity != null)
			{
				ACRTileE core = tile_entity.getCore();

				if(core != null)
				{
					InventoryHelper.dropInventoryItems(world, pos, core);
					world.updateComparatorOutputLevel(pos, this);

					tile_entity.destroyMultiBlock();
				}
			}
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos pos)
	{
		ACRTileE tile_entity = (ACRTileE) world.getTileEntity(pos);
		
		if(tile_entity != null)
		{
			ACRTileE core = tile_entity.getCore();
			return Container.calcRedstoneFromInventory((IInventory) core);
		}
		return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		boolean keepinv = (meta & 4) > 0;
		boolean formed = (meta & 8) > 0;

		return this.getDefaultState().withProperty(FORMED, formed).withProperty(KEEPINV, false);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		byte b0 = 0;
		int i = b0;

		if(((Boolean) state.getValue(FORMED)))
			i |= 8;
		if(((Boolean) state.getValue(KEEPINV)))
			i |= 4;

		return i;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FORMED, KEEPINV});
	}
}
