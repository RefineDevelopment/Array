package xyz.refinedev.practice.util.timer.impl;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.other.TimeUtil;
import xyz.refinedev.practice.util.timer.PlayerTimer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BridgeArrowTimer extends PlayerTimer implements Listener {

    private final Array plugin;

    public BridgeArrowTimer(Array plugin) {
        super("Arrow", TimeUnit.SECONDS.toMillis(plugin.getConfigHandler().getBOW_COOLDOWN()));

        this.plugin = plugin;
    }

    @Override
    protected void handleExpiry(Player player, UUID playerUUID) {
        super.handleExpiry(player, playerUUID);
        if (player == null) {
            return;
        }
        player.sendMessage(Locale.MATCH_BOW_COOLDOWN_EXPIRE.toString());
        if (!player.getInventory().contains(Material.ARROW)) {
            player.getInventory().addItem(new ItemStack(Material.ARROW));
        }
    }

    @EventHandler
    public void onArrowShoot(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof Arrow) {
            Player player = (Player)event.getEntity().getShooter();
            Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
            if (!profile.isInMatch() && !profile.getMatch().isTheBridgeMatch()) {
                this.clearCooldown(player);
                return;
            }

            long cooldown = this.getRemaining(player);
            if (cooldown > 0) {
                event.setCancelled(true);
                String time = TimeUtil.millisToSeconds(cooldown);
                String context = "second" + (time.equalsIgnoreCase("1.0") ? "" : "s");

                player.sendMessage(Locale.MATCH_BOW_COOLDOWN.toString().replace("<cooldown>", time + " " + context));
                player.updateInventory();
                return;
            }
            this.setCooldown(player, player.getUniqueId());
        }
    }
}

