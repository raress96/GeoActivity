package geoactivity.item;

import java.util.Locale;

/** The 19 chemical elements, in the 1.10.2 metadata order. */
public enum Element {
    HYDROGEN, HELIUM, LITHIUM, CARBON, NITROGEN, OXYGEN, NEON, SODIUM, MAGNESIUM, ALUMINIUM, SILICON, PHOSPHORUS,
    SULFUR, CHLORINE, POTASSIUM, CALCIUM, IRON, IODINE, GOLD;

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String itemId() {
        return key() + "_element_container";
    }
}
