package geoactivity.tool;

import geoactivity.registry.ModMenus;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Auto Stone Builder GUI: coal (80,34), source (53,58), target (107,58). Recomputes the conversion on close. */
public class AutoStoneBuilderMenu extends AbstractContainerMenu {
    private final Player player;
    private final int toolSlot;
    private final SimpleContainer container = new SimpleContainer(AutoStoneBuilderItem.SLOTS);

    public AutoStoneBuilderMenu(int id, Inventory inventory, int toolSlot) {
        super(ModMenus.AUTO_STONE_BUILDER.get(), id);
        this.player = inventory.player;
        this.toolSlot = toolSlot;
        NonNullList<ItemStack> items = ToolLogic.inventory(inventory.getItem(toolSlot), AutoStoneBuilderItem.SLOTS);
        for (int i = 0; i < items.size(); i++) {
            container.setItem(i, items.get(i));
        }
        addSlot(new Slot(container, 0, 80, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ToolLogic.isCoal(stack);
            }
        });
        addSlot(single(1, 53, 58));
        addSlot(single(2, 107, 58));
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
                    return index != AutoStoneBuilderMenu.this.toolSlot;
                }
            });
        }
    }

    private Slot single(int index, int x, int y) {
        return new Slot(container, index, x, y) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        ItemStack tool = player.getInventory().getItem(toolSlot);
        if (player.level().isClientSide() || !(tool.getItem() instanceof AutoStoneBuilderItem)) {
            return;
        }
        NonNullList<ItemStack> items = NonNullList.withSize(AutoStoneBuilderItem.SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, container.getItem(i));
        }
        ToolLogic.setInventory(tool, items);
        AutoStoneBuilderItem.search(tool, player.getInventory(), (ServerLevel) player.level());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getInventory().getItem(toolSlot).getItem() instanceof AutoStoneBuilderItem;
    }
}
