package me.drizzy.practice.match.kits.utils;

import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.kits.utils.events.ArmorClassEquipEvent;
import me.drizzy.practice.match.kits.utils.events.ArmorClassUnequipEvent;
import me.drizzy.practice.Array;
import me.drizzy.practice.match.kits.Archer;
import me.drizzy.practice.match.kits.Bard;
import me.drizzy.practice.match.kits.Rogue;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.events.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ArmorClassManager implements Listener {

    protected Map<UUID, ArmorClass> classWarmups = new HashMap<>();
    // Mapping to getInstance the PVP Class a player has equipped.
    private final Map<UUID, ArmorClass> equippedClassMap = new HashMap<>();
    private final List<ArmorClass> pvpClasses = new ArrayList<>();

    public ArmorClassManager(Array plugin) {
        pvpClasses.add(new Bard(plugin));
        pvpClasses.add(new Archer(plugin));
        pvpClasses.add(new Rogue(plugin));

        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (ArmorClass pvpClass : pvpClasses) {
            if (pvpClass instanceof Listener) {
                plugin.getServer().getPluginManager().registerEvents((Listener) pvpClass, plugin);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Profile profile = Profile.getByUuid(player.getUniqueId());
                    Match match = profile.getMatch();
                    if (match != null && match.isHCFMatch()) {
                        Bukkit.getScheduler().runTask(Array.getInstance(), () -> {
                            attemptEquip(player);
                        });
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20, 20);
    }

    public void onDisable() {
        for (Map.Entry<UUID, ArmorClass> entry : new HashMap<>(equippedClassMap).entrySet()) {
            this.setEquippedClass(Bukkit.getPlayer(entry.getKey()), null);
        }

        this.pvpClasses.clear();
        this.equippedClassMap.clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Profile.getByUuid(event.getEntity().getUniqueId()).isInMatch() && Profile.getByUuid(event.getEntity().getUniqueId()).getMatch().isHCFMatch()) {
            setEquippedClass(event.getEntity(), null);
        }
    }

    // FUCK ALL OF YOU SKIDS
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onArmorChange(ArmorEquipEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        Match match = profile.getMatch();
        if (match != null && match.isHCFMatch()) {
            Bukkit.getScheduler().runTask(Array.getInstance(), () -> {
                this.attemptEquip(event.getPlayer());
            });
        }
    }

    public void attemptEquip(Player player) {
        ArmorClass current = Array.getInstance().getArmorClassManager().getEquippedClass(player);
        if (current != null) {
            if (current.isApplicableFor(player)) {
                return;
            }

            Array.getInstance().getArmorClassManager().setEquippedClass(player, null);
        } else if ((current = classWarmups.get(player.getUniqueId())) != null) {
            if (current.isApplicableFor(player)) {
                return;
            }

        }

        Collection<ArmorClass> pvpClasses = Array.getInstance().getArmorClassManager().getPvpClasses();
        for (ArmorClass pvpClass : pvpClasses) {
            if (pvpClass.isApplicableFor(player)) {
                Array.getInstance().getArmorClassManager().setEquippedClass(player, pvpClass);
                break;
            }
        }
    }

    /**
     * Gets the {@link ArmorClass}es held by this manager
     *
     * @return set of {@link ArmorClass}es
     */
    public Collection<ArmorClass> getPvpClasses() {
        return pvpClasses;
    }

    /**
     * Gets the equipped {@link ArmorClass} of a {@link Player}.
     *
     * @param player the {@link Player} to getInstance for
     * @return the equipped {@link ArmorClass}
     */
    public ArmorClass getEquippedClass(Player player) {
        synchronized (equippedClassMap) {
            return equippedClassMap.get(player.getUniqueId());
        }
    }

    public boolean hasClassEquipped(Player player, ArmorClass pvpClass) {
        return getEquippedClass(player) == pvpClass;
    }

    /**
     * Sets the equipped {@link ArmorClass} of a {@link Player}.
     *
     * @param player   the {@link Player} to set for
     * @param pvpClass the class to equip or null to un-equip active
     */
    public void setEquippedClass(Player player, ArmorClass pvpClass) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Match match = profile.getMatch();
        if (match != null && match.isHCFMatch() ) {
            if (pvpClass == null) {
                ArmorClass equipped = this.equippedClassMap.remove(player.getUniqueId());
                if (equipped != null) {
                    equipped.onUnequip(player);
                    Bukkit.getPluginManager().callEvent(new ArmorClassUnequipEvent(player, equipped));
                }
            } else if (pvpClass.onEquip(player) && pvpClass != this.getEquippedClass(player)) {
                equippedClassMap.put(player.getUniqueId(), pvpClass);
                Bukkit.getPluginManager().callEvent(new ArmorClassEquipEvent(player, pvpClass));
            }
        }
    }
}
