package geoactivity.machine.crafting;

import geoactivity.item.NoCraftingRepair;
import geoactivity.machine.MachineBlockEntity;
import geoactivity.perk.ArmorPerk;
import geoactivity.perk.PerkData;
import geoactivity.perk.PerkableArmor;
import geoactivity.perk.PerkableTool;
import geoactivity.perk.ToolPerk;
import geoactivity.registry.ModBlockEntities;
import geoactivity.registry.ModDataComponents;
import geoactivity.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Crafting Machine: a fuelled 3×3 crafter for the {@link CraftingMachineRecipes}. 80 ticks per craft.
 * Slots: 0 result preview, 1 fuel, 2 output, 3 perk, 4-12 grid. A tool/armor perk in slot 3 is put on the result.
 */
public class CraftingMachineBlockEntity extends MachineBlockEntity {
    public static final int PREVIEW = 0, FUEL = 1, OUTPUT = 2, PERK = 3, GRID_START = 4;
    public static final int CRAFT_TIME = 80;

    int burnTime;
    int burnDuration;
    int cookTime;
    /** Result of the craft in progress; the grid was already consumed. */
    ItemStack pending = ItemStack.EMPTY;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> cookTime;
                case 1 -> burnTime;
                case 2 -> burnDuration;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> cookTime = value;
                case 1 -> burnTime = value;
                case 2 -> burnDuration = value;
                default -> { }
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    public CraftingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRAFTING_MACHINE.get(), pos, state, 13);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CraftingMachineBlockEntity be) {
        boolean dirty = false;
        if (be.burnTime > 0) {
            be.burnTime--;
        }
        be.updatePreview();
        if (be.pending.isEmpty() && be.canStart() && be.burnTime >= CRAFT_TIME) {
            be.pending = be.getItem(PREVIEW).copy();
            be.consumeGrid();
            dirty = true;
        }
        if (be.burnTime == 0 && (!be.pending.isEmpty() || be.canStart())) {
            ItemStack fuel = be.getItem(FUEL);
            be.burnDuration = be.burnTime = fuelTime(fuel);
            if (be.burnTime > 0) {
                fuel.shrink(1);
                dirty = true;
            }
        }
        if (be.burnTime > 0 && be.getItem(OUTPUT).isEmpty() && !be.pending.isEmpty()) {
            be.cookTime++;
            if (be.cookTime >= CRAFT_TIME) {
                be.cookTime = 0;
                be.finish();
                dirty = true;
            }
        } else {
            be.cookTime = 0;
        }
        if (dirty) {
            be.setChanged();
        }
    }

    public List<ItemStack> grid() {
        List<ItemStack> grid = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            grid.add(getItem(GRID_START + i));
        }
        return grid;
    }

    void updatePreview() {
        ItemStack result = findResult(grid());
        if (!ItemStack.matches(result, getItem(PREVIEW))) {
            setItem(PREVIEW, result);
        }
    }

    private boolean canStart() {
        return !getItem(PREVIEW).isEmpty() && getItem(OUTPUT).isEmpty();
    }

    private void consumeGrid() {
        for (int i = 0; i < 9; i++) {
            getItem(GRID_START + i).shrink(1);
        }
    }

    private void finish() {
        ItemStack result = pending;
        pending = ItemStack.EMPTY;
        applyPerk(result, getItem(PERK));
        setItem(OUTPUT, result);
    }

    /** Puts the perk from the perk slot on a perkable result and uses it up. Replaces any previous perk. */
    static void applyPerk(ItemStack result, ItemStack perkStack) {
        if (perkStack.isEmpty()) {
            return;
        }
        String key = null;
        if (result.getItem() instanceof PerkableTool) {
            for (ToolPerk perk : ToolPerk.values()) {
                if (perk != ToolPerk.BLANK && perk != ToolPerk.DROP_TELEPORT && perkStack.is(ModItems.toolPerk(perk))) {
                    key = perk.key();
                }
            }
        } else if (result.getItem() instanceof PerkableArmor) {
            if (perkStack.is(ModItems.toolPerk(ToolPerk.EFFICIENCY))) {
                key = ToolPerk.EFFICIENCY.key();
            }
            for (ArmorPerk perk : ArmorPerk.values()) {
                if (perkStack.is(ModItems.armorPerk(perk))) {
                    key = perk.key();
                }
            }
        }
        if (key != null) {
            result.set(ModDataComponents.PERKS, PerkData.of(key, 1));
            perkStack.shrink(1);
        }
    }

    /** Recipe result, or the 1.10.2 repair rule: two of the same damageable item → one with both durabilities + 5%. */
    public static ItemStack findResult(List<ItemStack> grid) {
        List<ItemStack> nonEmpty = grid.stream().filter(s -> !s.isEmpty()).toList();
        if (nonEmpty.size() == 2) {
            ItemStack a = nonEmpty.get(0);
            ItemStack b = nonEmpty.get(1);
            if (a.is(b.getItem()) && a.getCount() == 1 && b.getCount() == 1 && a.isDamageableItem()
                    && !(a.getItem() instanceof NoCraftingRepair)) {
                int max = a.getMaxDamage();
                int durability = (max - a.getDamageValue()) + (max - b.getDamageValue()) + max * 5 / 100;
                ItemStack repaired = new ItemStack(a.getItem());
                repaired.setDamageValue(Math.max(0, max - durability));
                return repaired;
            }
        }
        return CraftingMachineRecipes.find(grid).map(CraftingMachineRecipes.Recipe::result).orElse(ItemStack.EMPTY);
    }

    public static int fuelTime(ItemStack stack) {
        if (stack.is(Items.COAL_BLOCK)) return 82;
        if (stack.is(ModItems.LIGNITE_COAL.get())) return 164;
        if (stack.is(ModItems.BITUMINOUS_COAL.get())) return 328;
        if (stack.is(ModItems.ANTHRACITE_COAL.get())) return 490;
        return 0;
    }

    public static boolean isFuel(ItemStack stack) {
        return fuelTime(stack) > 0;
    }

    public static boolean isPerk(ItemStack stack) {
        return ModItems.TOOL_PERKS.values().stream().anyMatch(p -> stack.is(p.get()))
                || ModItems.ARMOR_PERKS.values().stream().anyMatch(p -> stack.is(p.get()));
    }

    public ContainerData data() {
        return data;
    }

    @Override
    protected void loadState(ValueInput input) {
        burnTime = input.getIntOr("burn_time", 0);
        burnDuration = input.getIntOr("burn_duration", 0);
        cookTime = input.getIntOr("cook_time", 0);
        pending = input.read("pending", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveState(ValueOutput output) {
        output.putInt("burn_time", burnTime);
        output.putInt("burn_duration", burnDuration);
        output.putInt("cook_time", cookTime);
        if (!pending.isEmpty()) {
            output.store("pending", ItemStack.OPTIONAL_CODEC, pending);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        // the preview is not a real item; an in-progress craft drops its result (its inputs are already used)
        setItem(PREVIEW, ItemStack.EMPTY);
        if (level != null && !pending.isEmpty()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), pending);
            pending = ItemStack.EMPTY;
        }
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[] {FUEL, OUTPUT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return slot == FUEL && isFuel(stack);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        // menus and hoppers both go through this; the grid and perk slots are filled by players only
        return slot == FUEL ? isFuel(stack) : slot == PERK ? isPerk(stack) : slot >= GRID_START;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == OUTPUT;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new CraftingMachineMenu(id, inventory, this, data);
    }
}
