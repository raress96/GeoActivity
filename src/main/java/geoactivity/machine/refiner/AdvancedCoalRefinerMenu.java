package geoactivity.machine.refiner;

import geoactivity.machine.ExperienceOutputSlot;
import geoactivity.machine.MachineMenu;
import geoactivity.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class AdvancedCoalRefinerMenu extends MachineMenu {
    public AdvancedCoalRefinerMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(7), new SimpleContainerData(4));
    }

    public AdvancedCoalRefinerMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.ADVANCED_COAL_REFINER.get(), id, container, data);
        addSlot(new FilteredSlot(container, AdvancedCoalRefinerBlockEntity.FUEL, 41, 33, AdvancedCoalRefinerBlockEntity::isFuel));
        addSlot(new FilteredSlot(container, AdvancedCoalRefinerBlockEntity.IN_1, 80, 15, CoalRefinerRecipes::isInput));
        addSlot(new FilteredSlot(container, AdvancedCoalRefinerBlockEntity.IN_2, 80, 50, CoalRefinerRecipes::isInput));
        addSlot(new ExperienceOutputSlot(inventory.player, container, AdvancedCoalRefinerBlockEntity.OUT_1, 117, 33, CoalRefinerRecipes::experienceFor));
        addSlot(new ExperienceOutputSlot(inventory.player, container, AdvancedCoalRefinerBlockEntity.OUT_2, 143, 33, CoalRefinerRecipes::experienceFor));
        addSlot(new FilteredSlot(container, AdvancedCoalRefinerBlockEntity.PERK_1, 8, 8, AdvancedCoalRefinerBlockEntity::isPerk));
        addSlot(new FilteredSlot(container, AdvancedCoalRefinerBlockEntity.PERK_2, 8, 28, AdvancedCoalRefinerBlockEntity::isPerk));
        addPlayerInventory(inventory);
    }

    public int cookProgress(int pixels) {
        return data(3) > 0 ? data(0) * pixels / data(3) : 0;
    }

    public int burnLeft(int pixels) {
        return data(2) > 0 ? data(1) * pixels / data(2) : 0;
    }

    public boolean isBurning() {
        return data(1) > 0;
    }

    public boolean hasInput(int lane) {
        return !container.getItem(AdvancedCoalRefinerBlockEntity.IN_1 + lane).isEmpty();
    }
}
