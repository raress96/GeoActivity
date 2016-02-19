package geoactivity.common.blocks.Machines;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
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
import geoactivity.client.GuiIDs;
import geoactivity.common.GAMod;
import geoactivity.common.GeoActivity;
import geoactivity.common.blocks.HardenedBrick.EnumHardenedBrick;
import geoactivity.common.blocks.Machines.AOR.AORTileE;
import geoactivity.common.itemblocks.MultiItemBlock;

public class AdvancedOreRefiner extends BaseContainerBlock
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool FORMED = PropertyBool.create("formed");
	public static final PropertyBool CORE = PropertyBool.create("core");

	public AdvancedOreRefiner(String name)
	{
		super(Material.iron, name, "pickaxe", 3, MultiItemBlock.class);
		this.setHardness(20.0F);
		this.setResistance(20.0F);
		this.setStepSound(Block.soundTypeStone);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH)
				.withProperty(FORMED, false).withProperty(CORE, true));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int var)
	{
		return new AORTileE();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(GAMod.advancedorerefiner);
	}

	@Override
	public Item getItem(World worldIn, BlockPos pos)
	{
		return Item.getItemFromBlock(GAMod.advancedorerefiner);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		if(!world.isRemote && ((Boolean)state.getValue(CORE)))
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side,
			float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
			return true;
		else
		{
			AORTileE tile_entity = (AORTileE) world.getTileEntity(pos);

			if(tile_entity == null || player.isSneaking())
				return false;

			AORTileE core = tile_entity.getCore();

			if(core != null)
			{
				player.openGui(GeoActivity.instance, GuiIDs.AOR, world, core.getPos().getX(), core.getPos().getY(),
						core.getPos().getZ());
			}
			else
				return false;

			return true;
		}
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

		if(!world.isRemote)
		{
			AORTileE tile_entity = (AORTileE) world.getTileEntity(pos);

			if(tile_entity != null)
				tile_entity.makeMultiblock();
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		AORTileE tile_entity = (AORTileE) world.getTileEntity(pos);

		if(tile_entity != null)
		{
			AORTileE core = tile_entity.getCore();
			if(core != null)
			{
				InventoryHelper.dropInventoryItems(world, pos, core);
				world.updateComparatorOutputLevel(pos, this);

				if(!((Boolean) state.getValue(CORE)))
				{
					ItemStack stack = null;
					if(tile_entity.isBrick)
						stack = new ItemStack(GAMod.hardenedbrick, 1, EnumHardenedBrick.ADVANCED.getMetadata());
					else
						stack = new ItemStack(GAMod.advancedcoalrefiner);

					if(stack != null)
					{
						EntityItem item =
								new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
						world.spawnEntityInWorld(item);
					}

					tile_entity.destroyMultiBlock();

					return;
				}
				else
					tile_entity.destroyMultiBlock();
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random)
	{
		if(state.getBlock() == GAMod.advancedorerefiner && !((Boolean) state.getValue(CORE)))
			return 0;
		return quantityDroppedWithBonus(fortune, random);
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, BlockPos pos)
	{
		AORTileE tile_entity = (AORTileE) world.getTileEntity(pos);

		if(tile_entity != null)
		{
			AORTileE core = tile_entity.getCore();
			return Container.calcRedstoneFromInventory((IInventory) core);
		}
		return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getHorizontal(meta);

		boolean formed = (meta & 4) > 0;
		boolean core = (meta & 8) > 0;

		return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(FORMED, formed)
				.withProperty(CORE, core);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		byte b0 = 0;
		int i = b0 | ((EnumFacing) state.getValue(FACING)).getHorizontalIndex();

		if(((Boolean) state.getValue(FORMED)))
			i |= 4;
		if(((Boolean) state.getValue(CORE)))
			i |= 8;

		return i;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING, FORMED, CORE});
	}
}
