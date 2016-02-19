package geoactivity.common.blocks.Machines;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import geoactivity.common.GeoActivity;
import geoactivity.common.lib.IHasName;

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
	
	@Override
    public int getRenderType()
    {
        return 3;
    }

	@Override
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState();
    }
}
