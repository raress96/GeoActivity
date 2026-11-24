package geoactivity.common;

import geoactivity.client.GUIHandler;
import geoactivity.common.lib.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class GeoActivity
{
	// Variables
	private static GUIHandler guiHandler = new GUIHandler();

	// Initialization
	@Instance(Reference.MOD_ID)
	public static GeoActivity instance;

	@SidedProxy(clientSide = "geoactivity.client.ClientProxy", serverSide = "geoactivity.common.CommonProxy")
	public static CommonProxy proxy;

	public static GAConfig config;

	@EventHandler
	public void PreInit(FMLPreInitializationEvent event)
	{
		config = new GAConfig(event);
		GAPreInit.preInit(event);

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
	}

	@EventHandler
	public void Init(FMLInitializationEvent event)
	{
		GAInit.init(event);
	}

	@EventHandler
	public void PostInit(FMLPostInitializationEvent event)
	{

	}

	public static CreativeTabs tabMain = new CreativeTabs(Reference.MOD_ID)
	{
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem()
		{
			return GAMod.gemLigniteCoal;
		}
	};

	public static CreativeTabs tabElement = new CreativeTabs(Reference.MOD_ID + "element")
	{
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem()
		{
			return GAMod.elementContainer;
		}
	};
}
