package geoactivity.armor;

import static geoactivity.machine.crafting.CraftingMachineRecipes.shaped;
import static geoactivity.machine.crafting.CraftingMachineRecipes.shapeless;

import geoactivity.machine.crafting.CraftingMachineRecipes;
import geoactivity.perk.ArmorPerk;
import geoactivity.perk.ToolPerk;
import geoactivity.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Crafting Machine recipes for Advanced Armor (1.10.2 {@code CrMCrafting}); pieces come out empty (max damage − 2). */
public final class ArmorRecipes {
    private ArmorRecipes() {}

    public static void register() {
        CraftingMachineRecipes.addProvider(ArmorRecipes::recipes);
    }

    private static List<CraftingMachineRecipes.Recipe> recipes() {
        Item alloy = ModItems.SMALL_PRECIOUS_ALLOY_INGOT.get();
        List<CraftingMachineRecipes.Recipe> list = new ArrayList<>();
        list.add(shaped(empty(ModItems.ADVANCED_HELMET.get()), () -> perks(ArmorPerk.RESPIRATION), List.of("XXX", "X X"), 'X', alloy));
        list.add(shaped(empty(ModItems.ADVANCED_CHESTPLATE.get()), () -> perks(), List.of("X X", "XXX", "XXX"), 'X', alloy));
        list.add(shaped(empty(ModItems.ADVANCED_LEGGINGS.get()), () -> perks(ArmorPerk.NO_FALL), List.of("X X", "X X", "XXX"), 'X', alloy));
        list.add(shaped(empty(ModItems.ADVANCED_BOOTS.get()), () -> perks(ArmorPerk.NO_FALL), List.of("X X", "X X"), 'X', alloy));
        // reset recipes: back to empty, ready for another perk
        list.add(shapeless(empty(ModItems.ADVANCED_HELMET.get()), () -> perks(ArmorPerk.RESPIRATION), ModItems.ADVANCED_HELMET.get()));
        list.add(shapeless(empty(ModItems.ADVANCED_CHESTPLATE.get()), () -> perks(), ModItems.ADVANCED_CHESTPLATE.get()));
        list.add(shapeless(empty(ModItems.ADVANCED_LEGGINGS.get()), () -> perks(ArmorPerk.NO_FALL), ModItems.ADVANCED_LEGGINGS.get()));
        list.add(shapeless(empty(ModItems.ADVANCED_BOOTS.get()), () -> perks(ArmorPerk.NO_FALL), ModItems.ADVANCED_BOOTS.get()));
        return list;
    }

    private static Supplier<ItemStack> empty(Item item) {
        return () -> {
            ItemStack stack = new ItemStack(item);
            stack.setDamageValue(stack.getMaxDamage() - 2);
            return stack;
        };
    }

    /** The five protection perks (+ efficiency, which armor also takes) and any slot-specific ones. */
    private static List<ItemStack> perks(ArmorPerk... extra) {
        List<ItemStack> list = new ArrayList<>();
        for (ArmorPerk perk : new ArmorPerk[] {ArmorPerk.GENERAL, ArmorPerk.BLAST, ArmorPerk.FIRE, ArmorPerk.MAGIC, ArmorPerk.PROJECTILE}) {
            list.add(new ItemStack(ModItems.armorPerk(perk)));
        }
        for (ArmorPerk perk : extra) {
            list.add(new ItemStack(ModItems.armorPerk(perk)));
        }
        list.add(new ItemStack(ModItems.toolPerk(ToolPerk.EFFICIENCY)));
        return list;
    }
}
