package geoactivity.machine.energy;

import geoactivity.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

/** Info-only menu: lava blocks, efficiency, FE/t, stored energy, next scan. Shows the player inventory like 1.10.2. */
public class GeothermalGeneratorMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final @Nullable BlockEntity be;

    public GeothermalGeneratorMenu(int id, Inventory inventory) {
        this(id, inventory, SplitIntData.client(5), null);
    }

    public GeothermalGeneratorMenu(int id, Inventory inventory, ContainerData data, @Nullable BlockEntity be) {
        super(ModMenus.GEOTHERMAL_GENERATOR.get(), id);
        this.data = data;
        this.be = be;
        addDataSlots(data);
        addStandardInventorySlots(inventory, 8, 84);
    }

    public int lavaBlocks() {
        return SplitIntData.read(data, 0);
    }

    public int efficiency() {
        return SplitIntData.read(data, 1);
    }

    public int perTick() {
        return SplitIntData.read(data, 2);
    }

    public int energy() {
        return SplitIntData.read(data, 3);
    }

    public int secondsToScan() {
        return (GeothermalGeneratorBlockEntity.INTERVAL - SplitIntData.read(data, 4)) / 20;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return be == null || (!be.isRemoved() && player.isWithinBlockInteractionRange(be.getBlockPos(), 4.0));
    }
}
