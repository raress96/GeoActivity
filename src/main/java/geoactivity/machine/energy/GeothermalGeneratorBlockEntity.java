package geoactivity.machine.energy;

import geoactivity.registry.ModBlockEntities;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import org.jspecify.annotations.Nullable;

/**
 * Geothermal Generator. Every 1200 ticks it counts the lava source blocks connected to its sides (within 25 blocks)
 * and makes lava × 4 × efficiency% FE/t, efficiency = clamp(120 − y, 1, 100). Two generators sharing a pool both
 * get half. Halfway through each cycle, lava in the 3×3×3 around it may turn to obsidian (40% per column).
 * Buffer 50k; outputs down, then up, at max(2 × FE/t, 80) per tick.
 */
public class GeothermalGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INTERVAL = 1200, MAX_DISTANCE_SQ = 625, PER_LAVA = 4, OBSIDIAN_CHANCE = 40, CAPACITY = 50_000;

    final MachineEnergy energy = new MachineEnergy(CAPACITY, 0, 80, this::setChanged);
    int ticks = INTERVAL / 2;
    int lavaBlocks;
    int efficiency = 1;
    int perTick;

    private final ContainerData data = new SplitIntData(() -> lavaBlocks, () -> efficiency, () -> perTick, energy::amount, () -> ticks);

    public GeothermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GEOTHERMAL_GENERATOR.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GeothermalGeneratorBlockEntity be) {
        if (be.ticks == 0) {
            be.scan(level, pos);
        }
        be.ticks++;
        if (be.ticks == INTERVAL / 2 && be.lavaBlocks > 0) {
            be.lavaToObsidian(level, pos);
        }
        if (be.ticks >= INTERVAL) {
            be.ticks = 0;
        }
        if (be.perTick > 0 && be.energy.amount() <= CAPACITY - be.perTick) {
            be.energy.add(be.perTick);
        }
        if (be.energy.amount() > 0) {
            int budget = Math.min(be.energy.amount(), be.energy.maxExtract());
            budget -= EnergyHandlerUtil.move(be.energy, level.getCapability(Capabilities.Energy.BLOCK, pos.below(), Direction.UP), budget, null);
            EnergyHandlerUtil.move(be.energy, level.getCapability(Capabilities.Energy.BLOCK, pos.above(), Direction.DOWN), budget, null);
        }
    }

    void scan(Level level, BlockPos pos) {
        lavaBlocks = 0;
        efficiency = Math.max(1, Math.min(120 - pos.getY(), 100));
        // iterative flood fill (1.10.2 recursed and could overflow the stack on big lakes)
        Set<BlockPos> seen = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            queue.add(pos.relative(dir));
        }
        while (!queue.isEmpty()) {
            BlockPos at = queue.poll();
            if (seen.contains(at)) {
                continue;
            }
            BlockState state = level.getBlockState(at);
            if (isLavaSource(state)) {
                if (pos.distSqr(at) < MAX_DISTANCE_SQ) {
                    seen.add(at);
                    lavaBlocks++;
                    for (Direction dir : Direction.values()) {
                        queue.add(at.relative(dir));
                    }
                }
            } else if (!at.equals(pos) && level.getBlockEntity(at) instanceof GeothermalGeneratorBlockEntity other) {
                seen.add(at);
                lavaBlocks /= 2;
                other.lavaBlocks /= 2;
                other.updateRates();
            }
        }
        updateRates();
        setChanged();
    }

    static boolean isLavaSource(BlockState state) {
        return state.is(Blocks.LAVA) && state.getFluidState().isSource();
    }

    void updateRates() {
        perTick = (int) (lavaBlocks * PER_LAVA * (efficiency / 100f));
        int transfer = Math.max(perTick * 2, 80);
        energy.setLimits(0, transfer);
    }

    private void lavaToObsidian(Level level, BlockPos pos) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos target = pos.offset(x, y, z);
                    if (isLavaSource(level.getBlockState(target)) && level.getRandom().nextInt(100) < OBSIDIAN_CHANCE) {
                        level.setBlockAndUpdate(target, Blocks.OBSIDIAN.defaultBlockState());
                        break;
                    }
                }
            }
        }
    }

    public int energyStored() {
        return energy.amount();
    }

    public int lavaBlocks() {
        return lavaBlocks;
    }

    public int perTick() {
        return perTick;
    }

    public @Nullable EnergyHandler energyFor(@Nullable Direction side) {
        return side == null || side == Direction.UP || side == Direction.DOWN ? energy : null;
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new GeothermalGeneratorMenu(id, inventory, data, this);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy.deserialize(input);
        ticks = input.getIntOr("ticks", INTERVAL / 2);
        lavaBlocks = input.getIntOr("lava_blocks", 0);
        efficiency = input.getIntOr("efficiency", 1);
        updateRates();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        energy.serialize(output);
        output.putInt("ticks", ticks);
        output.putInt("lava_blocks", lavaBlocks);
        output.putInt("efficiency", efficiency);
    }
}
