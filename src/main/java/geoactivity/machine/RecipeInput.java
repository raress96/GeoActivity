package geoactivity.machine;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

/**
 * A machine recipe input: one item, or an item tag (1.10.2 recipes matched OreDictionary names, which are tags now).
 */
public record RecipeInput(@Nullable Item item, @Nullable TagKey<Item> tag) {
    public static RecipeInput of(ItemLike item) {
        return new RecipeInput(item.asItem(), null);
    }

    public static RecipeInput of(TagKey<Item> tag) {
        return new RecipeInput(null, tag);
    }

    public boolean test(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return item != null ? stack.is(item) : stack.is(tag);
    }

    /** Items to show for this input (JEI, tests). */
    public List<ItemStack> displayStacks() {
        if (item != null) {
            return List.of(new ItemStack(item));
        }
        var stacks = new java.util.ArrayList<ItemStack>();
        BuiltInRegistries.ITEM.getTagOrEmpty(tag).forEach(h -> stacks.add(new ItemStack(h)));
        return stacks;
    }
}
