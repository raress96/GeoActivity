package geoactivity.machine.refiner;

import geoactivity.machine.ExperienceOutputSlot;
import geoactivity.machine.MachineMenu;
import geoactivity.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class CoalRefinerMenu extends MachineMenu {
    public CoalRefinerMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(3), new SimpleContainerData(3));
    }

    public CoalRefinerMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.COAL_REFINER.get(), id, container, data);
        addSlot(new FilteredSlot(container, CoalRefinerBlockEntity.INPUT, 66, 35, CoalRefinerRecipes::isInput));
        addSlot(new FilteredSlot(container, CoalRefinerBlockEntity.FUEL, 30, 35, CoalRefinerRecipes::isFuel));
        addSlot(new ExperienceOutputSlot(inventory.player, container, CoalRefinerBlockEntity.OUTPUT, 126, 35,
                CoalRefinerRecipes::experienceFor));
        addPlayerInventory(inventory);
    }

    public int cookProgress(int pixels) {
        return data(0) * pixels / CoalRefinerBlockEntity.COOK_TIME;
    }

    public int burnLeft(int pixels) {
        int duration = data(2) == 0 ? 400 : data(2);
        return data(1) * pixels / duration;
    }

    public boolean isBurning() {
        return data(1) > 0;
    }
}
