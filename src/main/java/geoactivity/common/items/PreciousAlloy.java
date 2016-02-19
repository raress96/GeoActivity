package geoactivity.common.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import geoactivity.common.lib.Reference;
import geoactivity.common.util.GeneralHelper;

public class PreciousAlloy extends BaseItem
{
	public static final String[] alloy_names = {"small", "normal", "large"};

	public PreciousAlloy(String name)
	{
		super(GeneralHelper.appendStringToArray(alloy_names, name + "_"));
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List list)
	{
		for(int i = 0;i < 3;i++)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack par1)
	{
		switch(par1.getMetadata())
		{
			case 0:
				return EnumRarity.UNCOMMON;
			case 1:
				return EnumRarity.RARE;
			case 2:
				return EnumRarity.EPIC;
			default:
				return EnumRarity.COMMON;
		}
	}
}
