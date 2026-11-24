package geoactivity.registry;

import geoactivity.GeoActivity;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    private ModTags() {}

    public static final class Blocks {
        public static final TagKey<Block> COAL_ORES = TagKey.create(Registries.BLOCK, GeoActivity.id("coal_ores"));

        private Blocks() {}
    }

    public static final class Items {
        public static final TagKey<Item> COAL_ORES = TagKey.create(Registries.ITEM, GeoActivity.id("coal_ores"));
        /** Lignite, bituminous and anthracite coal. */
        public static final TagKey<Item> COALS = TagKey.create(Registries.ITEM, GeoActivity.id("coals"));
        public static final TagKey<Item> FILLED_ELEMENT_CONTAINERS = TagKey.create(Registries.ITEM, GeoActivity.id("filled_element_containers"));

        private Items() {}
    }
}
