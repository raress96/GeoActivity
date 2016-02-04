package com.entirecraft.general.blocks.Machines;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.GeoActivity;
import com.entirecraft.general.blocks.Machines.CR.CRTileE;
import com.entirecraft.lib.GuiIDs;

public class CoalRefiner extends BaseContainerBlock
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public static final PropertyBool KEEPINV = PropertyBool.create("keepinv");

	public CoalRefiner(String name)
	{
		super(Material.iron, name, "pickaxe", 2);
		this.setHardness(3.5F);
		this.setResistance(15.0F);
		this.setStepSound(Block.soundTypeStone);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH)
			.withProperty(ACTIVE, false).withProperty(KEEPINV, false));
	}

	@Override
	public TileEntity createNewTileEntity(World par1World, int var)
	{
		return new CRTileE();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(GAMod.coalrefiner);
	}

	@Override
	public Item getItem(World worldIn, BlockPos pos)
	{
		return Item.getItemFromBlock(GAMod.coalrefiner);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		if(!world.isRemote)
		{
			Block block = world.getBlockState(pos.north()).getBlock();
			Block block1 = world.getBlockState(pos.south()).getBlock();
			Block block2 = world.getBlockState(pos.west()).getBlock();
			Block block3 = world.getBlockState(pos.east()).getBlock();
			EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

			if(enumfacing == EnumFacing.NORTH && block.isFullBlock() && !block1.isFullBlock())
			{
				enumfacing = EnumFacing.SOUTH;
			}
			else if(enumfacing == EnumFacing.SOUTH && block1.isFullBlock() && !block.isFullBlock())
			{
				enumfacing = EnumFacing.NORTH;
			}
			else if(enumfacing == EnumFacing.WEST && block2.isFullBlock() && !block3.isFullBlock())
			{
				enumfacing = EnumFacing.EAST;
			}
			else if(enumfacing == EnumFacing.EAST && block3.isFullBlock() && !block2.isFullBlock())
			{
				enumfacing = EnumFacing.WEST;
			}

			world.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if(((Boolean) state.getValue(ACTIVE)))
		{
			EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = (double) pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
			double d2 = (double) pos.getZ() + 0.5D;
			double d3 = 0.52D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;

			switch(enumfacing.ordinal())
			{
				case 4:
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
					world.spawnParticle(EnumParticleTypes.FLAME, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
				break;
				case 5:
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
					world.spawnParticle(EnumParticleTypes.FLAME, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D, new int[0]);
				break;
				case 2:
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D, new int[0]);
					world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D, new int[0]);
				break;
				case 3:
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D, new int[0]);
					world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D, new int[0]);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY,
		float hitZ)
	{
		if(world.isRemote)
			return true;
		else
		{
			CRTileE tile_entity = (CRTileE) world.getTileEntity(pos);

			if(tile_entity == null || player.isSneaking())
				return false;

			player.openGui(GeoActivity.instance, GuiIDs.CR, world, pos.getX(), pos.getY(), pos.getZ());

			return true;
		}
	}

	public static void setState(boolean active, World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos).withProperty(KEEPINV, true);
		TileEntity tile = world.getTileEntity(pos);

		if(active)
		{
			world.setBlockState(pos, state.withProperty(ACTIVE, true));
		}
		else
		{
			world.setBlockState(pos, state.withProperty(ACTIVE, false));
		}

		world.setBlockState(pos, world.getBlockState(pos).withProperty(KEEPINV, false));

		if(tile != null)
		{
			tile.validate();
			world.setTileEntity(pos, tile);
		}
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta,
		EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		if(!((Boolean) state.getValue(KEEPINV)))
		{
			CRTileE tile = (CRTileE) world.getTileEntity(pos);

			if(tile != null)
			{
				InventoryHelper.dropInventoryItems(world, pos, tile);
				world.updateComparatorOutputLevel(pos, this);
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
		return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(pos));
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getHorizontal(meta);

		boolean active = (meta & 4) > 0;
		boolean keepinv = (meta & 8) > 0;

		return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(ACTIVE, active).withProperty(KEEPINV, keepinv);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		byte b0 = 0;
		int i = b0 | ((EnumFacing) state.getValue(FACING)).getHorizontalIndex();

		if(((Boolean) state.getValue(KEEPINV)))
			i |= 8;
		if(((Boolean) state.getValue(ACTIVE)))
			i |= 4;

		return i;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING, ACTIVE, KEEPINV});
	}
}
