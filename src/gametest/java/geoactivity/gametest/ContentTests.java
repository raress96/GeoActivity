package geoactivity.gametest;

import geoactivity.GeoActivity;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModDataComponents;
import geoactivity.registry.ModItems;
import geoactivity.perk.ToolPerk;
import geoactivity.worldgen.ModWorldGen;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Phase 2: registration, translations, loot, recipes, ore generation and the simple items. */
final class ContentTests {
    private ContentTests() {}

    static void register() {
        GATests.test("every_item_has_a_name", 5, helper -> {
            Language lang = Language.getInstance();
            for (var entry : ModItems.ITEMS.getEntries()) {
                String key = entry.get().getDescriptionId();
                helper.assertTrue(lang.has(key), "missing translation " + key);
            }
            helper.succeed();
        });

        GATests.test("every_block_has_a_loot_table", 5, helper -> {
            var server = helper.getLevel().getServer();
            for (var entry : ModBlocks.BLOCKS.getEntries()) {
                Block block = entry.get();
                var key = block.getLootTable().orElseThrow();
                LootTable table = server.reloadableRegistries().getLootTable(key);
                helper.assertTrue(table != LootTable.EMPTY, "missing loot table for " + entry.getId());
            }
            helper.succeed();
        });

        GATests.test("recipe_count_matches_1_10_2", 5, helper -> {
            long count = helper.getLevel().recipeAccess().getRecipes().stream()
                    .filter(r -> r.id().identifier().getNamespace().equals(GeoActivity.MOD_ID))
                    .count();
            // 13 shaped (blocks + misc) + 3 ore smelting + 19 element-container smelting. Grows as phases land.
            helper.assertValueEqual(count, (long) GAExpected.RECIPES, "geoactivity recipe count");
            helper.succeed();
        });

        GATests.test("ore_features_place_ore_in_stone", 20, helper -> {
            var level = helper.getLevel();
            // fill a stone cube, then run each ore feature in the middle of it
            for (int x = 2; x < 13; x++) for (int y = 1; y < 7; y++) for (int z = 2; z < 13; z++) {
                helper.setBlock(x, y, z, Blocks.STONE);
            }
            var features = level.registryAccess().lookupOrThrow(Registries.FEATURE);
            int i = 0;
            for (ModWorldGen.Ore ore : ModWorldGen.ORES) {
                BlockPos center = helper.absolutePos(new BlockPos(4 + 3 * i++, 3, 7));
                var feature = features.getOrThrow(ore.featureKey()).value();
                boolean placed = false;
                for (int attempt = 0; attempt < 20 && !placed; attempt++) {
                    placed = feature.place(level, level.getChunkSource().getGenerator(), level.getRandom(), center);
                }
                helper.assertTrue(placed, ore.name() + " did not place");
            }
            for (ModWorldGen.Ore ore : ModWorldGen.ORES) {
                boolean[] found = {false};
                helper.forEveryBlockInStructure(pos -> {
                    if (helper.getBlockState(pos).is(ore.block().get())) {
                        found[0] = true;
                    }
                });
                helper.assertTrue(found[0], "no " + ore.name() + " generated");
            }
            helper.succeed();
        });

        GATests.test("ores_drop_coal_and_need_right_tier", 5, helper -> {
            var level = helper.getLevel();
            BlockPos pos = new BlockPos(1, 1, 1);
            check(helper, ModBlocks.LIGNITE_ORE.get(), ModItems.LIGNITE_COAL.get(), true);
            check(helper, ModBlocks.BITUMINOUS_ORE.get(), ModItems.BITUMINOUS_COAL.get(), true);
            check(helper, ModBlocks.ANTHRACITE_ORE.get(), ModItems.ANTHRACITE_COAL.get(), false);
            helper.setBlock(pos, ModBlocks.LIGNITE_ORE.get());
            helper.assertTrue(level.getBlockState(helper.absolutePos(pos)).getDestroySpeed(level, helper.absolutePos(pos)) == 3.0F,
                    "lignite hardness");
            helper.succeed();
        });

        GATests.test("drop_teleport_perk_binds_to_inventory", 5, helper -> {
            BlockPos chest = new BlockPos(2, 1, 2);
            helper.setBlock(chest, Blocks.CHEST);
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);
            player.setShiftKeyDown(true);
            ItemStack perk = new ItemStack(ModItems.toolPerk(ToolPerk.DROP_TELEPORT));
            player.setItemInHand(InteractionHand.MAIN_HAND, perk);
            BlockPos abs = helper.absolutePos(chest);
            var hit = new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false);
            perk.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
            var target = perk.get(ModDataComponents.DROP_TARGET.get());
            helper.assertTrue(target != null && target.pos().equals(abs), "perk bound to chest");

            // binding something that is not an inventory does nothing
            ItemStack other = new ItemStack(ModItems.toolPerk(ToolPerk.DROP_TELEPORT));
            BlockPos stone = new BlockPos(4, 1, 4);
            helper.setBlock(stone, Blocks.STONE);
            BlockPos absStone = helper.absolutePos(stone);
            other.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(absStone), Direction.UP, absStone, false)));
            helper.assertTrue(other.get(ModDataComponents.DROP_TARGET.get()) == null, "stone is not an inventory");
            helper.succeed();
        });
    }

    private static void check(net.minecraft.gametest.framework.GameTestHelper helper, Block ore, net.minecraft.world.item.Item coal,
            boolean ironIsEnough) {
        var level = helper.getLevel();
        var state = ore.defaultBlockState();
        BlockPos abs = helper.absolutePos(new BlockPos(1, 1, 1));
        List<ItemStack> drops = Block.getDrops(state, level, abs, null, null, new ItemStack(Items.DIAMOND_PICKAXE));
        helper.assertTrue(drops.size() == 1 && drops.getFirst().is(coal) && drops.getFirst().getCount() == 1,
                ore + " should drop one " + coal + " but dropped " + drops);
        helper.assertTrue(new ItemStack(Items.IRON_PICKAXE).isCorrectToolForDrops(state) == ironIsEnough, ore + " iron tier");
        helper.assertTrue(new ItemStack(Items.DIAMOND_PICKAXE).isCorrectToolForDrops(state), ore + " diamond tier");
        helper.assertFalse(new ItemStack(Items.STONE_PICKAXE).isCorrectToolForDrops(state), ore + " stone tier");
    }
}
