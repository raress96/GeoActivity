package geoactivity.common.blocks.Machines;

import geoactivity.common.GeoActivity;
import geoactivity.common.lib.IHasName;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class BaseContainerBlock extends BlockContainer implements IHasName
{
	protected final String name;

	public BaseContainerBlock(Material mat, String name, String tool, int tier)
	{
		super(mat);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setHarvestLevel(tool, tier);
		this.setCreativeTab(GeoActivity.tabMain);
		GameRegistry.registerBlock(this, name);
	}

	public <C extends ItemBlock> BaseContainerBlock(Material mat, String name, String tool, int tier, Class<C> item)
	{
		super(mat);
		this.name = name;
		this.setUnlocalizedName(name);
		this.setHarvestLevel(tool, tier);
		this.setCreativeTab(GeoActivity.tabMain);
		GameRegistry.registerBlock(this, item, name);
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
