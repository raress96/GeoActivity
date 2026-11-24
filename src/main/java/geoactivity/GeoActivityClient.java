package geoactivity;

import geoactivity.util.Tooltips;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/** Client-only entrypoint. Never loaded on a dedicated server. */
@Mod(value = GeoActivity.MOD_ID, dist = Dist.CLIENT)
public final class GeoActivityClient {
    public GeoActivityClient(IEventBus modBus, ModContainer container) {
        Tooltips.shiftDown = () -> Minecraft.getInstance().hasShiftDown();
    }
}
