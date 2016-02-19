package geoactivity.common.blocks.Machines.CrM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import geoactivity.common.GAMod;
import geoactivity.common.items.ArmorPerks.EnumArmorPerks;
import geoactivity.common.items.MachinePerks.EnumMachinePerks;
import geoactivity.common.items.ToolPerks.EnumToolPerks;
import geoactivity.common.util.BaseElectricTool;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class CrMCrafting
{
	public static final CrMCrafting instance = new CrMCrafting();
	private List<IRecipe> recipes = new ArrayList<IRecipe>();

	public static final CrMCrafting getInstance()
	{
		return instance;
	}

	public CrMCrafting()
	{
		/* Perks */

		addRecipe(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.BLANK.getMetadata()), new Object[] {" Z ", "XSX",
				" Y ", 'X', Items.redstone, 'S', GAMod.carbonfiber, 'Z', Items.glowstone_dust, 'Y', Items.quartz});

		//Tool Perks
		addShapelessRecipe(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.SPEED.getMetadata()), new Object[] {
				GAMod.tool_perks, Items.ender_pearl, Items.gold_ingot});
		addShapelessRecipe(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.EFFICIENCY.getMetadata()), new Object[] {
				GAMod.tool_perks, Items.magma_cream, Items.iron_ingot, Items.iron_ingot});
		addShapelessRecipe(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.DAMAGE.getMetadata()), new Object[] {
				GAMod.tool_perks, Blocks.redstone_block, Blocks.quartz_block});
		addShapelessRecipe(new ItemStack(GAMod.tool_perks, 1, EnumToolPerks.BYPASSARMOR.getMetadata()), new Object[] {
				GAMod.tool_perks, Items.diamond, Items.diamond, Items.blaze_rod});

		//Machine Perks
		addShapelessRecipe(new ItemStack(GAMod.machine_perks, 1, EnumMachinePerks.SPEED.getMetadata()), new Object[] {
				new ItemStack(GAMod.tool_perks, 1, 0), Items.ender_pearl, Items.gold_ingot, Items.gold_ingot,
				Items.gold_ingot, Items.gold_ingot});
		addShapelessRecipe(new ItemStack(GAMod.machine_perks, 1, EnumMachinePerks.EFFICIENCY.getMetadata()),
				new Object[] {new ItemStack(GAMod.tool_perks, 1, 0), Items.magma_cream, Blocks.iron_block});

		//Armor Perks
		addShapelessRecipe(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.GENERAL.getMetadata()), new Object[] {
				GAMod.tool_perks, Blocks.obsidian, Items.diamond});
		addShapelessRecipe(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.BLAST.getMetadata()), new Object[] {
				GAMod.tool_perks, Blocks.obsidian, Blocks.obsidian, Items.gold_ingot, Items.gold_ingot});
		addShapelessRecipe(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.FIRE.getMetadata()), new Object[] {
				GAMod.tool_perks, Blocks.netherrack, Items.fire_charge, Items.blaze_rod});
		addShapelessRecipe(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.MAGIC.getMetadata()), new Object[] {
				GAMod.tool_perks, Items.ghast_tear, Items.magma_cream, Items.fermented_spider_eye});
		addShapelessRecipe(new ItemStack(GAMod.armor_perks, 1, EnumArmorPerks.PROJECTILE.getMetadata()), new Object[] {
				GAMod.tool_perks, Blocks.brick_block, Blocks.brick_block, Items.gold_ingot, Items.gold_ingot});

		/* Tools */

		//Tier II
		addRecipe(new ItemStack(GAMod.advancedSword, 1, 998), new Object[] {" Y ", " Y ", " X ", 'X',
				GAMod.carbonstick, 'Y', new ItemStack(GAMod.preciousAlloy, 1, 0)});
		addRecipe(new ItemStack(GAMod.advancedPickaxe, 1, 998), new Object[] {"YYY", " X ", " X ", 'X',
				GAMod.carbonstick, 'Y', new ItemStack(GAMod.preciousAlloy, 1, 0)});
		addRecipe(new ItemStack(GAMod.advancedAxe, 1, 998), new Object[] {"YY ", "YX ", " X ", 'X', GAMod.carbonstick,
				'Y', new ItemStack(GAMod.preciousAlloy, 1, 0)});
		addRecipe(new ItemStack(GAMod.advancedShovel, 1, 998), new Object[] {" Y ", " X ", " X ", 'X',
				GAMod.carbonstick, 'Y', new ItemStack(GAMod.preciousAlloy, 1, 0)});

		//Tier III
		addRecipe(new ItemStack(GAMod.redstoneMiner, 1, 998), new Object[] {"RYR", " X ", " X ", 'X',
				GAMod.carbonstick, 'Y', new ItemStack(GAMod.preciousAlloy, 1, 1), 'R', Blocks.redstone_block});
		addRecipe(new ItemStack(GAMod.redstoneBattleAxe, 1, 998), new Object[] {"RYR", "RXR", " X ", 'X',
				GAMod.carbonstick, 'Y', new ItemStack(GAMod.preciousAlloy, 1, 1), 'R', Blocks.redstone_block});
		addRecipe(new ItemStack(GAMod.redstoneBattleMiner, 1, 1998), new Object[] {"RYR", "RXR", " X ", 'X',
				GAMod.carbonstick, 'Y', new ItemStack(GAMod.preciousAlloy, 1, 2), 'R', Blocks.redstone_block});
		addRecipe(BaseElectricTool.getUnchargedItem(GAMod.aredstoneBattleMiner), new Object[] {"YYY", "RXR", " X ",
				'X', GAMod.carbonstick, 'Y', new ItemStack(GAMod.preciousAlloy, 1, 2), 'R', Blocks.redstone_block});

		/* Armor */

		//Tier II
		addRecipe(new ItemStack(GAMod.advancedHelmet, 1, GAMod.advancedHelmet.getMaxDamage() - 2), new Object[] {"XXX",
				"X X", 'X', new ItemStack(GAMod.preciousAlloy, 1, 0)});
		addRecipe(new ItemStack(GAMod.advancedChest, 1, GAMod.advancedChest.getMaxDamage() - 2), new Object[] {"X X",
				"XXX", "XXX", 'X', new ItemStack(GAMod.preciousAlloy, 1, 0)});
		addRecipe(new ItemStack(GAMod.advancedPants, 1, GAMod.advancedPants.getMaxDamage() - 2), new Object[] {"X X",
				"X X", "XXX", 'X', new ItemStack(GAMod.preciousAlloy, 1, 0)});
		addRecipe(new ItemStack(GAMod.advancedBoots, 1, GAMod.advancedBoots.getMaxDamage() - 2), new Object[] {"X X",
				"X X", 'X', new ItemStack(GAMod.preciousAlloy, 1, 0)});

		/* Shapeless Recipes */

		addShapelessRecipe(new ItemStack(GAMod.advancedSword, 1, 998), new Object[] {new ItemStack(GAMod.advancedSword,
				1, OreDictionary.WILDCARD_VALUE)});
		addShapelessRecipe(new ItemStack(GAMod.advancedPickaxe, 1, 998), new Object[] {new ItemStack(
				GAMod.advancedPickaxe, 1, OreDictionary.WILDCARD_VALUE)});
		addShapelessRecipe(new ItemStack(GAMod.advancedAxe, 1, 998), new Object[] {new ItemStack(GAMod.advancedAxe, 1,
				OreDictionary.WILDCARD_VALUE)});
		addShapelessRecipe(new ItemStack(GAMod.advancedShovel, 1, 998), new Object[] {new ItemStack(
				GAMod.advancedShovel, 1, OreDictionary.WILDCARD_VALUE)});

		addShapelessRecipe(new ItemStack(GAMod.advancedHelmet, 1, GAMod.advancedHelmet.getMaxDamage() - 2),
				new Object[] {new ItemStack(GAMod.advancedHelmet, 1, OreDictionary.WILDCARD_VALUE)});
		addShapelessRecipe(new ItemStack(GAMod.advancedChest, 1, GAMod.advancedChest.getMaxDamage() - 2),
				new Object[] {new ItemStack(GAMod.advancedChest, 1, OreDictionary.WILDCARD_VALUE)});
		addShapelessRecipe(new ItemStack(GAMod.advancedPants, 1, GAMod.advancedPants.getMaxDamage() - 2),
				new Object[] {new ItemStack(GAMod.advancedPants, 1, OreDictionary.WILDCARD_VALUE)});
		addShapelessRecipe(new ItemStack(GAMod.advancedBoots, 1, GAMod.advancedBoots.getMaxDamage() - 2),
				new Object[] {new ItemStack(GAMod.advancedBoots, 1, OreDictionary.WILDCARD_VALUE)});
	}

	void addRecipe(ItemStack stack, Object obj[])
	{
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;

		if(obj[i] instanceof String[])
		{
			String as[] = (String[]) obj[i++];

			for(int l = 0;l < as.length;l++)
			{
				String s2 = as[l];
				k++;
				j = s2.length();
				s = (new StringBuilder()).append(s).append(s2).toString();
			}
		}
		else
			while(obj[i] instanceof String)
			{
				String s1 = (String) obj[i++];
				k++;
				j = s1.length();
				s = (new StringBuilder()).append(s).append(s1).toString();
			}

		HashMap<Character, ItemStack> hashmap = new HashMap<Character, ItemStack>();

		for(;i < obj.length;i += 2)
		{
			Character character = (Character) obj[i];
			ItemStack ItemStack = null;

			if(obj[i + 1] instanceof Item)
				ItemStack = new ItemStack((Item) obj[i + 1]);
			else if(obj[i + 1] instanceof Block)
				ItemStack = new ItemStack((Block) obj[i + 1], 1, -1);
			else if(obj[i + 1] instanceof ItemStack)
				ItemStack = (ItemStack) obj[i + 1];

			hashmap.put(character, ItemStack);
		}

		ItemStack aItemStack[] = new ItemStack[j * k];

		for(int i1 = 0;i1 < j * k;i1++)
		{
			char c = s.charAt(i1);

			if(hashmap.containsKey(Character.valueOf(c)))
				aItemStack[i1] = hashmap.get(Character.valueOf(c)).copy();
			else
				aItemStack[i1] = null;
		}

		recipes.add(new CrMShapedRecipe(j, k, aItemStack, stack));
	}

	public void addShapelessRecipe(ItemStack stack, Object... obj)
	{
		ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>();
		Object[] aobject = obj;
		int i = obj.length;

		for(int j = 0;j < i;++j)
		{
			Object object1 = aobject[j];

			if(object1 instanceof ItemStack)
			{
				arraylist.add(((ItemStack) object1).copy());
			}
			else if(object1 instanceof Item)
			{
				arraylist.add(new ItemStack((Item) object1));
			}
			else
			{
				if(!(object1 instanceof Block))
				{
					throw new RuntimeException("Invalid shapeless recipy!");
				}

				arraylist.add(new ItemStack((Block) object1));
			}
		}

		this.recipes.add(new CrMShapelessRecipe(stack, arraylist));
	}

	public ItemStack findMatchingRecipe(InventoryCrafting craftingInv, World world)
	{
		int i = 0;
		ItemStack itemstack = null;
		ItemStack itemstack1 = null;
		int j;

		for(j = 0;j < craftingInv.getSizeInventory();++j)
		{
			ItemStack itemstack2 = craftingInv.getStackInSlot(j);

			if(itemstack2 != null)
			{
				if(i == 0)
				{
					itemstack = itemstack2;
				}

				if(i == 1)
				{
					itemstack1 = itemstack2;
				}

				++i;
			}
		}

		if(i == 2 && itemstack.getItem() == itemstack1.getItem() && itemstack.stackSize == 1
				&& itemstack1.stackSize == 1 && itemstack.getItem().isRepairable())
		{
			Item item = itemstack.getItem();
			int j1 = item.getMaxDamage() - itemstack.getItemDamage();
			int k = item.getMaxDamage() - itemstack1.getItemDamage();
			int l = j1 + k + item.getMaxDamage() * 5 / 100;
			int i1 = item.getMaxDamage() - l;

			if(i1 < 0)
			{
				i1 = 0;
			}

			return new ItemStack(itemstack.getItem(), 1, i1);
		}
		else
		{
			for(j = 0;j < this.recipes.size();++j)
			{
				IRecipe irecipe = this.recipes.get(j);

				if(irecipe.matches(craftingInv, world))
				{
					return irecipe.getCraftingResult(craftingInv);
				}
			}

			return null;
		}
	}

	public List<IRecipe> getRecipeList()
	{
		return recipes;
	}
}
