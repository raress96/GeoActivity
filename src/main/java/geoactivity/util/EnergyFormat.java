package geoactivity.util;

import java.util.Locale;

/** 1.10.2 {@code GeneralHelper.getEnergyUnits}: 12345 -> "12.3K", 2500000 -> "2.5M". */
public final class EnergyFormat {
    private EnergyFormat() {}

    public static String units(int amount) {
        float thousands = amount / 1000f;
        if (thousands >= 1000) {
            float millions = thousands / 1000f;
            return millions < 100 ? String.format(Locale.ROOT, "%.1fM", millions) : (int) millions + "M";
        }
        return thousands < 100 ? String.format(Locale.ROOT, "%.1fK", thousands) : (int) thousands + "K";
    }
}
