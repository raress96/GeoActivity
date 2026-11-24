package geoactivity.client.screen;

import geoactivity.machine.chemistry.ChemistryMachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ChemistryMachineScreen extends MachineScreen<ChemistryMachineMenu> {
    public ChemistryMachineScreen(ChemistryMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "chemistry_machine");
    }
}
