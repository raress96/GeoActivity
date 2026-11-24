package geoactivity.tool;

import geoactivity.registry.ModMenus;
import geoactivity.registry.ModItems;
import geoactivity.perk.ToolPerk;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Inventory of the held GUI tool (fuel and perk slots), stored on the stack. Changes are written back when the menu
 * closes; Redstone tools then recompute their perks. The held tool can't be moved while the menu is open.
 */
public class ToolMenu extends AbstractContainerMenu {
    private final Player player;
    private final int toolSlot;
    private final ToolProfile profile;
    private final SimpleContainer container;

    public ToolMenu(int id, Inventory inventory, int toolSlot) {
        super(ModMenus.TOOL.get(), id);
        this.player = inventory.player;
        this.toolSlot = toolSlot;
        ItemStack tool = inventory.getItem(toolSlot);
        this.profile = tool.getItem() instanceof PerkTool perkTool ? perkTool.profile() : ToolProfiles.REINFORCED_MINER;
        int size = profile.inventorySize();
        this.container = new SimpleContainer(size);
        NonNullList<ItemStack> items = ToolLogic.inventory(tool, size);
        for (int i = 0; i < size; i++) {
            container.setItem(i, items.get(i));
        }
        if (profile.perkSource() == ToolProfile.PerkSource.CRAFTED) {
            container.setItem(profile.fuelSlots(), installedPerk(tool));
        }
        for (int i = 0; i < size; i++) {
            int[] pos = profile.slotPositions().get(i);
            addSlot(slotFor(i, pos[0], pos[1]));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            int index = col;
            addSlot(new Slot(inventory, col, 8 + col * 18, 142) {
                @Override
                public boolean mayPickup(Player p) {
                    return index != toolSlot;
                }
            });
        }
    }

    public ToolProfile profile() {
        return profile;
    }

    public ItemStack tool() {
        return player.getInventory().getItem(toolSlot);
    }

    private Slot slotFor(int index, int x, int y) {
        boolean fuel = index < profile.fuelSlots();
        boolean display = profile.perkSource() == ToolProfile.PerkSource.CRAFTED && !fuel;
        return new Slot(container, index, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !display && (fuel ? ToolLogic.isCoal(stack) : ToolLogic.isToolPerk(stack));
            }

            @Override
            public boolean mayPickup(Player p) {
                return !display;
            }
        };
    }

    /** Advanced tools show their perk (read-only) like 1.10.2. */
    private static ItemStack installedPerk(ItemStack tool) {
        var perks = ToolLogic.perks(tool);
        for (ToolPerk perk : ToolPerk.values()) {
            if (perks.has(perk)) {
                return new ItemStack(ModItems.toolPerk(perk));
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player.level().isClientSide()) {
            return;
        }
        ItemStack tool = tool();
        if (!(tool.getItem() instanceof PerkTool)) {
            return;
        }
        NonNullList<ItemStack> items = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, container.getItem(i));
        }
        if (profile.perkSource() == ToolProfile.PerkSource.CRAFTED) {
            items.set(profile.fuelSlots(), ItemStack.EMPTY);
        }
        ToolLogic.setInventory(tool, items);
        if (profile.perkSource() == ToolProfile.PerkSource.INVENTORY) {
            ToolLogic.recomputeInventoryPerks(tool, profile);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int toolSlots = container.getContainerSize();
        if (index < toolSlots) {
            if (!moveItemStackTo(stack, toolSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean moved = false;
            for (int i = 0; i < toolSlots && !stack.isEmpty(); i++) {
                if (slots.get(i).mayPlace(stack) && moveItemStackTo(stack, i, i + 1, false)) {
                    moved = true;
                }
            }
            if (!moved) {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return stack.getCount() == original.getCount() ? ItemStack.EMPTY : original;
    }

    @Override
    public boolean stillValid(Player player) {
        return tool().getItem() instanceof PerkTool;
    }
}
