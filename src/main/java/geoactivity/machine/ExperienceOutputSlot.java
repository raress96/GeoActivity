package geoactivity.machine;

import java.util.function.ToDoubleFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Output slot that pops XP when the player takes items (1.10.2 {@code CRSlot}): {@code xpPerItem × count}, with the
 * fractional part rounded up randomly.
 */
public class ExperienceOutputSlot extends MachineMenu.OutputSlot {
    private final Player player;
    private final ToDoubleFunction<ItemStack> xpPerItem;
    private int removeCount;

    public ExperienceOutputSlot(Player player, Container container, int index, int x, int y, ToDoubleFunction<ItemStack> xpPerItem) {
        super(container, index, x, y);
        this.player = player;
        this.xpPerItem = xpPerItem;
    }

    @Override
    public ItemStack remove(int amount) {
        if (hasItem()) {
            removeCount += Math.min(amount, getItem().getCount());
        }
        return super.remove(amount);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        checkTakeAchievements(stack);
        super.onTake(player, stack);
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        removeCount += amount;
        checkTakeAchievements(stack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(player, removeCount);
        if (player.level() instanceof ServerLevel level && removeCount > 0) {
            double total = removeCount * xpPerItem.applyAsDouble(stack);
            int xp = Mth.floor(total);
            if (xp < Mth.ceil(total) && Math.random() < total - xp) {
                xp++;
            }
            if (xp > 0) {
                ExperienceOrb.award(level, player.position(), xp);
            }
        }
        removeCount = 0;
    }
}
