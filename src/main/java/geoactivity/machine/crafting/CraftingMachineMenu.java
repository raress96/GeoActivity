package geoactivity.machine.crafting;

import geoactivity.machine.MachineMenu;
import geoactivity.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CraftingMachineMenu extends MachineMenu {
    public CraftingMachineMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(13), new SimpleContainerData(3));
    }

    public CraftingMachineMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.CRAFTING_MACHINE.get(), id, container, data);
        addSlot(new PreviewSlot(container, CraftingMachineBlockEntity.PREVIEW, 42, 62));
        addSlot(new FilteredSlot(container, CraftingMachineBlockEntity.FUEL, 89, 62, CraftingMachineBlockEntity::isFuel));
        addSlot(new OutputSlot(container, CraftingMachineBlockEntity.OUTPUT, 150, 42));
        addSlot(new FilteredSlot(container, CraftingMachineBlockEntity.PERK, 89, 24, CraftingMachineBlockEntity::isPerk));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(container, CraftingMachineBlockEntity.GRID_START + col + row * 3, 6 + col * 18, 6 + row * 18));
            }
        }
        addPlayerInventory(inventory);
    }

    @Override
    protected boolean isQuickMoveTarget(int slotIndex) {
        // like 1.10.2: shift-click fills the fuel and perk slots, never the grid
        return slotIndex < CraftingMachineBlockEntity.GRID_START;
    }

    public int craftProgress(int pixels) {
        return data(0) * pixels / CraftingMachineBlockEntity.CRAFT_TIME;
    }

    public int burnLeft(int pixels) {
        int duration = data(2) == 0 ? CraftingMachineBlockEntity.CRAFT_TIME : data(2);
        return data(1) * pixels / duration;
    }

    public boolean isBurning() {
        return data(1) > 0;
    }

    /** Shows the result of the current grid; can't be taken or filled. */
    static class PreviewSlot extends Slot {
        PreviewSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }
    }
}
