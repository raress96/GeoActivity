package geoactivity.registry;

import geoactivity.GeoActivity;
import geoactivity.machine.chemistry.ChemistryMachineMenu;
import geoactivity.machine.crafting.CraftingMachineMenu;
import geoactivity.machine.extractor.AtomExtractorMenu;
import geoactivity.machine.orerefiner.AdvancedOreRefinerMenu;
import geoactivity.machine.refiner.AdvancedCoalRefinerMenu;
import geoactivity.machine.refiner.CoalRefinerMenu;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, GeoActivity.MOD_ID);

    public static final Supplier<MenuType<CoalRefinerMenu>> COAL_REFINER = MENUS.register("coal_refiner",
            () -> IMenuTypeExtension.create((id, inv, buf) -> new CoalRefinerMenu(id, inv)));
    public static final Supplier<MenuType<CraftingMachineMenu>> CRAFTING_MACHINE = MENUS.register("crafting_machine",
            () -> IMenuTypeExtension.create((id, inv, buf) -> new CraftingMachineMenu(id, inv)));
    public static final Supplier<MenuType<AtomExtractorMenu>> ATOM_EXTRACTOR = MENUS.register("atom_extractor",
            () -> IMenuTypeExtension.create((id, inv, buf) -> new AtomExtractorMenu(id, inv)));
    public static final Supplier<MenuType<ChemistryMachineMenu>> CHEMISTRY_MACHINE = MENUS.register("chemistry_machine",
            () -> IMenuTypeExtension.create((id, inv, buf) -> new ChemistryMachineMenu(id, inv)));

    public static final Supplier<MenuType<AdvancedCoalRefinerMenu>> ADVANCED_COAL_REFINER = MENUS.register("advanced_coal_refiner",
            () -> IMenuTypeExtension.create((id, inv, buf) -> new AdvancedCoalRefinerMenu(id, inv)));
    public static final Supplier<MenuType<AdvancedOreRefinerMenu>> ADVANCED_ORE_REFINER = MENUS.register("advanced_ore_refiner",
            () -> IMenuTypeExtension.create((id, inv, buf) -> new AdvancedOreRefinerMenu(id, inv)));

    private ModMenus() {}
}
