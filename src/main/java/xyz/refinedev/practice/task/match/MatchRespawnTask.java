package xyz.refinedev.practice.task.match;

import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TitleAPI;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/10/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class MatchRespawnTask extends BukkitRunnable {

    private final Array plugin;
    private final Player player;
    private final Match match;

    private int respawn = 4;

    @Override
    public void run() {
        if (this.respawn <= 1) {
            TeamPlayer teamPlayer = match.getTeamPlayer(player);

            player.removePotionEffect(PotionEffectType.WEAKNESS);
            player.teleport(teamPlayer.getPlayerSpawn());
            match.getAllPlayers().forEach(matchPlayer -> matchPlayer.showPlayer(player));
            player.setFallDistance(50.0f);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.sendMessage(CC.translate("&aYou have respawned!"));
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10.0f, 1.0f);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                player.resetMaxHealth();
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                match.getKit().applyToPlayer(player);
                PlayerUtil.giveWoolKit(match, player);
                TitleAPI.sendRespawning(player);
                this.cancel();
            }, 2L);
        }

        if (this.respawn == 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0));
            match.getAllPlayers().forEach(matchPlayer -> matchPlayer.hidePlayer(player));
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.updateInventory();
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setVelocity(player.getVelocity().add(new Vector(0.0, 0.25, 0.0)));
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setVelocity(player.getVelocity().add(new Vector(0.0, 0.15, 0.0)));
            player.setAllowFlight(true);
            player.setFlying(true);
            if (player.getKiller() != null) {
                player.teleport(player.getKiller());
            }
        }
        --this.respawn;
        TitleAPI.sendRespawnCountdown(player, this.respawn);
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10.0f, 1.0f);
    }
}
