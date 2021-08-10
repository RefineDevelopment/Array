package xyz.refinedev.practice.match.task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/10/2021
 * Project: Array
 */

public class MatchBowCooldownTask extends BukkitRunnable {

    private final Array plugin = Array.getInstance();

    @Override
    public void run() {
        for ( Player player : Array.getInstance().getServer().getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if ((profile.isInFight() || profile.isInEvent()) && !profile.getBowCooldown().hasExpired()) {
                int seconds = Math.round(profile.getBowCooldown().getRemaining()) / 1_000;

                player.setLevel(seconds);
                player.setExp(profile.getBowCooldown().getRemaining() / (plugin.getConfigHandler().getBOW_COOLDOWN() * 1_000F));
            } else {
                if (profile.isInFight() || profile.isInEvent()) {
                    if (!profile.getBowCooldown().isNotified() && !profile.isInLobby()) {
                        profile.getBowCooldown().setNotified(true);
                        player.sendMessage(Locale.MATCH_EPEARL_EXPIRE.toString());
                    }
                }
                if (player.getLevel() > 0) player.setLevel(0);
                if (player.getExp() > 0.0F) player.setExp(0.0F);
            }
        }
    }
}
