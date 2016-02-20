package geoactivity.common.util;

import java.util.ArrayList;
import java.util.List;

import geoactivity.common.GAMod;
import geoactivity.common.items.ToolPerks.EnumToolPerks;
import net.minecraft.item.ItemStack;

public class PerkHelper
{	
	public static List<ItemStack> getCommonToolPerks()
	{
		ArrayList<ItemStack> perks = new ArrayList<ItemStack>();
		
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.EFFICIENCY.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.DAMAGE.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.BYPASSARMOR.getMetadata()));

		return perks;
	}
	
	public static List<ItemStack> getAdvancedPickaxePerks()
	{
		List<ItemStack> perks = getCommonToolPerks();
		
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.SPEED.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.EXPERIENCE.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.SMELTING.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.FORTUNE.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.SILKTOUCH.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.RADIUS.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.CAPITATOR.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.NODROPS.getMetadata()));

		return perks;
	}
	
	public static List<ItemStack> getAdvancedShovelPerks()
	{
		List<ItemStack> perks = getCommonToolPerks();
		
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.SPEED.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.SMELTING.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.FORTUNE.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.SILKTOUCH.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.RADIUS.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.CAPITATOR.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.NODROPS.getMetadata()));
		
		return perks;
	}
	
	public static List<ItemStack> getAdvancedAxePerks()
	{
		List<ItemStack> perks = getCommonToolPerks();
		
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.SPEED.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.SMELTING.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.SILKTOUCH.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.CAPITATOR.getMetadata()));
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.NODROPS.getMetadata()));
		
		return perks;
	}
	
	public static List<ItemStack> getAdvancedSwordPerks()
	{
		List<ItemStack> perks = getCommonToolPerks();
		
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.EXPERIENCE.getMetadata()));
		
		return perks;
	}
	
	public static List<ItemStack> getAllToolPerks()
	{
		List<ItemStack> perks = getAdvancedPickaxePerks();
		
		perks.add(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.DROPTP.getMetadata()));
		
		return perks;
	}
}
