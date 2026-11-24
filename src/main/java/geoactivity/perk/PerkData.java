package geoactivity.perk;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Perks installed on a tool or armor piece: perk key ({@link ToolPerk#key()} / {@link ArmorPerk#key()}) to level.
 * Replaces the 1.10.2 NBT flags ("speed", "auto-smelt", "widerRadius", ...).
 */
public record PerkData(Map<String, Integer> levels) {
    public static final PerkData EMPTY = new PerkData(Map.of());
    public static final Codec<PerkData> CODEC = Codec.unboundedMap(Codec.STRING, Codec.INT).xmap(PerkData::new, PerkData::levels);
    public static final StreamCodec<ByteBuf, PerkData> STREAM_CODEC = ByteBufCodecs.<ByteBuf, String, Integer, Map<String, Integer>>map(
            size -> new TreeMap<>(), ByteBufCodecs.STRING_UTF8, ByteBufCodecs.VAR_INT).map(PerkData::new, PerkData::levels);

    public PerkData {
        levels = Map.copyOf(levels);
    }

    public static PerkData of(String key, int level) {
        return new PerkData(Map.of(key, level));
    }

    public int level(ToolPerk perk) {
        return levels.getOrDefault(perk.key(), 0);
    }

    public int level(ArmorPerk perk) {
        return levels.getOrDefault(perk.key(), 0);
    }

    public boolean has(ToolPerk perk) {
        return level(perk) > 0;
    }

    public boolean has(ArmorPerk perk) {
        return level(perk) > 0;
    }

    public boolean isEmpty() {
        return levels.isEmpty();
    }
}
