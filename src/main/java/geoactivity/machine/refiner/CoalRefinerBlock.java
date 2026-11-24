package geoactivity.machine.refiner;

import geoactivity.machine.MachineBlock;
import geoactivity.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public class CoalRefinerBlock extends MachineBlock<CoalRefinerBlockEntity> {
    public CoalRefinerBlock(Properties properties) {
        super(properties, ModBlockEntities.COAL_REFINER);
    }

    @Override
    protected BlockEntityTicker<CoalRefinerBlockEntity> serverTicker() {
        return CoalRefinerBlockEntity::serverTick;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        var facing = state.getValue(FACING);
        double x = pos.getX() + 0.5 + facing.getStepX() * 0.52;
        double y = pos.getY() + random.nextDouble() * 6.0 / 16.0;
        double z = pos.getZ() + 0.5 + facing.getStepZ() * 0.52;
        double side = random.nextDouble() * 0.6 - 0.3;
        x += facing.getAxis() == net.minecraft.core.Direction.Axis.Z ? side : 0;
        z += facing.getAxis() == net.minecraft.core.Direction.Axis.X ? side : 0;
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
        level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
    }
}
