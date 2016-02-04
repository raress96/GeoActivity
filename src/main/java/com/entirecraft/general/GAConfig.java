package com.entirecraft.general;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class GAConfig 
{
	public int ligniteBlocks;
	public int ligniteMinY;
	public int ligniteMaxY;
	public int ligniteSize;
	
	public int bituminousBlocks;
	public int bituminousMinY;
	public int bituminousMaxY;
	public int bituminousSize;
	
	public int anthraciteBlocks;
	public int anthraciteMinY;
	public int anthraciteMaxY;
	public int anthraciteSize;
	
	public GAConfig(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory()+"/GeoActivity", "geoactivity.cfg"));
		config.load();
		
		oreConfig(config, "Ore Generation.");
		
		config.save();
	}
	
	private void oreConfig(Configuration config, String baseCategory)
	{
		//Lignite Ore
		ligniteBlocks = config.get(baseCategory + "Lignite Ore", "blocks/chunk", 14).getInt(14);
		ligniteMinY = config.get(baseCategory + "Lignite Ore", "minY", 46).getInt(46);
		ligniteMaxY = config.get(baseCategory + "Lignite Ore", "maxY", 64).getInt(64);
		ligniteSize = config.get(baseCategory + "Lignite Ore", "max vane size", 3).getInt(3);
		
		//Bituminous Ore
		bituminousBlocks = config.get(baseCategory + "Bituminous Ore", "blocks/chunk", 14).getInt(14);
		bituminousMinY = config.get(baseCategory + "Bituminous Ore", "minY", 34).getInt(34);
		bituminousMaxY = config.get(baseCategory + "Bituminous Ore", "maxY", 48).getInt(48);
		bituminousSize = config.get(baseCategory + "Bituminous Ore", "max vane size", 3).getInt(3);
		
		//Anthracite Ore
		anthraciteBlocks = config.get(baseCategory + "Anthracite Ore", "blocks/chunk", 14).getInt(14);
		anthraciteMinY = config.get(baseCategory + "Anthracite Ore", "minY", 18).getInt(18);
		anthraciteMaxY = config.get(baseCategory + "Anthracite Ore", "maxY", 38).getInt(38);
		anthraciteSize = config.get(baseCategory + "Anthracite Ore", "max vane size", 3).getInt(3);
	}
}
