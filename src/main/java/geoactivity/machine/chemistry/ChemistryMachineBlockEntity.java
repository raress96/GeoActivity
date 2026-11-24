package geoactivity.machine.chemistry;

import geoactivity.machine.MachineBlockEntity;
import geoactivity.registry.ModBlockEntities;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

/**
 * Chemistry Machine: two input slots. Crafts when a coal block is put on top, the four diagonal neighbours are
 * Advanced Hardened Bricks, and item frames on those bricks show the element containers the recipe needs.
 * Each element group needs its own brick (frames on one brick: one element type).
 */
public class ChemistryMachineBlockEntity extends MachineBlockEntity {
    public static final int[][] BRICK_OFFSETS = {{-1, -1}, {1, -1}, {1, 1}, {-1, 1}};

    public ChemistryMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHEMISTRY_MACHINE.get(), pos, state, 2);
    }

    public static boolean hasBricks(Level level, BlockPos pos) {
        for (int[] offset : BRICK_OFFSETS) {
            if (!level.getBlockState(pos.offset(offset[0], 0, offset[1])).is(ModBlocks.ADVANCED_HARDENED_BRICK.get())) {
                return false;
            }
        }
        return true;
    }

    /** Item frames hanging on the brick at {@code offset} (same boxes as 1.10.2). */
    public static List<ItemFrame> framesOn(Level level, BlockPos pos, int[] offset) {
        double x = pos.getX() + offset[0];
        double z = pos.getZ() + offset[1];
        AABB box = new AABB(x - 0.3, pos.getY() - 1, z - 0.3, x + 1.3, pos.getY() + 1, z + 1.3);
        return level.getEntitiesOfClass(ItemFrame.class, box);
    }

    /** Called when a neighbour changes. Returns true if something was crafted. */
    public boolean tryCraft() {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return false;
        }
        BlockPos above = worldPosition.above();
        if (!level.getBlockState(above).is(Blocks.COAL_BLOCK) || !hasBricks(level, worldPosition)) {
            return false;
        }
        var recipe = ChemistryRecipes.find(getItem(0), getItem(1)).orElse(null);
        if (recipe == null) {
            return false;
        }
        List<List<ItemFrame>> groups = new ArrayList<>();
        for (int[] offset : BRICK_OFFSETS) {
            List<ItemFrame> frames = framesOn(level, worldPosition, offset);
            groups.add(frames.size() < 5 ? frames : List.of());
        }
        Set<Integer> used = new HashSet<>();
        for (ChemistryRecipes.ElementGroup group : recipe.groups()) {
            Integer brick = findBrick(groups, used, group);
            if (brick == null) {
                return false;
            }
            used.add(brick);
        }
        for (int brick : used) {
            groups.get(brick).forEach(frame -> frame.setItem(ItemStack.EMPTY));
        }
        getItem(0).shrink(1);
        getItem(1).shrink(1);
        setChanged();
        level.removeBlock(above, false);
        level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(),
                recipe.result().get()));
        return true;
    }

    private static @Nullable Integer findBrick(List<List<ItemFrame>> groups, Set<Integer> used, ChemistryRecipes.ElementGroup group) {
        var item = ModItems.element(group.element());
        for (int i = 0; i < groups.size(); i++) {
            if (used.contains(i) || groups.get(i).isEmpty()) {
                continue;
            }
            long count = groups.get(i).stream().filter(f -> f.getItem().is(item)).count();
            if (count == group.count()) {
                return i;
            }
        }
        return null;
    }

    @Override
    protected void loadState(ValueInput input) {
    }

    @Override
    protected void saveState(ValueOutput output) {
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new ChemistryMachineMenu(id, inventory, this);
    }

    static SimpleContainerData noData() {
        return new SimpleContainerData(0);
    }
}
