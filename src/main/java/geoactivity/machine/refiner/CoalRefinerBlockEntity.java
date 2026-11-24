package geoactivity.machine.refiner;

import geoactivity.machine.MachineBlock;
import geoactivity.machine.MachineBlockEntity;
import geoactivity.registry.ModBlockEntities;
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

/** Coal Refiner: a furnace for the coal recipes. 400 ticks per item. Slots: 0 input, 1 fuel, 2 output. */
public class CoalRefinerBlockEntity extends MachineBlockEntity {
    public static final int INPUT = 0, FUEL = 1, OUTPUT = 2;
    public static final int COOK_TIME = 400;

    int burnTime;
    int burnDuration;
    int cookTime;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> cookTime;
                case 1 -> burnTime;
                case 2 -> burnDuration;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> cookTime = value;
                case 1 -> burnTime = value;
                case 2 -> burnDuration = value;
                default -> { }
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public CoalRefinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COAL_REFINER.get(), pos, state, 3);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CoalRefinerBlockEntity be) {
        boolean wasBurning = be.burnTime > 0;
        boolean dirty = false;
        if (be.burnTime > 0) {
            be.burnTime--;
        }
        if (be.burnTime == 0 && be.canSmelt()) {
            ItemStack fuel = be.getItem(FUEL);
            be.burnDuration = be.burnTime = CoalRefinerRecipes.fuelTime(fuel);
            if (be.burnTime > 0) {
                dirty = true;
                fuel.shrink(1);
            }
        }
        if (be.burnTime > 0 && be.canSmelt()) {
            be.cookTime++;
            if (be.cookTime == COOK_TIME) {
                be.cookTime = 0;
                be.smelt();
                dirty = true;
            }
        } else {
            be.cookTime = 0;
        }
        if (wasBurning != be.burnTime > 0) {
            dirty = true;
            MachineBlock.setLit(level, pos, state, be.burnTime > 0);
        }
        if (dirty) {
            be.setChanged();
        }
    }

    private boolean canSmelt() {
        return CoalRefinerRecipes.find(getItem(INPUT)).map(r -> canMerge(OUTPUT, r.output())).orElse(false);
    }

    private void smelt() {
        CoalRefinerRecipes.find(getItem(INPUT)).ifPresent(r -> {
            merge(OUTPUT, r.output());
            getItem(INPUT).shrink(1);
        });
    }

    public ContainerData data() {
        return data;
    }

    @Override
    protected void loadState(ValueInput input) {
        burnTime = input.getIntOr("burn_time", 0);
        burnDuration = input.getIntOr("burn_duration", 0);
        cookTime = input.getIntOr("cook_time", 0);
    }

    @Override
    protected void saveState(ValueOutput output) {
        output.putInt("burn_time", burnTime);
        output.putInt("burn_duration", burnDuration);
        output.putInt("cook_time", cookTime);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return (slot == INPUT && CoalRefinerRecipes.isInput(stack)) || (slot == FUEL && CoalRefinerRecipes.isFuel(stack));
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == OUTPUT;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new CoalRefinerMenu(id, inventory, this, data);
    }
}
