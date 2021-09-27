package xyz.refinedev.practice.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;

@RequiredArgsConstructor
public class MatchPearlCooldownTask extends BukkitRunnable {

    private final Array plugin;

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());

            if ((profile.isInFight() || profile.isInEvent()) && !profile.getEnderpearlCooldown().hasExpired()) {
                int seconds = Math.round(profile.getEnderpearlCooldown().getRemaining()) / 1_000;

                player.setLevel(seconds);
                player.setExp(profile.getEnderpearlCooldown().getRemaining() / (Array.getInstance().getConfigHandler().getENDERPEARL_COOLDOWN() * 1_000F));
            } else {
                if (profile.isInFight() || profile.isInEvent()) {
                    if (!profile.getEnderpearlCooldown().isNotified() && !profile.isInLobby()) {
                        profile.getEnderpearlCooldown().setNotified(true);
                        player.sendMessage(Locale.MATCH_EPEARL_EXPIRE.toString());
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
