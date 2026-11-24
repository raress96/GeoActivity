package geoactivity.machine.extractor;

import geoactivity.machine.MachineBlock;
import geoactivity.registry.ModBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public class AtomExtractorBlock extends MachineBlock<AtomExtractorBlockEntity> {
    public AtomExtractorBlock(Properties properties) {
        super(properties, ModBlockEntities.ATOM_EXTRACTOR);
    }

    @Override
    protected BlockEntityTicker<AtomExtractorBlockEntity> serverTicker() {
        return AtomExtractorBlockEntity::serverTick;
    }
}
