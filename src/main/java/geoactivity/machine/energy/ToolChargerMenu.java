package geoactivity.machine.energy;

import geoactivity.machine.MachineMenu;
import geoactivity.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;

public class ToolChargerMenu extends MachineMenu {
    public ToolChargerMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(1), SplitIntData.client(1));
    }

    public ToolChargerMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.TOOL_CHARGER.get(), id, container, data);
        addSlot(new FilteredSlot(container, 0, 80, 35, ToolChargerBlockEntity::isChargeable) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addPlayerInventory(inventory);
    }

    public int energy() {
        return SplitIntData.read(data, 0);
    }
}
