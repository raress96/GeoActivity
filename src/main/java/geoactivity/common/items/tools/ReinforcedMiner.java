package geoactivity.common.items.tools;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import geoactivity.common.GAMod;
import geoactivity.common.GeoActivity;
import geoactivity.common.items.tools.RMLogic.RMContainer;
import geoactivity.common.items.tools.RMLogic.RMGUI;
import geoactivity.common.items.tools.RMLogic.RMInventory;
import geoactivity.common.lib.IHasName;
import geoactivity.common.lib.IOpenableGUI;
import geoactivity.common.lib.ToolsHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ReinforcedMiner extends ItemTool implements IHasName, IOpenableGUI
{
	private String name;

	public ReinforcedMiner(String name)
	{
		super(2, GAMod.ReinforcedMaterial, null);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setCreativeTab(GeoActivity.tabMain);
		this.setNoRepair();
		GameRegistry.registerItem(this, name);
		this.setHarvestLevel("pickaxe", GAMod.ReinforcedMaterial.getHarvestLevel());
		this.setHarvestLevel("shovel", GAMod.ReinforcedMaterial.getHarvestLevel());
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
	public void onCreated(ItemStack stack, World world, EntityPlayer player)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = stack.getTagCompound();
		tag.setBoolean("destroyed", true);
		stack.setTagCompound(tag);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean bool)
	{
		if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("destroyed"))
			list.add("\u00A7cDestroyed");
		list.add("");
		list.add("\u00A79+4 Attack Damage");
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase entityBase)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		EntityPlayer player = (EntityPlayer) entityBase;
		if(this.getDamage(stack) >= 498 && player.capabilities.isCreativeMode == false)
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
		if(this.getDamage(stack) >= 498 && player.capabilities.isCreativeMode == false)
		{
			NBTTagCompound tag = stack.getTagCompound();
			tag.setBoolean("destroyed", true);
			stack.setTagCompound(tag);
		}
		if(stack.getTagCompound().getBoolean("destroyed"))
			return false;
		return ToolsHelper.onLeftClickEntity(stack, player, entity, 4);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
	{
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		if(this.getDamage(stack) >= 498 && player.capabilities.isCreativeMode == false)
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
				player.openGui(GeoActivity.instance, 0, world, (int) player.posX, (int) player.posY,
					(int) player.posZ);
			else
			{
				RMInventory inv = new RMInventory(player.inventory.getCurrentItem(), player);
				inv.setCharge();
			}
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
		Block block = state.getBlock();

		if(block.getHarvestLevel(state) <= this.getToolMaterial().getHarvestLevel()
			&& (block.isToolEffective("pickaxe", state)
			|| block.isToolEffective("shovel", state)))
			return this.efficiencyOnProperMaterial;

		Material mat = block.getMaterial();
		for(Material m : GAMod.minerMaterials)
			if(m == mat)
				return this.efficiencyOnProperMaterial;

		return 1.0F;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(ItemStack stack)
	{
		return HashMultimap.create();
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		RMInventory inv = new RMInventory(player.getHeldItem(), player);
		return new RMGUI(inv, player);
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		RMInventory inv = new RMInventory(player.getHeldItem(), player);
		return new RMContainer(inv, player);
	}
}
