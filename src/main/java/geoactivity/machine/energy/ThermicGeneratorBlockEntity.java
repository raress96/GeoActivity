package geoactivity.machine.energy;

import geoactivity.registry.ModBlockEntities;
import geoactivity.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import org.jspecify.annotations.Nullable;

/**
 * Thermic Generator: 20 FE/t for each Thermal Hardened Brick on its top or sides that can see the sky, in daytime.
 * Buffer 10k; pushes up to 1000 FE/t into the block below (the only side it connects to).
 */
public class ThermicGeneratorBlockEntity extends BlockEntity {
    public static final int PER_BRICK = 20, CAPACITY = 10_000, MAX_OUTPUT = 1000;

    final MachineEnergy energy = new MachineEnergy(CAPACITY, 0, MAX_OUTPUT, this::setChanged);
    int bricks;

    public ThermicGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THERMIC_GENERATOR.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ThermicGeneratorBlockEntity be) {
        if (level.getGameTime() % 20 == 0) {
            be.bricks = countBricks(level, pos);
        }
        int produced = PER_BRICK * be.bricks;
        if (be.bricks > 0 && level.isBrightOutside() && be.energy.amount() <= CAPACITY - produced) {
            be.energy.add(produced);
        }
        if (be.energy.amount() > 0) {
            EnergyHandler below = level.getCapability(Capabilities.Energy.BLOCK, pos.below(), Direction.UP);
            EnergyHandlerUtil.move(be.energy, below, Math.min(be.energy.amount(), MAX_OUTPUT), null);
        }
    }

    public static int countBricks(Level level, BlockPos pos) {
        int count = 0;
        for (Direction dir : new Direction[] {Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            BlockPos brick = pos.relative(dir);
            if (level.getBlockState(brick).is(ModBlocks.THERMAL_HARDENED_BRICK.get()) && level.canSeeSky(brick.above())) {
                count++;
            }
        }
        return count;
    }

    public int energyStored() {
        return energy.amount();
    }

    public @Nullable EnergyHandler energyFor(@Nullable Direction side) {
        return side == null || side == Direction.DOWN ? energy : null;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy.deserialize(input);
        bricks = input.getIntOr("bricks", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energy.serialize(output);
        output.putInt("bricks", bricks);
    }
}
