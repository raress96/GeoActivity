package geoactivity;

import com.mojang.logging.LogUtils;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModCreativeTabs;
import geoactivity.registry.ModDataComponents;
import geoactivity.registry.ModItems;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(GeoActivity.MOD_ID)
public final class GeoActivity {
    public static final String MOD_ID = "geoactivity";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GeoActivity(IEventBus modBus, ModContainer container) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModDataComponents.COMPONENTS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
