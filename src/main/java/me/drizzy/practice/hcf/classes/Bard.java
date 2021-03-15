package me.drizzy.practice.hcf.classes;

import me.drizzy.practice.match.Match;
import me.drizzy.practice.hcf.HCFClasses;
import me.drizzy.practice.hcf.bard.BardData;
import me.drizzy.practice.hcf.bard.EffectData;
import me.drizzy.practice.Array;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Color;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Bard extends HCFClasses implements Listener {

    public static int HELD_EFFECT_DURATION_TICKS = 100; // the amount of time in ticks to apply a Held potion effect for faction members
    public static Map<UUID, Long> archerJumpCooldowns = new HashMap<>();
    private static final long BUFF_COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(10L); // time in milliseconds for Bard buff cooldowns
    private static final int TEAMMATE_NEARBY_RADIUS = 25;
    private static final long HELD_REAPPLY_TICKS = 20L;
    private final Map<UUID, BardData> bardDataMap = new HashMap<>();
    private final Map<Material, EffectData> bardEffects = new EnumMap<>(Material.class);
    private final Array plugin;

    public Bard(Array plugin) {
        super("Bard", TimeUnit.SECONDS.toMillis(1L));
        this.plugin = plugin;

        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

        this.bardEffects.put(Material.FERMENTED_SPIDER_EYE, new EffectData(60, new PotionEffect(PotionEffectType.INVISIBILITY, 120, 1), new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0)));
        this.bardEffects.put(Material.WHEAT, new EffectData(35, new PotionEffect(PotionEffectType.SATURATION, 120, 1), new PotionEffect(PotionEffectType.SATURATION, 100, 0)));
        this.bardEffects.put(Material.SUGAR, new EffectData(25, new PotionEffect(PotionEffectType.SPEED, 120, 2), new PotionEffect(PotionEffectType.SPEED, 100, 1)));
        this.bardEffects.put(Material.BLAZE_POWDER, new EffectData(45, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0)));
        this.bardEffects.put(Material.IRON_INGOT, new EffectData(35, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 2), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0)));
        this.bardEffects.put(Material.GHAST_TEAR, new EffectData(45, new PotionEffect(PotionEffectType.REGENERATION, 60, 2), new PotionEffect(PotionEffectType.REGENERATION, 100, 0)));
        this.bardEffects.put(Material.FEATHER, new EffectData(25, new PotionEffect(PotionEffectType.JUMP, 120, 7), new PotionEffect(PotionEffectType.JUMP, 100, 1)));
        this.bardEffects.put(Material.SPIDER_EYE, new EffectData(50, new PotionEffect(PotionEffectType.WITHER, 100, 1), null));
        this.bardEffects.put(Material.MAGMA_CREAM, new EffectData(10, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 900, 0), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0)));
    }

    @Override
    public boolean onEquip(Player player) {
        if (!super.onEquip(player)) {
            return false;
        }

        Profile profile = Profile.getByUuid(player.getUniqueId());
        Match match = profile.getMatch();
        if (match != null && (match.isHCFMatch() )) {
            Team team = profile.getMatch().getTeam(player);
            BardData bardData = new BardData();
            bardDataMap.put(player.getUniqueId(), bardData);
            bardData.startEnergyTracking();
            bardData.heldTask = new BukkitRunnable() {
                int lastEnergy;

                @SuppressWarnings("deprecation")
                @Override
                public void run() {
                    // Apply the bard effects here.
                    ItemStack held = player.getItemInHand();
                    if (held != null) {
                        EffectData bardEffect = bardEffects.get(held.getType());
                        if (bardEffect == null) return;

                        // Apply the held effect to faction members.
                        if (player.getItemInHand().getType() == Material.FEATHER) {
                            plugin.getEffectRestorer().setRestoreEffect(player, bardEffect.heldable);
                        }
                        Match match = profile.getMatch();
                        Team team = profile.getMatch().getTeam(player);
                        if (team != null) {
                            Collection<Entity> nearbyEntities = player.getNearbyEntities(TEAMMATE_NEARBY_RADIUS, TEAMMATE_NEARBY_RADIUS, TEAMMATE_NEARBY_RADIUS);
                            for (Entity nearby : nearbyEntities) {
                                if (nearby instanceof Player && !player.equals(nearby)) {
                                    Player target = (Player) nearby;
                                    if (team.getAliveTeamPlayers().contains(match.getTeamPlayer(target))) {
                                        plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.heldable);
                                    }
                                }
                            }
                        }
                    }

                    int energy = (int) getEnergy(player);
                    // the -1 check is for offsets with the energy per millisecond
                    if (energy != 0 && energy != lastEnergy && (energy % 10 == 0 || lastEnergy - energy - 1 > 0 || energy == BardData.MAX_ENERGY)) {
                        lastEnergy = energy;
                        player.sendMessage(Color.translate("&bBard Energy: &r" + energy));
                    }
                }
            }.runTaskTimer(plugin, 0L, HELD_REAPPLY_TICKS);
            return true;
        }
        return true;
    }

    @Override
    public void onUnequip(Player player) {
        super.onUnequip(player);
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Match match = profile.getMatch();
        if (match != null && (match.isHCFMatch() )) {
            clearBardData(player.getUniqueId());
        }
    }

    private void clearBardData(UUID uuid) {
        BardData bardData = bardDataMap.remove(uuid);
        if (bardData != null && bardData.getHeldTask() != null) {
            bardData.getHeldTask().cancel();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        Match match = profile.getMatch();
        if (match != null && (match.isHCFMatch() )) {
            clearBardData(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        Match match = profile.getMatch();
        if (match != null && (match.isHCFMatch() )) {
            clearBardData(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemHeld(PlayerItemHeldEvent event) {

        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        Match match = profile.getMatch();
        if (match != null && (match.isHCFMatch() )) {
            Player player = event.getPlayer();
            HCFClasses equipped = plugin.getHCFManager().getEquippedClass(player);
            if (equipped == null || !equipped.equals(this)) {
                return;
            }

            UUID uuid = player.getUniqueId();
            long timestamp = archerJumpCooldowns.getOrDefault(event.getPlayer().getUniqueId(), 0L);
            long millis = System.currentTimeMillis();
            long remaining = timestamp - millis;
            if (remaining > 0L) {
                return;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());
        Match match = profile.getMatch();
        if (match != null && (match.isHCFMatch() )) {
            Team team = profile.getMatch().getTeam(event.getPlayer());
            if (!event.hasItem()) {
                return;
            }
            Action action = event.getAction();
            if (action == Action.RIGHT_CLICK_AIR || (!event.isCancelled() && action == Action.RIGHT_CLICK_BLOCK)) {
                ItemStack stack = event.getItem();
                EffectData bardEffect = this.bardEffects.get(stack.getType());
                if (bardEffect == null || bardEffect.clickable == null) {
                    return;
                }
                event.setUseItemInHand(Event.Result.DENY);
                Player player = event.getPlayer();
                BardData bardData = this.bardDataMap.get(player.getUniqueId());
                if (bardData != null) {
                    if (!this.canUseBardEffect(player, bardData, bardEffect, true)) {
                        return;
                    }
                    if (stack.getAmount() > 1) {
                        stack.setAmount(stack.getAmount() - 1);
                    } else {
                        player.setItemInHand(new ItemStack(Material.AIR, 1));
                    }
                    if (team != null && !bardEffect.clickable.getType().equals(PotionEffectType.WITHER)) {
                        Collection<Entity> nearbyEntities = player.getNearbyEntities(30.0, 30.0, 30.0);
                        for (Entity nearby : nearbyEntities) {
                            if (nearby instanceof Player && !player.equals(nearby)) {
                                Player target = (Player) nearby;
                                if (!team.getAliveTeamPlayers().contains(match.getTeamPlayer(target))) {
                                    continue;
                                }
                                plugin.getEffectRestorer().setRestoreEffect(target, bardEffect.clickable);
                            }
                        }
                    }
                    if (bardEffect.clickable.getType() != PotionEffectType.INCREASE_DAMAGE) {
                        plugin.getEffectRestorer().setRestoreEffect(player, bardEffect.clickable);
                    }
                    bardData.setBuffCooldown(BUFF_COOLDOWN_MILLIS);

                    @SuppressWarnings("unused")
                    double newEnergy = this.setEnergy(player, bardData.getEnergy() - bardEffect.energyCost);
                    player.sendMessage(Color.translate("&bYou have just used a &lBard Buff &bthat cost you " + bardEffect.energyCost + " &bof your Energy."));
                }
            }
        }
    }

    private boolean canUseBardEffect(Player player, BardData bardData, EffectData bardEffect, boolean sendFeedback) {
        String errorFeedback = null;
        double currentEnergy = bardData.getEnergy();
        if (bardEffect.energyCost > currentEnergy) {
            errorFeedback = CC.translate("&bYou do not have enough energy for this! You need " + bardEffect.energyCost + " energy, but you only have " + currentEnergy);
        }

        long remaining = bardData.getRemainingBuffDelay() / 1000;
        if (remaining > 0L) {
            errorFeedback = ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + remaining + ChatColor.RED + " seconds.";
        }

        if (sendFeedback && errorFeedback != null) {
            player.sendMessage(errorFeedback);
        }

        return errorFeedback == null;
    }

    @Override
    public boolean isApplicableFor(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null || helmet.getType() != Material.GOLD_HELMET) return false;

        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null || chestplate.getType() != Material.GOLD_CHESTPLATE) return false;

        ItemStack leggings = player.getInventory().getLeggings();
        if (leggings == null || leggings.getType() != Material.GOLD_LEGGINGS) return false;

        ItemStack boots = player.getInventory().getBoots();
        return !(boots == null || boots.getType() != Material.GOLD_BOOTS);
    }

    public long getRemainingBuffDelay(Player player) {
        synchronized (bardDataMap) {
            BardData bardData = bardDataMap.get(player.getUniqueId());
            return bardData == null ? 0L : bardData.getRemainingBuffDelay();
        }
    }

    /**
     * Gets the energy of a {@link Player}.
     *
     * @param player the {@link Player} to getInstance for
     * @return the energy, or 0 if not tracking this player
     */
    public double getEnergy(Player player) {
        synchronized (bardDataMap) {
            BardData bardData = bardDataMap.get(player.getUniqueId());
            return bardData == null ? 0 : bardData.getEnergy();
        }
    }

    public long getEnergyMillis(Player player) {
        synchronized (bardDataMap) {
            BardData bardData = bardDataMap.get(player.getUniqueId());
            return bardData == null ? 0 : bardData.getEnergyMillis();
        }
    }

    /**
     * Sets the energy of a {@link Player}.
     *
     * @param player the {@link Player} to set for
     * @param energy the energy amount to set
     * @return the new energy amount
     */
    public double setEnergy(Player player, double energy) {
        BardData bardData = bardDataMap.get(player.getUniqueId());
        if (bardData == null) return 0.0;

        bardData.setEnergy(energy);
        return bardData.getEnergy();
    }
}
