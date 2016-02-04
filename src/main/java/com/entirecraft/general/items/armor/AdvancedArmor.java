package com.entirecraft.general.items.armor;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.GeoActivity;
import com.entirecraft.general.items.armor.AdvLogic.AdvAInventory;
import com.entirecraft.lib.GuiIDs;
import com.entirecraft.lib.IHasName;
import com.entirecraft.lib.Reference;
import com.entirecraft.util.GeneralHelper;

public class AdvancedArmor extends ItemArmor implements ISpecialArmor, IHasName
{
	private String name;

	public AdvancedArmor(String name, int renderIndex, int type)
	{
		super(GAMod.AdvancedArmorMaterial, renderIndex, type);
		setNoRepair();
		this.name = name;
		this.setUnlocalizedName(name);
		this.setCreativeTab(GeoActivity.tabMain);
		GameRegistry.registerItem(this, name);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getName(int meta)
	{
		return this.getName();
	}

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

			if(stack.getTagCompound().getByte("all") > 0)
				list.add("\u00A77Extra protection against all damage!");
			else if(stack.getTagCompound().getByte("blast") > 0)
				list.add("\u00A77Extra protection against explosions!");
			else if(stack.getTagCompound().getByte("fire") > 0)
				list.add("\u00A77Extra protection against fire damage!");
			else if(stack.getTagCompound().getByte("magic") > 0)
				list.add("\u00A77Extra protection against magic damage!");
			else if(stack.getTagCompound().getByte("projectile") > 0)
				list.add("\u00A77Extra protection against projectiles!");

			switch(this.armorType)
			{
				case 0:
					if(stack.getTagCompound().getBoolean("respiration"))
						list.add("\u00A77Breathe underwater.");
				break;
				case 2:
				case 3:
					if(stack.getTagCompound().getBoolean("fall"))
						list.add("\u00A77Take no fall damage.");
			}
		}
	}

	@Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
	{
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(!world.isRemote)
			if(player.isSneaking())
				player.openGui(GeoActivity.instance, GuiIDs.ADVArmor, world, (int) player.posX,
						(int) player.posY, (int) player.posZ);
			else
			{
				AdvAInventory inv = new AdvAInventory(player.inventory.getCurrentItem(), player);
				inv.setCharge();
			}
		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return stack;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		if(stack.getItemDamage() >= stack.getMaxDamage() - 2)
		{
			NBTTagCompound tag = stack.getTagCompound();
			tag.setBoolean("destroyed", true);
			stack.setTagCompound(tag);

			return;
		}

		switch(this.armorType)
		{
			case 0:
				if(stack.getTagCompound().getBoolean("respiration")
						&& stack.getItemDamage() < stack.getMaxDamage() - 16)
					if(player.getAir() == 0)
					{
						player.setAir(150);
						stack.damageItem(16, player);
					}
			break;
			case 2:
			case 3:
				if(stack.getTagCompound().getBoolean("fall"))
				{
					if(player.fallDistance > 0)
						player.fallDistance = 0;

				}
		}
	}

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack stack, DamageSource source,
			double damage, int slot)
	{
		int h = getArmorMaterial().getDamageReductionAmount(0);
		int c = getArmorMaterial().getDamageReductionAmount(1);
		int p = getArmorMaterial().getDamageReductionAmount(2);
		int b = getArmorMaterial().getDamageReductionAmount(3);

		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		if(stack.getTagCompound().getBoolean("destroyed"))
			return new ArmorProperties(0, 1 / damage, 1);

		if(stack.getTagCompound().getByte("all") > 0)
		{
			h += 1;
			c += 2;
			p += 1;
			b += 1;
		}
		else if(stack.getTagCompound().getByte("blast") > 0 && source.isExplosion())
		{
			h += 2;
			c += 3;
			p += 2;
			b += 2;
		}
		else if(stack.getTagCompound().getByte("fire") > 0 && source.isFireDamage())
		{
			h += 2;
			c += 3;
			p += 2;
			b += 2;
		}
		else if(stack.getTagCompound().getByte("magic") > 0 && source.isMagicDamage())
		{
			h += 2;
			c += 3;
			p += 2;
			b += 2;
		}
		else if(stack.getTagCompound().getByte("projectile") > 0 && source.isProjectile())
		{
			h += 2;
			c += 3;
			p += 2;
			b += 2;
		}

		switch(slot)
		{
			case 3:
				return new ArmorProperties(0, h / damage, getArmorMaterial().getDamageReductionAmount(0));
			case 2:
				return new ArmorProperties(1, c / damage, getArmorMaterial().getDamageReductionAmount(1));
			case 1:
				return new ArmorProperties(1, p / damage, getArmorMaterial().getDamageReductionAmount(2));
			case 0:
				return new ArmorProperties(0, b / damage, getArmorMaterial().getDamageReductionAmount(3));
			default:
				return null;
		}
	}

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack stack, int slot)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		if(stack.getTagCompound().getBoolean("destroyed"))
			return 1;

		switch(slot)
		{
			case 3:
				return 3;
			case 2:
				return 8;
			case 1:
				return 6;
			case 0:
				return 3;
			default:
				return 0;
		}
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		if(stack.getItemDamage() >= stack.getMaxDamage() - 2)
		{
			NBTTagCompound tag = stack.getTagCompound();
			tag.setBoolean("destroyed", true);
			stack.setTagCompound(tag);

			return;
		}

		if(stack.getTagCompound().getByte("all") > 0)
			stack.damageItem(1, entity);
		else if(stack.getTagCompound().getByte("blast") > 0 && source.isExplosion())
			stack.damageItem(1, entity);
		else if(stack.getTagCompound().getByte("fire") > 0 && source.isFireDamage())
			stack.damageItem(1, entity);
		else if(stack.getTagCompound().getByte("magic") > 0 && source.isMagicDamage())
			stack.damageItem(1, entity);
		else if(stack.getTagCompound().getByte("projectile") > 0 && source.isProjectile())
			stack.damageItem(1, entity);
		else if(!source.isUnblockable())
			stack.damageItem(2, entity);
	}
}
