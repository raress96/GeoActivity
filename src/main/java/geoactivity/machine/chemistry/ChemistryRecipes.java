package geoactivity.machine.chemistry;

import geoactivity.item.Element;
import geoactivity.perk.ArmorPerk;
import geoactivity.perk.ToolPerk;
import geoactivity.registry.ModItems;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

/**
 * Chemistry Machine recipes, transcribed from 1.10.2 {@code ChMCrafting} (19). Two inputs (order-insensitive) plus up
 * to four element groups: each group is {@code count} item frames on one Advanced Hardened Brick showing that element.
 */
public final class ChemistryRecipes {
    public record ElementGroup(Element element, int count) {}

    public record Recipe(Supplier<Item> first, @Nullable Supplier<Item> second, Supplier<ItemStack> result, List<ElementGroup> groups) {
        public boolean matches(ItemStack a, ItemStack b) {
            return matchesOrdered(a, b) || matchesOrdered(b, a);
        }

        private boolean matchesOrdered(ItemStack a, ItemStack b) {
            boolean firstOk = a.is(first.get());
            boolean secondOk = second == null ? b.isEmpty() : b.is(second.get());
            return firstOk && secondOk;
        }
    }

    private static List<Recipe> recipes;

    private ChemistryRecipes() {}

    public static List<Recipe> all() {
        if (recipes == null) {
            recipes = List.of(
                    // Tool perks
                    r(blank(), () -> Items.LAPIS_LAZULI, toolPerk(ToolPerk.EXPERIENCE), g(Element.GOLD, 2), g(Element.CHLORINE, 4), g(Element.HELIUM, 2)),
                    r(blank(), () -> Items.COAL, toolPerk(ToolPerk.SMELTING), g(Element.SULFUR, 4), g(Element.OXYGEN, 2), g(Element.MAGNESIUM, 2)),
                    r(blank(), () -> Items.ENDER_EYE, toolPerk(ToolPerk.FORTUNE), g(Element.GOLD, 4), g(Element.SILICON, 4)),
                    r(blank(), () -> Items.IRON_INGOT, toolPerk(ToolPerk.SILK_TOUCH),
                            g(Element.LITHIUM, 4), g(Element.SODIUM, 2), g(Element.ALUMINIUM, 2), g(Element.CALCIUM, 2)),
                    r(() -> ModItems.toolPerk(ToolPerk.SPEED), () -> Items.DIAMOND, toolPerk(ToolPerk.RADIUS),
                            g(Element.HYDROGEN, 4), g(Element.CARBON, 4), g(Element.NITROGEN, 4), g(Element.NEON, 4)),
                    r(() -> ModItems.toolPerk(ToolPerk.EFFICIENCY), () -> Items.DIAMOND, toolPerk(ToolPerk.CAPITATOR),
                            g(Element.PHOSPHORUS, 4), g(Element.POTASSIUM, 4), g(Element.IRON, 4), g(Element.MAGNESIUM, 4)),
                    r(blank(), () -> Items.ENDER_PEARL, toolPerk(ToolPerk.NO_DROPS), g(Element.CARBON, 4), g(Element.PHOSPHORUS, 4), g(Element.SULFUR, 4)),
                    r(() -> Items.EMERALD, () -> Items.ENDER_EYE, toolPerk(ToolPerk.DROP_TELEPORT),
                            g(Element.HYDROGEN, 4), g(Element.LITHIUM, 4), g(Element.HELIUM, 4), g(Element.OXYGEN, 4)),
                    // Armor perks
                    r(blank(), () -> Items.FEATHER, () -> new ItemStack(ModItems.armorPerk(ArmorPerk.NO_FALL)), g(Element.HELIUM, 4), g(Element.HELIUM, 4)),
                    r(blank(), () -> Items.INK_SAC, () -> new ItemStack(ModItems.armorPerk(ArmorPerk.RESPIRATION)), g(Element.OXYGEN, 4), g(Element.HYDROGEN, 4)),
                    // Others
                    r(() -> ModItems.CARBON_STICK.get(), () -> Items.BLAZE_ROD, () -> new ItemStack(ModItems.CARBON_ORE_CUTTER.get(), 2),
                            g(Element.CARBON, 4), g(Element.CARBON, 4), g(Element.CHLORINE, 4), g(Element.ALUMINIUM, 4)),
                    r(() -> Items.GOLD_INGOT, () -> Items.GOLD_INGOT, stack(Items.BLAZE_ROD, 4), g(Element.NITROGEN, 4), g(Element.NITROGEN, 4)),
                    r(() -> Items.BLAZE_ROD, () -> Items.BLAZE_ROD, stack(Items.EMERALD, 1),
                            g(Element.SILICON, 4), g(Element.SILICON, 4), g(Element.SILICON, 4), g(Element.SILICON, 4)),
                    r(() -> Items.QUARTZ, () -> Items.GLOWSTONE_DUST, stack(Items.ENDER_PEARL, 2),
                            g(Element.LITHIUM, 4), g(Element.LITHIUM, 4), g(Element.SILICON, 4), g(Element.SILICON, 4)),
                    r(() -> Items.QUARTZ, () -> Items.GOLD_NUGGET, stack(Items.GHAST_TEAR, 2),
                            g(Element.HELIUM, 4), g(Element.NITROGEN, 4), g(Element.CHLORINE, 4), g(Element.CHLORINE, 4)),
                    r(() -> Items.LEATHER, () -> Items.BLAZE_POWDER, stack(Items.GUNPOWDER, 6), g(Element.PHOSPHORUS, 4), g(Element.SULFUR, 4), g(Element.SULFUR, 4)),
                    r(() -> Items.CLAY_BALL, () -> Items.CLAY_BALL, stack(Items.LEATHER, 6), g(Element.CARBON, 4), g(Element.CARBON, 4)),
                    r(() -> Items.ENDER_PEARL, () -> Items.GLOWSTONE_DUST, stack(Items.SLIME_BALL, 2), g(Element.SILICON, 4), g(Element.SILICON, 4)),
                    r(() -> Items.STRING, () -> Items.STRING, stack(Items.SPIDER_EYE, 2), g(Element.NITROGEN, 4), g(Element.CHLORINE, 4)));
        }
        return recipes;
    }

    public static Optional<Recipe> find(ItemStack a, ItemStack b) {
        return all().stream().filter(r -> r.matches(a, b)).findFirst();
    }

    private static Supplier<Item> blank() {
        return () -> ModItems.toolPerk(ToolPerk.BLANK);
    }

    private static Supplier<ItemStack> toolPerk(ToolPerk perk) {
        return () -> new ItemStack(ModItems.toolPerk(perk));
    }

    private static Supplier<ItemStack> stack(Item item, int count) {
        return () -> new ItemStack(item, count);
    }

    private static ElementGroup g(Element element, int count) {
        return new ElementGroup(element, count);
    }

    private static Recipe r(Supplier<Item> first, @Nullable Supplier<Item> second, Supplier<ItemStack> result, ElementGroup... groups) {
        return new Recipe(first, second, result, List.of(groups));
    }
}
