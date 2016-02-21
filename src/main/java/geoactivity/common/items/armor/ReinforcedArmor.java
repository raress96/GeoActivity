package geoactivity.common.items.armor;

import geoactivity.common.GAMod;
import geoactivity.common.GeoActivity;
import geoactivity.common.lib.IHasName;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ReinforcedArmor extends ItemArmor implements IHasName
{
	private String name;

	public ReinforcedArmor(String name, int renderIndex, int type)
	{
		super(GAMod.ReinforcedArmorMaterial, renderIndex, type);
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
}
