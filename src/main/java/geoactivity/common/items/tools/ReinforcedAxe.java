package geoactivity.common.items.tools;

import net.minecraft.item.ItemAxe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import geoactivity.common.GAMod;
import geoactivity.common.GeoActivity;
import geoactivity.common.lib.IHasName;

public class ReinforcedAxe extends ItemAxe implements IHasName
{
	private String name;

	public ReinforcedAxe(String name)
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
