package xyz.refinedev.practice.pvpclasses;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import xyz.refinedev.practice.util.chat.Color;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Represents a class that can apply PVP buffs for players.
 */
public abstract class PvPClass {

    public static long DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8L);

    protected Set<PotionEffect> passiveEffects = new HashSet<>();
    protected String name;
    protected long warmupDelay;

    public PvPClass(String name, long warmupDelay) {
        this.name = name;
        this.warmupDelay = warmupDelay;
    }

    /**
     * Gets the name of this {@link PvPClass}.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the warmup delay of this {@link PvPClass}.
     *
     * @return the warmup delay in milliseconds
     */
    public long getWarmupDelay() {
        return warmupDelay;
    }

    /**
     * Method called when a {@link Player} equips a {@link PvPClass}.
     *
     * @param player the equipping {@link Player}
     * @return true if successfully equipped
     */
    public boolean onEquip(Player player) {
        for (PotionEffect effect : passiveEffects) {
            player.addPotionEffect(effect, true);
        }

        player.sendMessage(Color.translate("&bClass: " + name + "  &aEnabled!"));
        return true;
    }

    /**
     * Method called when a {@link Player} unequips a {@link PvPClass}.
     *
     * @param player the unequipping {@link Player}
     */
    public void onUnequip(Player player) {
        for (PotionEffect effect : passiveEffects) {
            for (PotionEffect active : player.getActivePotionEffects()) {
                if (active.getDuration() > DEFAULT_MAX_DURATION && active.getType().equals(effect.getType()) && active.getAmplifier() == effect.getAmplifier()) {
                    player.removePotionEffect(effect.getType());
                    break;
                }
            }
        }
    }

    public abstract boolean isApplicableFor(Player player);
}