package geoactivity.gametest;

import net.minecraft.world.item.ItemStack;

/** Reaches the package-private perk application of the Crafting Machine. */
final class CraftingMachineTestAccess {
    private CraftingMachineTestAccess() {}

    static void applyPerk(ItemStack result, ItemStack perk) {
        geoactivity.machine.crafting.CraftingMachinePerks.apply(result, perk);
    }
}
