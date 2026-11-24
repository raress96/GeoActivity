package geoactivity.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Base for machines with an item inventory. Sided automation rules follow the 1.10.2 {@code ISidedInventory}
 * implementations: every slot is visible from every face, subclasses decide what may go in or out.
 */
public abstract class MachineBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    private NonNullList<ItemStack> items;
    private final int[] allSlots;

    protected MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
        super(type, pos, state);
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.allSlots = new int[size];
        for (int i = 0; i < size; i++) {
            allSlots[i] = i;
        }
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    protected Component getDefaultName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        loadState(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        saveState(output);
    }

    /** Machine-specific fields (burn time, progress, ...). */
    protected abstract void loadState(ValueInput input);

    protected abstract void saveState(ValueOutput output);

    @Override
    public int[] getSlotsForFace(Direction side) {
        return allSlots;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return canPlaceItemThroughFace(slot, stack, null);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
    }

    /** Adds {@code result} to {@code slot} if it fits (same item and components, within stack size). */
    protected boolean canMerge(int slot, ItemStack result) {
        if (result.isEmpty()) {
            return true;
        }
        ItemStack current = getItem(slot);
        if (current.isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(current, result)
                && current.getCount() + result.getCount() <= Math.min(getMaxStackSize(), current.getMaxStackSize());
    }

    protected void merge(int slot, ItemStack result) {
        if (result.isEmpty()) {
            return;
        }
        ItemStack current = getItem(slot);
        if (current.isEmpty()) {
            setItem(slot, result.copy());
        } else {
            current.grow(result.getCount());
        }
    }
}
