package geoactivity.tool;

import java.util.List;
import java.util.Set;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * Everything that differs between the 1.10.2 GUI tools (Reinforced Miner, Advanced, Redstone and RF tools).
 * See docs/GAMEPLAY_SPEC.md §6.
 */
public record ToolProfile(
        Set<Category> categories,
        boolean hoe,
        float speed,
        TagKey<Block> incorrectTier,
        float attackDamage,
        float attackSpeed,
        int maxDamage,
        /** Unusable ("destroyed") at this much damage; refuelling brings it back. */
        int unusableAt,
        /** Damage repaired per lignite / bituminous / anthracite coal. */
        int[] fuel,
        /** Extra repair per efficiency level, multiplied by 2/3/4 for the three coals. */
        int efficiencyBonus,
        int hitCost,
        int blockCost,
        PerkSource perkSource,
        int fuelSlots,
        int perkSlots,
        Limits limits,
        Area area,
        Capitator capitator,
        String texture,
        List<int[]> slotPositions) {

    public enum Category {
        PICKAXE(BlockTags.MINEABLE_WITH_PICKAXE),
        SHOVEL(BlockTags.MINEABLE_WITH_SHOVEL),
        AXE(BlockTags.MINEABLE_WITH_AXE);

        public final TagKey<Block> tag;

        Category(TagKey<Block> tag) {
            this.tag = tag;
        }
    }

    /** Where the perks come from: one perk from the Crafting Machine, or perk items in the tool's own inventory. */
    public enum PerkSource { NONE, CRAFTED, INVENTORY }

    /** Wider Radius: none, 3×3 of the same block as the centre, 3×3 of anything mineable, or RF levels (3×3 .. 9×9×5). */
    public enum Area { NONE, SAME_BLOCK, ANY, LEVELS }

    /**
     * Capitator: none, fell matching blocks (Advanced Axe), fell any logs (Battle Axe), column of matching blocks
     * (Redstone Miner), or logs-else-column (Battle Miners).
     */
    public enum Capitator { NONE, TREE_SAME, TREE_LOGS, COLUMN_SAME, LOGS_OR_COLUMN }

    /** Max levels for stackable perks (Redstone tools put several perk items in). */
    public record Limits(int speed, int efficiency, int damage, int fortune, int radius) {
        public static final Limits SINGLE = new Limits(1, 1, 1, 1, 1);
    }

    public boolean usesFuel() {
        return fuel.length > 0;
    }

    /** Fuel slot indices come first in the tool inventory, perk slots after. */
    public int inventorySize() {
        return fuelSlots + perkSlots;
    }
}
