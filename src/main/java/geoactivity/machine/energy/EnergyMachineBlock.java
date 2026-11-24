package geoactivity.machine.energy;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/** A plain (non-rotating) energy machine block: server ticker, and a menu if its block entity provides one. */
public class EnergyMachineBlock<E extends BlockEntity> extends BaseEntityBlock {
    private final Supplier<BlockEntityType<E>> type;
    private final BlockEntityTicker<E> ticker;

    public EnergyMachineBlock(Properties properties, Supplier<BlockEntityType<E>> type, BlockEntityTicker<E> ticker) {
        super(properties);
        this.type = type;
        this.ticker = ticker;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return type.get().create(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> actual) {
        return level.isClientSide() || actual != type.get() ? null : (BlockEntityTicker<T>) ticker;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown() || !(level.getBlockEntity(pos) instanceof MenuProvider provider)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            player.openMenu(provider, pos);
        }
        return InteractionResult.SUCCESS;
    }
}
