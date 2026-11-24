package geoactivity.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CarbonOreCutter extends BaseItem
{
	public CarbonOreCutter(String name)
	{
		super(name);
		this.setMaxStackSize(16);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean bool)
	{
		list.add("\u00A77Used in the Advanced Ore Refiner to increase yield.");
	}
}
