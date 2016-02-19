package geoactivity.common;

import geoactivity.common.blocks.CustomCoalOres;
import geoactivity.common.blocks.HardenedBrick;
import geoactivity.common.blocks.TransparentBlock;
import geoactivity.common.blocks.Machines.AdvancedCoalRefiner;
import geoactivity.common.blocks.Machines.AdvancedOreRefiner;
import geoactivity.common.blocks.Machines.AtomExtractor;
import geoactivity.common.blocks.Machines.ChemistryMachine;
import geoactivity.common.blocks.Machines.CoalRefiner;
import geoactivity.common.blocks.Machines.CraftingMachine;
import geoactivity.common.blocks.Machines.GeothermalGenerator;
import geoactivity.common.blocks.Machines.ThermicGenerator;
import geoactivity.common.blocks.Machines.ThermicMelter;
import geoactivity.common.blocks.Machines.ToolCharger;
import geoactivity.common.items.ArmorPerks;
import geoactivity.common.items.AutoStoneBuilder;
import geoactivity.common.items.BaseItem;
import geoactivity.common.items.CarbonOreCutter;
import geoactivity.common.items.CustomCoalItems;
import geoactivity.common.items.ElementContainer;
import geoactivity.common.items.MachinePerks;
import geoactivity.common.items.PreciousAlloy;
import geoactivity.common.items.ToolPerks;
import geoactivity.common.items.armor.AdvancedArmor;
import geoactivity.common.items.armor.ReinforcedArmor;
import geoactivity.common.items.tools.ReinforcedAxe;
import geoactivity.common.items.tools.ReinforcedHoe;
import geoactivity.common.items.tools.ReinforcedMiner;
import geoactivity.common.items.tools.ReinforcedPickaxe;
import geoactivity.common.items.tools.ReinforcedShovel;
import geoactivity.common.items.tools.ReinforcedSword;
import geoactivity.common.items.tools.Adv.AdvAxe;
import geoactivity.common.items.tools.Adv.AdvPick;
import geoactivity.common.items.tools.Adv.AdvShovel;
import geoactivity.common.items.tools.Adv.AdvSword;
import geoactivity.common.items.tools.Red.ARedstoneBattleMiner;
import geoactivity.common.items.tools.Red.RedstoneBattleAxe;
import geoactivity.common.items.tools.Red.RedstoneBattleMiner;
import geoactivity.common.items.tools.Red.RedstoneMiner;
import geoactivity.common.lib.IHasName;
import geoactivity.common.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GAPreInit
{
	public static void preInit(FMLPreInitializationEvent event)
	{
		registerItems();
		registerBlocks();

		if (event.getSide() == Side.CLIENT)
			registerMetadataTextures();

		GameRegistry.registerWorldGenerator(new GAWorldGenerator(), 2);
	}

	private static void registerItems()
	{
		/* Items */
		GAMod.gemLigniteCoal = new CustomCoalItems("lignitecoal");
		GAMod.gemBituminousCoal = new CustomCoalItems("bituminouscoal");
		GAMod.gemAnthraciteCoal = new CustomCoalItems("anthracitecoal");

		GAMod.graphite = new BaseItem("graphitecoal");
		GAMod.carbonfiber = new BaseItem("carbonfiber");
		GAMod.carbonstick = new BaseItem("carbonstick");
		GAMod.carbonOreCutter = new CarbonOreCutter("carbonorecutter");

		GAMod.preciousAlloy = new PreciousAlloy("preciousalloyingot");

		GAMod.tool_perks = new ToolPerks("perk");
		GAMod.machine_perks = new MachinePerks("perk_m");
		GAMod.armor_perks = new ArmorPerks("perk_a");

		GAMod.elementContainer = new ElementContainer("elementcontainer");

		GAMod.autoSBuilder = new AutoStoneBuilder("autostonebuilder");

		/* Tools */

		//Tier I
		GAMod.reinforcedSword = new ReinforcedSword("reinforcedsword");
		GAMod.reinforcedPickaxe = new ReinforcedPickaxe("reinforcedpickaxe");
		GAMod.reinforcedAxe = new ReinforcedAxe("reinforcedaxe");
		GAMod.reinforcedShovel = new ReinforcedShovel("reinforcedshovel");
		GAMod.reinforcedHoe = new ReinforcedHoe("reinforcedhoe");
		GAMod.reinforcedMiner = new ReinforcedMiner("reinforcedminer");

		//Tier II
		GAMod.advancedSword = new AdvSword("advancedsword");
		GAMod.advancedPickaxe = new AdvPick("advancedpick");
		GAMod.advancedAxe = new AdvAxe("advancedaxe");
		GAMod.advancedShovel = new AdvShovel("advancedshovel");

		//Tier III
		GAMod.redstoneMiner = new RedstoneMiner("redstoneminer");
		GAMod.redstoneBattleAxe = new RedstoneBattleAxe("redstonebattleaxe");
		GAMod.redstoneBattleMiner = new RedstoneBattleMiner("redstonebattleminer");
		GAMod.aredstoneBattleMiner = new ARedstoneBattleMiner("aredstonebattleminer");

		/* Armor */

		//Tier I
		GAMod.reinforcedHelmet = new ReinforcedArmor("reinforcedhelmet", 1, 0);
		GAMod.reinforcedChest = new ReinforcedArmor("reinforcedchest", 1, 1);
		GAMod.reinforcedPants = new ReinforcedArmor("reinforcedpants", 2, 2);
		GAMod.reinforcedBoots = new ReinforcedArmor("reinforcedboots", 1, 3);

		//Tier II
		GAMod.advancedHelmet = new AdvancedArmor("advancedhelmet", 1, 0);
		GAMod.advancedChest = new AdvancedArmor("advancedchest", 1, 1);
		GAMod.advancedPants = new AdvancedArmor("advancedpants", 2, 2);
		GAMod.advancedBoots = new AdvancedArmor("advancedboots", 1, 3);
	}

	private static void registerBlocks()
	{
		//Ores
		GAMod.oreLignite = new CustomCoalOres("ligniteore", 2).setHardness(3.0F);
		GAMod.oreBituminous = new CustomCoalOres("bituminousore", 2).setHardness(4.0F);
		GAMod.oreAnthracite = new CustomCoalOres("anthraciteore", 3).setHardness(5.0F);

		//General Blocks
		GAMod.hardenedglass = new TransparentBlock("hardenedglass", Material.glass, 3).setHardness(20.0F).setResistance(130.0F);
		GAMod.graphitebase = new TransparentBlock("graphitebase", Material.rock, 2).setHardness(3.0F).setResistance(15.0F);

		GAMod.hardenedbrick = new HardenedBrick("hardenedbrick");

		/* Machines */

		//Tier I
		GAMod.coalrefiner = new CoalRefiner("coalrefiner");
		GAMod.craftingmachine = new CraftingMachine("craftingmachine");

		//Tier II
		GAMod.atomextractor = new AtomExtractor("atomextractor");
		GAMod.chemistrymachine = new ChemistryMachine("chemistrymachine");
		GAMod.advancedcoalrefiner = new AdvancedCoalRefiner("advancedcoalrefiner");
		GAMod.advancedorerefiner = new AdvancedOreRefiner("advancedorerefiner");

		GAMod.thermicgenerator = new ThermicGenerator("thermicgenerator");
		GAMod.toolcharger = new ToolCharger("toolcharger");
		GAMod.geothermalgenerator = new GeothermalGenerator("geothermalgenerator");
		GAMod.thermicmelter = new ThermicMelter("thermicmelter");

	}

	private static void registerBlock(Block block, String uname, String tool, int tier)
	{
		GameRegistry.registerBlock(block, Reference.MOD_ID + uname);
		block.setHarvestLevel(tool, tier);
	}

	@SideOnly(Side.CLIENT)
	private static void registerMetadataTextures()
	{
		int i;

		for(i = 0;i < PreciousAlloy.alloy_names.length;i++)
			ModelBakery.addVariantName(GAMod.preciousAlloy, Reference.MOD_ID + ":" + ((IHasName)GAMod.preciousAlloy).getName(i));

		for(i = 0;i < ToolPerks.EnumToolPerks.values().length;i++)
			ModelBakery.addVariantName(GAMod.tool_perks, Reference.MOD_ID + ":" + ((IHasName)GAMod.tool_perks).getName(i));

		for(i = 0;i < MachinePerks.EnumMachinePerks.values().length;i++)
			ModelBakery.addVariantName(GAMod.machine_perks, Reference.MOD_ID + ":" + ((IHasName)GAMod.machine_perks).getName(i));

		for(i = 0;i < ArmorPerks.EnumArmorPerks.values().length;i++)
			ModelBakery.addVariantName(GAMod.armor_perks, Reference.MOD_ID + ":" + ((IHasName)GAMod.armor_perks).getName(i));

		for(i = 0;i < ElementContainer.EnumElements.values().length;i++)
			ModelBakery.addVariantName(GAMod.elementContainer, Reference.MOD_ID + ":" + ((IHasName)GAMod.elementContainer).getName(i));

		Item temp = GameRegistry.findItem(Reference.MOD_ID, ((IHasName)GAMod.hardenedbrick).getName());
		for(i = 0;i < HardenedBrick.EnumHardenedBrick.values().length;i++)
			ModelBakery.addVariantName(temp, Reference.MOD_ID + ":" + ((IHasName)GAMod.hardenedbrick).getName(i));
	}
}
