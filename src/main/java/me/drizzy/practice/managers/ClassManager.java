package me.drizzy.practice.managers;

import me.drizzy.practice.pvpclasses.PvPClass;
import me.drizzy.practice.pvpclasses.events.ArmorClassEquipEvent;
import me.drizzy.practice.pvpclasses.events.ArmorClassUnequipEvent;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.Array;
import me.drizzy.practice.pvpclasses.classes.Archer;
import me.drizzy.practice.pvpclasses.classes.Bard;
import me.drizzy.practice.pvpclasses.classes.Rogue;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.events.ArmorEquipEvent;
import me.drizzy.practice.util.inventory.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ClassManager implements Listener {

    // Mapping to get the PVP Class a player has equipped.
    private Map<UUID, PvPClass> equippedClassMap = new HashMap<>();

    protected Map<UUID, PvPClass> classWarmups = new HashMap<>();

    private List<PvPClass> pvpClasses = new ArrayList<>();

    public ClassManager(Array plugin) {
        pvpClasses.add(new Bard(plugin));
        pvpClasses.add(new Archer(plugin));
        pvpClasses.add(new Rogue(plugin));

        Bukkit.getPluginManager().registerEvents(this, plugin);
        for ( PvPClass pvpClass : pvpClasses) {
            if (pvpClass instanceof Listener) {
                plugin.getServer().getPluginManager().registerEvents((Listener) pvpClass, plugin);
            }
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    Profile profile = Profile.getByUuid(player.getUniqueId());
                    Match match = profile.getMatch();
                    if (match != null && match.isHCFMatch()) {
                        Bukkit.getScheduler().runTask(Array.getInstance(), () -> {
                            attemptEquip(player);
                        });
                    }
                }
            }
        }.runTaskTimer(plugin , 1 , 1);
    }

    public void onDisable() {
        for (Map.Entry<UUID, PvPClass> entry : new HashMap<>(equippedClassMap).entrySet()) {
            this.setEquippedClass(Bukkit.getPlayer(entry.getKey()), null);
        }

        this.pvpClasses.clear();
        this.equippedClassMap.clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());
        if (profile.isInMatch() && profile.getMatch().isHCFMatch()) {
            setEquippedClass(event.getEntity(), null);
        }
    }

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
        PvPClass current = Array.getInstance().getClassManager().getEquippedClass(player);
        if (current != null) {
            if (current.isApplicableFor(player)) {
                return;
            }

            Array.getInstance().getClassManager().setEquippedClass(player, null);
        } else if ((current = classWarmups.get(player.getUniqueId())) != null) {
            if (current.isApplicableFor(player)) {
                return;
            }

        }

        Collection<PvPClass> pvpClasses = Array.getInstance().getClassManager().getPvpClasses();
        for ( PvPClass pvpClass : pvpClasses) {
            if (pvpClass.isApplicableFor(player)) {
                Array.getInstance().getClassManager().setEquippedClass(player, pvpClass);
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

    public boolean hasClassEquipped(Player player, PvPClass pvpClass) {
        return getEquippedClass(player) == pvpClass;
    }

    /**
     * Sets the equipped {@link PvPClass} of a {@link Player}.
     *
     * @param player   the {@link Player} to set for
     * @param pvpClass the class to equip or null to un-equip active
     */
    public void setEquippedClass(Player player, PvPClass pvpClass) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Match match = profile.getMatch();
        if (match != null && match.isHCFMatch()) {
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

    public static void giveBardKit(Player player) {
        player.getInventory().setHelmet(new ItemBuilder(Material.GOLD_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setChestplate(new ItemBuilder(Material.GOLD_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setLeggings(new ItemBuilder(Material.GOLD_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setBoots(new ItemBuilder(Material.GOLD_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).enchantment(Enchantment.PROTECTION_FALL, 4).build());

        player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2).enchantment(Enchantment.FIRE_ASPECT, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setItem(1, new ItemBuilder(Material.ENDER_PEARL).amount(16).build());
        player.getInventory().setItem(2, new ItemBuilder(Material.IRON_INGOT).amount(64).build());
        player.getInventory().setItem(3, new ItemBuilder(Material.BLAZE_POWDER).amount(64).build());
        player.getInventory().setItem(4, new ItemBuilder(Material.GHAST_TEAR).amount(16).build());
        player.getInventory().setItem(5, new ItemBuilder(Material.POTION).amount(1).durability(8259).build());
        player.getInventory().setItem(8, new ItemBuilder(Material.COOKED_BEEF).amount(64).build());
        player.getInventory().setItem(18, new ItemBuilder(Material.FEATHER).amount(32).build());
        player.getInventory().setItem(9, new ItemBuilder(Material.SUGAR).amount(64).build());
        player.getInventory().setItem(35, new ItemBuilder(Material.MAGMA_CREAM).amount(64).build());
        player.getInventory().setItem(26, new ItemBuilder(Material.SPIDER_EYE).amount(32).build());

        ItemStack pots = new ItemBuilder(Material.POTION).durability(16421).build();

        while (player.getInventory().firstEmpty() != -1) {
            if (player.getInventory().firstEmpty() == -1) {
                return;
            }
            player.getInventory().addItem(pots);
        }
        player.updateInventory();

        Array.getInstance().getClassManager().attemptEquip(player);
    }

    public static void giveDiamondKit(Player player) {
        player.getInventory().setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).enchantment(Enchantment.PROTECTION_FALL, 4).build());

        player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2).enchantment(Enchantment.FIRE_ASPECT, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setItem(1, new ItemBuilder(Material.ENDER_PEARL).amount(16).build());
        player.getInventory().setItem(2, new ItemBuilder(Material.POTION).amount(1).durability(8259).build());
        player.getInventory().setItem(3, new ItemBuilder(Material.POTION).amount(1).durability(8226).build());
        player.getInventory().setItem(8, new ItemBuilder(Material.COOKED_BEEF).amount(64).build());


        player.getInventory().setItem(17, new ItemBuilder(Material.POTION).amount(1).durability(8226).build());
        player.getInventory().setItem(26, new ItemBuilder(Material.POTION).amount(1).durability(8226).build());
        player.getInventory().setItem(35, new ItemBuilder(Material.POTION).amount(1).durability(8226).build());

        ItemStack pots = new ItemBuilder(Material.POTION).durability(16421).build();
        while (player.getInventory().firstEmpty() != -1) {
            if (player.getInventory().firstEmpty() == -1) {
                return;
            }
            player.getInventory().addItem(pots);
        }
        player.updateInventory();
    }


    public static void giveArcherKit(Player player) {
        player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).enchantment(Enchantment.PROTECTION_FALL, 4).build());

        player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 2).enchantment(Enchantment.FIRE_ASPECT, 2).enchantment(Enchantment.DURABILITY, 3).build());
        player.getInventory().setItem(2, new ItemBuilder(Material.ENDER_PEARL).amount(16).build());
        player.getInventory().setItem(1, new ItemBuilder(Material.BOW).enchantment(Enchantment.ARROW_DAMAGE, 3).enchantment(Enchantment.DURABILITY, 3).enchantment(Enchantment.ARROW_FIRE, 1).enchantment(Enchantment.ARROW_INFINITE, 1).build());
        player.getInventory().setItem(3, new ItemBuilder(Material.POTION).amount(1).durability(8259).build());

        player.getInventory().setItem(7, new ItemBuilder(Material.COOKED_BEEF).amount(64).build());
        player.getInventory().setItem(8, new ItemBuilder(Material.SUGAR).amount(64).build());

        player.getInventory().setItem(26, new ItemBuilder(Material.ARROW).amount(1).build());
        player.getInventory().setItem(17, new ItemBuilder(Material.FEATHER).amount(64).build());

        ItemStack pots = new ItemBuilder(Material.POTION).durability(16421).build();
        while (player.getInventory().firstEmpty() != -1) {
            if (player.getInventory().firstEmpty() == -1) {
                return;
            }
            player.getInventory().addItem(pots);
        }
        player.updateInventory();

        Array.getInstance().getClassManager().attemptEquip(player);
    }
}
