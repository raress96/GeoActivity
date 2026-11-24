package geoactivity.gametest;

import geoactivity.item.Element;
import geoactivity.machine.MachineBlock;
import geoactivity.machine.chemistry.ChemistryMachineBlockEntity;
import geoactivity.machine.chemistry.ChemistryRecipes;
import geoactivity.machine.crafting.CraftingMachineBlockEntity;
import geoactivity.machine.crafting.CraftingMachineRecipes;
import geoactivity.machine.extractor.AtomExtractorBlockEntity;
import geoactivity.machine.extractor.AtomExtractorRecipes;
import geoactivity.machine.refiner.CoalRefinerBlockEntity;
import geoactivity.machine.refiner.CoalRefinerRecipes;
import geoactivity.perk.ToolPerk;
import geoactivity.registry.ModBlocks;
import geoactivity.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;

/** Phase 3: Coal Refiner, Atom Extractor, Crafting Machine, Chemistry Machine. */
final class MachineTests {
    private MachineTests() {}

    static void register() {
        GATests.test("recipe_tables_match_1_10_2", 5, helper -> {
            helper.assertValueEqual(CoalRefinerRecipes.all().size(), 8, "coal refiner recipes");
            helper.assertValueEqual(AtomExtractorRecipes.all().size(), 58, "atom extractor recipes (59 with LOG/LOG2 split)");
            helper.assertValueEqual(ChemistryRecipes.all().size(), 19, "chemistry recipes");
            helper.assertValueEqual(CraftingMachineRecipes.all().size(), GAExpected.CRAFTING_MACHINE_RECIPES, "crafting machine recipes");
            helper.succeed();
        });

        GATests.test("coal_refiner_refines_coal", 500, helper -> {
            BlockPos pos = new BlockPos(2, 1, 2);
            helper.setBlock(pos, ModBlocks.COAL_REFINER.get());
            var be = helper.getBlockEntity(pos, CoalRefinerBlockEntity.class);
            be.setItem(CoalRefinerBlockEntity.INPUT, new ItemStack(ModItems.ANTHRACITE_COAL.get()));
            be.setItem(CoalRefinerBlockEntity.FUEL, new ItemStack(ModItems.LIGNITE_COAL.get(), 2));
            helper.runAtTickTime(10, () -> {
                helper.assertBlockProperty(pos, MachineBlock.LIT, true);
                helper.assertValueEqual(be.getItem(CoalRefinerBlockEntity.FUEL).getCount(), 1, "one lignite burning");
            });
            helper.runAtTickTime(390, () -> helper.assertTrue(be.getItem(CoalRefinerBlockEntity.OUTPUT).isEmpty(), "not done before 400 ticks"));
            helper.succeedWhen(() -> {
                ItemStack out = be.getItem(CoalRefinerBlockEntity.OUTPUT);
                helper.assertTrue(out.is(ModItems.GRAPHITE.get()) && out.getCount() == 24, "24 graphite from anthracite, got " + out);
                helper.assertTrue(be.getItem(CoalRefinerBlockEntity.INPUT).isEmpty(), "input used");
            });
        });

        GATests.test("coal_refiner_hopper_rules", 5, helper -> {
            BlockPos pos = new BlockPos(2, 1, 2);
            helper.setBlock(pos, ModBlocks.COAL_REFINER.get());
            var be = helper.getBlockEntity(pos, CoalRefinerBlockEntity.class);
            helper.assertTrue(be.canPlaceItemThroughFace(0, new ItemStack(ModItems.LIGNITE_ORE.get()), Direction.UP), "ore into input");
            helper.assertTrue(be.canPlaceItemThroughFace(1, new ItemStack(ModItems.ANTHRACITE_COAL.get()), Direction.UP), "coal into fuel");
            helper.assertFalse(be.canPlaceItemThroughFace(1, new ItemStack(Items.DIRT), Direction.UP), "dirt is not fuel");
            helper.assertFalse(be.canPlaceItemThroughFace(2, new ItemStack(Items.COAL), Direction.UP), "nothing into output");
            helper.assertTrue(be.canTakeItemThroughFace(2, new ItemStack(ModItems.GRAPHITE.get()), Direction.DOWN), "take output");
            helper.assertFalse(be.canTakeItemThroughFace(1, new ItemStack(Items.COAL), Direction.DOWN), "fuel stays");
            var cap = helper.getLevel().getCapability(Capabilities.Item.BLOCK, helper.absolutePos(pos), Direction.UP);
            helper.assertTrue(cap != null && cap.size() == 3, "item capability exposed");
            helper.succeed();
        });

        GATests.test("atom_extractor_splits_iron_with_upgrades", 300, helper -> {
            BlockPos pos = new BlockPos(3, 2, 3);
            helper.setBlock(pos, ModBlocks.ATOM_EXTRACTOR.get());
            for (Direction dir : new Direction[] {Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST}) {
                helper.setBlock(pos.relative(dir), ModBlocks.ADVANCED_HARDENED_BRICK.get());
            }
            var be = helper.getBlockEntity(pos, AtomExtractorBlockEntity.class);
            be.setItem(AtomExtractorBlockEntity.INPUT, new ItemStack(Items.IRON_INGOT));
            be.setItem(AtomExtractorBlockEntity.CONTAINERS, new ItemStack(ModItems.EMPTY_ELEMENT_CONTAINER.get(), 10));
            // 400 ticks base, 4 bricks: 400 - 400*4/8 - 40 = 160
            helper.assertValueEqual(AtomExtractorBlockEntity.upgradedTime(400, 4), 160, "upgraded time");
            helper.runAtTickTime(5, () -> helper.assertValueEqual(be.getItem(AtomExtractorBlockEntity.CONTAINERS).getCount(), 7,
                    "3 containers used up front"));
            helper.runAtTickTime(150, () -> helper.assertTrue(be.getItem(2).isEmpty(), "still working at 150"));
            helper.succeedWhen(() -> {
                helper.assertTrue(be.getItem(2).is(ModItems.element(Element.IRON)) && be.getItem(2).getCount() == 2, "2 iron");
                helper.assertTrue(be.getItem(3).is(ModItems.element(Element.LITHIUM)) && be.getItem(3).getCount() == 1, "1 lithium");
            });
        });

        GATests.test("atom_extractor_needs_containers", 60, helper -> {
            BlockPos pos = new BlockPos(2, 1, 2);
            helper.setBlock(pos, ModBlocks.ATOM_EXTRACTOR.get());
            var be = helper.getBlockEntity(pos, AtomExtractorBlockEntity.class);
            be.setItem(AtomExtractorBlockEntity.INPUT, new ItemStack(Items.DIAMOND));
            be.setItem(AtomExtractorBlockEntity.CONTAINERS, new ItemStack(ModItems.EMPTY_ELEMENT_CONTAINER.get(), 31));
            helper.runAtTickTime(40, () -> {
                helper.assertValueEqual(be.getItem(AtomExtractorBlockEntity.INPUT).getCount(), 1, "diamond needs 32 containers");
                helper.succeed();
            });
        });

        GATests.test("crafting_machine_crafts_speed_perk", 200, helper -> {
            BlockPos pos = new BlockPos(2, 1, 2);
            helper.setBlock(pos, ModBlocks.CRAFTING_MACHINE.get());
            var be = helper.getBlockEntity(pos, CraftingMachineBlockEntity.class);
            int g = CraftingMachineBlockEntity.GRID_START;
            be.setItem(g, new ItemStack(ModItems.toolPerk(ToolPerk.BLANK)));
            be.setItem(g + 4, new ItemStack(Items.ENDER_PEARL));
            be.setItem(g + 8, new ItemStack(Items.GOLD_INGOT));
            be.setItem(CraftingMachineBlockEntity.FUEL, new ItemStack(ModItems.LIGNITE_COAL.get()));
            helper.runAtTickTime(3, () -> helper.assertTrue(
                    be.getItem(CraftingMachineBlockEntity.PREVIEW).isEmpty() || be.getItem(g).isEmpty(), "grid used when craft starts"));
            helper.succeedWhen(() -> {
                helper.assertTrue(be.getItem(CraftingMachineBlockEntity.OUTPUT).is(ModItems.toolPerk(ToolPerk.SPEED)), "speed perk made");
                helper.assertTrue(be.getItem(g).isEmpty() && be.getItem(g + 4).isEmpty(), "grid consumed");
            });
        });

        GATests.test("crafting_machine_shaped_and_repair", 5, helper -> {
            var grid = new java.util.ArrayList<>(java.util.Collections.nCopies(9, ItemStack.EMPTY));
            grid.set(1, new ItemStack(Items.GLOWSTONE_DUST));
            grid.set(3, new ItemStack(Items.REDSTONE));
            grid.set(4, new ItemStack(ModItems.CARBON_FIBER.get()));
            grid.set(5, new ItemStack(Items.REDSTONE));
            grid.set(7, new ItemStack(Items.QUARTZ));
            helper.assertTrue(CraftingMachineBlockEntity.findResult(grid).is(ModItems.toolPerk(ToolPerk.BLANK)), "blank perk");
            grid.set(7, ItemStack.EMPTY);
            helper.assertTrue(CraftingMachineBlockEntity.findResult(grid).isEmpty(), "incomplete pattern");

            ItemStack a = new ItemStack(Items.IRON_PICKAXE);
            a.setDamageValue(200);
            ItemStack b = new ItemStack(Items.IRON_PICKAXE);
            b.setDamageValue(200);
            ItemStack repaired = CraftingMachineBlockEntity.findResult(List.of(a, ItemStack.EMPTY, b, ItemStack.EMPTY, ItemStack.EMPTY,
                    ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));
            int max = a.getMaxDamage();
            int expected = Math.max(0, max - ((max - 200) * 2 + max * 5 / 100));
            helper.assertTrue(repaired.is(Items.IRON_PICKAXE) && repaired.getDamageValue() == expected, "repair +5%: " + repaired);
            helper.succeed();
        });

        GATests.test("chemistry_machine_crafts_with_item_frames", 40, helper -> {
            BlockPos machine = new BlockPos(4, 1, 4);
            setUpChemistry(helper, machine, Element.NITROGEN, Element.CHLORINE);
            var be = helper.getBlockEntity(machine, ChemistryMachineBlockEntity.class);
            be.setItem(0, new ItemStack(Items.STRING));
            be.setItem(1, new ItemStack(Items.STRING));
            helper.setBlock(machine.above(), Blocks.COAL_BLOCK);
            helper.runAtTickTime(2, () -> {
                helper.assertBlockNotPresent(Blocks.COAL_BLOCK, machine.above());
                helper.assertTrue(be.getItem(0).isEmpty() && be.getItem(1).isEmpty(), "inputs used");
                helper.assertItemEntityCountIs(Items.SPIDER_EYE, machine.above(), 2.0, 2);
                long filled = helper.getLevel().getEntitiesOfClass(ItemFrame.class, helper.getBounds())
                        .stream().filter(f -> !f.getItem().isEmpty()).count();
                helper.assertValueEqual(filled, 0L, "frames cleared");
                helper.succeed();
            });
        });

        GATests.test("chemistry_machine_needs_right_frames", 40, helper -> {
            BlockPos machine = new BlockPos(4, 1, 4);
            setUpChemistry(helper, machine, Element.NITROGEN, Element.NITROGEN); // recipe wants nitrogen + chlorine
            var be = helper.getBlockEntity(machine, ChemistryMachineBlockEntity.class);
            be.setItem(0, new ItemStack(Items.STRING));
            be.setItem(1, new ItemStack(Items.STRING));
            helper.setBlock(machine.above(), Blocks.COAL_BLOCK);
            helper.runAtTickTime(2, () -> {
                helper.assertBlockPresent(Blocks.COAL_BLOCK, machine.above());
                helper.assertValueEqual(be.getItem(0).getCount(), 1, "nothing crafted");
                helper.succeed();
            });
        });
    }

    /** Chemistry Machine with its four bricks; 4 frames of {@code first} on one brick and 4 of {@code second} on another. */
    private static void setUpChemistry(GameTestHelper helper, BlockPos machine, Element first, Element second) {
        helper.setBlock(machine, ModBlocks.CHEMISTRY_MACHINE.get());
        int[][] offsets = ChemistryMachineBlockEntity.BRICK_OFFSETS;
        for (int[] offset : offsets) {
            helper.setBlock(machine.offset(offset[0], 0, offset[1]), ModBlocks.ADVANCED_HARDENED_BRICK.get());
        }
        frames(helper, machine.offset(offsets[0][0], 0, offsets[0][1]), ModItems.element(first));
        frames(helper, machine.offset(offsets[2][0], 0, offsets[2][1]), ModItems.element(second));
    }

    private static void frames(GameTestHelper helper, BlockPos brick, Item item) {
        var level = helper.getLevel();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos at = helper.absolutePos(brick.relative(dir));
            ItemFrame frame = new ItemFrame(level, at, dir);
            frame.setItem(new ItemStack(item), false);
            level.addFreshEntity(frame);
        }
    }
}
