package geoactivity.registry;

import geoactivity.GeoActivity;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GeoActivity.MOD_ID);

    public static final Supplier<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.geoactivity"))
            .icon(() -> ModItems.LIGNITE_COAL.get().getDefaultInstance())
            .displayItems((params, output) -> {
                Set<Item> elements = elementItems();
                for (DeferredHolder<Item, ? extends Item> entry : ModItems.ITEMS.getEntries()) {
                    if (!elements.contains(entry.get())) {
                        output.accept(entry.get());
                    }
                }
            })
            .build());

    public static final Supplier<CreativeModeTab> ELEMENTS = TABS.register("elements", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.geoactivity.elements"))
            .icon(() -> ModItems.EMPTY_ELEMENT_CONTAINER.get().getDefaultInstance())
            .withTabsBefore(GeoActivity.id("main"))
            .displayItems((params, output) -> {
                output.accept(ModItems.EMPTY_ELEMENT_CONTAINER.get());
                ModItems.ELEMENT_CONTAINERS.values().forEach(item -> output.accept(item.get()));
            })
            .build());

    private ModCreativeTabs() {}

    private static Set<Item> elementItems() {
        var set = new java.util.HashSet<Item>();
        set.add(ModItems.EMPTY_ELEMENT_CONTAINER.get());
        ModItems.ELEMENT_CONTAINERS.values().forEach(item -> set.add(item.get()));
        return set;
    }
}
