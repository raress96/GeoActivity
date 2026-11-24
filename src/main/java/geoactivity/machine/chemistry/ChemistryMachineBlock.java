package geoactivity.machine.chemistry;

import geoactivity.machine.MachineBlock;
import geoactivity.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

/** Chemistry Machine. Doesn't tick: crafting is triggered by a coal block placed on top. */
public class ChemistryMachineBlock extends MachineBlock<ChemistryMachineBlockEntity> {
    public ChemistryMachineBlock(Properties properties) {
        super(properties, ModBlockEntities.CHEMISTRY_MACHINE);
    }

    @Override
    protected @Nullable BlockEntityTicker<ChemistryMachineBlockEntity> serverTicker() {
        return null;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ChemistryMachineBlockEntity be) {
            be.tryCraft();
        }
    }
}
