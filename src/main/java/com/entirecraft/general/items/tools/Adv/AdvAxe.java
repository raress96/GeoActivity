package com.entirecraft.general.items.tools.Adv;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.GeoActivity;
import com.entirecraft.general.items.tools.Adv.Logic.AdvTInventory;
import com.entirecraft.lib.GuiIDs;
import com.entirecraft.lib.IHasName;
import com.entirecraft.lib.ToolsHelper;
import com.entirecraft.util.BaseTool;
import com.entirecraft.util.GeneralHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class AdvAxe extends ItemTool implements IHasName
{
	private Random random = new Random();
	private String name;

	public AdvAxe(String name)
	{
		super(0, GAMod.AdvancedMaterial, null);
		this.setNoRepair();
		this.setHarvestLevel("axe", GAMod.AdvancedMaterial.getHarvestLevel());
		this.name = name;
		this.setUnlocalizedName(name);
		this.setCreativeTab(GeoActivity.tabMain);
		GameRegistry.registerItem(this, name);
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public String getName(int meta)
	{
		return this.getName();
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
	{
		return false;
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
			else if(stack.getTagCompound().getBoolean("silk-touch"))
				list.add("\u00A77Has Silk-Touch enchant!");
			else if(stack.getTagCompound().getBoolean("capitator"))
				list.add("\u00A77Destroys trees very quickly!");
			else if(stack.getTagCompound().getBoolean("nodrops"))
				list.add("\u00A77Gives no block drops.");
			else if(stack.getTagCompound().getBoolean("barmor"))
				list.add("\u00A77Deals damage that bypasses armor.");
		}
		list.add("");

		BaseTool.addDamageInfo(stack, 7, list);
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

		return ToolsHelper.onLeftClickEntity(stack, player, entity, 7);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
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
			return true;

		World world = player.worldObj;

		if(stack.getTagCompound().getBoolean("capitator"))
		{
			Block block = world.getBlockState(pos).getBlock();
			if(block != null && block.isWood(world, pos))
			{
				breakTree(world, stack, block, pos, player);
				return false;
			}
		}

		destroyBlock(stack, world, pos, player);

		return false;
	}

	private void breakTree(World world, ItemStack stack, Block block, BlockPos pos, EntityPlayer player)
	{
		for(int xPos = pos.getX() - 1;xPos <= pos.getX() + 1;xPos++)
			for(int yPos = pos.getY() - 1;yPos <= pos.getY() + 1;yPos++)
				for(int zPos = pos.getZ() - 1;zPos <= pos.getZ() + 1;zPos++)
				{
					BlockPos newPos = new BlockPos(xPos, yPos, zPos);
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
							return;

						destroyBlock(stack, world, newPos, player);
						breakTree(world, stack, block, newPos, player);
					}
				}
	}

	private void destroyBlock(ItemStack stack, World world, BlockPos pos, EntityPlayer player)
	{
		IBlockState blockState = world.getBlockState(pos);

		if(blockState != null)
		{
			Block block = blockState.getBlock();

			int hlvl = block.getHarvestLevel(blockState);

			String tool = block.getHarvestTool(blockState);

			if(hlvl <= 3 && (tool == null || tool.equals("axe")))
			{
				if(player.canPlayerEdit(pos, EnumFacing.DOWN, stack))
					if(!player.capabilities.isCreativeMode)
					{
						if(stack.getTagCompound().getBoolean("nodrops"))
						{
							this.onBlockDestroyed(stack, world, block, pos, player);

							if(!world.isRemote)
								world.setBlockToAir(pos);
						}
						else if(stack.getTagCompound().getBoolean("silk-touch"))
						{
							ItemStack result = new ItemStack(block, 1, block.getMetaFromState(blockState));

							this.onBlockDestroyed(stack, world, block, pos, player);
							ToolsHelper.spawnStackInWorld(world, pos, result);
						}
						else if(stack.getTagCompound().getBoolean("auto-smelt"))
						{
							ItemStack smeltStack =
								new ItemStack(block.getItemDropped(blockState, random, 0), block.quantityDropped(
									blockState, 0, random), block.damageDropped(blockState));

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
						else if(stack.getTagCompound().getBoolean("capitator"))
						{
							ItemStack result =
								new ItemStack(block.getItemDropped(blockState, random, 0), block.quantityDropped(
									blockState, 0, random), block.damageDropped(blockState));

							if(EnchantmentHelper.getEnchantmentLevel(33, stack) > 0)
								result = new ItemStack(block, 1, block.getMetaFromState(blockState));

							byte fortune = (byte) EnchantmentHelper.getEnchantmentLevel(35, stack);

							if(fortune > 0)
								result =
									new ItemStack(block.getItemDropped(blockState, random, fortune),
										block.quantityDropped(blockState, fortune, random),
										block.damageDropped(blockState));

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
				player.openGui(GeoActivity.instance, GuiIDs.ADV, world, (int) player.posX,
					(int) player.posY, (int) player.posZ);
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

		if(stack.getTagCompound().getBoolean("capitator"))
			eff -= 8.0F;
		else if(stack.hasTagCompound() && stack.getTagCompound().getByte("speed") == 1)
			eff += 2.0F;

		Block block = state.getBlock();

		if(block.getHarvestLevel(state) <= this.getToolMaterial().getHarvestLevel()
			&& block.isToolEffective("axe", state))
			return eff;

		Material mat = block.getMaterial();
		for(Material m : GAMod.axeMaterials)
			if(m == mat)
				return eff;

		return 1.0F;
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack stack)
	{
		return HashMultimap.create();
	}
}
