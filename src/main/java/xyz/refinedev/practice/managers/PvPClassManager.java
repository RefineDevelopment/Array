package xyz.refinedev.practice.managers;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.pvpclasses.PvPClass;
import xyz.refinedev.practice.pvpclasses.classes.Archer;
import xyz.refinedev.practice.pvpclasses.classes.Bard;
import xyz.refinedev.practice.pvpclasses.classes.Rogue;
import xyz.refinedev.practice.pvpclasses.events.ArmorClassEquipEvent;
import xyz.refinedev.practice.pvpclasses.events.ArmorClassUnequipEvent;
import xyz.refinedev.practice.util.events.ArmorEquipEvent;
import xyz.refinedev.practice.util.inventory.InventoryUtil;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.*;

@RequiredArgsConstructor
public class PvPClassManager {

    private final Array plugin;

    private final List<PvPClass> pvpClasses = new ArrayList<>();
    private final Map<UUID, PvPClass> equippedClassMap = new HashMap<>();
    protected final Map<UUID, PvPClass> classWarmups = new HashMap<>();

    public void init() {
        pvpClasses.add(new Bard(plugin));
        pvpClasses.add(new Archer(plugin));
        pvpClasses.add(new Rogue(plugin));

        for ( PvPClass pvpClass : pvpClasses) {
            this.plugin.getServer().getPluginManager().registerEvents((Listener) pvpClass, this.plugin);
        }

        TaskUtil.runTimer(() -> {
            for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
                if (!profile.isInFight()) continue;

                Match match = profile.getMatch();
                if (!match.isHCFMatch()) continue;

                this.attemptEquip(player);
            }
        }, 2L, 2L);
    }

    public void onDisable() {
        for (Map.Entry<UUID, PvPClass> entry : new HashMap<>(equippedClassMap).entrySet()) {
            this.setEquippedClass(Bukkit.getPlayer(entry.getKey()), null);
        }

        this.pvpClasses.clear();
        this.equippedClassMap.clear();
    }

    /**
     * Attempt to register and equip a {@link PvPClass}
     * on the player, if the are wearing the correct gear then
     * it will equip the specified {@link PvPClass} to them
     *
     * @param player {@link Player} the player equipping the class
     */
    public void attemptEquip(Player player) {
        PvPClass current = this.getEquippedClass(player);

        if (current != null) {
            if (current.isApplicableFor(player)) {
                return;
            }
            this.setEquippedClass(player, null);
        } else if (classWarmups.containsKey(player.getUniqueId())) {
            current = classWarmups.get(player.getUniqueId());
            if (current.isApplicableFor(player)) {
                return;
            }
        }

        Collection<PvPClass> pvpClasses = this.getPvpClasses();
        for ( PvPClass pvpClass : pvpClasses) {
            if (pvpClass.isApplicableFor(player)) {
                this.setEquippedClass(player, pvpClass);
                break;
            }
        }
    }

    /**
     * Gets the {@link PvPClass}es held by this manager
     *
     * @return set of {@link PvPClass}es
     */
    public Collection<PvPClass> getPvpClasses() {
        return pvpClasses;
    }

    /**
     * Gets the equipped {@link PvPClass} of a {@link Player}.
     *
     * @param player the {@link Player} to get for
     * @return the equipped {@link PvPClass}
     */
    public PvPClass getEquippedClass(Player player) {
        synchronized (equippedClassMap) {
            return equippedClassMap.get(player.getUniqueId());
        }
    }

    /**
     * Sets the equipped {@link PvPClass} of a {@link Player}.
     *
     * @param player   the {@link Player} to set for
     * @param pvpClass the class to equip or null to un-equip active
     */
    public void setEquippedClass(Player player, PvPClass pvpClass) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (!profile.isInFight()) return;

        Match match = profile.getMatch();
        if (!match.isHCFMatch()) return;

        if (pvpClass == null) {
            PvPClass equipped = this.equippedClassMap.get(player.getUniqueId());
            if (equipped != null) {
                equipped.onUnequip(player);
                Bukkit.getPluginManager().callEvent(new ArmorClassUnequipEvent(player, equipped));
            }
            this.equippedClassMap.remove(player.getUniqueId());
        } else if (pvpClass.onEquip(player) && pvpClass != this.getEquippedClass(player)) {
            equippedClassMap.put(player.getUniqueId(), pvpClass);
            Bukkit.getPluginManager().callEvent(new ArmorClassEquipEvent(player, pvpClass));
        }
    }
}
