package geoactivity.machine.extractor;

import geoactivity.item.Element;
import geoactivity.machine.RecipeInput;
import geoactivity.registry.ModItems;
import java.util.List;
import java.util.Optional;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

/**
 * Atom Extractor recipes, transcribed from 1.10.2 {@code AERecipes} (59 entries; the two log entries LOG/LOG2 are one
 * {@code #minecraft:logs} recipe now, so 58). Inputs that had an OreDictionary name match the equivalent tag.
 */
public final class AtomExtractorRecipes {
    public record Output(Element element, int count) {
        public ItemStack stack() {
            return new ItemStack(ModItems.element(element), count);
        }
    }

    /** {@code time} is in ticks, before Advanced Hardened Brick upgrades. */
    public record Recipe(RecipeInput input, int time, List<Output> outputs) {
        /** Empty element containers consumed per cycle (one per output container). */
        public int containersNeeded() {
            return outputs.stream().mapToInt(Output::count).sum();
        }
    }

    private static List<Recipe> recipes;

    private AtomExtractorRecipes() {}

    public static List<Recipe> all() {
        if (recipes == null) {
            recipes = List.of(
                r(item(Items.APPLE), 200, out(Element.CARBON, 1), out(Element.POTASSIUM, 1), out(Element.OXYGEN, 1)),
                r(item(Items.COAL), 200, out(Element.CARBON, 2)),
                r(tag(Tags.Items.GEMS_DIAMOND), 1200, out(Element.CARBON, 32)),
                r(tag(Tags.Items.INGOTS_IRON), 400, out(Element.IRON, 2), out(Element.LITHIUM, 1)),
                r(tag(Tags.Items.INGOTS_GOLD), 600, out(Element.GOLD, 2), out(Element.LITHIUM, 1)),
                r(tag(Tags.Items.STRINGS), 100, out(Element.CARBON, 1), out(Element.NITROGEN, 1)),
                r(tag(Tags.Items.GUNPOWDERS), 400, out(Element.PHOSPHORUS, 1), out(Element.SULFUR, 1), out(Element.IODINE, 1)),
                r(item(Items.WHEAT_SEEDS), 50, out(Element.POTASSIUM, 1), out(Element.CALCIUM, 1)),
                r(tag(Tags.Items.CROPS_WHEAT), 300, out(Element.MAGNESIUM, 1), out(Element.PHOSPHORUS, 1)),
                r(item(Items.GOLDEN_APPLE), 1500, out(Element.POTASSIUM, 2), out(Element.GOLD, 16)),
                r(tag(Tags.Items.DUSTS_REDSTONE), 300, out(Element.IRON, 1), out(Element.LITHIUM, 1), out(Element.NEON, 1)),
                r(tag(Tags.Items.LEATHERS), 100, out(Element.CARBON, 1), out(Element.SILICON, 1)),
                r(item(Items.CLAY_BALL), 200, out(Element.ALUMINIUM, 1), out(Element.SILICON, 1)),
                r(tag(Tags.Items.SLIME_BALLS), 500, out(Element.SILICON, 2), out(Element.SODIUM, 2)),
                r(tag(Tags.Items.EGGS), 100, out(Element.CALCIUM, 1), out(Element.PHOSPHORUS, 1)),
                r(tag(Tags.Items.DUSTS_GLOWSTONE), 600, out(Element.SULFUR, 2), out(Element.NEON, 2), out(Element.HELIUM, 1)),
                r(item(Items.COD), 200, out(Element.CALCIUM, 1), out(Element.IODINE, 1)),
                r(item(Items.INK_SAC), 50, out(Element.PHOSPHORUS, 1), out(Element.SULFUR, 1)),
                r(tag(Tags.Items.BONES), 200, out(Element.MAGNESIUM, 1), out(Element.CALCIUM, 1)),
                r(item(Items.MELON_SLICE), 400, out(Element.HYDROGEN, 1), out(Element.OXYGEN, 1), out(Element.NITROGEN, 1)),
                r(item(Items.PUMPKIN_SEEDS), 60, out(Element.POTASSIUM, 1), out(Element.CALCIUM, 1)),
                r(item(Items.MELON_SEEDS), 60, out(Element.POTASSIUM, 1), out(Element.CALCIUM, 1)),
                r(item(Items.ROTTEN_FLESH), 300, out(Element.IODINE, 1)),
                r(tag(Tags.Items.ENDER_PEARLS), 800, out(Element.HELIUM, 2), out(Element.LITHIUM, 2), out(Element.SILICON, 4)),
                r(tag(Tags.Items.RODS_BLAZE), 600, out(Element.HYDROGEN, 4), out(Element.HELIUM, 1), out(Element.ALUMINIUM, 2)),
                r(item(Items.GHAST_TEAR), 800, out(Element.HELIUM, 2), out(Element.NITROGEN, 2), out(Element.CHLORINE, 2)),
                r(tag(Tags.Items.CROPS_NETHER_WART), 200, out(Element.SODIUM, 2), out(Element.SULFUR, 1)),
                r(item(Items.SPIDER_EYE), 400, out(Element.NITROGEN, 1), out(Element.CHLORINE, 1)),
                r(tag(Tags.Items.GEMS_EMERALD), 2000, out(Element.ALUMINIUM, 12), out(Element.SILICON, 10), out(Element.OXYGEN, 10)),
                r(tag(Tags.Items.CROPS_CARROT), 200, out(Element.PHOSPHORUS, 1), out(Element.POTASSIUM, 1), out(Element.SODIUM, 1)),
                r(tag(Tags.Items.CROPS_POTATO), 200, out(Element.NITROGEN, 1), out(Element.IODINE, 1)),
                r(item(Items.GOLDEN_CARROT), 300, out(Element.PHOSPHORUS, 1), out(Element.GOLD, 1)),
                r(tag(Tags.Items.NETHER_STARS), 5000, out(Element.HELIUM, 10), out(Element.LITHIUM, 10), out(Element.NEON, 12)),
                r(tag(Tags.Items.BRICKS_NETHER), 200, out(Element.PHOSPHORUS, 1), out(Element.POTASSIUM, 1)),
                r(tag(Tags.Items.GEMS_QUARTZ), 300, out(Element.NEON, 1), out(Element.GOLD, 1)),
                r(item(Items.FLINT), 200, out(Element.IODINE, 1), out(Element.PHOSPHORUS, 1)),
                r(tag(Tags.Items.GEMS_LAPIS), 600, out(Element.CHLORINE, 1), out(Element.SILICON, 2), out(Element.MAGNESIUM, 1)),
                r(tag(Tags.Items.FEATHERS), 200, out(Element.SODIUM, 1), out(Element.NITROGEN, 1)),
                r(item(Items.SUGAR_CANE), 200, out(Element.NITROGEN, 1), out(Element.OXYGEN, 1)),
                r(item(Items.GRASS_BLOCK), 100, out(Element.NITROGEN, 1), out(Element.IODINE, 1)),
                r(tag(ItemTags.SAPLINGS), 200, out(Element.HYDROGEN, 1), out(Element.MAGNESIUM, 1)),
                r(tag(Tags.BlockItems.ORES_GOLD.item()), 700, out(Element.GOLD, 5)),
                r(tag(Tags.BlockItems.ORES_IRON.item()), 500, out(Element.IRON, 5)),
                r(tag(ItemTags.LOGS), 400, out(Element.HYDROGEN, 1), out(Element.PHOSPHORUS, 1)),
                r(tag(Tags.BlockItems.ORES_LAPIS.item()), 600, out(Element.CHLORINE, 3), out(Element.SILICON, 4), out(Element.MAGNESIUM, 4)),
                r(item(Items.COBWEB), 400, out(Element.CARBON, 2), out(Element.NITROGEN, 3)),
                r(tag(ItemTags.WOOL), 200, out(Element.CARBON, 1)),
                r(tag(Tags.BlockItems.OBSIDIANS.item()), 1000, out(Element.LITHIUM, 2), out(Element.NEON, 1), out(Element.MAGNESIUM, 2)),
                r(tag(Tags.BlockItems.ORES_REDSTONE.item()), 400, out(Element.NEON, 2), out(Element.LITHIUM, 3)),
                r(item(Items.SNOW_BLOCK), 200, out(Element.HELIUM, 1), out(Element.NITROGEN, 1)),
                r(item(Items.CACTUS), 200, out(Element.SULFUR, 2)),
                r(item(Items.PUMPKIN), 400, out(Element.POTASSIUM, 1), out(Element.MAGNESIUM, 1)),
                r(item(Items.DRAGON_EGG), 5000, out(Element.LITHIUM, 16), out(Element.SILICON, 16)),
                r(tag(Tags.BlockItems.ORES_QUARTZ.item()), 300, out(Element.NEON, 3)),
                r(item(ModItems.LIGNITE_COAL.get()), 400, out(Element.CARBON, 8)),
                r(item(ModItems.BITUMINOUS_COAL.get()), 600, out(Element.CARBON, 16)),
                r(item(ModItems.ANTHRACITE_COAL.get()), 800, out(Element.CARBON, 24)),
                r(item(ModItems.GRAPHITE.get()), 100, out(Element.CARBON, 1)));
        }
        return recipes;
    }

    public static Optional<Recipe> find(ItemStack input) {
        return all().stream().filter(r -> r.input().test(input)).findFirst();
    }

    public static boolean isInput(ItemStack stack) {
        return find(stack).isPresent();
    }

    private static RecipeInput item(Item item) {
        return RecipeInput.of(item);
    }

    private static RecipeInput tag(TagKey<Item> tag) {
        return RecipeInput.of(tag);
    }

    private static Output out(Element element, int count) {
        return new Output(element, count);
    }

    private static Recipe r(RecipeInput input, int time, Output... outputs) {
        return new Recipe(input, time, List.of(outputs));
    }
}
