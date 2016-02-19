package geoactivity.common.items.tools;

import net.minecraft.item.ItemHoe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import geoactivity.common.GAMod;
import geoactivity.common.GeoActivity;
import geoactivity.common.lib.IHasName;

public class ReinforcedHoe extends ItemHoe implements IHasName
{
	private String name;

	public ReinforcedHoe(String name)
	{
		super(GAMod.ReinforcedMaterial);
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
}
