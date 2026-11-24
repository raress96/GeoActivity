package geoactivity.machine.chemistry;

import geoactivity.machine.MachineMenu;
import geoactivity.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;

public class ChemistryMachineMenu extends MachineMenu {
    public ChemistryMachineMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(2));
    }

    public ChemistryMachineMenu(int id, Inventory inventory, Container container) {
        super(ModMenus.CHEMISTRY_MACHINE.get(), id, container, new SimpleContainerData(0));
        addSlot(new Slot(container, 0, 53, 31));
        addSlot(new Slot(container, 1, 107, 31));
        addPlayerInventory(inventory);
    }
}
