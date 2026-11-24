package geoactivity.machine.extractor;

import geoactivity.machine.MachineMenu;
import geoactivity.registry.ModItems;
import geoactivity.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class AtomExtractorMenu extends MachineMenu {
    public AtomExtractorMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(5), new SimpleContainerData(2));
    }

    public AtomExtractorMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.ATOM_EXTRACTOR.get(), id, container, data);
        addSlot(new FilteredSlot(container, AtomExtractorBlockEntity.INPUT, 22, 30, AtomExtractorRecipes::isInput));
        addSlot(new FilteredSlot(container, AtomExtractorBlockEntity.CONTAINERS, 50, 55,
                s -> s.is(ModItems.EMPTY_ELEMENT_CONTAINER.get())));
        addSlot(new OutputSlot(container, 2, 86, 30));
        addSlot(new OutputSlot(container, 3, 112, 30));
        addSlot(new OutputSlot(container, 4, 138, 30));
        addPlayerInventory(inventory);
    }

    public int progress(int pixels) {
        return data(1) > 0 ? data(0) * pixels / data(1) : 0;
    }
}
