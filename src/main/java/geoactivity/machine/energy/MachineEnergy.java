package geoactivity.machine.energy;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

/**
 * Energy buffer of a machine (FE, which is RF-compatible). The insert/extract limits apply to other blocks talking to
 * this machine through the energy capability; the machine itself changes the amount with {@link #add}/{@link #remove}.
 */
public class MachineEnergy extends SimpleEnergyHandler {
    private final Runnable onChanged;

    public MachineEnergy(int capacity, int maxInsert, int maxExtract, Runnable onChanged) {
        super(capacity, maxInsert, maxExtract);
        this.onChanged = onChanged;
    }

    public int amount() {
        return energy;
    }

    public int capacity() {
        return capacity;
    }

    public int maxExtract() {
        return maxExtract;
    }

    public void setLimits(int maxInsert, int maxExtract) {
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    public void add(int amount) {
        set(Math.min(capacity, energy + amount));
    }

    public void remove(int amount) {
        set(Math.max(0, energy - amount));
    }

    @Override
    protected void onEnergyChanged(int previousAmount) {
        onChanged.run();
    }
}
