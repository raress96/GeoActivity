package geoactivity.tool;

import geoactivity.GeoActivity;
import geoactivity.perk.PerkData;
import geoactivity.perk.ToolPerk;
import geoactivity.util.Smelting;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * The perk engine (1.10.2 {@code onBlockStartBreak} + {@code destroyBlock}):
 * <ul>
 *   <li>{@link #onBreak}: a destroyed tool can't break blocks; Wider Radius and Capitator break the extra blocks
 *   through the player's normal block breaking (so protection, drops, tool wear and events all apply).</li>
 *   <li>{@link #onDrops}: drop perks for every block a perk tool breaks: No Drops > Silk Touch > Fortune > Auto-Smelt,
 *   then Drop Teleportation; Experience gives +1 XP for ores that don't drop themselves.</li>
 *   <li>{@link #onIncomingDamage}: Armor Bypass, a third of a melee hit ignores the target's armor.</li>
 * </ul>
 */
@EventBusSubscriber(modid = GeoActivity.MOD_ID)
public final class ToolEvents {
    /** Players currently breaking extra blocks, so those breaks don't trigger more area breaks. */
    private static final Set<Player> AREA_BREAKING = Collections.newSetFromMap(new WeakHashMap<>());
    /** Tree felling limit (1.10.2 recursed without one). */
    private static final int MAX_TREE = 256;

    private ToolEvents() {}

    /**
     * Armor Bypass (1.10.2 only had the tooltip, "damage/3 armor bypass damage"): the target's armor reduces only
     * the other two thirds of a melee hit.
     */
    @SubscribeEvent
    static void onIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (!source.is(DamageTypes.PLAYER_ATTACK) || !(source.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack weapon = source.getWeaponItem();
        if (weapon != null && weapon.getItem() instanceof PerkTool tool && tool.isUsable(weapon, player)
                && ToolLogic.perks(weapon).has(ToolPerk.ARMOR_BYPASS)) {
            event.addReductionModifier(DamageContainer.Reduction.ARMOR, (container, reduction) -> reduction * 2.0F / 3.0F);
        }
    }

    @SubscribeEvent
    static void onBreak(BreakBlockEvent event) {
        if (event.isCanceled()) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof PerkTool tool) || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!tool.isUsable(stack, player)) {
            event.setCanceled(true);
            return;
        }
        if (AREA_BREAKING.contains(player)) {
            return;
        }
        List<BlockPos> extra = extraBlocks(serverPlayer, tool, stack, event.getPos(), event.getState());
        if (extra.isEmpty()) {
            return;
        }
        AREA_BREAKING.add(player);
        try {
            for (BlockPos pos : extra) {
                if (!tool.isUsable(stack, player) || stack != player.getMainHandItem()) {
                    break;
                }
                serverPlayer.gameMode.destroyBlock(pos);
            }
        } finally {
            AREA_BREAKING.remove(player);
        }
    }

    /** Blocks broken together with {@code origin} (not including it). */
    static List<BlockPos> extraBlocks(ServerPlayer player, PerkTool tool, ItemStack stack, BlockPos origin, BlockState originState) {
        PerkData perks = ToolLogic.perks(stack);
        ToolProfile profile = tool.profile();
        ServerLevel level = player.level();
        int radius = perks.level(ToolPerk.RADIUS);
        if (radius > 0 && profile.area() != ToolProfile.Area.NONE) {
            Direction face = hitFace(player, origin);
            int range = profile.area() == ToolProfile.Area.LEVELS ? radius : 1;
            int depth = range > 2 ? range / 2 : 0;
            int rx = face.getAxis() == Direction.Axis.X ? depth : range;
            int ry = face.getAxis() == Direction.Axis.Y ? depth : range;
            int rz = face.getAxis() == Direction.Axis.Z ? depth : range;
            List<BlockPos> list = new ArrayList<>();
            for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-rx, -ry, -rz), origin.offset(rx, ry, rz))) {
                if (pos.equals(origin)) {
                    continue;
                }
                BlockState state = level.getBlockState(pos);
                boolean same = profile.area() != ToolProfile.Area.SAME_BLOCK || state.is(originState.getBlock());
                if (same && canAreaBreak(profile, level, pos, state)) {
                    list.add(pos.immutable());
                }
            }
            return list;
        }
        if (perks.has(ToolPerk.CAPITATOR)) {
            return switch (profile.capitator()) {
                case NONE -> List.of();
                case TREE_SAME -> originState.is(BlockTags.LOGS) ? tree(level, origin, s -> s.is(originState.getBlock())) : List.of();
                case TREE_LOGS -> originState.is(BlockTags.LOGS) ? tree(level, origin, s -> s.is(BlockTags.LOGS)) : List.of();
                case COLUMN_SAME -> column(profile, level, origin, originState, 10, 9, true);
                case LOGS_OR_COLUMN -> originState.is(BlockTags.LOGS)
                        ? tree(level, origin, s -> s.is(BlockTags.LOGS))
                        : column(profile, level, origin, originState, 6, 5, false);
            };
        }
        return List.of();
    }

    /** Only blocks the tool could mine normally: breakable, right tool category and tier. */
    static boolean canAreaBreak(ToolProfile profile, ServerLevel level, BlockPos pos, BlockState state) {
        float hardness = state.getDestroySpeed(level, pos);
        return !state.isAir() && hardness > 0 && ToolLogic.isEffective(profile, state);
    }

    private static List<BlockPos> column(ToolProfile profile, ServerLevel level, BlockPos origin, BlockState originState,
            int up, int down, boolean sameOnly) {
        List<BlockPos> list = new ArrayList<>();
        for (int sign : new int[] {1, -1}) {
            int max = sign > 0 ? up : down;
            for (int i = 1; i <= max; i++) {
                BlockPos pos = origin.above(sign * i);
                BlockState state = level.getBlockState(pos);
                if (sameOnly && !state.is(originState.getBlock())) {
                    break;
                }
                if (canAreaBreak(profile, level, pos, state)) {
                    list.add(pos);
                }
            }
        }
        return list;
    }

    private static List<BlockPos> tree(ServerLevel level, BlockPos origin, java.util.function.Predicate<BlockState> matches) {
        Set<BlockPos> found = new LinkedHashSet<>();
        Set<BlockPos> seen = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>(List.of(origin));
        seen.add(origin);
        while (!queue.isEmpty() && found.size() < MAX_TREE) {
            BlockPos at = queue.poll();
            for (BlockPos next : BlockPos.betweenClosed(at.offset(-1, -1, -1), at.offset(1, 1, 1))) {
                BlockPos pos = next.immutable();
                if (seen.add(pos) && matches.test(level.getBlockState(pos))) {
                    found.add(pos);
                    queue.add(pos);
                }
            }
        }
        return new ArrayList<>(found);
    }

    /** The face the player is looking at (1.10.2 raytraced 4.5 blocks; defaults to UP). */
    private static Direction hitFace(ServerPlayer player, BlockPos origin) {
        HitResult hit = player.pick(player.blockInteractionRange() + 1.0, 1.0F, false);
        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK && blockHit.getBlockPos().equals(origin)) {
            return blockHit.getDirection();
        }
        return Direction.UP;
    }

    // --- drops ---

    @SubscribeEvent
    static void onDrops(BlockDropsEvent event) {
        ItemStack tool = event.getTool();
        if (!(tool.getItem() instanceof PerkTool perkTool) || !(event.getBreaker() instanceof Player player)) {
            return;
        }
        PerkData perks = ToolLogic.perks(tool);
        GlobalPos target = ToolLogic.dropTarget(tool);
        if (perks.isEmpty() && target == null) {
            return;
        }
        ServerLevel level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        if (perks.has(ToolPerk.NO_DROPS)) {
            event.getDrops().clear();
            if (perkTool.profile().perkSource() == ToolProfile.PerkSource.INVENTORY) {
                level.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            }
            return;
        }

        List<ItemStack> drops;
        if (perks.has(ToolPerk.SILK_TOUCH)) {
            drops = dropsWith(level, pos, state, event, player, tool, Enchantments.SILK_TOUCH, 1);
        } else if (perks.has(ToolPerk.FORTUNE)) {
            drops = dropsWith(level, pos, state, event, player, tool, Enchantments.FORTUNE, perks.level(ToolPerk.FORTUNE));
        } else {
            drops = new ArrayList<>(event.getDrops().stream().map(ItemEntity::getItem).toList());
        }
        if (perks.has(ToolPerk.SMELTING)) {
            drops = drops.stream().map(d -> {
                ItemStack smelted = Smelting.result(level, d);
                return smelted.isEmpty() ? d : smelted.copyWithCount(d.getCount() * smelted.getCount());
            }).toList();
        }
        if (perks.has(ToolPerk.EXPERIENCE) && state.is(Tags.BlockItems.ORES.block())
                && drops.stream().noneMatch(d -> d.is(state.getBlock().asItem()))) {
            ExperienceOrb.award(level, net.minecraft.world.phys.Vec3.atCenterOf(pos), 1);
        }
        if (target != null && perkTool.profile().perkSource() == ToolProfile.PerkSource.INVENTORY
                && target.dimension().equals(level.dimension())) {
            drops = teleport(level, target.pos(), drops);
        }
        event.getDrops().clear();
        for (ItemStack drop : drops) {
            if (!drop.isEmpty()) {
                event.getDrops().add(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
            }
        }
    }

    /** Recomputes the block's loot as if the tool had {@code enchantment} at {@code level} (perks act like enchantments). */
    private static List<ItemStack> dropsWith(ServerLevel level, BlockPos pos, BlockState state, BlockDropsEvent event, Player player,
            ItemStack tool, ResourceKey<Enchantment> enchantment, int enchantLevel) {
        ItemStack copy = tool.copy();
        Holder<Enchantment> holder = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment);
        copy.enchant(holder, Math.max(enchantLevel, copy.getEnchantments().getLevel(holder)));
        return new ArrayList<>(Block.getDrops(state, level, pos, event.getBlockEntity(), player, copy));
    }

    /** Inserts drops into the bound inventory; what doesn't fit stays as drops. */
    private static List<ItemStack> teleport(ServerLevel level, BlockPos targetPos, List<ItemStack> drops) {
        var handler = level.getCapability(Capabilities.Item.BLOCK, targetPos, null);
        if (handler == null) {
            return drops;
        }
        List<ItemStack> left = new ArrayList<>();
        for (ItemStack drop : drops) {
            int inserted;
            try (var tx = Transaction.openRoot()) {
                inserted = ResourceHandlerUtil.insertStacking(handler, ItemResource.of(drop), drop.getCount(), tx);
                tx.commit();
            }
            if (inserted < drop.getCount()) {
                left.add(drop.copyWithCount(drop.getCount() - inserted));
            }
        }
        return left;
    }
}
