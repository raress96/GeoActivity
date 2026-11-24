package geoactivity.machine;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Base menu: machine slots first, then the 27 + 9 player slots at the standard position (8, 84).
 * Shift-click moves machine → player, and player → the first machine slot that accepts the item.
 */
public abstract class MachineMenu extends AbstractContainerMenu {
    protected final Container container;
    protected final ContainerData data;
    private int machineSlots = -1;

    protected MachineMenu(MenuType<?> type, int id, Container container, ContainerData data) {
        super(type, id);
        this.container = container;
        this.data = data;
        addDataSlots(data);
    }

    /** Call after adding the machine slots. */
    protected void addPlayerInventory(Inventory inventory) {
        addPlayerInventory(inventory, 84);
    }

    protected void addPlayerInventory(Inventory inventory, int top) {
        machineSlots = slots.size();
        addStandardInventorySlots(inventory, 8, top);
    }

    public int data(int index) {
        return data.get(index);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int playerStart = machineSlots;
        int hotbarStart = playerStart + 27;
        int end = playerStart + 36;
        if (index < machineSlots) {
            if (!moveItemStackTo(stack, playerStart, end, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, original);
        } else {
            boolean moved = false;
            for (int i = 0; i < machineSlots && !stack.isEmpty(); i++) {
                Slot target = slots.get(i);
                if (isQuickMoveTarget(i) && target.mayPlace(stack) && moveItemStackTo(stack, i, i + 1, false)) {
                    moved = true;
                }
            }
            if (!moved) {
                if (index < hotbarStart) {
                    if (!moveItemStackTo(stack, hotbarStart, end, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(stack, playerStart, hotbarStart, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    /** Whether shift-clicking from the player inventory may fill this machine slot. */
    protected boolean isQuickMoveTarget(int slotIndex) {
        return true;
    }

    /** Slot that only accepts items matching a predicate. */
    public static class FilteredSlot extends Slot {
        private final java.util.function.Predicate<ItemStack> filter;

        public FilteredSlot(Container container, int index, int x, int y, java.util.function.Predicate<ItemStack> filter) {
            super(container, index, x, y);
            this.filter = filter;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return filter.test(stack);
        }
    }

    /** Output slot: nothing can be put in. */
    public static class OutputSlot extends Slot {
        public OutputSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
