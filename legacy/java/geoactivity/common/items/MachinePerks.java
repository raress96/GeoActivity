package geoactivity.common.items;

import java.util.List;

import geoactivity.common.util.GeneralHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MachinePerks extends BaseItem
{
	public MachinePerks(String name)
	{
		super(GeneralHelper.appendStringToArray(GeneralHelper.getEnumStrings(EnumMachinePerks.class), name + "_"));
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean bool)
	{
		if(stack.getMetadata() >= EnumMachinePerks.values().length)
			return;

		if(!GeneralHelper.isShiftKeyDown())
		{
			list.add(GeneralHelper.shiftForInfo);
			return;
		}

		switch(EnumMachinePerks.values()[stack.getMetadata()])
		{
			case SPEED:
				list.add("\u00A7cMachines run faster.");
			break;
			case EFFICIENCY:
				list.add("\u00A7cMachines use less fuel.");
			break;
			default:
				list.add("\u00A7aJust a blank machine perk");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list)
	{
		for(int i = 0;i < EnumMachinePerks.values().length;i++)
		{
			list.add(new ItemStack(item, 1, i));
		}
	}

	public static enum EnumMachinePerks
	{
		SPEED(0, "speed"),
		EFFICIENCY(1, "efficiency");

		int meta;
		String name;

		private EnumMachinePerks(int meta, String name)
		{
			this.meta = meta;
			this.name = name;
		}

		public int getMetadata()
		{
			return this.meta;
		}

		@Override
		public String toString()
		{
			return this.name;
		}
	}
}
