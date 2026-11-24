package geoactivity;

import geoactivity.client.screen.AdvancedCoalRefinerScreen;
import geoactivity.client.screen.AdvancedOreRefinerScreen;
import geoactivity.client.screen.AtomExtractorScreen;
import geoactivity.client.screen.ChemistryMachineScreen;
import geoactivity.client.screen.CoalRefinerScreen;
import geoactivity.client.screen.CraftingMachineScreen;
import geoactivity.client.screen.GeothermalGeneratorScreen;
import geoactivity.client.screen.ToolChargerScreen;
import geoactivity.client.screen.ToolScreen;
import geoactivity.client.screen.AutoStoneBuilderScreen;
import geoactivity.registry.ModMenus;
import geoactivity.util.Tooltips;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Client-only entrypoint. Never loaded on a dedicated server. */
@Mod(value = GeoActivity.MOD_ID, dist = Dist.CLIENT)
public final class GeoActivityClient {
    public GeoActivityClient(IEventBus modBus, ModContainer container) {
        Tooltips.shiftDown = () -> Minecraft.getInstance().hasShiftDown();
        modBus.addListener(GeoActivityClient::registerScreens);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.COAL_REFINER.get(), CoalRefinerScreen::new);
        event.register(ModMenus.CRAFTING_MACHINE.get(), CraftingMachineScreen::new);
        event.register(ModMenus.ATOM_EXTRACTOR.get(), AtomExtractorScreen::new);
        event.register(ModMenus.CHEMISTRY_MACHINE.get(), ChemistryMachineScreen::new);
        event.register(ModMenus.ADVANCED_COAL_REFINER.get(), AdvancedCoalRefinerScreen::new);
        event.register(ModMenus.ADVANCED_ORE_REFINER.get(), AdvancedOreRefinerScreen::new);
        event.register(ModMenus.GEOTHERMAL_GENERATOR.get(), GeothermalGeneratorScreen::new);
        event.register(ModMenus.TOOL_CHARGER.get(), ToolChargerScreen::new);
        event.register(ModMenus.TOOL.get(), ToolScreen::new);
        event.register(ModMenus.AUTO_STONE_BUILDER.get(), AutoStoneBuilderScreen::new);
    }
}
