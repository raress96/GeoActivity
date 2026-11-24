package geoactivity.machine.orerefiner;

import geoactivity.machine.MultiblockBlockEntity;
import geoactivity.perk.MachinePerk;
import geoactivity.registry.ModBlockEntities;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import geoactivity.registry.ModTags;
import geoactivity.util.Smelting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.Tags;
import org.jspecify.annotations.Nullable;

/**
 * Advanced Ore Refiner: turns each ore into 2-3 of its smelting result (3-4 with a Carbon Ore Cutter).
 * Slots: 0-3 machine perks, 4-7 ores, 8-11 outputs, 12 fuel (new coals only), 13 logs, 14 carbon ore cutter.
 *
 * <p>Fuel works in two phases (1.10.2): burning 8 logs starts a heat-up where "heat" rises by 10 + 2·speed per tick
 * up to the coal's value; then one coal is used and the heat drains by 1 per tick while smelting.
 */
public class AdvancedOreRefinerBlockEntity extends MultiblockBlockEntity {
    public static final int PERK_FIRST = 0, ORE_FIRST = 4, OUT_FIRST = 8, FUEL = 12, WOOD = 13, CUTTER = 14;
    public static final int LOGS_PER_HEAT = 8, CUTTER_CYCLES = 16;

    int burnTime;
    int burnDuration;
    int cookTime;
    int special;
    boolean heating;
    int perkSpeed;
    int perkEfficiency;
    /** Member only: was this an Advanced Hardened Brick (else an Advanced Coal Refiner)? */
    boolean brick;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            AdvancedOreRefinerBlockEntity c = core() instanceof AdvancedOreRefinerBlockEntity core ? core : AdvancedOreRefinerBlockEntity.this;
            return switch (index) {
                case 0 -> c.cookTime;
                case 1 -> c.burnTime;
                case 2 -> c.burnDuration == 0 ? (int) (50 * c.baseTime()) : c.burnDuration;
                case 3 -> c.cookTotal();
                case 4 -> c.special;
                case 5 -> c.workingLanes();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
            return 6;
        }
    };

    public AdvancedOreRefinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ADVANCED_ORE_REFINER.get(), pos, state, 15);
    }

    // --- formation ---

    public static boolean tryForm(Level level, BlockPos core, Direction front) {
        List<AdvancedOreRefinerStructure.Part> parts = AdvancedOreRefinerStructure.find(level, core, front);
        if (parts == null) {
            return false;
        }
        List<BlockPos> members = new ArrayList<>();
        members.add(core);
        BlockState memberState = ModBlocks.ADVANCED_ORE_REFINER.get().defaultBlockState()
                .setValue(AdvancedOreRefinerBlock.CORE, false).setValue(AdvancedOreRefinerBlock.FORMED, true);
        for (var part : parts) {
            // an ACR cube that still holds items drops them before it becomes part of the refiner
            if (!part.brick() && level.getBlockEntity(part.pos()) instanceof MultiblockBlockEntity acr && acr.isCore()) {
                Containers.dropContents(level, part.pos(), acr);
            }
            level.setBlock(part.pos(), memberState, Block.UPDATE_ALL | Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS);
            if (level.getBlockEntity(part.pos()) instanceof AdvancedOreRefinerBlockEntity be) {
                be.brick = part.brick();
                be.setCore(core);
            }
            members.add(part.pos());
            level.invalidateCapabilities(part.pos());
        }
        if (level.getBlockEntity(core) instanceof AdvancedOreRefinerBlockEntity coreBe) {
            coreBe.setCore(core);
            coreBe.setMembers(members);
            level.setBlock(core, level.getBlockState(core).setValue(AdvancedOreRefinerBlock.FORMED, true), Block.UPDATE_ALL);
            level.invalidateCapabilities(core);
        }
        return true;
    }

    /** Puts every member back to its original block (brick or unformed ACR); the core stays, unformed. */
    @Override
    protected void breakStructure(BlockPos removed) {
        if (level == null) {
            return;
        }
        for (BlockPos pos : members()) {
            if (pos.equals(worldPosition) || pos.equals(removed)) {
                continue;
            }
            if (level.getBlockEntity(pos) instanceof AdvancedOreRefinerBlockEntity member) {
                level.setBlock(pos, member.originalBlock().defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS);
            }
        }
        invalidateAll();
        burnTime = burnDuration = cookTime = special = 0;
        heating = false;
        setMembers(List.of());
        setCore(null);
        if (!removed.equals(worldPosition)) {
            BlockState state = level.getBlockState(worldPosition);
            if (state.hasProperty(AdvancedOreRefinerBlock.FORMED)) {
                level.setBlock(worldPosition, state.setValue(AdvancedOreRefinerBlock.FORMED, false), Block.UPDATE_ALL);
            }
        }
    }

    Block originalBlock() {
        return brick ? ModBlocks.ADVANCED_HARDENED_BRICK.get() : ModBlocks.ADVANCED_COAL_REFINER.get();
    }

    // --- processing (core only) ---

    public static void serverTick(Level level, BlockPos pos, BlockState state, AdvancedOreRefinerBlockEntity be) {
        if (!be.isCore() || !(level instanceof ServerLevel server)) {
            return;
        }
        int before = be.burnTime;
        boolean dirty = false;
        ItemStack cutter = be.getItem(CUTTER);
        if (!cutter.isEmpty() && be.special == 0) {
            cutter.shrink(1);
            be.special = CUTTER_CYCLES;
            dirty = true;
        }
        if (be.burnTime > 0 && !be.heating) {
            be.burnTime--;
        }
        if (be.heating) {
            be.burnDuration = be.fuelTime(be.getItem(FUEL));
            if (be.burnTime < be.burnDuration) {
                be.burnTime += 10 + 2 * be.perkSpeed;
            } else {
                be.burnTime = 0;
                be.heating = false;
            }
        }
        if (!be.heating && be.canSmelt(server)) {
            if (be.getItem(WOOD).getCount() >= LOGS_PER_HEAT && be.burnTime == 0) {
                be.countPerks();
                be.burnDuration = be.fuelTime(be.getItem(FUEL));
                if (be.burnDuration > 0) {
                    be.getItem(WOOD).shrink(LOGS_PER_HEAT);
                    be.heating = true;
                    dirty = true;
                }
            }
            if (be.burnTime > 0) {
                be.cookTime++;
                if (be.cookTime >= be.cookTotal()) {
                    be.cookTime = 0;
                    be.smelt(server);
                    dirty = true;
                    if (be.special > 0) {
                        be.special--;
                    }
                }
            } else {
                be.cookTime = 0;
            }
        }
        if (be.heating && be.burnTime >= be.burnDuration) {
            be.getItem(FUEL).shrink(1);
            be.heating = false;
            dirty = true;
        }
        if (dirty || (before > 0) != (be.burnTime > 0)) {
            be.setChanged();
        }
    }

    double baseTime() {
        return 2.0 - 0.25 * perkSpeed;
    }

    int cookTotal() {
        return (int) (50 * baseTime());
    }

    private void countPerks() {
        perkSpeed = perkEfficiency = 0;
        for (int slot = PERK_FIRST; slot < ORE_FIRST; slot++) {
            ItemStack perk = getItem(slot);
            if (perk.is(ModItems.machinePerk(MachinePerk.SPEED))) {
                perkSpeed++;
            } else if (perk.is(ModItems.machinePerk(MachinePerk.EFFICIENCY))) {
                perkEfficiency++;
            }
        }
    }

    /** base × (2 − 0.25·speed) × (1 + 0.25·efficiency); lignite 1600, bituminous 3200, anthracite 4800. */
    int fuelTime(ItemStack stack) {
        int base = baseFuel(stack);
        return (int) (base * baseTime() * (1.0 + 0.25 * perkEfficiency));
    }

    static int baseFuel(ItemStack stack) {
        if (stack.is(ModItems.LIGNITE_COAL.get())) return 1600;
        if (stack.is(ModItems.BITUMINOUS_COAL.get())) return 3200;
        if (stack.is(ModItems.ANTHRACITE_COAL.get())) return 4800;
        return 0;
    }

    public static boolean isFuel(ItemStack stack) {
        return stack.is(ModTags.Items.COALS);
    }

    public static boolean isWood(ItemStack stack) {
        return stack.is(ItemTags.LOGS);
    }

    public static boolean isCutter(ItemStack stack) {
        return stack.is(ModItems.CARBON_ORE_CUTTER.get());
    }

    public static boolean isPerk(ItemStack stack) {
        return ModItems.MACHINE_PERKS.values().stream().anyMatch(p -> stack.is(p.get()));
    }

    /** Client-safe check: an ore item. The server also needs a smelting result. */
    public static boolean looksLikeOre(ItemStack stack) {
        return stack.is(Tags.BlockItems.ORES.item());
    }

    private ItemStack resultFor(ServerLevel level, int oreSlot) {
        ItemStack ore = getItem(oreSlot);
        return looksLikeOre(ore) ? Smelting.result(level, ore) : ItemStack.EMPTY;
    }

    private boolean laneReady(ServerLevel level, int lane) {
        ItemStack result = resultFor(level, ORE_FIRST + lane);
        return !result.isEmpty() && canMerge(OUT_FIRST + lane, result.copyWithCount(4));
    }

    private boolean canSmelt(ServerLevel level) {
        for (int lane = 0; lane < 4; lane++) {
            if (laneReady(level, lane)) {
                return true;
            }
        }
        return false;
    }

    private void smelt(ServerLevel level) {
        for (int lane = 0; lane < 4; lane++) {
            ItemStack result = resultFor(level, ORE_FIRST + lane);
            if (result.isEmpty()) {
                continue;
            }
            int count = special > 0 ? 3 + level.getRandom().nextInt(6) / 5 : 2 + level.getRandom().nextInt(4) / 3;
            ItemStack out = result.copyWithCount(count);
            if (canMerge(OUT_FIRST + lane, out)) {
                merge(OUT_FIRST + lane, out);
                getItem(ORE_FIRST + lane).shrink(1);
            }
        }
    }

    /** Bit mask of ore lanes that hold a smeltable ore (for the GUI arrows). */
    private int workingLanes() {
        if (!(level instanceof ServerLevel server)) {
            return 0;
        }
        int mask = 0;
        for (int lane = 0; lane < 4; lane++) {
            if (!resultFor(server, ORE_FIRST + lane).isEmpty()) {
                mask |= 1 << lane;
            }
        }
        return mask;
    }

    public ContainerData data() {
        return data;
    }

    @Override
    protected void loadMachineState(ValueInput input) {
        burnTime = input.getIntOr("burn_time", 0);
        burnDuration = input.getIntOr("burn_duration", 0);
        cookTime = input.getIntOr("cook_time", 0);
        special = input.getIntOr("special", 0);
        heating = input.getBooleanOr("heating", false);
        perkSpeed = input.getIntOr("perk_speed", 0);
        perkEfficiency = input.getIntOr("perk_efficiency", 0);
        brick = input.getBooleanOr("brick", false);
    }

    @Override
    protected void saveMachineState(ValueOutput output) {
        output.putInt("burn_time", burnTime);
        output.putInt("burn_duration", burnDuration);
        output.putInt("cook_time", cookTime);
        output.putInt("special", special);
        output.putBoolean("heating", heating);
        output.putInt("perk_speed", perkSpeed);
        output.putInt("perk_efficiency", perkEfficiency);
        output.putBoolean("brick", brick);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[] {4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (!isFormed()) {
            return false;
        }
        if (slot >= ORE_FIRST && slot < OUT_FIRST) {
            return looksLikeOre(stack) && (!(level instanceof ServerLevel server) || !Smelting.result(server, stack).isEmpty());
        }
        return switch (slot) {
            case FUEL -> isFuel(stack);
            case WOOD -> isWood(stack);
            case CUTTER -> isCutter(stack);
            case 0, 1, 2, 3 -> side == null && isPerk(stack);
            default -> false;
        };
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return isFormed() && slot >= OUT_FIRST && slot < FUEL;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new AdvancedOreRefinerMenu(id, inventory, this, data);
    }

    /** Members drop their original block; the core drops the refiner (loot table). */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null && isFormed() && !isCore()) {
            Block.popResource(level, pos, new ItemStack(originalBlock()));
        }
        super.preRemoveSideEffects(pos, state);
    }
}
