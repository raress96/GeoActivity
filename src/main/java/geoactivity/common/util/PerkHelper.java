package geoactivity.common.util;

import java.util.ArrayList;
import java.util.List;

import geoactivity.common.GAMod;
import geoactivity.common.items.ArmorPerks.EnumArmorPerks;
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

	public static List<ItemStack> getCommonArmorPerks()
	{
		List<ItemStack> perks = new ArrayList<ItemStack>();

		perks.add(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.GENERAL.getMetadata()));
		perks.add(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.BLAST.getMetadata()));
		perks.add(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.FIRE.getMetadata()));
		perks.add(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.MAGIC.getMetadata()));
		perks.add(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.PROJECTILE.getMetadata()));

		return perks;
	}

	public static List<ItemStack> getAdvancedHelmetPerks()
	{
		List<ItemStack> perks = getCommonArmorPerks();

		perks.add(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.RESPIRATION.getMetadata()));

		return perks;
	}

	public static List<ItemStack> getAdvancedChestplatePerks()
	{
		List<ItemStack> perks = getCommonArmorPerks();

		return perks;
	}

	public static List<ItemStack> getAdvancedPantsPerks()
	{
		List<ItemStack> perks = getCommonArmorPerks();

		perks.add(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.NOFALL.getMetadata()));

		return perks;
	}

	public static List<ItemStack> getAdvancedBootsPerks()
	{
		List<ItemStack> perks = getCommonArmorPerks();

		perks.add(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.NOFALL.getMetadata()));

		return perks;
	}
}
