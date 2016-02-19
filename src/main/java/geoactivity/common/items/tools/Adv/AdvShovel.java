package geoactivity.common.items.tools.Adv;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import geoactivity.client.GuiIDs;
import geoactivity.common.GAMod;
import geoactivity.common.GeoActivity;
import geoactivity.common.items.tools.Adv.Logic.AdvTContainer;
import geoactivity.common.items.tools.Adv.Logic.AdvTGUI;
import geoactivity.common.items.tools.Adv.Logic.AdvTInventory;
import geoactivity.common.lib.ToolsHelper;
import geoactivity.common.util.BaseGUITool;
import geoactivity.common.util.GeneralHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class AdvShovel extends BaseGUITool
{
	public AdvShovel(String name)
	{
		super(name, GAMod.AdvancedMaterial);

		this.setHarvestLevel("shovel", GAMod.AdvancedMaterial.getHarvestLevel());
	}

	@SuppressWarnings("all")
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
	{
		if(stack.hasTagCompound())
		{
			if(stack.getTagCompound().getBoolean("destroyed"))
				list.add("\u00A7cDestroyed");

			if(!GeneralHelper.isShiftKeyDown())
			{
				list.add(GeneralHelper.shiftForInfo);
				return;
			}

			if(stack.getTagCompound().getByte("speed") == 1)
				list.add("\u00A77Has extra speed!");
			else if(stack.getTagCompound().getByte("efficiency") == 1)
				list.add("\u00A77Uses less fuel!");
			else if(stack.getTagCompound().getBoolean("auto-smelt"))
				list.add("\u00A77Auto smelts!");
			else if(stack.getTagCompound().getByte("fortune") == 1)
				list.add("\u00A77Has Fortune I enchant!");
			else if(stack.getTagCompound().getBoolean("silk-touch"))
				list.add("\u00A77Has Silk-Touch enchant!");
			else if(stack.getTagCompound().getBoolean("widerRadius"))
				list.add("\u00A77Has wider mining radius!");
			else if(stack.getTagCompound().getBoolean("capitator"))
				list.add("\u00A77Destroys blocks in a vertical line.");
			else if(stack.getTagCompound().getBoolean("nodrops"))
				list.add("\u00A77Gives no block drops.");
			else if(stack.getTagCompound().getBoolean("barmor"))
				list.add("\u00A77Deals damage that bypasses armor.");
		}
		list.add("");

		addDamageInfo(stack, 5, list);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase entityBase)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		EntityPlayer player = (EntityPlayer) entityBase;
		if(this.getDamage(stack) >= 998 && player.capabilities.isCreativeMode == false)
		{
			NBTTagCompound tag = stack.getTagCompound();
			tag.setBoolean("destroyed", true);
			stack.setTagCompound(tag);
		}
		if(stack.getTagCompound().getBoolean("destroyed"))
			return false;
		stack.damageItem(2, entityBase);
		return true;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		if(this.getDamage(stack) >= 998 && player.capabilities.isCreativeMode == false)
		{
			NBTTagCompound tag = stack.getTagCompound();
			tag.setBoolean("destroyed", true);
			stack.setTagCompound(tag);
		}
		if(stack.getTagCompound().getBoolean("destroyed"))
			return false;
		return ToolsHelper.onLeftClickEntity(stack, player, entity, 5);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		if(this.getDamage(stack) >= 998 && !player.capabilities.isCreativeMode)
		{
			NBTTagCompound tag = stack.getTagCompound();
			tag.setBoolean("destroyed", true);
			stack.setTagCompound(tag);
		}
		if(stack.getTagCompound().getBoolean("destroyed"))
			return true;

		World world = player.worldObj;

		if(stack.getTagCompound().getBoolean("widerRadius"))
		{
			MovingObjectPosition mop = ToolsHelper.raytraceFromEntity(world, player, true, 5.0D);
			if(mop == null)
				return super.onBlockStartBreak(stack, pos, player);

			int xRange = 1;
			int yRange = 1;
			int zRange = 1;
			switch(mop.sideHit)
			{
				case DOWN:
				case UP:
					yRange = 0;
				break;
				case NORTH:
				case SOUTH:
					zRange = 0;
				break;
				case EAST:
				case WEST:
					xRange = 0;
				break;
			}
			Block block = world.getBlockState(pos).getBlock();
			if(block != null)
			{
				BlockPos newPos = null;
				for(int xPos = pos.getX() - xRange;xPos <= pos.getX() + xRange;xPos++)
					for(int yPos = pos.getY() - yRange;yPos <= pos.getY() + yRange;yPos++)
						for(int zPos = pos.getZ() - zRange;zPos <= pos.getZ() + zRange;zPos++)
						{
							newPos = new BlockPos(xPos, yPos, zPos);
							Block nblock = world.getBlockState(newPos).getBlock();

							if(block == nblock)
							{
								if(this.getDamage(stack) >= 998 && player.capabilities.isCreativeMode == false)
								{
									NBTTagCompound tag = stack.getTagCompound();
									tag.setBoolean("destroyed", true);
									stack.setTagCompound(tag);
								}
								if(stack.getTagCompound().getBoolean("destroyed"))
									return true;

								destroyBlock(stack, world, newPos, player);
							}
						}

				return false;
			}
		}
		else if(stack.getTagCompound().getBoolean("capitator"))
		{
			Block block = world.getBlockState(pos).getBlock();
			if(block != null)
			{
				Block nblock = block;
				BlockPos newPos = null;
				int yPos = pos.getY();
				while(yPos != pos.getY() + 10 && nblock != null && nblock == block)
				{
					newPos = pos.add(0, yPos, 0);
					nblock = world.getBlockState(newPos).getBlock();

					if(block == nblock)
					{
						if(this.getDamage(stack) >= 998 && player.capabilities.isCreativeMode == false)
						{
							NBTTagCompound tag = stack.getTagCompound();
							tag.setBoolean("destroyed", true);
							stack.setTagCompound(tag);
						}
						if(stack.getTagCompound().getBoolean("destroyed"))
							return true;

						destroyBlock(stack, world, newPos, player);
					}

					yPos++;
				}

				yPos = pos.getY() - 1;
				nblock = block;
				while(yPos != pos.getY() - 10 && nblock != null && nblock == block)
				{
					newPos = pos.add(0, yPos, 0);
					nblock = world.getBlockState(newPos).getBlock();

					if(block == nblock)
					{
						if(this.getDamage(stack) >= 998 && player.capabilities.isCreativeMode == false)
						{
							NBTTagCompound tag = stack.getTagCompound();
							tag.setBoolean("destroyed", true);
							stack.setTagCompound(tag);
						}
						if(stack.getTagCompound().getBoolean("destroyed"))
							return true;

						destroyBlock(stack, world, newPos, player);
					}

					yPos--;
				}

				return false;
			}
		}

		destroyBlock(stack, world, pos, player);

		return false;
	}

	private void destroyBlock(ItemStack stack, World world, BlockPos pos, EntityPlayer player)
	{
		IBlockState blockState = world.getBlockState(pos);

		if(blockState != null)
		{
			Block block = blockState.getBlock();

			int hlvl = block.getHarvestLevel(blockState);

			String tool = block.getHarvestTool(blockState);

			if(hlvl <= 3 && (tool == null || tool.equals("shovel")))
			{
				if(player.canPlayerEdit(pos, EnumFacing.DOWN, stack))
					if(!player.capabilities.isCreativeMode)
					{
						if(stack.getTagCompound().getBoolean("nodrops"))
						{
							this.onBlockDestroyed(stack, world, block, pos, player);

							if(!world.isRemote)
								world.setBlockToAir(pos);

							return;
						}
						else if(stack.getTagCompound().getBoolean("silk-touch"))
						{
							ItemStack result = new ItemStack(block, 1, block.getMetaFromState(blockState));

							this.onBlockDestroyed(stack, world, block, pos, player);
							ToolsHelper.spawnStackInWorld(world, pos, result);
						}
						else if(stack.getTagCompound().getBoolean("auto-smelt"))
						{
							ItemStack smeltStack = new ItemStack(block.getItemDropped(blockState, random, 0),
								block.quantityDropped(blockState, 0, random), block.damageDropped(blockState));

							if(smeltStack.getItem() != null)
							{
								ItemStack result = FurnaceRecipes.instance().getSmeltingResult(smeltStack);

								if(result != null)
								{
									result.stackSize = block.quantityDropped(blockState, 0, random);

									this.onBlockDestroyed(stack, world, block, pos, player);
									ToolsHelper.spawnStackInWorld(world, pos, result);
								}
							}
						}
						else if(stack.getTagCompound().getByte("fortune") == 1)
						{
							ItemStack result = new ItemStack(block.getItemDropped(blockState, random, 1),
								block.quantityDropped(blockState, 1, random), block.damageDropped(blockState));

							this.onBlockDestroyed(stack, world, block, pos, player);
							ToolsHelper.spawnStackInWorld(world, pos, result);
						}
						else if(stack.getTagCompound().getBoolean("widerRadius") || stack.getTagCompound().getBoolean("capitator"))
						{
							ItemStack result = new ItemStack(block.getItemDropped(blockState, random, 0),
								block.quantityDropped(blockState, 0, random), block.damageDropped(blockState));

							if(EnchantmentHelper.getEnchantmentLevel(33, stack) > 0)
								result = new ItemStack(block, 1, block.getMetaFromState(blockState));

							byte fortune = (byte) EnchantmentHelper.getEnchantmentLevel(35, stack);

							if(fortune > 0)
								result = new ItemStack(block.getItemDropped(blockState, random, fortune),
									block.quantityDropped(blockState, fortune, random), block.damageDropped(blockState));

							this.onBlockDestroyed(stack, world, block, pos, player);
							ToolsHelper.spawnStackInWorld(world, pos, result);
						}
					}
					else
						world.setBlockToAir(pos);
			}
		}
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, BlockPos pos, EntityLivingBase entity)
	{
		if(block != null && (double) block.getBlockHardness(world, pos) != 0.0D)
		{
			stack.damageItem(1, entity);
		}
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(!world.isRemote)
			if(player.isSneaking())
				player.openGui(GeoActivity.instance, GuiIDs.ADV, world, (int) player.posX, (int) player.posY,
					(int) player.posZ);
			else
			{
				AdvTInventory inv = new AdvTInventory(player.inventory.getCurrentItem(), player);
				inv.setCharge();
			}
		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return stack;
	}

	@Override
	public boolean canHarvestBlock(Block block, ItemStack stack)
	{
		return this.getToolSpeed(stack, block.getDefaultState()) > 1.0F;
	}

	@Override
	public float getDigSpeed(ItemStack stack, IBlockState state)
	{
		return this.getToolSpeed(stack, state);
	}

	@Override
	public float getStrVsBlock(ItemStack stack, Block block)
	{
		return this.getToolSpeed(stack, block.getDefaultState());
	}

	private float getToolSpeed(ItemStack stack, IBlockState state)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		float eff = this.efficiencyOnProperMaterial;

		if(stack.getTagCompound().getBoolean("widerRadius") || stack.getTagCompound().getBoolean("capitator"))
			eff -= 8.0F;
		else if(stack.hasTagCompound() && stack.getTagCompound().getByte("speed") == 1)
			eff += 2.0F;

		Block block = state.getBlock();

		if(block.getHarvestLevel(state) <= this.getToolMaterial().getHarvestLevel()
			&& block.isToolEffective("shovel", state))
			return eff;

		Material mat = block.getMaterial();
		for(Material m : GAMod.shovelMaterials)
			if(m == mat)
				return eff;

		return 1.0F;
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack stack)
	{
		return HashMultimap.create();
	}
	
	@Override
	public GuiContainer getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		AdvTInventory inv = new AdvTInventory(player.getHeldItem(), player);
		return new AdvTGUI(inv, player);
	}
	
	@Override
	public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		AdvTInventory inv = new AdvTInventory(player.getHeldItem(), player);
		return new AdvTContainer(inv, player);
	}
}
