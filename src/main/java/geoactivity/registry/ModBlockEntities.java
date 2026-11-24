package geoactivity.registry;

import geoactivity.GeoActivity;
import geoactivity.machine.chemistry.ChemistryMachineBlockEntity;
import geoactivity.machine.crafting.CraftingMachineBlockEntity;
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

    private ModBlockEntities() {}

    /** Hoppers/pipes see machine inventories through their sided rules. */
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (var type : java.util.List.of(COAL_REFINER, CRAFTING_MACHINE, ATOM_EXTRACTOR, CHEMISTRY_MACHINE)) {
            event.registerBlockEntity(Capabilities.Item.BLOCK, type.get(), (be, side) -> new WorldlyContainerWrapper((WorldlyContainer) be, side));
        }
        // multiblocks: only while formed (every member forwards to the core's inventory)
        for (var type : java.util.List.of(ADVANCED_COAL_REFINER, ADVANCED_ORE_REFINER)) {
            event.registerBlockEntity(Capabilities.Item.BLOCK, type.get(),
                    (be, side) -> be.isFormed() ? new WorldlyContainerWrapper(be, side) : null);
        }
    }
}
