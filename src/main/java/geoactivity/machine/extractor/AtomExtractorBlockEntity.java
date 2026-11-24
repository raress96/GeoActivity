package geoactivity.machine.extractor;

import geoactivity.machine.MachineBlock;
import geoactivity.machine.MachineBlockEntity;
import geoactivity.registry.ModBlockEntities;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Atom Extractor: breaks an item into element containers. Slots: 0 input, 1 empty containers, 2-4 outputs.
 * Each Advanced Hardened Brick touching it (max 4) shortens the time by time/8 + 10 ticks.
 */
public class AtomExtractorBlockEntity extends MachineBlockEntity {
    public static final int INPUT = 0, CONTAINERS = 1, OUT_FIRST = 2;

    int cookTime;
    int totalTime;
    /** The item being processed (taken out of the input slot when a cycle starts). */
    ItemStack processing = ItemStack.EMPTY;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? cookTime : totalTime;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                cookTime = value;
            } else {
                totalTime = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public AtomExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ATOM_EXTRACTOR.get(), pos, state, 5);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AtomExtractorBlockEntity be) {
        boolean wasWorking = be.cookTime > 0;
        boolean dirty = false;
        if (be.cookTime == 0 && be.processing.isEmpty()) {
            var recipe = be.startableRecipe();
            if (recipe != null) {
                be.totalTime = upgradedTime(recipe.time(), countUpgrades(level, pos));
                be.processing = be.getItem(INPUT).copyWithCount(1);
                be.getItem(INPUT).shrink(1);
                be.getItem(CONTAINERS).shrink(recipe.containersNeeded());
                dirty = true;
            }
        }
        if (!be.processing.isEmpty()) {
            be.cookTime++;
            if (be.cookTime >= be.totalTime) {
                be.cookTime = 0;
                be.totalTime = 0;
                be.finish();
                dirty = true;
            }
        } else {
            be.cookTime = 0;
            be.totalTime = 0;
        }
        if (wasWorking != be.cookTime > 0) {
            dirty = true;
            MachineBlock.setLit(level, pos, state, be.cookTime > 0);
        }
        if (dirty) {
            be.setChanged();
        }
    }

    private AtomExtractorRecipes.@Nullable Recipe startableRecipe() {
        var recipe = AtomExtractorRecipes.find(getItem(INPUT)).orElse(null);
        if (recipe == null) {
            return null;
        }
        ItemStack containers = getItem(CONTAINERS);
        if (!containers.is(ModItems.EMPTY_ELEMENT_CONTAINER.get()) || containers.getCount() < recipe.containersNeeded()) {
            return null;
        }
        List<AtomExtractorRecipes.Output> outputs = recipe.outputs();
        for (int i = 0; i < outputs.size(); i++) {
            if (!canMerge(OUT_FIRST + i, outputs.get(i).stack())) {
                return null;
            }
        }
        return recipe;
    }

    private void finish() {
        AtomExtractorRecipes.find(processing).ifPresent(recipe -> {
            List<AtomExtractorRecipes.Output> outputs = recipe.outputs();
            for (int i = 0; i < outputs.size(); i++) {
                ItemStack result = outputs.get(i).stack();
                if (canMerge(OUT_FIRST + i, result)) {
                    merge(OUT_FIRST + i, result);
                }
            }
        });
        processing = ItemStack.EMPTY;
    }

    /** 1.10.2: time - time*upgrades/8 - 10*upgrades, at least 1. */
    public static int upgradedTime(int time, int upgrades) {
        int result = time - time * upgrades / 8 - 10 * upgrades;
        return Math.max(result, 1);
    }

    /** Advanced Hardened Bricks on the 6 faces, max 4. */
    public static int countUpgrades(Level level, BlockPos pos) {
        int count = 0;
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).is(ModBlocks.ADVANCED_HARDENED_BRICK.get())) {
                count++;
            }
        }
        return Math.min(count, 4);
    }

    public ContainerData data() {
        return data;
    }

    @Override
    protected void loadState(ValueInput input) {
        cookTime = input.getIntOr("cook_time", 0);
        totalTime = input.getIntOr("total_time", 0);
        processing = input.read("processing", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveState(ValueOutput output) {
        output.putInt("cook_time", cookTime);
        output.putInt("total_time", totalTime);
        if (!processing.isEmpty()) {
            output.store("processing", ItemStack.OPTIONAL_CODEC, processing);
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return (slot == CONTAINERS && stack.is(ModItems.EMPTY_ELEMENT_CONTAINER.get()))
                || (slot == INPUT && AtomExtractorRecipes.isInput(stack));
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot >= OUT_FIRST;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new AtomExtractorMenu(id, inventory, this, data);
    }
}
