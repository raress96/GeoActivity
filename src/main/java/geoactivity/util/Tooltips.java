package geoactivity.util;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

/**
 * Tooltip helpers. Detail lines live in the lang file as {@code <base>.0}, {@code <base>.1}, ... and are shown
 * while Shift is held, like in 1.10.2.
 */
public final class Tooltips {
    /** Set by the client entrypoint; always false on a dedicated server. */
    public static BooleanSupplier shiftDown = () -> false;

    private Tooltips() {}

    public static void shiftForInfo(Consumer<Component> out, String base, ChatFormatting firstLineStyle) {
        if (!shiftDown.getAsBoolean()) {
            out.accept(Component.translatable("tooltip.geoactivity.hold_shift",
                    Component.translatable("tooltip.geoactivity.shift").withStyle(ChatFormatting.GREEN))
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        lines(out, base, firstLineStyle);
    }

    public static void lines(Consumer<Component> out, String base, ChatFormatting firstLineStyle) {
        Language language = Language.getInstance();
        for (int i = 0; language.has(base + "." + i); i++) {
            out.accept(Component.translatable(base + "." + i).withStyle(i == 0 ? firstLineStyle : ChatFormatting.GRAY));
        }
    }
}
