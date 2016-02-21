package geoactivity.common.items;

import java.util.List;

import geoactivity.common.GeoActivity;
import geoactivity.common.util.GeneralHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElementContainer extends BaseItem
{
	public ElementContainer(String name)
	{
		super(GeneralHelper.appendStringToArray(GeneralHelper.getEnumStrings(EnumElements.class), name + "_"));
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(32);
		this.setCreativeTab(GeoActivity.tabElement);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list)
	{
		for(int i = 0;i < EnumElements.values().length;i++)
			list.add(new ItemStack(item, 1, i));
	}

	public static enum EnumElements
	{
		EMPTY(0, "empty"),
		HYDROGEN(1, "hydrogen"),
		HELIUM(2, "helium"),
		LITHIUM(3, "lithium"),
		CARBON(4, "carbon"),
		NITROGEN(5, "nitrogen"),
		OXYGEN(6, "oxygen"),
		NEON(7, "neon"),
		SODIUM(8, "sodium"),
		MAGNESIUM(9, "magnesium"),
		ALUMINIUM(10, "aluminium"),
		SILICON(11, "silicon"),
		PHOSPHORUS(12, "phosphorus"),
		SULFUR(13, "sulfur"),
		CHLORINE(14, "chlorine"),
		POTASSIUM(15, "potassium"),
		CALCIUM(16, "calcium"),
		IRON(17, "iron"),
		IODINE(18, "iodine"),
		GOLD(19, "gold");

		int meta;
		String name;

		private EnumElements(int meta, String name)
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
