package geoactivity.machine.refiner;

import geoactivity.machine.MultiblockBlockEntity;
import geoactivity.perk.MachinePerk;
import geoactivity.registry.ModBlockEntities;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Advanced Coal Refiner: a 2×2×2 cube of ACR blocks. The block placed last becomes the core. Two lanes of Coal
 * Refiner recipes. Slots: 0 fuel, 1-2 inputs, 3-4 outputs, 5-6 machine perks.
 */
public class AdvancedCoalRefinerBlockEntity extends MultiblockBlockEntity {
    public static final int FUEL = 0, IN_1 = 1, IN_2 = 2, OUT_1 = 3, OUT_2 = 4, PERK_1 = 5, PERK_2 = 6;

    int burnTime;
    int burnDuration;
    int cookTime;
    int perkSpeed;
    int perkEfficiency;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            AdvancedCoalRefinerBlockEntity core = core() instanceof AdvancedCoalRefinerBlockEntity c ? c : AdvancedCoalRefinerBlockEntity.this;
            return switch (index) {
                case 0 -> core.cookTime;
                case 1 -> core.burnTime;
                case 2 -> core.burnDuration;
                case 3 -> core.cookTotal();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
            return 4;
        }
    };

    public AdvancedCoalRefinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ADVANCED_COAL_REFINER.get(), pos, state, 7);
    }

    // --- formation ---

    /** Tries to form a cube that contains {@code placed}; the placed block becomes the core. */
    public static boolean tryForm(Level level, BlockPos placed) {
        for (int dx = -1; dx <= 0; dx++) {
            for (int dy = -1; dy <= 0; dy++) {
                for (int dz = -1; dz <= 0; dz++) {
                    List<BlockPos> cube = cube(placed.offset(dx, dy, dz));
                    BlockPos opposite = cube.get(7 - cube.indexOf(placed));
                    if (cube.stream().allMatch(p -> isFreeAcr(level, p))
                            && acrNeighbours(level, placed) == 3 && acrNeighbours(level, opposite) == 3) {
                        form(level, placed, cube);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** The 8 positions of a cube at origin; index i and 7 - i are opposite corners. */
    static List<BlockPos> cube(BlockPos origin) {
        List<BlockPos> list = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            list.add(origin.offset(i & 1, (i >> 1) & 1, (i >> 2) & 1));
        }
        return list;
    }

    private static boolean isFreeAcr(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof AdvancedCoalRefinerBlockEntity be && !be.isFormed();
    }

    private static int acrNeighbours(Level level, BlockPos pos) {
        int count = 0;
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).is(ModBlocks.ADVANCED_COAL_REFINER.get())) {
                count++;
            }
        }
        return count;
    }

    private static void form(Level level, BlockPos core, List<BlockPos> cube) {
        for (BlockPos pos : cube) {
            if (level.getBlockEntity(pos) instanceof AdvancedCoalRefinerBlockEntity be) {
                be.setCore(core);
                AdvancedCoalRefinerBlock.setFormed(level, pos, true);
                level.invalidateCapabilities(pos);
            }
        }
        if (level.getBlockEntity(core) instanceof AdvancedCoalRefinerBlockEntity coreBe) {
            coreBe.setMembers(cube);
        }
    }

    @Override
    protected void breakStructure(BlockPos removed) {
        if (level == null) {
            return;
        }
        List<BlockPos> members = members();
        for (BlockPos pos : members) {
            if (!pos.equals(removed) && level.getBlockEntity(pos) instanceof AdvancedCoalRefinerBlockEntity be) {
                be.setCore(null);
                AdvancedCoalRefinerBlock.setFormed(level, pos, false);
            }
        }
        invalidateAll();
        burnTime = burnDuration = cookTime = 0;
        setMembers(List.of());
        setCore(null);
        if (!removed.equals(worldPosition)) {
            AdvancedCoalRefinerBlock.setFormed(level, worldPosition, false);
        }
    }

    // --- processing (core only) ---

    public static void serverTick(Level level, BlockPos pos, BlockState state, AdvancedCoalRefinerBlockEntity be) {
        if (!be.isCore()) {
            return;
        }
        boolean dirty = false;
        boolean wasBurning = be.burnTime > 0;
        if (be.burnTime > 0) {
            be.burnTime--;
        }
        if (be.burnTime == 0 && be.canSmelt()) {
            be.countPerks();
            ItemStack fuel = be.getItem(FUEL);
            be.burnDuration = be.burnTime = be.fuelTime(fuel);
            if (be.burnTime > 0) {
                fuel.shrink(1);
                dirty = true;
            }
        }
        if (be.burnTime > 0 && be.canSmelt()) {
            be.cookTime++;
            if (be.cookTime >= be.cookTotal()) {
                be.cookTime = 0;
                be.smelt();
                dirty = true;
            }
        } else {
            be.cookTime = 0;
        }
        if (dirty || wasBurning != be.burnTime > 0) {
            be.setChanged();
        }
    }

    int cookTotal() {
        return (int) (200 * (1.0 - 0.25 * perkSpeed));
    }

    private void countPerks() {
        perkSpeed = perkEfficiency = 0;
        for (int slot = PERK_1; slot <= PERK_2; slot++) {
            ItemStack perk = getItem(slot);
            if (perk.is(ModItems.machinePerk(MachinePerk.SPEED))) {
                perkSpeed++;
            } else if (perk.is(ModItems.machinePerk(MachinePerk.EFFICIENCY))) {
                perkEfficiency++;
            }
        }
    }

    /** base × (1 − 0.25·speed) × (1 + 0.5·efficiency); base coal 200, lignite 1600, bituminous 3200, anthracite 4800. */
    int fuelTime(ItemStack stack) {
        int base = baseFuel(stack);
        return (int) (base * (1.0 - 0.25 * perkSpeed) * (1.0 + 0.5 * perkEfficiency));
    }

    static int baseFuel(ItemStack stack) {
        if (stack.is(Items.COAL)) return 200;
        if (stack.is(ModItems.LIGNITE_COAL.get())) return 1600;
        if (stack.is(ModItems.BITUMINOUS_COAL.get())) return 3200;
        if (stack.is(ModItems.ANTHRACITE_COAL.get())) return 4800;
        return 0;
    }

    public static boolean isFuel(ItemStack stack) {
        return baseFuel(stack) > 0;
    }

    public static boolean isPerk(ItemStack stack) {
        return ModItems.MACHINE_PERKS.values().stream().anyMatch(p -> stack.is(p.get()));
    }

    private boolean laneReady(int in, int out) {
        return CoalRefinerRecipes.find(getItem(in)).map(r -> canMerge(out, r.output())).orElse(false);
    }

    private boolean canSmelt() {
        return laneReady(IN_1, OUT_1) || laneReady(IN_2, OUT_2);
    }

    private void smelt() {
        for (int lane = 0; lane < 2; lane++) {
            int in = IN_1 + lane;
            int out = OUT_1 + lane;
            if (laneReady(in, out)) {
                merge(out, CoalRefinerRecipes.find(getItem(in)).orElseThrow().output());
                getItem(in).shrink(1);
            }
        }
    }

    public ContainerData data() {
        return data;
    }

    @Override
    protected void loadMachineState(ValueInput input) {
        burnTime = input.getIntOr("burn_time", 0);
        burnDuration = input.getIntOr("burn_duration", 0);
        cookTime = input.getIntOr("cook_time", 0);
        perkSpeed = input.getIntOr("perk_speed", 0);
        perkEfficiency = input.getIntOr("perk_efficiency", 0);
    }

    @Override
    protected void saveMachineState(ValueOutput output) {
        output.putInt("burn_time", burnTime);
        output.putInt("burn_duration", burnDuration);
        output.putInt("cook_time", cookTime);
        output.putInt("perk_speed", perkSpeed);
        output.putInt("perk_efficiency", perkEfficiency);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[] {FUEL, IN_1, IN_2, OUT_1, OUT_2};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (!isFormed()) {
            return false;
        }
        return switch (slot) {
            case FUEL -> isFuel(stack);
            case IN_1, IN_2 -> CoalRefinerRecipes.isInput(stack);
            case PERK_1, PERK_2 -> side == null && isPerk(stack);
            default -> false;
        };
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return isFormed() && (slot == OUT_1 || slot == OUT_2);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new AdvancedCoalRefinerMenu(id, inventory, this, data);
    }

    /** Used by the Advanced Ore Refiner, which swallows ACR cubes. */
    public static boolean isAcr(BlockState state) {
        return state.is(ModBlocks.ADVANCED_COAL_REFINER.get());
    }

    static Block block() {
        return ModBlocks.ADVANCED_COAL_REFINER.get();
    }
}
