package geoactivity.gametest;

import com.mojang.authlib.GameProfile;
import geoactivity.machine.crafting.CraftingMachineRecipes;
import geoactivity.machine.energy.ToolChargerBlockEntity;
import geoactivity.perk.PerkData;
import geoactivity.perk.ToolPerk;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModDataComponents;
import geoactivity.registry.ModItems;
import geoactivity.tool.AutoStoneBuilderItem;
import geoactivity.tool.PerkTool;
import geoactivity.tool.ToolLogic;
import geoactivity.tool.ToolProfiles;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Phase 6: GUI tools, the perk engine and the Auto Stone Builder. */
final class ToolTests {
    private ToolTests() {}

    static void register() {
        GATests.test("tool_radius_breaks_3x3_of_the_same_block", 20, helper -> {
            for (int x = 2; x <= 4; x++) for (int z = 2; z <= 4; z++) helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
            helper.setBlock(new BlockPos(4, 1, 4), Blocks.ANDESITE); // not the centre block: stays
            ItemStack tool = new ItemStack(ModItems.REDSTONE_MINER.get());
            NonNullList<ItemStack> inv = NonNullList.withSize(8, ItemStack.EMPTY);
            inv.set(4, new ItemStack(ModItems.toolPerk(ToolPerk.RADIUS)));
            ToolLogic.setInventory(tool, inv);
            ToolLogic.recomputeInventoryPerks(tool, ToolProfiles.REDSTONE_MINER);
            helper.assertValueEqual(ToolLogic.level(tool, ToolPerk.RADIUS), 1, "radius perk from the tool inventory");
            ServerPlayer player = survivalPlayer(helper, tool, new BlockPos(3, 3, 3));
            player.gameMode.destroyBlock(helper.absolutePos(new BlockPos(3, 1, 3)));
            for (int x = 2; x <= 4; x++) for (int z = 2; z <= 4; z++) {
                if (x == 4 && z == 4) continue;
                helper.assertBlockPresent(Blocks.AIR, new BlockPos(x, 1, z));
            }
            helper.assertBlockPresent(Blocks.ANDESITE, new BlockPos(4, 1, 4));
            helper.assertValueEqual(tool.getDamageValue(), 8, "1 wear per block broken (8 of 9 are stone)");
            helper.assertItemEntityCountIs(Items.COBBLESTONE, new BlockPos(3, 1, 3), 3.0, 8);
            helper.succeed();
        });

        GATests.test("tool_drop_perks_smelt_silk_and_no_drops", 20, helper -> {
            check(helper, PerkData.of(ToolPerk.SMELTING.key(), 1), new BlockPos(2, 1, 2), Items.IRON_INGOT);
            check(helper, PerkData.of(ToolPerk.SILK_TOUCH.key(), 1), new BlockPos(6, 1, 2), Items.IRON_ORE);
            check(helper, PerkData.of(ToolPerk.NO_DROPS.key(), 1), new BlockPos(10, 1, 2), null);
            helper.succeed();
        });

        GATests.test("tool_destroyed_until_refuelled", 20, helper -> {
            ItemStack tool = new ItemStack(ModItems.ADVANCED_PICKAXE.get());
            tool.setDamageValue(998);
            BlockPos pos = new BlockPos(3, 1, 3);
            helper.setBlock(pos, Blocks.STONE);
            ServerPlayer player = survivalPlayer(helper, tool, pos.above(2));
            helper.assertFalse(player.gameMode.destroyBlock(helper.absolutePos(pos)), "a destroyed tool can't mine");
            helper.assertBlockPresent(Blocks.STONE, pos);
            helper.assertTrue(tool.getItem().getDefaultAttributeModifiers(tool).modifiers().isEmpty(), "no attack bonus when destroyed");
            NonNullList<ItemStack> inv = NonNullList.withSize(2, ItemStack.EMPTY);
            inv.set(0, new ItemStack(ModItems.BITUMINOUS_COAL.get(), 5));
            ToolLogic.setInventory(tool, inv);
            ToolLogic.refuel(tool, ToolProfiles.ADVANCED_PICKAXE);
            // 200 per bituminous while it fits: 998 -> 798 -> 598 -> 398 -> 198 (the 5th would go below 0)
            helper.assertValueEqual(tool.getDamageValue(), 198, "refuelled damage");
            helper.assertValueEqual(ToolLogic.inventory(tool, 2).get(0).getCount(), 1, "coal left");
            helper.assertTrue(player.gameMode.destroyBlock(helper.absolutePos(pos)), "works again");
            helper.succeed();
        });

        GATests.test("tool_capitator_fells_a_tree", 20, helper -> {
            for (int y = 1; y <= 5; y++) helper.setBlock(new BlockPos(3, y, 3), Blocks.OAK_LOG);
            helper.setBlock(new BlockPos(4, 4, 3), Blocks.OAK_LOG);
            helper.setBlock(new BlockPos(3, 6, 3), Blocks.OAK_LEAVES);
            ItemStack axe = new ItemStack(ModItems.ADVANCED_AXE.get());
            axe.set(ModDataComponents.PERKS, PerkData.of(ToolPerk.CAPITATOR.key(), 1));
            ServerPlayer player = survivalPlayer(helper, axe, new BlockPos(1, 1, 3));
            player.gameMode.destroyBlock(helper.absolutePos(new BlockPos(3, 1, 3)));
            for (int y = 1; y <= 5; y++) helper.assertBlockPresent(Blocks.AIR, new BlockPos(3, y, 3));
            helper.assertBlockPresent(Blocks.AIR, new BlockPos(4, 4, 3));
            helper.assertBlockPresent(Blocks.OAK_LEAVES, new BlockPos(3, 6, 3));
            helper.succeed();
        });

        GATests.test("crafting_machine_puts_perk_on_advanced_tool", 5, helper -> {
            helper.assertValueEqual(CraftingMachineRecipes.all().size(), GAExpected.CRAFTING_MACHINE_RECIPES, "crafting machine recipes");
            var grid = new java.util.ArrayList<>(java.util.Collections.nCopies(9, ItemStack.EMPTY));
            grid.set(0, new ItemStack(ModItems.SMALL_PRECIOUS_ALLOY_INGOT.get()));
            grid.set(1, new ItemStack(ModItems.SMALL_PRECIOUS_ALLOY_INGOT.get()));
            grid.set(2, new ItemStack(ModItems.SMALL_PRECIOUS_ALLOY_INGOT.get()));
            grid.set(4, new ItemStack(ModItems.CARBON_STICK.get()));
            grid.set(7, new ItemStack(ModItems.CARBON_STICK.get()));
            ItemStack pick = geoactivity.machine.crafting.CraftingMachineBlockEntity.findResult(grid);
            helper.assertTrue(pick.is(ModItems.ADVANCED_PICKAXE.get()) && pick.getDamageValue() == 998, "crafted empty: " + pick);
            pick.enchant(helper.getLevel().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                    .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING), 1);
            ItemStack perk = new ItemStack(ModItems.toolPerk(ToolPerk.FORTUNE));
            CraftingMachineTestAccess.applyPerk(pick, perk);
            helper.assertValueEqual(ToolLogic.level(pick, ToolPerk.FORTUNE), 1, "fortune installed");
            helper.assertTrue(perk.isEmpty(), "perk used");
            helper.assertFalse(pick.getEnchantments().isEmpty(), "enchantments kept (1.10.2 wiped them)");
            helper.succeed();
        });

        GATests.test("rf_miner_charges_and_uses_energy", 40, helper -> {
            BlockPos charger = new BlockPos(2, 1, 2);
            helper.setBlock(charger, ModBlocks.TOOL_CHARGER.get());
            var tc = helper.getBlockEntity(charger, ToolChargerBlockEntity.class);
            try (var tx = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) {
                for (int i = 0; i < 10; i++) {
                    tc.energyInput().insert(20_000, tx);
                }
                tx.commit();
            }
            ItemStack miner = new ItemStack(ModItems.ADVANCED_REDSTONE_BATTLE_MINER.get());
            tc.setItem(0, miner);
            helper.runAtTickTime(15, () -> {
                ItemStack charged = tc.getItem(0);
                helper.assertValueEqual(PerkTool.energy(charged), 200_000, "all buffered energy moved into the tool");
                BlockPos pos = new BlockPos(5, 1, 5);
                helper.setBlock(pos, Blocks.STONE);
                ItemStack tool = charged.copy();
                ServerPlayer player = survivalPlayer(helper, tool, pos.above(2));
                player.gameMode.destroyBlock(helper.absolutePos(pos));
                helper.assertValueEqual(PerkTool.energy(player.getMainHandItem()), 200_000 - ToolProfiles.RF_PER_USE, "1000 FE per block");
                helper.succeed();
            });
        });

        GATests.test("drop_teleport_sends_drops_to_chest", 20, helper -> {
            BlockPos chest = new BlockPos(1, 1, 1);
            helper.setBlock(chest, Blocks.CHEST);
            ItemStack perk = new ItemStack(ModItems.toolPerk(ToolPerk.DROP_TELEPORT));
            perk.set(ModDataComponents.DROP_TARGET, GlobalPos.of(helper.getLevel().dimension(), helper.absolutePos(chest)));
            ItemStack tool = new ItemStack(ModItems.REDSTONE_MINER.get());
            NonNullList<ItemStack> inv = NonNullList.withSize(8, ItemStack.EMPTY);
            inv.set(5, perk);
            ToolLogic.setInventory(tool, inv);
            ToolLogic.recomputeInventoryPerks(tool, ToolProfiles.REDSTONE_MINER);
            BlockPos pos = new BlockPos(4, 1, 4);
            helper.setBlock(pos, Blocks.STONE);
            ServerPlayer player = survivalPlayer(helper, tool, pos.above(2));
            player.gameMode.destroyBlock(helper.absolutePos(pos));
            helper.assertContainerContains(chest, Items.COBBLESTONE);
            helper.assertItemEntityNotPresent(Items.COBBLESTONE, pos, 3.0);
            helper.succeed();
        });

        GATests.test("auto_stone_builder_smelts_cobble_into_stone", 20, helper -> {
            ItemStack builder = new ItemStack(ModItems.AUTO_STONE_BUILDER.get());
            NonNullList<ItemStack> inv = NonNullList.withSize(3, ItemStack.EMPTY);
            inv.set(1, new ItemStack(Items.COBBLESTONE));
            inv.set(2, new ItemStack(Items.STONE));
            ToolLogic.setInventory(builder, inv);
            BlockPos floor = new BlockPos(3, 1, 3);
            helper.setBlock(floor, Blocks.DIRT);
            ServerPlayer player = survivalPlayer(helper, builder, floor.above(2));
            player.getInventory().setItem(10, new ItemStack(Items.COBBLESTONE, 20));
            AutoStoneBuilderItem.search(builder, player.getInventory(), helper.getLevel());
            var state = AutoStoneBuilderItem.state(builder);
            helper.assertValueEqual(state.blocks(), 20, "20 cobblestone pulled");
            helper.assertValueEqual(state.cost(), 2, "smelting costs 2");
            helper.assertTrue(player.getInventory().getItem(10).isEmpty(), "cobblestone taken from inventory");
            BlockPos abs = helper.absolutePos(floor);
            builder.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false)));
            helper.assertBlockPresent(Blocks.STONE, floor.above());
            helper.assertValueEqual(AutoStoneBuilderItem.state(builder).blocks(), 19, "one placed");
            helper.assertValueEqual(builder.getDamageValue(), 2, "wear = cost");
            helper.succeed();
        });
    }

    private static void check(GameTestHelper helper, PerkData perks, BlockPos pos, net.minecraft.world.item.Item expected) {
        helper.setBlock(pos, Blocks.IRON_ORE);
        ItemStack tool = new ItemStack(ModItems.ADVANCED_PICKAXE.get());
        tool.set(ModDataComponents.PERKS, perks);
        ServerPlayer player = survivalPlayer(helper, tool, pos.above(2));
        player.gameMode.destroyBlock(helper.absolutePos(pos));
        var drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new net.minecraft.world.phys.AABB(helper.absolutePos(pos)).inflate(1.5));
        if (expected == null) {
            helper.assertTrue(drops.isEmpty(), "no drops: " + drops);
        } else {
            helper.assertTrue(drops.size() == 1 && drops.getFirst().getItem().is(expected), perks + " should drop " + expected + ": " + drops);
        }
    }

    /** A survival server player (the vanilla mock one is always creative) holding {@code tool}, looking down. */
    static ServerPlayer survivalPlayer(GameTestHelper helper, ItemStack tool, BlockPos at) {
        var level = helper.getLevel();
        var cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "test-survival"), false);
        ServerPlayer player = new ServerPlayer(level.getServer(), level, cookie.gameProfile(), cookie.clientInformation());
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        level.getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        player.setGameMode(GameType.SURVIVAL);
        Vec3 pos = helper.absoluteVec(Vec3.atBottomCenterOf(at));
        player.snapTo(pos.x, pos.y, pos.z, 0.0F, 90.0F);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        return player;
    }
}
