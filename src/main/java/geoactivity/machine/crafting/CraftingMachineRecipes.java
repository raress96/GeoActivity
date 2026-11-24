package geoactivity.machine.crafting;

import geoactivity.machine.RecipeInput;
import geoactivity.perk.ArmorPerk;
import geoactivity.perk.MachinePerk;
import geoactivity.perk.ToolPerk;
import geoactivity.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

/**
 * Crafting Machine recipes, from 1.10.2 {@code CrMCrafting}: 13 shaped + 19 shapeless. Each may list the perks that
 * fit the result (shown in JEI only, like in 1.10.2). Inputs match by item; 1.10.2 matched metadata, which is the
 * item now.
 */
public final class CraftingMachineRecipes {
    public sealed interface Recipe permits Shaped, Shapeless {
        ItemStack result();

        List<ItemStack> compatiblePerks();

        /** grid is row-major, 9 stacks. */
        boolean matches(List<ItemStack> grid);
    }

    /** {@code pattern} is row-major, width × height, null = empty. Matches mirrored too, like vanilla. */
    public record Shaped(int width, int height, List<Optional<RecipeInput>> pattern, Supplier<ItemStack> resultSupplier,
            Supplier<List<ItemStack>> perks) implements Recipe {
        @Override
        public ItemStack result() {
            return resultSupplier.get();
        }

        @Override
        public List<ItemStack> compatiblePerks() {
            return perks.get();
        }

        @Override
        public boolean matches(List<ItemStack> grid) {
            for (int x = 0; x <= 3 - width; x++) {
                for (int y = 0; y <= 3 - height; y++) {
                    if (matchesAt(grid, x, y, true) || matchesAt(grid, x, y, false)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean matchesAt(List<ItemStack> grid, int offX, int offY, boolean mirrored) {
            for (int col = 0; col < 3; col++) {
                for (int row = 0; row < 3; row++) {
                    int px = col - offX;
                    int py = row - offY;
                    Optional<RecipeInput> expected = Optional.empty();
                    if (px >= 0 && py >= 0 && px < width && py < height) {
                        expected = pattern.get((mirrored ? width - px - 1 : px) + py * width);
                    }
                    ItemStack actual = grid.get(col + row * 3);
                    if (expected.isEmpty() != actual.isEmpty()) {
                        return false;
                    }
                    if (expected.isPresent() && !expected.get().test(actual)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public record Shapeless(List<RecipeInput> inputs, Supplier<ItemStack> resultSupplier, Supplier<List<ItemStack>> perks)
            implements Recipe {
        @Override
        public ItemStack result() {
            return resultSupplier.get();
        }

        @Override
        public List<ItemStack> compatiblePerks() {
            return perks.get();
        }

        @Override
        public boolean matches(List<ItemStack> grid) {
            List<RecipeInput> remaining = new ArrayList<>(inputs);
            for (ItemStack stack : grid) {
                if (stack.isEmpty()) {
                    continue;
                }
                boolean found = false;
                for (int i = 0; i < remaining.size(); i++) {
                    if (remaining.get(i).test(stack)) {
                        remaining.remove(i);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return remaining.isEmpty();
        }
    }

    private static final List<Supplier<List<Recipe>>> EXTRA = new ArrayList<>();
    private static List<Recipe> recipes;

    private CraftingMachineRecipes() {}

    /** Later phases (tools, armor) add their recipes here before the first lookup. */
    public static void addProvider(Supplier<List<Recipe>> provider) {
        EXTRA.add(provider);
        recipes = null;
    }

    public static List<Recipe> all() {
        if (recipes == null) {
            List<Recipe> list = new ArrayList<>();
            list.add(shaped(() -> new ItemStack(ModItems.toolPerk(ToolPerk.BLANK)), List.of(" Z ", "XSX", " Y "),
                    'X', Items.REDSTONE, 'S', ModItems.CARBON_FIBER.get(), 'Z', Items.GLOWSTONE_DUST, 'Y', Items.QUARTZ));
            // Tool perks
            list.add(shapeless(toolPerk(ToolPerk.SPEED), blank(), Items.ENDER_PEARL, Items.GOLD_INGOT));
            list.add(shapeless(toolPerk(ToolPerk.EFFICIENCY), blank(), Items.MAGMA_CREAM, Items.IRON_INGOT, Items.IRON_INGOT));
            list.add(shapeless(toolPerk(ToolPerk.DAMAGE), blank(), Items.REDSTONE_BLOCK, Items.QUARTZ_BLOCK));
            list.add(shapeless(toolPerk(ToolPerk.ARMOR_BYPASS), blank(), Items.DIAMOND, Items.DIAMOND, Items.BLAZE_ROD));
            // Machine perks
            list.add(shapeless(() -> new ItemStack(ModItems.machinePerk(MachinePerk.SPEED)), blank(), Items.ENDER_PEARL,
                    Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT));
            list.add(shapeless(() -> new ItemStack(ModItems.machinePerk(MachinePerk.EFFICIENCY)), blank(), Items.MAGMA_CREAM, Items.IRON_BLOCK));
            // Armor perks
            list.add(shapeless(armorPerk(ArmorPerk.GENERAL), blank(), Items.OBSIDIAN, Items.DIAMOND));
            list.add(shapeless(armorPerk(ArmorPerk.BLAST), blank(), Items.OBSIDIAN, Items.OBSIDIAN, Items.GOLD_INGOT, Items.GOLD_INGOT));
            list.add(shapeless(armorPerk(ArmorPerk.FIRE), blank(), Items.NETHERRACK, Items.FIRE_CHARGE, Items.BLAZE_ROD));
            list.add(shapeless(armorPerk(ArmorPerk.MAGIC), blank(), Items.GHAST_TEAR, Items.MAGMA_CREAM, Items.FERMENTED_SPIDER_EYE));
            list.add(shapeless(armorPerk(ArmorPerk.PROJECTILE), blank(), Items.BRICKS, Items.BRICKS, Items.GOLD_INGOT, Items.GOLD_INGOT));
            for (Supplier<List<Recipe>> extra : EXTRA) {
                list.addAll(extra.get());
            }
            recipes = List.copyOf(list);
        }
        return recipes;
    }

    public static Optional<Recipe> find(List<ItemStack> grid) {
        return all().stream().filter(r -> r.matches(grid)).findFirst();
    }

    // --- builders (also used by the tool/armor phases) ---

    /** {@code keys} alternates a char and an {@link ItemLike}. */
    public static Shaped shaped(Supplier<ItemStack> result, List<String> rows, Object... keys) {
        return shaped(result, List::of, rows, keys);
    }

    public static Shaped shaped(Supplier<ItemStack> result, Supplier<List<ItemStack>> perks, List<String> rows, Object... keys) {
        int width = rows.getFirst().length();
        List<Optional<RecipeInput>> pattern = new ArrayList<>();
        for (String row : rows) {
            for (char c : row.toCharArray()) {
                pattern.add(Optional.ofNullable(lookup(c, keys)));
            }
        }
        return new Shaped(width, rows.size(), pattern, result, perks);
    }

    public static Shapeless shapeless(Supplier<ItemStack> result, ItemLike... inputs) {
        return shapeless(result, List::of, inputs);
    }

    public static Shapeless shapeless(Supplier<ItemStack> result, Supplier<List<ItemStack>> perks, ItemLike... inputs) {
        List<RecipeInput> list = new ArrayList<>();
        for (ItemLike input : inputs) {
            list.add(RecipeInput.of(input));
        }
        return new Shapeless(list, result, perks);
    }

    private static @Nullable RecipeInput lookup(char c, Object... keys) {
        for (int i = 0; i < keys.length; i += 2) {
            if ((char) keys[i] == c) {
                return RecipeInput.of((ItemLike) keys[i + 1]);
            }
        }
        return null;
    }

    private static ItemLike blank() {
        return ModItems.toolPerk(ToolPerk.BLANK);
    }

    private static Supplier<ItemStack> toolPerk(ToolPerk perk) {
        return () -> new ItemStack(ModItems.toolPerk(perk));
    }

    private static Supplier<ItemStack> armorPerk(ArmorPerk perk) {
        return () -> new ItemStack(ModItems.armorPerk(perk));
    }
}
