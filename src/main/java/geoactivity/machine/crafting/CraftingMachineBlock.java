package geoactivity.machine.crafting;

import geoactivity.machine.MachineBlock;
import geoactivity.registry.ModBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public class CraftingMachineBlock extends MachineBlock<CraftingMachineBlockEntity> {
    public CraftingMachineBlock(Properties properties) {
        super(properties, ModBlockEntities.CRAFTING_MACHINE);
    }

    @Override
    protected BlockEntityTicker<CraftingMachineBlockEntity> serverTicker() {
        return CraftingMachineBlockEntity::serverTick;
    }
}
