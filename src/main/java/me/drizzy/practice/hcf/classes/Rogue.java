package me.drizzy.practice.hcf.classes;

import me.drizzy.practice.hcf.HCFClasses;
import me.drizzy.practice.Array;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Rogue extends HCFClasses implements Listener {

    public static PotionEffect ROGUE_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 3);
    public static PotionEffect ROGUE_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 160, 4);
    public static long ROGUE_SPEED_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(45L);
    public static long ROGUE_JUMP_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
    public static Map<UUID, Long> rogueSpeedCooldowns = new HashMap<>();
    public static Map<UUID, Long> rogueJumpCooldowns = new HashMap<>();
    public static Map<UUID, Long> stabCoolDown = new HashMap<>();
    private static final DecimalFormat ROUGEDECIMAL = new DecimalFormat("0.0");
    private final Array plugin;

    public Rogue(Array plugin) {
        super("Rogue", TimeUnit.MILLISECONDS.toMillis(1L));

        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (((event.getEntity() instanceof Player)) && ((event.getDamager() instanceof Player))) {
            Entity entity = event.getEntity();
            Entity damager = event.getDamager();
            Player attacker = (Player) damager;
            if (plugin.getHCFManager().getEquippedClass(attacker) == this) {
                ItemStack stack = attacker.getItemInHand();
                if ((stack != null) && (stack.getType() == Material.GOLD_SWORD)
                        && (stack.getEnchantments().isEmpty())) {
                    Player player = (Player) entity;
                    if (direction(attacker) == direction(player)) {
                        Damageable damage = player;
                        if (damage.getHealth() <= 0.0D) {
                            return;
                        }
                        if (stabCoolDown.containsKey(attacker.getUniqueId())) {
                            final long value = stabCoolDown.get(attacker.getUniqueId());
                            if (value > System.currentTimeMillis()) {
                                event.setCancelled(true);
                                attacker.sendMessage(ChatColor.YELLOW + "You can not do this for another " + ChatColor.RED + ROUGEDECIMAL.format((value - System.currentTimeMillis()) / 1000.0D) + " seconds" + ChatColor.YELLOW + "!");
                                return;
                            }
                        }
                        stabCoolDown.put(attacker.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));

                        if (damage.getHealth() <= 6.0D) {
                            damage.damage(20.0D);
                        } else {
                            damage.setHealth(damage.getHealth() - 6.0D);
                        }
                        player.sendMessage("§c" + attacker.getName() + ChatColor.YELLOW + " has backstabbed you.");
                        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                        attacker.sendMessage(ChatColor.YELLOW + "You have backstabbed " + "§c" + player.getName()
                                + ChatColor.YELLOW + '.');
                        attacker.setItemInHand(new ItemStack(Material.AIR, 1));
                        attacker.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        stabCoolDown.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Action action = event.getAction();
        if (((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (event.hasItem()) && (event.getItem().getType() == Material.SUGAR)) {
            if (this.plugin.getHCFManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }
            long timestamp = rogueSpeedCooldowns.getOrDefault(event.getPlayer().getUniqueId(), 0L);
            long millis = System.currentTimeMillis();
            long remaining = timestamp - millis;
            if (remaining > 0L) {
                player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
            } else {
                ItemStack stack = player.getItemInHand();
                if (stack.getAmount() == 1) {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                } else {
                    stack.setAmount(stack.getAmount() - 1);
                }

                this.plugin.getEffectRestorer().setRestoreEffect(player, ROGUE_SPEED_EFFECT);
                rogueSpeedCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + ROGUE_SPEED_COOLDOWN_DELAY);
            }
        } else if (((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (event.hasItem()) && (event.getItem().getType() == Material.FEATHER)) {

            if (this.plugin.getHCFManager().getEquippedClass(event.getPlayer()) != this) {
                return;
            }

            long timestamp = rogueJumpCooldowns.getOrDefault(event.getPlayer().getUniqueId(), 0L);
            long millis = System.currentTimeMillis();
            long remaining1 = timestamp - millis;
            if (remaining1 > 0L) {
                player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + DurationFormatUtils.formatDurationWords(remaining1, true, true) + ".");
            } else {
                ItemStack stack = player.getItemInHand();
                if (stack.getAmount() == 1) {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                } else {
                    stack.setAmount(stack.getAmount() - 1);
                }

                this.plugin.getEffectRestorer().setRestoreEffect(player, ROGUE_JUMP_EFFECT);
                Rogue.rogueJumpCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + ROGUE_JUMP_COOLDOWN_DELAY);
            }
        }
    }

    @Override
    public boolean isApplicableFor(final Player player) {
        if (player.getInventory().getBoots() == null || player.getInventory().getHelmet() == null || player.getInventory().getChestplate() == null || player.getInventory().getLeggings() == null) {
            return false;
        }
        final ItemStack helmet = player.getInventory().getHelmet();
        final ItemStack chestplate = player.getInventory().getChestplate();
        final ItemStack leggings = player.getInventory().getLeggings();
        final ItemStack boots = player.getInventory().getBoots();
        return helmet.getType().equals(Material.CHAINMAIL_HELMET) && chestplate.getType().equals(Material.CHAINMAIL_CHESTPLATE) && leggings.getType().equals(Material.CHAINMAIL_LEGGINGS) && boots.getType().equals(Material.CHAINMAIL_BOOTS);
    }

    public Byte direction(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            return 0xC; // S > E
        } else if (22.5 <= rotation && rotation < 67.5) {
            return 0xE; // SW > SE
        } else if (67.5 <= rotation && rotation < 112.5) {
            return 0x0; // W > E
        } else if (112.5 <= rotation && rotation < 157.5) {
            return 0x2; // NW > SW
        } else if (157.5 <= rotation && rotation < 202.5) {
            return 0x4; // N > W
        } else if (202.5 <= rotation && rotation < 247.5) {
            return 0x6; // NE > NW
        } else if (247.5 <= rotation && rotation < 292.5) {
            return 0x8; // E > N
        } else if (292.5 <= rotation && rotation < 337.5) {
            return 0xA; // SE > NE
        } else if (337.5 <= rotation && rotation < 360.0) {
            return 0xC; // S > E
        } else {
            return null;
        }
    }
}
