package geoactivity.tool;

import geoactivity.perk.PerkData;
import geoactivity.perk.ToolPerk;
import geoactivity.registry.ModDataComponents;
import geoactivity.registry.ModItems;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/** Shared GUI-tool rules (fuel, perks, mining). */
public final class ToolLogic {
    private ToolLogic() {}

    public static PerkData perks(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.PERKS.get(), PerkData.EMPTY);
    }

    public static int level(ItemStack stack, ToolPerk perk) {
        return perks(stack).level(perk);
    }

    // --- inventory (fuel + perk slots) stored on the stack ---

    public static NonNullList<ItemStack> inventory(ItemStack stack, int size) {
        NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
        stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        return items;
    }

    public static void setInventory(ItemStack stack, NonNullList<ItemStack> items) {
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }

    /**
     * 1.10.2 {@code setCharge}: burn coal while the repair still fits, fuel slots in order. 1.10.2 only burned the
     * first slot, so the Redstone tools' other 3 fuel slots did nothing (fixed in the 3.0.0 balance pass).
     */
    public static void refuel(ItemStack stack, ToolProfile profile) {
        if (!profile.usesFuel() || profile.fuelSlots() == 0) {
            return;
        }
        NonNullList<ItemStack> items = inventory(stack, profile.inventorySize());
        int efficiency = level(stack, ToolPerk.EFFICIENCY);
        for (int slot = 0; slot < profile.fuelSlots(); slot++) {
            ItemStack coal = items.get(slot);
            int fuel = fuelValue(coal, profile, efficiency);
            while (fuel > 0 && !coal.isEmpty() && stack.getDamageValue() - fuel > 0) {
                stack.setDamageValue(stack.getDamageValue() - fuel);
                coal.shrink(1);
            }
        }
        setInventory(stack, items);
    }

    static int fuelValue(ItemStack coal, ToolProfile profile, int efficiency) {
        int bonus = profile.efficiencyBonus() * Math.min(efficiency, profile.limits().efficiency());
        if (coal.is(ModItems.LIGNITE_COAL.get())) return profile.fuel()[0] + bonus * 2;
        if (coal.is(ModItems.BITUMINOUS_COAL.get())) return profile.fuel()[1] + bonus * 3;
        if (coal.is(ModItems.ANTHRACITE_COAL.get())) return profile.fuel()[2] + bonus * 4;
        return 0;
    }

    public static boolean isCoal(ItemStack stack) {
        return stack.is(ModItems.LIGNITE_COAL.get()) || stack.is(ModItems.BITUMINOUS_COAL.get()) || stack.is(ModItems.ANTHRACITE_COAL.get());
    }

    public static boolean isToolPerk(ItemStack stack) {
        return ModItems.TOOL_PERKS.values().stream().anyMatch(p -> stack.is(p.get()));
    }

    /**
     * Redstone tools: perk items in the tool inventory become the tool's perks (1.10.2 {@code RedInventory.writeToNBT}),
     * with the same caps and exclusions: no drops beats smelting/silk/fortune/drop teleport, smelting or fortune beat
     * silk touch, wider radius beats capitator.
     */
    public static void recomputeInventoryPerks(ItemStack stack, ToolProfile profile) {
        NonNullList<ItemStack> items = inventory(stack, profile.inventorySize());
        Map<String, Integer> levels = new HashMap<>();
        GlobalPos target = null;
        for (int i = profile.fuelSlots(); i < items.size(); i++) {
            ItemStack item = items.get(i);
            for (ToolPerk perk : ToolPerk.values()) {
                if (perk != ToolPerk.BLANK && item.is(ModItems.toolPerk(perk))) {
                    if (perk == ToolPerk.DROP_TELEPORT) {
                        GlobalPos bound = item.get(ModDataComponents.DROP_TARGET.get());
                        if (bound == null) {
                            continue;
                        }
                        target = bound;
                    }
                    levels.merge(perk.key(), 1, Integer::sum);
                }
            }
        }
        var limits = profile.limits();
        cap(levels, ToolPerk.SPEED, limits.speed());
        cap(levels, ToolPerk.EFFICIENCY, limits.efficiency());
        cap(levels, ToolPerk.DAMAGE, limits.damage());
        cap(levels, ToolPerk.FORTUNE, limits.fortune());
        cap(levels, ToolPerk.RADIUS, limits.radius());
        for (ToolPerk flag : new ToolPerk[] {ToolPerk.EXPERIENCE, ToolPerk.SMELTING, ToolPerk.SILK_TOUCH, ToolPerk.CAPITATOR,
                ToolPerk.NO_DROPS, ToolPerk.DROP_TELEPORT, ToolPerk.ARMOR_BYPASS}) {
            cap(levels, flag, 1);
        }
        if (levels.containsKey(ToolPerk.NO_DROPS.key())) {
            levels.remove(ToolPerk.SMELTING.key());
            levels.remove(ToolPerk.SILK_TOUCH.key());
            levels.remove(ToolPerk.FORTUNE.key());
            levels.remove(ToolPerk.DROP_TELEPORT.key());
            target = null;
        }
        if (levels.containsKey(ToolPerk.SMELTING.key()) || levels.containsKey(ToolPerk.FORTUNE.key())) {
            levels.remove(ToolPerk.SILK_TOUCH.key());
        }
        if (levels.containsKey(ToolPerk.RADIUS.key())) {
            levels.remove(ToolPerk.CAPITATOR.key());
        }
        stack.set(ModDataComponents.PERKS, new PerkData(levels));
        if (target != null) {
            stack.set(ModDataComponents.DROP_TARGET, target);
        } else {
            stack.remove(ModDataComponents.DROP_TARGET);
        }
    }

    private static void cap(Map<String, Integer> levels, ToolPerk perk, int max) {
        levels.computeIfPresent(perk.key(), (k, v) -> max <= 0 ? null : Math.min(v, max));
    }

    // --- mining ---

    /** Is this block one the tool mines (right category, tier at most diamond / iron)? */
    public static boolean isEffective(ToolProfile profile, BlockState state) {
        if (state.is(profile.incorrectTier())) {
            return false;
        }
        for (ToolProfile.Category category : profile.categories()) {
            if (state.is(category.tag)) {
                return true;
            }
        }
        return false;
    }

    /** Mining speed: base, +2 per speed level, −8 with radius/capitator, halved at RF radius level 3+. */
    public static float speed(ItemStack stack, ToolProfile profile) {
        PerkData perks = perks(stack);
        float speed = profile.speed();
        if (perks.has(ToolPerk.RADIUS) || perks.has(ToolPerk.CAPITATOR)) {
            speed -= 8.0F;
        }
        speed += 2.0F * perks.level(ToolPerk.SPEED);
        if (perks.level(ToolPerk.RADIUS) > 2) {
            speed /= 2;
        }
        return Math.max(speed, 1.0F);
    }

    public static @Nullable GlobalPos dropTarget(ItemStack stack) {
        return stack.get(ModDataComponents.DROP_TARGET.get());
    }

    public static boolean isCreative(@Nullable Player player) {
        return player != null && player.isCreative();
    }
}
