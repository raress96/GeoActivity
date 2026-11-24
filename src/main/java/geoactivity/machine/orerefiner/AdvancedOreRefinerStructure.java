package geoactivity.machine.orerefiner;

import geoactivity.registry.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * The 28-block Advanced Ore Refiner structure behind the core (1.10.2 {@code AORTileE.makeMultiblock}), with
 * {@code back} = the direction away from the core's front and y relative to the core:
 * <pre>
 * depth 1: advanced bricks at y-1..y+2
 * depth 2: bricks at y-1 and y+2, air at y and y+1
 * depth 2-3, y..y+1, lateral ±1..±2: two 2×2×2 Advanced Coal Refiner cubes (16 blocks)
 * depth 3: bricks at y-1 and y+2, air at y and y+1
 * depth 4: bricks at y-1..y+2
 * </pre>
 */
public final class AdvancedOreRefinerStructure {
    public record Part(BlockPos pos, boolean brick) {}

    private AdvancedOreRefinerStructure() {}

    /** The 28 parts if the structure behind {@code core} (facing {@code front}) is complete, else null. */
    public static @Nullable List<Part> find(Level level, BlockPos core, Direction front) {
        Direction back = front.getOpposite();
        Direction side = front.getClockWise();
        List<Part> parts = new ArrayList<>(28);
        // depth 1
        for (int y = -1; y <= 2; y++) {
            if (!brick(level, core.relative(back, 1).above(y), parts)) return null;
        }
        // depths 2 and 3: brick ring around an air gap
        for (int depth = 2; depth <= 3; depth++) {
            BlockPos center = core.relative(back, depth);
            if (!brick(level, center.below(), parts) || !brick(level, center.above(2), parts)) return null;
            if (!level.isEmptyBlock(center) || !level.isEmptyBlock(center.above())) return null;
        }
        // the two ACR cubes
        for (int y = 0; y <= 1; y++) {
            for (int lateral : new int[] {-2, -1, 1, 2}) {
                for (int depth = 2; depth <= 3; depth++) {
                    BlockPos pos = core.relative(back, depth).relative(side, lateral).above(y);
                    if (!level.getBlockState(pos).is(ModBlocks.ADVANCED_COAL_REFINER.get())) return null;
                    parts.add(new Part(pos, false));
                }
            }
        }
        // depth 4
        for (int y = -1; y <= 2; y++) {
            if (!brick(level, core.relative(back, 4).above(y), parts)) return null;
        }
        return parts;
    }

    private static boolean brick(Level level, BlockPos pos, List<Part> parts) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.ADVANCED_HARDENED_BRICK.get())) {
            return false;
        }
        parts.add(new Part(pos, true));
        return true;
    }
}
