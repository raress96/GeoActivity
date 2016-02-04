package com.entirecraft.general.itemblocks;

import java.util.List;

import com.entirecraft.general.GAMod;
import com.entirecraft.general.blocks.BaseBlock;
import com.entirecraft.general.blocks.HardenedBrick.EnumHardenedBrick;
import com.entirecraft.general.items.ToolPerks.EnumToolPerks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BrickItemBlock extends ItemBlock
{
	public BrickItemBlock(Block block)
	{
		super(block);
		this.setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata (int meta)
	{
		return meta;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		int meta = stack.getMetadata();
		if(meta < EnumHardenedBrick.values().length)
			return ((BaseBlock)GAMod.hardenedbrick).getName(meta);
		return ((BaseBlock)GAMod.hardenedbrick).getName();
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
	{
		if(stack.getMetadata() >= EnumToolPerks.values().length)
			return ;

		switch(EnumHardenedBrick.values()[stack.getMetadata()])
		{
			case ADVANCED:
				list.add("\u00A77Used for more advanced machines.");
			break;
			case THERMAL:
				list.add("\u00A77Used to power the Thermic Generator.");
				list.add("\u00A7cNeeds to see the sky!");
			break;
			default:
			break;
		}
	}
}
