package geoactivity.common.items;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomCoalItems extends BaseItem
{
	public CustomCoalItems(String coalType)
	{
		super(coalType);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack)
	{
		if(name.equals("anthracitecoal"))
			return EnumRarity.UNCOMMON;
		else
			return EnumRarity.COMMON;
	}
}
