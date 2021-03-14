package me.drizzy.practice.hcf.bard;

import com.google.common.base.Preconditions;
import org.bukkit.scheduler.BukkitTask;

public class BardData {

    public static double ENERGY_PER_MILLISECOND = 1.0D;
    public static double MIN_ENERGY = 0.0D;
    public static double MAX_ENERGY = 120.0D;
    public static long MAX_ENERGY_MILLIS = (long) (MAX_ENERGY * 1000L);
    public long energyStart;
    public long buffCooldown;
    public BukkitTask heldTask;

    public BukkitTask getHeldTask() {
        return this.heldTask;
    }

    public long getBuffCooldown() {
        return this.buffCooldown;
    }

    public void setBuffCooldown(long millis) {
        this.buffCooldown = System.currentTimeMillis() + millis;
    }

    public long getRemainingBuffDelay() {
        return this.buffCooldown - System.currentTimeMillis();
    }

    /**
     * Begins tracking energy.
     */
    public void startEnergyTracking() {
        this.setEnergy(0);
    }

    /**
     * Gets the energy in milliseconds.
     *
     * @return energy in milliseconds
     */
    public long getEnergyMillis() {
        if (this.energyStart == 0L) {
            return 0L;
        }

        return Math.min(MAX_ENERGY_MILLIS, (long) (ENERGY_PER_MILLISECOND * (System.currentTimeMillis() - this.energyStart)));
    }

    public double getEnergy() {
        // TODO: double check
        return Math.round(this.getEnergyMillis() / 100.0) / 10.0;
    }

    public void setEnergy(double energy) {
        Preconditions.checkArgument(energy >= MIN_ENERGY, "Energy cannot be less than " + MIN_ENERGY);
        Preconditions.checkArgument(energy <= MAX_ENERGY, "Energy cannot be more than " + MAX_ENERGY);
        this.energyStart = (long) (System.currentTimeMillis() - (1000L * energy));
    }
}
