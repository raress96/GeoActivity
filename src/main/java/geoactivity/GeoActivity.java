package geoactivity;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(GeoActivity.MOD_ID)
public final class GeoActivity {
    public static final String MOD_ID = "geoactivity";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GeoActivity(IEventBus modBus, ModContainer container) {
    }
}
