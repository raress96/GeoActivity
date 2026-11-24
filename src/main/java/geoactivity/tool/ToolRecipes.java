package geoactivity.tool;

import static geoactivity.machine.crafting.CraftingMachineRecipes.shaped;
import static geoactivity.machine.crafting.CraftingMachineRecipes.shapeless;

import geoactivity.machine.crafting.CraftingMachineRecipes;
import geoactivity.perk.ToolPerk;
import geoactivity.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Crafting Machine recipes for tier II and III tools (1.10.2 {@code CrMCrafting}); tools come out empty of fuel. */
public final class ToolRecipes {
    private ToolRecipes() {}

    public static void register() {
        CraftingMachineRecipes.addProvider(ToolRecipes::recipes);
    }

    private static List<CraftingMachineRecipes.Recipe> recipes() {
        Item alloy = ModItems.SMALL_PRECIOUS_ALLOY_INGOT.get();
        Item stick = ModItems.CARBON_STICK.get();
        List<CraftingMachineRecipes.Recipe> list = new ArrayList<>();
        // Tier II
        list.add(shaped(damaged(ModItems.ADVANCED_SWORD.get(), 998), ToolRecipes::swordPerks, List.of(" Y ", " Y ", " X "), 'X', stick, 'Y', alloy));
        list.add(shaped(damaged(ModItems.ADVANCED_PICKAXE.get(), 998), ToolRecipes::pickaxePerks, List.of("YYY", " X ", " X "), 'X', stick, 'Y', alloy));
        list.add(shaped(damaged(ModItems.ADVANCED_AXE.get(), 998), ToolRecipes::axePerks, List.of("YY ", "YX ", " X "), 'X', stick, 'Y', alloy));
        list.add(shaped(damaged(ModItems.ADVANCED_SHOVEL.get(), 998), ToolRecipes::shovelPerks, List.of(" Y ", " X ", " X "), 'X', stick, 'Y', alloy));
        // Tier III
        Item alloyNormal = ModItems.PRECIOUS_ALLOY_INGOT.get();
        Item alloyLarge = ModItems.LARGE_PRECIOUS_ALLOY_INGOT.get();
        list.add(shaped(damaged(ModItems.REDSTONE_MINER.get(), 998), List.of("RYR", " X ", " X "), 'X', stick, 'Y', alloyNormal, 'R', Items.REDSTONE_BLOCK));
        list.add(shaped(damaged(ModItems.REDSTONE_BATTLE_AXE.get(), 998), List.of("RYR", "RXR", " X "), 'X', stick, 'Y', alloyNormal, 'R', Items.REDSTONE_BLOCK));
        list.add(shaped(damaged(ModItems.REDSTONE_BATTLE_MINER.get(), 1998), List.of("RYR", "RXR", " X "), 'X', stick, 'Y', alloyLarge, 'R', Items.REDSTONE_BLOCK));
        list.add(shaped(() -> new ItemStack(ModItems.ADVANCED_REDSTONE_BATTLE_MINER.get()), List.of("YYY", "RXR", " X "),
                'X', stick, 'Y', alloyLarge, 'R', Items.REDSTONE_BLOCK));
        // "reset" recipes: put a tool back to empty (and perk-less, ready for a new perk)
        list.add(shapeless(damaged(ModItems.ADVANCED_SWORD.get(), 998), ToolRecipes::swordPerks, ModItems.ADVANCED_SWORD.get()));
        list.add(shapeless(damaged(ModItems.ADVANCED_PICKAXE.get(), 998), ToolRecipes::pickaxePerks, ModItems.ADVANCED_PICKAXE.get()));
        list.add(shapeless(damaged(ModItems.ADVANCED_AXE.get(), 998), ToolRecipes::axePerks, ModItems.ADVANCED_AXE.get()));
        list.add(shapeless(damaged(ModItems.ADVANCED_SHOVEL.get(), 998), ToolRecipes::shovelPerks, ModItems.ADVANCED_SHOVEL.get()));
        return list;
    }

    static Supplier<ItemStack> damaged(Item item, int damage) {
        return () -> {
            ItemStack stack = new ItemStack(item);
            stack.setDamageValue(damage);
            return stack;
        };
    }

    private static List<ItemStack> perks(ToolPerk... perks) {
        List<ItemStack> list = new ArrayList<>();
        for (ToolPerk perk : new ToolPerk[] {ToolPerk.EFFICIENCY, ToolPerk.DAMAGE, ToolPerk.ARMOR_BYPASS}) {
            list.add(new ItemStack(ModItems.toolPerk(perk)));
        }
        for (ToolPerk perk : perks) {
            list.add(new ItemStack(ModItems.toolPerk(perk)));
        }
        return list;
    }

    static List<ItemStack> pickaxePerks() {
        return perks(ToolPerk.SPEED, ToolPerk.EXPERIENCE, ToolPerk.SMELTING, ToolPerk.FORTUNE, ToolPerk.SILK_TOUCH, ToolPerk.RADIUS,
                ToolPerk.CAPITATOR, ToolPerk.NO_DROPS);
    }

    static List<ItemStack> shovelPerks() {
        return perks(ToolPerk.SPEED, ToolPerk.SMELTING, ToolPerk.FORTUNE, ToolPerk.SILK_TOUCH, ToolPerk.RADIUS, ToolPerk.CAPITATOR, ToolPerk.NO_DROPS);
    }

    static List<ItemStack> axePerks() {
        return perks(ToolPerk.SPEED, ToolPerk.SMELTING, ToolPerk.SILK_TOUCH, ToolPerk.CAPITATOR, ToolPerk.NO_DROPS);
    }

    static List<ItemStack> swordPerks() {
        return perks(ToolPerk.EXPERIENCE);
    }
}
