package me.array.ArrayPractice.match.task;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchPearlCooldownTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Practice.getInstance().getServer().getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if ((profile.isInFight() || profile.isInEvent()) && !profile.getEnderpearlCooldown().hasExpired()) {
                int seconds = Math.round(profile.getEnderpearlCooldown().getRemaining()) / 1_000;

                player.setLevel(seconds);
                player.setExp(profile.getEnderpearlCooldown().getRemaining() / 16_000.0F);
            } else {
                if (profile.isInFight() || profile.isInEvent()) {
                    if (!profile.getEnderpearlCooldown().isNotified()) {
                        profile.getEnderpearlCooldown().setNotified(true);
                        player.sendMessage(CC.GREEN + "You can now pearl again.");
                    }
                }

                if (player.getLevel() > 0) {
                    player.setLevel(0);
                }

                if (player.getExp() > 0.0F) {
                    player.setExp(0.0F);
                }
            }
        }
    }

}
