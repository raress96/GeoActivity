package com.entirecraft.general.items.tools.Adv;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
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

public class AdvSword extends ItemSword implements IHasName
{
	private String name;

	public AdvSword(String name)
	{
		super(GAMod.AdvancedMaterial);
		this.setNoRepair();
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

			if(stack.getTagCompound().getByte("efficiency") == 1)
				list.add("\u00A77Uses less fuel!");
			else if(stack.getTagCompound().getBoolean("experience"))
				list.add("\u00A77Gives you more experience.");
			else if(stack.getTagCompound().getBoolean("barmor"))
				list.add("\u00A77Deals damage that bypasses armor.");
		}
		list.add("");

		BaseTool.addDamageInfo(stack, 8, list);
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
		if(stack.getTagCompound().getBoolean("experience"))
			if(!entity.isEntityAlive())
				player.addExperience(1);
		stack.damageItem(1, entityBase);
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
		return ToolsHelper.onLeftClickEntity(stack, player, entity, 8);
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

		return false;
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
	public Multimap getAttributeModifiers(ItemStack stack)
	{
		return HashMultimap.create();
	}
}
