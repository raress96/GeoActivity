package com.entirecraft.general;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.entirecraft.core.GeoWorld;
import com.entirecraft.general.blocks.BaseBlock;
import com.entirecraft.general.blocks.CustomCoalOres;
import com.entirecraft.general.blocks.HardenedBrick;
import com.entirecraft.general.blocks.TransparentBlock;
import com.entirecraft.general.blocks.Machines.AdvancedCoalRefiner;
import com.entirecraft.general.blocks.Machines.AdvancedOreRefiner;
import com.entirecraft.general.blocks.Machines.AtomExtractor;
import com.entirecraft.general.blocks.Machines.ChemistryMachine;
import com.entirecraft.general.blocks.Machines.CoalRefiner;
import com.entirecraft.general.blocks.Machines.CraftingMachine;
import com.entirecraft.general.blocks.Machines.GeothermalGenerator;
import com.entirecraft.general.blocks.Machines.ThermicGenerator;
import com.entirecraft.general.blocks.Machines.ThermicMelter;
import com.entirecraft.general.blocks.Machines.ToolCharger;
import com.entirecraft.general.itemblocks.MultiItemBlock;
import com.entirecraft.general.items.ArmorPerks;
import com.entirecraft.general.items.AutoStoneBuilder;
import com.entirecraft.general.items.BaseItem;
import com.entirecraft.general.items.CarbonOreCutter;
import com.entirecraft.general.items.CustomCoalItems;
import com.entirecraft.general.items.ElementContainer;
import com.entirecraft.general.items.MachinePerks;
import com.entirecraft.general.items.PreciousAlloy;
import com.entirecraft.general.items.ToolPerks;
import com.entirecraft.general.items.armor.AdvancedArmor;
import com.entirecraft.general.items.armor.ReinforcedArmor;
import com.entirecraft.general.items.tools.ReinforcedAxe;
import com.entirecraft.general.items.tools.ReinforcedHoe;
import com.entirecraft.general.items.tools.ReinforcedMiner;
import com.entirecraft.general.items.tools.ReinforcedPickaxe;
import com.entirecraft.general.items.tools.ReinforcedShovel;
import com.entirecraft.general.items.tools.ReinforcedSword;
import com.entirecraft.general.items.tools.Adv.AdvAxe;
import com.entirecraft.general.items.tools.Adv.AdvPick;
import com.entirecraft.general.items.tools.Adv.AdvShovel;
import com.entirecraft.general.items.tools.Adv.AdvSword;
import com.entirecraft.general.items.tools.Red.ARedstoneBattleMiner;
import com.entirecraft.general.items.tools.Red.RedstoneBattleAxe;
import com.entirecraft.general.items.tools.Red.RedstoneBattleMiner;
import com.entirecraft.general.items.tools.Red.RedstoneMiner;
import com.entirecraft.lib.IHasName;
import com.entirecraft.lib.Reference;

public class GAPreInit 
{
	public static void preInit(FMLPreInitializationEvent event)
	{				
		registerItems();
		registerBlocks();
		
		if (event.getSide() == Side.CLIENT)
			registerMetadataTextures();
		
		GameRegistry.registerWorldGenerator(new GeoWorld(), 2);
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
