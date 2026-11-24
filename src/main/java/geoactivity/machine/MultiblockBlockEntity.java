package geoactivity.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Block entity of a multiblock member. One member is the core: it owns the inventory, ticks and remembers all member
 * positions. Every other member forwards its inventory to the core, so menus, hoppers and comparators work from any
 * block of the structure (like 1.10.2).
 */
public abstract class MultiblockBlockEntity extends MachineBlockEntity {
    private @Nullable BlockPos corePos;
    private List<BlockPos> members = List.of();

    protected MultiblockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
        super(type, pos, state, size);
    }

    public boolean isFormed() {
        return corePos != null;
    }

    public boolean isCore() {
        return worldPosition.equals(corePos);
    }

    public @Nullable MultiblockBlockEntity core() {
        if (corePos == null || level == null) {
            return null;
        }
        if (isCore()) {
            return this;
        }
        return level.getBlockEntity(corePos) instanceof MultiblockBlockEntity core && core.isCore() ? core : null;
    }

    /** Member positions (only meaningful on the core). */
    public List<BlockPos> members() {
        return members;
    }

    protected void setCore(@Nullable BlockPos core) {
        this.corePos = core == null ? null : core.immutable();
        setChanged();
    }

    protected void setMembers(List<BlockPos> members) {
        this.members = List.copyOf(members);
        setChanged();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        MultiblockBlockEntity core = core();
        return core != null && core != this ? core.getItems() : super.getItems();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        MultiblockBlockEntity core = core();
        if (core != null && core != this) {
            core.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return isFormed() && super.stillValid(player);
    }

    /**
     * Called when this block is removed: drop the shared inventory here and break the structure apart.
     * Subclasses restore/unform the other members in {@link #breakStructure}.
     */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        MultiblockBlockEntity core = core();
        if (level == null || core == null) {
            super.preRemoveSideEffects(pos, state);
            return;
        }
        Containers.dropContents(level, pos, this);
        core.clearContent();
        core.breakStructure(pos);
    }

    /** Core only: undo the structure; {@code removed} is the block being broken. */
    protected abstract void breakStructure(BlockPos removed);

    protected void invalidateAll() {
        if (level != null) {
            for (BlockPos member : members) {
                level.invalidateCapabilities(member);
            }
        }
    }

    @Override
    protected void loadState(ValueInput input) {
        corePos = input.read("core", BlockPos.CODEC).orElse(null);
        members = input.read("members", BlockPos.CODEC.listOf()).map(ArrayList::new).map(List::copyOf).orElse(List.of());
        loadMachineState(input);
    }

    @Override
    protected void saveState(ValueOutput output) {
        if (corePos != null) {
            output.store("core", BlockPos.CODEC, corePos);
        }
        if (!members.isEmpty()) {
            output.store("members", BlockPos.CODEC.listOf(), members);
        }
        saveMachineState(output);
    }

    protected abstract void loadMachineState(ValueInput input);

    protected abstract void saveMachineState(ValueOutput output);

    /** Sided rules are evaluated against the core. */
    public Optional<MultiblockBlockEntity> coreIfFormed() {
        return Optional.ofNullable(core());
    }
}
