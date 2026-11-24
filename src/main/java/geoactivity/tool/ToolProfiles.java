package geoactivity.tool;

import static geoactivity.tool.ToolProfile.Category.AXE;
import static geoactivity.tool.ToolProfile.Category.PICKAXE;
import static geoactivity.tool.ToolProfile.Category.SHOVEL;

import geoactivity.tool.ToolProfile.Area;
import geoactivity.tool.ToolProfile.Capitator;
import geoactivity.tool.ToolProfile.Limits;
import geoactivity.tool.ToolProfile.PerkSource;
import java.util.List;
import java.util.Set;
import net.minecraft.tags.BlockTags;

/** The 1.10.2 numbers for each GUI tool. */
public final class ToolProfiles {
    private static final int[] ADV_FUEL = {100, 200, 300};
    private static final int[] RED_FUEL = {130, 260, 400};
    private static final int[] NO_FUEL = {};

    private static final List<int[]> ONE_FUEL = List.of(new int[] {80, 34});
    private static final List<int[]> ADV_SLOTS = List.of(new int[] {80, 34}, new int[] {9, 9});
    private static final List<int[]> RED_SLOTS = List.of(new int[] {80, 34}, new int[] {62, 52}, new int[] {80, 52}, new int[] {98, 52},
            new int[] {9, 9}, new int[] {9, 58}, new int[] {151, 9}, new int[] {151, 58});
    private static final List<int[]> BATTLE_MINER_SLOTS = List.of(new int[] {80, 34}, new int[] {62, 52}, new int[] {80, 52},
            new int[] {98, 52}, new int[] {9, 9}, new int[] {9, 58}, new int[] {151, 9}, new int[] {151, 58}, new int[] {9, 34},
            new int[] {45, 9}, new int[] {115, 9}, new int[] {151, 34});
    private static final List<int[]> RF_SLOTS = List.of(new int[] {9, 9}, new int[] {9, 34}, new int[] {9, 58}, new int[] {45, 9},
            new int[] {45, 34}, new int[] {45, 58}, new int[] {115, 9}, new int[] {115, 34}, new int[] {115, 58}, new int[] {151, 9},
            new int[] {151, 34}, new int[] {151, 58});

    public static final ToolProfile REINFORCED_MINER = new ToolProfile(Set.of(PICKAXE, SHOVEL), false, 8.0F,
            BlockTags.INCORRECT_FOR_IRON_TOOL, 5.0F, -3.0F, 500, 498, ADV_FUEL, 0, 2, 1,
            PerkSource.NONE, 1, 0, Limits.SINGLE, Area.NONE, Capitator.NONE, "reinforced_miner", ONE_FUEL);

    public static final ToolProfile ADVANCED_PICKAXE = advanced(Set.of(PICKAXE), 5.0F, Area.SAME_BLOCK, Capitator.NONE);
    public static final ToolProfile ADVANCED_SHOVEL = advanced(Set.of(SHOVEL), 5.5F, Area.SAME_BLOCK, Capitator.NONE);
    public static final ToolProfile ADVANCED_AXE = advanced(Set.of(AXE), 6.0F, Area.NONE, Capitator.TREE_SAME);
    public static final ToolProfile ADVANCED_SWORD = new ToolProfile(Set.of(), false, 10.0F, BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            7.0F, -2.4F, 1000, 998, ADV_FUEL, 10, 1, 0, PerkSource.CRAFTED, 1, 1, Limits.SINGLE, Area.NONE, Capitator.NONE,
            "advanced_tool", ADV_SLOTS);

    public static final ToolProfile REDSTONE_MINER = redstone(Set.of(PICKAXE, SHOVEL), false, 6.0F, 1000, 998, 4,
            Area.SAME_BLOCK, Capitator.COLUMN_SAME, "redstone_tool", RED_SLOTS);
    public static final ToolProfile REDSTONE_BATTLE_AXE = redstone(Set.of(AXE), true, 10.0F, 1000, 998, 4,
            Area.NONE, Capitator.TREE_LOGS, "redstone_tool", RED_SLOTS);
    public static final ToolProfile REDSTONE_BATTLE_MINER = redstone(Set.of(PICKAXE, SHOVEL, AXE), true, 12.0F, 2000, 1998, 8,
            Area.ANY, Capitator.LOGS_OR_COLUMN, "redstone_battle_miner", BATTLE_MINER_SLOTS);

    /** Advanced Redstone Battle Miner: FE instead of fuel; damage isn't used (it can't wear out). */
    public static final ToolProfile ADVANCED_REDSTONE_BATTLE_MINER = new ToolProfile(Set.of(PICKAXE, SHOVEL, AXE), true, 12.0F,
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 12.0F, -3.0F, 0, 0, NO_FUEL, 0, 1, 1, PerkSource.INVENTORY, 0, 12,
            new Limits(4, 0, 4, 4, 4), Area.LEVELS, Capitator.LOGS_OR_COLUMN, "advanced_redstone_battle_miner", RF_SLOTS);

    /** Advanced Armor: same fuel and GUI as the Advanced tools; "destroyed" at max damage − 2 (checked per piece). */
    public static final ToolProfile ADVANCED_ARMOR = new ToolProfile(Set.of(), false, 1.0F, BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            0.0F, 0.0F, 0, 0, ADV_FUEL, 10, 0, 0, PerkSource.CRAFTED, 1, 1, Limits.SINGLE, Area.NONE, Capitator.NONE,
            "advanced_tool", ADV_SLOTS);

    /** RF miner: 5M FE, 20k FE/t in, 1000 FE per use (double at radius level 3+). */
    public static final int RF_CAPACITY = 5_000_000, RF_MAX_INPUT = 20_000, RF_PER_USE = 1000;

    private ToolProfiles() {}

    private static ToolProfile advanced(Set<ToolProfile.Category> categories, float damage, Area area, Capitator capitator) {
        return new ToolProfile(categories, false, 10.0F, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, damage, -3.0F, 1000, 998, ADV_FUEL,
                10, 2, 1, PerkSource.CRAFTED, 1, 1, Limits.SINGLE, area, capitator, "advanced_tool", ADV_SLOTS);
    }

    private static ToolProfile redstone(Set<ToolProfile.Category> categories, boolean hoe, float damage, int max, int unusable,
            int perkSlots, Area area, Capitator capitator, String texture, List<int[]> slots) {
        return new ToolProfile(categories, hoe, 10.0F, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, damage, -3.0F, max, unusable, RED_FUEL,
                10, 1, 1, PerkSource.INVENTORY, 4, perkSlots, new Limits(2, 2, 2, 4, 1), area, capitator, texture, slots);
    }
}
