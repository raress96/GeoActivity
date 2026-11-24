package geoactivity.perk;

import java.util.Locale;

/** Perks that go into the Advanced Coal Refiner and Advanced Ore Refiner. */
public enum MachinePerk {
    SPEED, EFFICIENCY;

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String itemId() {
        return "machine_" + key() + "_perk";
    }
}
