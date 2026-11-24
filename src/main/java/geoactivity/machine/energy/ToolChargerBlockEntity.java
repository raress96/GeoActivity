package geoactivity.machine.energy;

import geoactivity.machine.MachineBlockEntity;
import geoactivity.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import org.jspecify.annotations.Nullable;

/** Tool Charger: stores 10M FE (20k FE/t in from any side) and charges the energy item in its slot every tick. */
public class ToolChargerBlockEntity extends MachineBlockEntity {
    public static final int CAPACITY = 10_000_000, MAX_TRANSFER = 20_000;

    final MachineEnergy energy = new MachineEnergy(CAPACITY, MAX_TRANSFER, MAX_TRANSFER, this::setChanged);
    private final ContainerData data = new SplitIntData(energy::amount);

    public ToolChargerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TOOL_CHARGER.get(), pos, state, 1);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ToolChargerBlockEntity be) {
        ItemStack tool = be.getItem(0);
        if (be.energy.amount() > 0 && !tool.isEmpty()) {
            EnergyHandler item = itemEnergy(tool);
            int moved = EnergyHandlerUtil.move(be.energy, item, Math.min(be.energy.amount(), MAX_TRANSFER), null);
            if (moved > 0) {
                be.setChanged();
            }
        }
    }

    static @Nullable EnergyHandler itemEnergy(ItemStack stack) {
        return stack.isEmpty() ? null : Capabilities.Energy.ITEM.getCapability(stack, ItemAccess.forStack(stack));
    }

    public static boolean isChargeable(ItemStack stack) {
        return itemEnergy(stack) != null;
    }

    public int energyStored() {
        return energy.amount();
    }

    /** External view: insert only. */
    public EnergyHandler energyInput() {
        return new net.neoforged.neoforge.transfer.energy.LimitingEnergyHandler(energy, MAX_TRANSFER, 0);
    }

    @Override
    protected void loadState(ValueInput input) {
        energy.deserialize(input);
    }

    @Override
    protected void saveState(ValueOutput output) {
        energy.serialize(output);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return isChargeable(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new ToolChargerMenu(id, inventory, this, data);
    }
}
