package geoactivity.registry;

import geoactivity.GeoActivity;
import geoactivity.machine.chemistry.ChemistryMachineBlockEntity;
import geoactivity.machine.crafting.CraftingMachineBlockEntity;
import geoactivity.machine.energy.GeothermalGeneratorBlockEntity;
import geoactivity.machine.energy.ThermicGeneratorBlockEntity;
import geoactivity.machine.energy.ThermicMelterBlockEntity;
import geoactivity.machine.energy.ToolChargerBlockEntity;
import geoactivity.machine.extractor.AtomExtractorBlockEntity;
import geoactivity.machine.orerefiner.AdvancedOreRefinerBlockEntity;
import geoactivity.machine.refiner.AdvancedCoalRefinerBlockEntity;
import geoactivity.machine.refiner.CoalRefinerBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, GeoActivity.MOD_ID);

    public static final Supplier<BlockEntityType<CoalRefinerBlockEntity>> COAL_REFINER = BLOCK_ENTITIES.register("coal_refiner",
            () -> new BlockEntityType<>(CoalRefinerBlockEntity::new, ModBlocks.COAL_REFINER.get()));
    public static final Supplier<BlockEntityType<CraftingMachineBlockEntity>> CRAFTING_MACHINE = BLOCK_ENTITIES.register("crafting_machine",
            () -> new BlockEntityType<>(CraftingMachineBlockEntity::new, ModBlocks.CRAFTING_MACHINE.get()));
    public static final Supplier<BlockEntityType<AtomExtractorBlockEntity>> ATOM_EXTRACTOR = BLOCK_ENTITIES.register("atom_extractor",
            () -> new BlockEntityType<>(AtomExtractorBlockEntity::new, ModBlocks.ATOM_EXTRACTOR.get()));
    public static final Supplier<BlockEntityType<ChemistryMachineBlockEntity>> CHEMISTRY_MACHINE = BLOCK_ENTITIES.register("chemistry_machine",
            () -> new BlockEntityType<>(ChemistryMachineBlockEntity::new, ModBlocks.CHEMISTRY_MACHINE.get()));

    public static final Supplier<BlockEntityType<AdvancedCoalRefinerBlockEntity>> ADVANCED_COAL_REFINER = BLOCK_ENTITIES.register(
            "advanced_coal_refiner", () -> new BlockEntityType<>(AdvancedCoalRefinerBlockEntity::new, ModBlocks.ADVANCED_COAL_REFINER.get()));
    public static final Supplier<BlockEntityType<AdvancedOreRefinerBlockEntity>> ADVANCED_ORE_REFINER = BLOCK_ENTITIES.register(
            "advanced_ore_refiner", () -> new BlockEntityType<>(AdvancedOreRefinerBlockEntity::new, ModBlocks.ADVANCED_ORE_REFINER.get()));

    public static final Supplier<BlockEntityType<ThermicGeneratorBlockEntity>> THERMIC_GENERATOR = BLOCK_ENTITIES.register(
            "thermic_generator", () -> new BlockEntityType<>(ThermicGeneratorBlockEntity::new, ModBlocks.THERMIC_GENERATOR.get()));
    public static final Supplier<BlockEntityType<ToolChargerBlockEntity>> TOOL_CHARGER = BLOCK_ENTITIES.register(
            "tool_charger", () -> new BlockEntityType<>(ToolChargerBlockEntity::new, ModBlocks.TOOL_CHARGER.get()));
    public static final Supplier<BlockEntityType<GeothermalGeneratorBlockEntity>> GEOTHERMAL_GENERATOR = BLOCK_ENTITIES.register(
            "geothermal_generator", () -> new BlockEntityType<>(GeothermalGeneratorBlockEntity::new, ModBlocks.GEOTHERMAL_GENERATOR.get()));
    public static final Supplier<BlockEntityType<ThermicMelterBlockEntity>> THERMIC_MELTER = BLOCK_ENTITIES.register(
            "thermic_melter", () -> new BlockEntityType<>(ThermicMelterBlockEntity::new, ModBlocks.THERMIC_MELTER.get()));

    private ModBlockEntities() {}

    /** Hoppers/pipes see machine inventories through their sided rules. */
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (var type : java.util.List.of(COAL_REFINER, CRAFTING_MACHINE, ATOM_EXTRACTOR, CHEMISTRY_MACHINE)) {
            event.registerBlockEntity(Capabilities.Item.BLOCK, type.get(), (be, side) -> new WorldlyContainerWrapper((WorldlyContainer) be, side));
        }
        event.registerBlockEntity(Capabilities.Item.BLOCK, TOOL_CHARGER.get(), (be, side) -> new WorldlyContainerWrapper(be, side));
        // energy: generators output on their sides, the melter and charger only accept
        event.registerBlockEntity(Capabilities.Energy.BLOCK, THERMIC_GENERATOR.get(), ThermicGeneratorBlockEntity::energyFor);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, GEOTHERMAL_GENERATOR.get(), GeothermalGeneratorBlockEntity::energyFor);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, THERMIC_MELTER.get(), (be, side) -> be.energy());
        event.registerBlockEntity(Capabilities.Energy.BLOCK, TOOL_CHARGER.get(), (be, side) -> be.energyInput());
        // the RF miner takes FE from chargers and other mods' power items (never gives it back)
        event.registerItem(Capabilities.Energy.ITEM, (stack, access) -> new net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler(
                access, ModDataComponents.ENERGY.get(), geoactivity.tool.ToolProfiles.RF_CAPACITY, geoactivity.tool.ToolProfiles.RF_MAX_INPUT, 0),
                ModItems.ADVANCED_REDSTONE_BATTLE_MINER.get());
        // multiblocks: only while formed (every member forwards to the core's inventory)
        for (var type : java.util.List.of(ADVANCED_COAL_REFINER, ADVANCED_ORE_REFINER)) {
            event.registerBlockEntity(Capabilities.Item.BLOCK, type.get(),
                    (be, side) -> be.isFormed() ? new WorldlyContainerWrapper(be, side) : null);
        }
    }
}
