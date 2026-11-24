package geoactivity.machine.energy;

import java.util.function.IntSupplier;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

/**
 * Server-side menu data for int values. Vanilla syncs container data as 16-bit shorts, so each value is sent as a low
 * and a high half (1.10.2 sent energy as one short, which overflowed above 32767). The client side is a plain
 * {@link SimpleContainerData} of {@link #slots} size, read back with {@link #read}.
 */
public final class SplitIntData implements ContainerData {
    private final IntSupplier[] values;

    public SplitIntData(IntSupplier... values) {
        this.values = values;
    }

    public static int slots(int values) {
        return values * 2;
    }

    public static ContainerData client(int values) {
        return new SimpleContainerData(slots(values));
    }

    public static int read(ContainerData data, int index) {
        return (data.get(index * 2) & 0xFFFF) | ((data.get(index * 2 + 1) & 0xFFFF) << 16);
    }

    @Override
    public int get(int slot) {
        int value = values[slot / 2].getAsInt();
        return slot % 2 == 0 ? value & 0xFFFF : (value >>> 16) & 0xFFFF;
    }

    @Override
    public void set(int slot, int value) {}

    @Override
    public int getCount() {
        return values.length * 2;
    }
}
