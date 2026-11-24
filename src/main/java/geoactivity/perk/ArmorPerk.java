package geoactivity.perk;

import java.util.Locale;

/** Perks that go on Advanced Armor. */
public enum ArmorPerk {
    GENERAL("general_protection"),
    BLAST("blast_protection"),
    FIRE("fire_protection"),
    MAGIC("magic_protection"),
    PROJECTILE("projectile_protection"),
    NO_FALL("no_fall"),
    RESPIRATION("respiration");

    private final String itemPrefix;

    ArmorPerk(String itemPrefix) {
        this.itemPrefix = itemPrefix;
    }

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String itemId() {
        return itemPrefix + "_perk";
    }
}
