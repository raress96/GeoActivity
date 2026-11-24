package geoactivity.machine.energy;

import geoactivity.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Thermic Melter: drains 10 FE/t; every 800 ticks turns obsidian in a 5×5×5 cube back into lava at 50k FE each.
 * Buffer 200k, accepts 500 FE/t from any side.
 */
public class ThermicMelterBlockEntity extends BlockEntity {
    public static final int CAPACITY = 200_000, MAX_INPUT = 500, PASSIVE = 10, PER_OBSIDIAN = 50_000, INTERVAL = 800;

    final MachineEnergy energy = new MachineEnergy(CAPACITY, MAX_INPUT, 0, this::setChanged);
    int ticks = INTERVAL / 2;

    public ThermicMelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THERMIC_MELTER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ThermicMelterBlockEntity be) {
        if (be.energy.amount() >= PASSIVE) {
            be.energy.remove(PASSIVE);
        }
        be.ticks++;
        if (be.ticks >= INTERVAL) {
            if (be.energy.amount() >= PER_OBSIDIAN) {
                be.melt(level, pos);
            }
            be.ticks = 0;
            be.setChanged();
        }
    }

    /** Same loop as 1.10.2: at most one obsidian per (x, y) column, until the buffer drops below 50k. */
    private void melt(Level level, BlockPos pos) {
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos target = pos.offset(x, y, z);
                    if (level.getBlockState(target).is(Blocks.OBSIDIAN)) {
                        level.setBlockAndUpdate(target, Blocks.LAVA.defaultBlockState());
                        energy.remove(PER_OBSIDIAN);
                        if (energy.amount() < PER_OBSIDIAN) {
                            return;
                        }
                        break;
                    }
                }
            }
        }
    }

    public int energyStored() {
        return energy.amount();
    }

    public MachineEnergy energy() {
        return energy;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy.deserialize(input);
        ticks = input.getIntOr("ticks", INTERVAL / 2);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energy.serialize(output);
        output.putInt("ticks", ticks);
    }
}
