package geoactivity.perk;

import java.util.Locale;

/** Perks that go on tools. Old 1.10.2 meta order is kept as the enum order. */
public enum ToolPerk {
    BLANK, SPEED, EFFICIENCY, DAMAGE, EXPERIENCE, SMELTING, FORTUNE, SILK_TOUCH, RADIUS, CAPITATOR, NO_DROPS,
    DROP_TELEPORT, ARMOR_BYPASS;

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String itemId() {
        return key() + "_perk";
    }
}
