package geoactivity.machine.orerefiner;

import geoactivity.machine.MachineMenu;
import geoactivity.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class AdvancedOreRefinerMenu extends MachineMenu {
    /** The panel is 40 px taller than the default 166. */
    public static final int HEIGHT = 206;

    public AdvancedOreRefinerMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(15), new SimpleContainerData(6));
    }

    public AdvancedOreRefinerMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.ADVANCED_ORE_REFINER.get(), id, container, data);
        for (int i = 0; i < 4; i++) {
            addSlot(new FilteredSlot(container, i, 8, 8 + 20 * i, AdvancedOreRefinerBlockEntity::isPerk));
        }
        int[][] ore = {{41, 18}, {125, 18}, {41, 58}, {125, 58}};
        int[][] out = {{74, 18}, {92, 18}, {74, 58}, {92, 58}};
        for (int i = 0; i < 4; i++) {
            addSlot(new FilteredSlot(container, AdvancedOreRefinerBlockEntity.ORE_FIRST + i, ore[i][0], ore[i][1],
                    AdvancedOreRefinerBlockEntity::looksLikeOre));
        }
        for (int i = 0; i < 4; i++) {
            addSlot(new OutputSlot(container, AdvancedOreRefinerBlockEntity.OUT_FIRST + i, out[i][0], out[i][1]));
        }
        addSlot(new FilteredSlot(container, AdvancedOreRefinerBlockEntity.FUEL, 134, 99, AdvancedOreRefinerBlockEntity::isFuel));
        addSlot(new FilteredSlot(container, AdvancedOreRefinerBlockEntity.WOOD, 98, 99, AdvancedOreRefinerBlockEntity::isWood));
        addSlot(new FilteredSlot(container, AdvancedOreRefinerBlockEntity.CUTTER, 8, 99, AdvancedOreRefinerBlockEntity::isCutter));
        addPlayerInventory(inventory, 124);
    }

    public int cookProgress(int pixels) {
        return data(3) > 0 ? data(0) * pixels / data(3) : 0;
    }

    public int heat(int pixels) {
        return data(2) > 0 ? Math.min(pixels, data(1) * pixels / data(2)) : 0;
    }

    public int special() {
        return data(4);
    }

    public boolean laneWorking(int lane) {
        return (data(5) & (1 << lane)) != 0;
    }
}
