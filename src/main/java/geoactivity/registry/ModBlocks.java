package geoactivity.registry;

import geoactivity.GeoActivity;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Blocks. Explosion resistance is converted from 1.10.2 ({@code setResistance(r)} meant r × 3 / 5 in explosion math).
 */
public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GeoActivity.MOD_ID);

    // Ores: pickaxe, drop their coal + 3 XP. Mining tier via tags (lignite/bituminous iron, anthracite diamond).
    public static final DeferredBlock<DropExperienceBlock> LIGNITE_ORE = ore("lignite_ore", 3.0F);
    public static final DeferredBlock<DropExperienceBlock> BITUMINOUS_ORE = ore("bituminous_ore", 4.0F);
    public static final DeferredBlock<DropExperienceBlock> ANTHRACITE_ORE = ore("anthracite_ore", 5.0F);

    public static final DeferredBlock<Block> HARDENED_BRICK = brick("hardened_brick");
    public static final DeferredBlock<Block> ADVANCED_HARDENED_BRICK = brick("advanced_hardened_brick");
    public static final DeferredBlock<Block> THERMAL_HARDENED_BRICK = brick("thermal_hardened_brick");

    public static final DeferredBlock<TransparentBlock> HARDENED_GLASS = BLOCKS.registerBlock("hardened_glass", TransparentBlock::new,
            p -> p.mapColor(MapColor.NONE).strength(20.0F, 78.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)
                    .noOcclusion().isValidSpawn((s, l, pos, e) -> false).isRedstoneConductor((s, l, pos) -> false)
                    .isSuffocating((s, l, pos) -> false));
    public static final DeferredBlock<TransparentBlock> GRAPHITE_BASE = BLOCKS.registerBlock("graphite_base", TransparentBlock::new,
            p -> p.mapColor(MapColor.COLOR_BLACK).strength(3.0F, 9.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)
                    .noOcclusion().isValidSpawn((s, l, pos, e) -> false).isRedstoneConductor((s, l, pos) -> false)
                    .isSuffocating((s, l, pos) -> false));

    private ModBlocks() {}

    private static DeferredBlock<DropExperienceBlock> ore(String name, float hardness) {
        return BLOCKS.registerBlock(name, p -> new DropExperienceBlock(ConstantInt.of(3), p),
                p -> p.mapColor(MapColor.STONE).strength(hardness, 9.0F).requiresCorrectToolForDrops().sound(SoundType.STONE));
    }

    private static DeferredBlock<Block> brick(String name) {
        return BLOCKS.registerBlock(name, Block::new,
                p -> p.mapColor(MapColor.STONE).strength(20.0F, 90.0F).requiresCorrectToolForDrops().sound(SoundType.STONE));
    }
}
