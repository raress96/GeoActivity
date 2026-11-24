package geoactivity.machine.crafting;

import net.minecraft.world.item.ItemStack;

/** Public entry to the Crafting Machine's perk rule (used by tests). */
public final class CraftingMachinePerks {
    private CraftingMachinePerks() {}

    public static void apply(ItemStack result, ItemStack perk) {
        CraftingMachineBlockEntity.applyPerk(result, perk);
    }
}
